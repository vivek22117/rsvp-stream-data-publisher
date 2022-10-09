package com.dd.position.simulator.journey;

import com.dd.position.simulator.journey.model.Position;
import com.dd.position.simulator.publisher.PositionPublisher;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class Journey implements Callable<Object> {

    private final PositionPublisher publisher;
    private final List<String> positions;
    private final String vehicleName;
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Journey(PositionPublisher publisher, List<String> positions, String vehicleName) {
        this.publisher = publisher;
        this.positions = Collections.unmodifiableList(positions);
        this.vehicleName = vehicleName;
    }

    @Override
    public Object call() throws Exception {
        List<Position> listOfPositions = new ArrayList<>();

        while (true) {
            for (String nextData : positions) {
                if (Math.random() < 0.5) continue;

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
