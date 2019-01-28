package com.shamgar.peoplefeedbackadmin.models;

import java.util.ArrayList;

public class SpamModel {

    ArrayList<String> images,tagId,user;

    public SpamModel(ArrayList<String> images, ArrayList<String> tagId, ArrayList<String> user) {
        this.images = images;
        this.tagId = tagId;
        this.user = user;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getTagId() {
        return tagId;
    }

    public void setTagId(ArrayList<String> tagId) {
        this.tagId = tagId;
    }

    public ArrayList<String> getUser() {
        return user;
    }

    public void setUser(ArrayList<String> user) {
        this.user = user;
    }
}
