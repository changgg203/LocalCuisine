package com.example.localcuisine.data.remote;

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

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(@NonNull Exception e);
    }
}
