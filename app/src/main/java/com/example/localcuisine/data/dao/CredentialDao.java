// data/dao/CredentialDao.java
package com.example.localcuisine.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.localcuisine.data.entity.CredentialEntity;

@Dao
public interface CredentialDao {

    @Insert
    void insert(CredentialEntity credential);

    @Query("SELECT * FROM credentials WHERE userId = :userId")
    CredentialEntity findByUserId(int userId);
}
