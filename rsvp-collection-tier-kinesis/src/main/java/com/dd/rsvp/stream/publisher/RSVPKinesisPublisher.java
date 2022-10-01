package com.dd.rsvp.stream.publisher;

import com.dd.rsvp.stream.domain.RSVPEventRecord;
import com.dd.rsvp.stream.utility.GzipUtility;
import com.dd.rsvp.stream.utility.JsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
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
public class RSVPKinesisPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSVPKinesisPublisher.class);

    private final KinesisClient kinesisClient;
    private final JsonUtility jsonUtility;
    private final Gson gson;

    @Value("${stream.name}")
    private String streamName;

    @Autowired
    public RSVPKinesisPublisher(KinesisClient kinesisClient, JsonUtility jsonUtility, Gson gson) {
        this.kinesisClient = kinesisClient;
        this.jsonUtility = jsonUtility;
        this.gson = gson;
    }

    public void publish(WebSocketMessage<?> message) {
        RSVPEventRecord rsvpEventRecord = gson.fromJson(message.getPayload().toString(), RSVPEventRecord.class);
        LOGGER.debug("RSVP Record..." + rsvpEventRecord.toString());

        List<RSVPEventRecord> rsvpEventRecords = Collections.singletonList(rsvpEventRecord);

        // Do some processing or enhancement of data
        List<PutRecordsRequestEntry> requestEntries = rsvpEventRecords.stream()
                .map(record -> {
                    try {
                        return jsonUtility.convertToString(record);
                    } catch (JsonProcessingException e) {
                        LOGGER.error("Unable to convert record into json object");
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
            LOGGER.error("Failed to publish records...");
        }
        LOGGER.debug("RSVP event published successfully..");
    }
}
