package com.ddsolutions.kinesis.domain;

import java.io.Serializable;

public class Member implements Serializable {
    private int member_id;
    private String photo;
    private String member_name;

    public Member(int member_id, String photo, String member_name) {
        this.member_id = member_id;
        this.photo = photo;
        this.member_name = member_name;
    }

    public int getMember_id() {
        return member_id;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getMember_name() {
        return member_name;
    }

    public void setMember_name(String member_name) {
        this.member_name = member_name;
    }
}
