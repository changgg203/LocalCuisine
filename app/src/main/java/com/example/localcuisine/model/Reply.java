package com.example.localcuisine.model;

import com.google.firebase.Timestamp;

public class Reply {

    private String id;
    private String reviewId;

    private String content;
    private Timestamp createdAt;

    // ===== Author =====
    private String authorId;

    // Firebase / Gson cần constructor rỗng
    public Reply() {
    }

    public Reply(String reviewId, String content, String authorId) {
        this.reviewId = reviewId;
        this.content = content;
        this.authorId = authorId;
        this.createdAt = Timestamp.now();
    }

    // ===== Getter / Setter =====

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getAuthorId() {
        return authorId;
    }
}
