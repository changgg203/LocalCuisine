// data/entity/CommentEntity.java
package com.example.localcuisine.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "comments",
        indices = {
                @Index("foodId"),
                @Index("parentCommentId"),
                @Index("userId")
        }
)
public class CommentEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int foodId;              // Món ăn nào
    public int userId;              // Ai viết
    public Integer parentCommentId; // null nếu là comment gốc
    public String content;
    public long createdAt;

    public CommentEntity(
            int foodId,
            int userId,
            Integer parentCommentId,
            String content,
            long createdAt
    ) {
        this.foodId = foodId;
        this.userId = userId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.createdAt = createdAt;
    }
}
