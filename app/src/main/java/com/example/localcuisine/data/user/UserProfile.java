// data/user/UserProfile.java
package com.example.localcuisine.data.user;

import com.example.localcuisine.model.Region;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashSet;
import java.util.Set;

/**
 * UserProfile – đại diện cho 1 người dùng trong hệ thống.
 * <p>
 * Gồm 2 phần:
 * 1. Thông tin tài khoản (Firebase / Profile)
 * 2. Thông tin preference dùng cho Recommendation
 */
public class UserProfile {

    /* =========================
       Account / Identity
       ========================= */

    public String uid;
    public String displayName;
    public String email;
    public String phone;
    public String bio;
    public String language;

    /* =========================
       Recommendation preference
       ========================= */

    public Region region = Region.ALL;

    // gu ăn uống
    public Set<String> preferredTags = new HashSet<>();
    public Set<String> preferredTypes = new HashSet<>(); // FoodType.name()

    // trạng thái
    public boolean loggedIn;

    public UserProfile() {
        // empty constructor
    }

    /* =========================
       Factory from Firestore
       ========================= */

    public static UserProfile fromDocument(String uid, DocumentSnapshot doc) {
        UserProfile p = new UserProfile();
        p.uid = uid;
        p.displayName = doc.getString("displayName");
        p.email = doc.getString("email");
        p.phone = doc.getString("phone");
        p.bio = doc.getString("bio");
        p.language = doc.getString("language");

        // ---- optional fields (nếu có trong Firestore) ----
        String regionStr = doc.getString("region");
        if (regionStr != null) {
            try {
                p.region = Region.valueOf(regionStr);
            } catch (Exception ignored) {
            }
        }

        return p;
    }
}
