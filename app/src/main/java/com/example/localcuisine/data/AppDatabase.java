// data/AppDatabase.java
package com.example.localcuisine.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.localcuisine.data.dao.CommentDao;
import com.example.localcuisine.data.dao.CredentialDao;
import com.example.localcuisine.data.dao.NotificationDao;
import com.example.localcuisine.data.dao.UserDao;
import com.example.localcuisine.data.entity.CommentEntity;
import com.example.localcuisine.data.entity.CredentialEntity;
import com.example.localcuisine.data.entity.NotificationEntity;
import com.example.localcuisine.data.entity.UserEntity;

@Database(
        entities = {UserEntity.class, CredentialEntity.class, CommentEntity.class, NotificationEntity.class},
        version = 3
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "local_cuisine.db"
            ).fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract CredentialDao credentialDao();

    public abstract CommentDao commentDao();

    public abstract NotificationDao notificationDao();
}
