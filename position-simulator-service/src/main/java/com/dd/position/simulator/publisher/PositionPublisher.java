package com.dd.position.simulator.publisher;

import com.dd.position.simulator.JsonUtility;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kinesis.KinesisClient;

@Component
@Slf4j
public class PositionPublisher {

    private final KinesisClient kinesisClient;
    private final JsonUtility jsonUtility;
    private final Gson gson;

    @Value("${kinesis.stream.name}")
    private String streamName;

    public PositionPublisher(KinesisClient kinesisClient, JsonUtility jsonUtility, Gson gson) {
        this.kinesisClient = kinesisClient;
        this.jsonUtility = jsonUtility;
        this.gson = gson;
    }


}
