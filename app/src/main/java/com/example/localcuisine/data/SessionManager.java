// data/SessionManager.java
package com.example.localcuisine.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_UID = "firebase_uid";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ===== SAVE =====
    public void saveUser(@Nullable String uid) {
        if (uid == null) {
            logout();
        } else {
            prefs.edit().putString(KEY_UID, uid).apply();
        }
    }

    // ===== GET =====
    @Nullable
    public String getUserId() {
        return prefs.getString(KEY_UID, null);
    }

    // ===== STATE =====
    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    // ===== CLEAR =====
    public void logout() {
        prefs.edit().clear().apply();
    }
}
