package com.example.localcuisine.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.localcuisine.data.user.UserProfile;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdminUserRepository
 *
 * - CRUD cho collection `users` trên Firestore
 * - Cache cục bộ cho admin UI
 */
public class AdminUserRepository {

    private static AdminUserRepository INSTANCE;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final com.example.localcuisine.data.remote.user.FirebaseUserDataSource remote = new com.example.localcuisine.data.remote.user.FirebaseUserDataSource();

    private final List<UserProfile> cache = new ArrayList<>();
    private boolean loaded = false;

    private AdminUserRepository() {}

    public static synchronized AdminUserRepository getInstance() {
        if (INSTANCE == null) INSTANCE = new AdminUserRepository();
        return INSTANCE;
    }

    // --------------------------------------------------
    // Load
    // --------------------------------------------------

    public void loadAll(@NonNull LoadCallback cb) {
        loadAll(false, cb);
    }

    /**
     * Load tất cả users. forceRefresh = true để bỏ qua cache và fetch lại từ Firestore.
     */
    public void loadAll(boolean forceRefresh, @NonNull LoadCallback cb) {
        if (loaded && !forceRefresh) {
            cb.onSuccess(new ArrayList<>(cache));
            return;
        }
        if (forceRefresh) {
            loaded = false;
        }

        remote.getAllUsers(new com.example.localcuisine.data.remote.user.FirebaseUserDataSource.Callback<List<UserProfile>>() {
            @Override
            public void onSuccess(List<UserProfile> users) {
                cache.clear();
                if (users != null) cache.addAll(users);
                loaded = true;
                cb.onSuccess(new ArrayList<>(cache));
            }

            @Override
            public void onError(@NonNull Exception e) {
                cb.onError(e);
            }
        });
    }

    // --------------------------------------------------
    // Query
    // --------------------------------------------------

    @Nullable
    public UserProfile getByUid(@NonNull String uid) {
        for (UserProfile p : cache) {
            if (p.uid != null && p.uid.equals(uid)) return p;
        }
        return null;
    }

    @NonNull
    public List<UserProfile> getAllCached() {
        return new ArrayList<>(cache);
    }

    // --------------------------------------------------
    // Create / Update / Delete
    // --------------------------------------------------

    public void add(@NonNull UserProfile profile, @NonNull ActionCallback cb) {
        // ensure uid
        String uid = profile.uid != null ? profile.uid : String.valueOf(System.currentTimeMillis());
        profile.uid = uid;

        Map<String, Object> data = mapToData(profile);

        db.collection("users")
                .document(uid)
                .set(data)
                .addOnSuccessListener(v -> {
                    cache.add(profile);
                    cb.onSuccess();
                })
                .addOnFailureListener(cb::onError);
    }

    public void update(@NonNull UserProfile profile, @NonNull ActionCallback cb) {
        if (profile.uid == null) {
            cb.onError(new IllegalArgumentException("User uid is required"));
            return;
        }

        Map<String, Object> data = mapToData(profile);

        db.collection("users")
                .document(profile.uid)
                .set(data)
                .addOnSuccessListener(v -> {
                    replaceCache(profile);
                    cb.onSuccess();
                })
                .addOnFailureListener(cb::onError);
    }

    public void delete(@NonNull String uid, @NonNull ActionCallback cb) {
        db.collection("users")
                .document(uid)
                .delete()
                .addOnSuccessListener(v -> {
                    removeFromCache(uid);
                    cb.onSuccess();
                })
                .addOnFailureListener(cb::onError);
    }

    // --------------------------------------------------
    // Cache helpers
    // --------------------------------------------------

    private void replaceCache(@NonNull UserProfile profile) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).uid != null && cache.get(i).uid.equals(profile.uid)) {
                cache.set(i, profile);
                return;
            }
        }
        cache.add(profile);
    }

    private void removeFromCache(@NonNull String uid) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).uid != null && cache.get(i).uid.equals(uid)) {
                cache.remove(i);
                return;
            }
        }
    }

    private Map<String, Object> mapToData(@NonNull UserProfile p) {
        Map<String, Object> m = new HashMap<>();
        m.put("displayName", p.displayName);
        m.put("email", p.email);
        m.put("phone", p.phone);
        m.put("bio", p.bio);
        m.put("language", p.language);
        m.put("region", p.region != null ? p.region.name() : null);
        m.put("isAdmin", p.isAdmin);
        m.put("updatedAt", FieldValue.serverTimestamp());
        return m;
    }

    // --------------------------------------------------
    // Callbacks
    // --------------------------------------------------

    public interface LoadCallback {
        void onSuccess(@NonNull List<UserProfile> users);
        void onError(@NonNull Exception e);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(@NonNull Exception e);
    }
}
