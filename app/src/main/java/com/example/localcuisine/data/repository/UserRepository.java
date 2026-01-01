package com.example.localcuisine.data.repository;

import androidx.annotation.NonNull;

import com.example.localcuisine.data.user.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    if (doc.exists()) {
                        callback.onSuccess(UserProfile.fromDocument(uid, doc));
                    } else {
                        callback.onNotFound();
                    }
                })
                .addOnFailureListener(callback::onError);
    }

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

    // ===================== SAVE / UPDATE PROFILE =====================

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
