package com.dd.position.simulator.journey.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Position {

    private String vehicle;
    private String lat;
    private String longitude;
    private String time;
}
