package com.example.localcuisine.ui.comment;

import com.example.localcuisine.data.entity.CommentEntity;

public class CommentItem {
    public CommentEntity comment;
    public int level; // độ sâu (0 = root)

    public CommentItem(CommentEntity comment, int level) {
        this.comment = comment;
        this.level = level;
    }
}
