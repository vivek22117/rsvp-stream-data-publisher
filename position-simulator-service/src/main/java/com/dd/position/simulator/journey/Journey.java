package com.dd.position.simulator.journey;

import com.dd.position.simulator.journey.model.Position;
import com.dd.position.simulator.publisher.PositionPublisher;
import org.slf4j.Logger;
import software.amazon.awssdk.services.kinesis.KinesisClient;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;

import static org.slf4j.LoggerFactory.getLogger;

public class Journey implements Callable<Object> {

    private static final Logger logger = getLogger(Journey.class);


    private final PositionPublisher publisher;
    private final List<String> positions;
    private final String vehicleName;
    private final String queueName;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Journey(PositionPublisher publisher, List<String> positions, String vehicleName, String queueName) {
        this.publisher = publisher;
        this.positions = Collections.unmodifiableList(positions);
        this.vehicleName = vehicleName;
        this.queueName = queueName;
    }

    @Override
    public Object call() throws Exception {
        List<Position> listOfPositions = new ArrayList<>();

        while (true) {
            for(String nextData : positions) {
                if(Math.random() < 0.5) continue;

                String[] data = nextData.split("\"");
                String latitude = data[1];
                String longitude = data[3];

                Position position = Position.builder()
                        .vehicle(vehicleName)
                        .lat(latitude)
                        .longitude(longitude)
                        .time(formatter.format(new Date()))
                        .build();
                listOfPositions.add(position);

                if (listOfPositions.size() == 11) {
                    publisher.publish(listOfPositions);
                }
            }
        }
    }
}
