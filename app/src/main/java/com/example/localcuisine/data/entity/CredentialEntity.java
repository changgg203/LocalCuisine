// data/entity/CredentialEntity.java
package com.example.localcuisine.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "credentials")
public class CredentialEntity {

    @PrimaryKey
    public int userId;

    public String passwordHash;
    public String salt;

    public CredentialEntity(int userId, String passwordHash, String salt) {
        this.userId = userId;
        this.passwordHash = passwordHash;
        this.salt = salt;
    }
}
