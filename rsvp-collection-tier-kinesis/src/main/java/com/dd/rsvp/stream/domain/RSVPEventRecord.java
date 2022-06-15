package com.dd.rsvp.stream.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RSVPEventRecord implements Serializable {
    private Venue venue;
    private String visibility;
    private String response;
    private int guests;
    private Member member;
    private int rsvp_id;
    private long mtime;
    private Event event;
    private Group group;
}
