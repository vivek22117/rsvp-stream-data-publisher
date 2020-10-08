package com.ddsolutions.kinesis.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Group implements Serializable {
    private List<GroupTopic> group_topics;
    private String group_city;
    private String group_country;
    private int group_id;
    private String group_name;
    private double group_lon;
    private String group_urlname;
    private String group_state;
    private double group_lat;

}
