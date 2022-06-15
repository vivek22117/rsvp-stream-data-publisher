package com.dd.rsvp.stream.domain;

import java.io.Serializable;

public class Event implements Serializable {
    private String event_name;
    private String event_id;
    private long time;
    private String event_url;

    public Event(String event_name, String event_id, long time, String event_url) {
        this.event_name = event_name;
        this.event_id = event_id;
        this.time = time;
        this.event_url = event_url;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEvent_url() {
        return event_url;
    }

    public void setEvent_url(String event_url) {
        this.event_url = event_url;
    }
}
