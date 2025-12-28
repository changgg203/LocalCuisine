package com.example.localcuisine.data.favorite;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreFavoriteRepository implements FavoriteRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String favDoc(String uid, int foodId) {
        return "users/" + uid + "/favorites/" + foodId;
    }

    @Override
    public void isFavorite(
            @NonNull String uid,
            int foodId,
            @NonNull BooleanCallback cb
    ) {
        db.document(favDoc(uid, foodId))
                .get()
                .addOnSuccessListener(snap -> cb.onResult(snap.exists()))
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void addFavorite(
            @NonNull String uid,
            int foodId,
            @NonNull ActionCallback cb
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("foodId", foodId);
        data.put("createdAt", Timestamp.now());

        db.document(favDoc(uid, foodId))
                .set(data)
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void removeFavorite(
            @NonNull String uid,
            int foodId,
            @NonNull ActionCallback cb
    ) {
        db.document(favDoc(uid, foodId))
                .delete()
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void getAllFavoriteIds(
            @NonNull String uid,
            @NonNull ListCallback cb
    ) {
        db.collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Integer> ids = new ArrayList<>();
                    qs.getDocuments().forEach(doc -> {
                        try {
                            ids.add(Integer.parseInt(doc.getId()));
                        } catch (NumberFormatException ignored) {
                        }
                    });
                    cb.onResult(ids);
                })
                .addOnFailureListener(cb::onError);
    }
}
