package com.dd.rsvp.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Venue implements Serializable {
    private String venue_name;
    private double lon;
    private double lat;
    private int venue_id;

}
