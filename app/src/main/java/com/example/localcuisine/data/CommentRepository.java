package com.example.localcuisine.data;

import android.content.Context;

import com.example.localcuisine.data.dao.CommentDao;
import com.example.localcuisine.data.dao.NotificationDao;
import com.example.localcuisine.data.entity.CommentEntity;
import com.example.localcuisine.data.entity.NotificationEntity;

public class CommentRepository {

    private final CommentDao commentDao;
    private final NotificationDao notificationDao;

    public CommentRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        commentDao = db.commentDao();
        notificationDao = db.notificationDao();
    }

    public void addComment(
            int foodId,
            int userId,
            Integer parentCommentId,
            int parentUserId,
            String content
    ) {
        long now = System.currentTimeMillis();

        // 1. Insert comment
        commentDao.insert(new CommentEntity(
                foodId,
                userId,
                parentCommentId,
                content,
                now
        ));

        // 2. Trigger notification nếu là reply & khác user
        if (parentCommentId != null && parentUserId != userId) {

            NotificationEntity notification = new NotificationEntity(
                    "COMMENT_REPLY",          // type
                    parentUserId,             // receiver
                    userId,                   // sender
                    foodId,                   // context
                    String.valueOf(parentCommentId), // review/comment id
                    "Có người đã trả lời bình luận của bạn"
            );

            notificationDao.insert(notification);
        }
    }
}
