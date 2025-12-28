package com.example.localcuisine.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Review {

    private String id;
    private int foodId;

    private float rating;
    private String comment;
    private Timestamp createdAt;

    // ===== Author =====
    private String authorId;

    // ===== Replies =====
    @Exclude

    private List<Reply> replies = new ArrayList<>();

    // Firebase / Gson cần constructor rỗng
    public Review() {
    }

    // Constructor chuẩn
    public Review(int foodId, float rating, String comment, String authorId) {
        this.foodId = foodId;
        this.rating = rating;
        this.comment = comment;
        this.authorId = authorId;
        this.createdAt = Timestamp.now();
    }

    // ===== Replies =====
    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public void addReply(Reply reply) {
        if (reply != null) {
            replies.add(reply);
        }
    }

    // ===== Getter / Setter =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getFoodId() {
        return foodId;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getAuthorId() {
        return authorId;
    }
}
