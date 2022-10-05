package com.dd.position.simulator.publisher;

import com.dd.position.simulator.journey.model.Position;
import com.dd.position.simulator.utils.GzipUtility;
import com.dd.position.simulator.utils.JsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PositionPublisher {

    private final KinesisClient kinesisClient;
    private final JsonUtility jsonUtility;

    @Value("${kinesis.stream.name}")
    private String streamName;

    public PositionPublisher(KinesisClient kinesisClient, JsonUtility jsonUtility) {
        this.kinesisClient = kinesisClient;
        this.jsonUtility = jsonUtility;
    }

    public void publish(List<Position> records) throws JsonProcessingException {
        log.debug("position records..." + jsonUtility.convertToString(records));

        List<Position> positionEventRecords = Collections.unmodifiableList(records);

        // Do some processing or enhancement of data
        List<PutRecordsRequestEntry> requestEntries = positionEventRecords.stream()
                .map(record -> {
                    try {
                        return jsonUtility.convertToString(record);
                    } catch (JsonProcessingException e) {
                        log.error("Unable to convert record into json object");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(GzipUtility::serializeData).filter(Objects::nonNull)
                .map(GzipUtility::compressData).filter(Objects::nonNull)
                .map(data ->
                        PutRecordsRequestEntry.builder()
                                .partitionKey(UUID.randomUUID().toString())
                                .data(SdkBytes.fromByteArray(data))
                                .build()).collect(Collectors.toList());

        PutRecordsRequest putRecordsRequest =
                PutRecordsRequest.builder()
                        .streamName(streamName)
                        .records(requestEntries)
                        .build();
        PutRecordsResponse putRecordsResponse = kinesisClient.putRecords(putRecordsRequest);
        if (putRecordsResponse == null || putRecordsResponse.failedRecordCount() > 0) {
            log.error("Failed to publish records...");
        }
        log.debug("Position event published successfully..");
    }

}
