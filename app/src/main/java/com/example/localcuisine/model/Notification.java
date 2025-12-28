package com.example.localcuisine.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Notification {

    private String id;
    private String title;
    private String content;
    private boolean read;
    private Timestamp createdAt;

    public Notification() {
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
