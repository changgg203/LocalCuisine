// data/remote/FirestoreNotification.java
package com.example.localcuisine.data.remote.notification;

public class FirestoreNotification {

    private String id;          // documentId
    private String type;        // "REPLY", "REVIEW", "FAVORITE"
    private int foodId;
    private String title;
    private String content;
    private boolean isRead;
    private com.google.firebase.Timestamp createdAt;

    public FirestoreNotification() {
    }

    // ===== getters =====
    public String getId() {
        return id;
    }

    // ===== setters (Firestore cần) =====
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public com.google.firebase.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(com.google.firebase.Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
