package com.shamgar.peoplefeedbackadmin.models;

public class Posts {

    String user;
    String latitude;
    String longitude;
    String address;
    String imageUrl;
    String heading;
    String description;
    String postedOn;
    String tagId;
    String currentUserId;
    String state;
    String district;
    String constituancy;

    public Posts(String user, String latitude, String longitude, String address, String imageUrl, String heading, String description, String postedOn, String tagId, String currentUserId, String state, String district, String constituancy) {
        this.user = user;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.imageUrl = imageUrl;
        this.heading = heading;
        this.description = description;
        this.postedOn = postedOn;
        this.tagId = tagId;
        this.currentUserId = currentUserId;
        this.state = state;
        this.district = district;
        this.constituancy = constituancy;
    }

    public Posts() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getConstituancy() {
        return constituancy;
    }

    public void setConstituancy(String constituancy) {
        this.constituancy = constituancy;
    }
}
