// data/dao/NotificationDao.java
package com.example.localcuisine.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.localcuisine.data.entity.NotificationEntity;

import java.util.List;

@Dao
public interface NotificationDao {

    @Insert
    void insert(NotificationEntity notification);

    @Query("SELECT * FROM notifications WHERE receiverUserId = :userId ORDER BY createdAt DESC")
    List<NotificationEntity> getByUser(int userId);

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    void markAsRead(int id);

    @Query("SELECT COUNT(*) FROM notifications WHERE receiverUserId = :userId AND isRead = 0")
    int getUnreadCount(String userId);
}
