package com.example.localcuisine.data.remote;

import com.google.firebase.Timestamp;

public class FirestoreFoodDoc {

    public int id;
    public String name;
    public String description;
    public String region;
    public java.util.List<String> types;
    public java.util.List<String> tags;
    public String bestTime;
    public String location;
    public String imageUrl;

    public Timestamp createdAt;

    public FirestoreFoodDoc() {
    }
}
