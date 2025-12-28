// data/dao/CommentDao.java
package com.example.localcuisine.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.localcuisine.data.entity.CommentEntity;

import java.util.List;

@Dao
public interface CommentDao {

    @Insert
    void insert(CommentEntity comment);

    @Query("SELECT * FROM comments WHERE foodId = :foodId ORDER BY createdAt ASC")
    List<CommentEntity> getCommentsByFood(int foodId);
}
