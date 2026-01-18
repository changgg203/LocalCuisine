package com.example.localcuisine.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.localcuisine.data.auth.SessionStore;
import com.example.localcuisine.data.auth.UserRole;
import com.example.localcuisine.data.user.UserProfile;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * UserRepository
 * <p>
 * - Load / save UserProfile từ Firestore
 * - Bootstrap admin role vào SessionStore
 * <p>
 * COMPATIBLE với code cũ:
 * - Vẫn dùng được new UserRepository()
 */
public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Context appContext;

    // ===== Constructor COMPAT =====
    public UserRepository() {
        FirebaseApp app = FirebaseApp.getInstance();
        this.appContext = app.getApplicationContext();
    }

    // ===================== UID =====================

    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private String getUidOrThrow() {
        FirebaseUser user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("User not logged in");
        }
        return user.getUid();
    }

    // ===================== LOAD PROFILE =====================

    public void loadMyProfile(@NonNull LoadProfileCallback callback) {
        String uid;
        try {
            uid = getUidOrThrow();
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        // Không có profile → mặc định USER
                        syncRole(false);
                        callback.onNotFound();
                        return;
                    }

                    UserProfile profile = UserProfile.fromDocument(uid, doc);

                    // ===== BOOTSTRAP ROLE =====
                    syncRole(profile.isAdmin);

                    callback.onSuccess(profile);
                })
                .addOnFailureListener(e -> {
                    syncRole(false);
                    callback.onError(e);
                });
    }

    // ===================== SAVE PROFILE =====================

    public void saveMyProfile(
            String displayName,
            String phone,
            String bio,
            String language,
            @NonNull SaveProfileCallback callback
    ) {
        FirebaseUser user;
        try {
            user = getCurrentUser();
            if (user == null) throw new IllegalStateException();
        } catch (Exception e) {
            callback.onError(e);
            return;
        }

        String uid = user.getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("displayName", displayName);
        data.put("phone", phone);
        data.put("bio", bio);
        data.put("language", language);
        data.put("email", user.getEmail());
        data.put("updatedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(callback::onError);
    }

    // ===================== ROLE SYNC =====================

    private void syncRole(boolean isAdmin) {
        SessionStore session = new SessionStore(appContext);
        session.setUserRole(isAdmin ? UserRole.ADMIN : UserRole.USER);
    }

    // ===================== CALLBACK =====================

    public interface LoadProfileCallback {
        void onSuccess(UserProfile profile);

        void onNotFound();

        void onError(Exception e);
    }

    public interface SaveProfileCallback {
        void onSuccess();

        void onError(Exception e);
    }
}
