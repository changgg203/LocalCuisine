package com.example.localcuisine.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "notifications",
        indices = {
                @Index("receiverUserId"),
                @Index("isRead"),
                @Index("type")
        }
)
public class NotificationEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    // ===== Routing =====
    public String type;          // "REPLY", "REVIEW", "SYSTEM"
    public int receiverUserId;
    public int senderUserId;

    // ===== Context =====
    public int foodId;           // để mở FoodDetail
    public String reviewId;      // nullable

    // ===== Content =====
    public String message;
    public boolean isRead;
    public long createdAt;

    public NotificationEntity(
            String type,
            int receiverUserId,
            int senderUserId,
            int foodId,
            String reviewId,
            String message
    ) {
        this.type = type;
        this.receiverUserId = receiverUserId;
        this.senderUserId = senderUserId;
        this.foodId = foodId;
        this.reviewId = reviewId;
        this.message = message;
        this.isRead = false;
        this.createdAt = System.currentTimeMillis();
    }
}
