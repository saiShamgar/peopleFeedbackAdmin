package com.shamgar.peoplefeedbackadmin.models;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class SpamModel {

    String  constituency,state,tagid,userNum,district;

    public SpamModel(String constituency, String state, String tagid, String userNum, String district) {
        this.constituency = constituency;
        this.state = state;
        this.tagid = tagid;
        this.userNum = userNum;
        this.district = district;
    }

    public SpamModel (){

    }

    public String getConstituency() {
        return constituency;
    }

    public void setConstituency(String constituency) {
        this.constituency = constituency;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getUserNum() {
        return userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
