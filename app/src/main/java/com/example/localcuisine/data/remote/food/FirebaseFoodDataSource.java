package com.example.localcuisine.data.remote.food;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseFoodDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAllFoods(@NonNull Callback<List<FirestoreFoodDoc>> cb) {
        db.collection("foods")
                .get()
                .addOnSuccessListener(snap -> {
                    List<FirestoreFoodDoc> out = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        FirestoreFoodDoc d = doc.toObject(FirestoreFoodDoc.class);
                        // nếu không lưu field "id" thì có thể parse doc.getId() ở đây
                        out.add(d);
                    }
                    cb.onSuccess(out);
                })
                .addOnFailureListener(cb::onError);
    }

    public void getFoodById(int id, @NonNull Callback<FirestoreFoodDoc> cb) {
        db.collection("foods")
                .document(String.valueOf(id))
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        cb.onSuccess(null);
                        return;
                    }
                    cb.onSuccess(doc.toObject(FirestoreFoodDoc.class));
                })
                .addOnFailureListener(cb::onError);
    }

    // --------------------------------------------------
    // Create
    // --------------------------------------------------

    public void addFood(
            @NonNull FirestoreFoodDoc doc,
            @NonNull Callback<Void> cb
    ) {
        db.collection("foods")
                .document(String.valueOf(doc.id))
                .set(doc)
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    // --------------------------------------------------
    // Update
    // --------------------------------------------------

    public void updateFood(
            @NonNull FirestoreFoodDoc doc,
            @NonNull Callback<Void> cb
    ) {
        db.collection("foods")
                .document(String.valueOf(doc.id))
                .set(doc)
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    // --------------------------------------------------
    // Delete
    // --------------------------------------------------

    public void deleteFood(
            int foodId,
            @NonNull Callback<Void> cb
    ) {
        db.collection("foods")
                .document(String.valueOf(foodId))
                .delete()
                .addOnSuccessListener(unused -> cb.onSuccess(null))
                .addOnFailureListener(cb::onError);
    }

    // --------------------------------------------------
    // Callback
    // --------------------------------------------------

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(@NonNull Exception e);
    }
}
