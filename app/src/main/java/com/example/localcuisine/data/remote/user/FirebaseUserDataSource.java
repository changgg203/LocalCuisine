package com.example.localcuisine.data.remote.user;

import androidx.annotation.NonNull;

import com.example.localcuisine.data.user.UserProfile;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUserDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getAllUsers(@NonNull Callback<List<UserProfile>> cb) {
        db.collection("users")
                .get()
                .addOnSuccessListener(snap -> {
                    List<UserProfile> out = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) {
                        UserProfile p = UserProfile.fromDocument(doc.getId(), doc);
                        out.add(p);
                    }
                    cb.onSuccess(out);
                })
                .addOnFailureListener(cb::onError);
    }

    public interface Callback<T> {
        void onSuccess(T data);

        void onError(@NonNull Exception e);
    }
}
