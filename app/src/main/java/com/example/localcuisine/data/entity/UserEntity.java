// data/entity/UserEntity.java
package com.example.localcuisine.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String displayName;
    public long createdAt;

    public UserEntity(String username, String displayName, long createdAt) {
        this.username = username;
        this.displayName = displayName;
        this.createdAt = createdAt;
    }
}
