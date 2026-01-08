package com.example.localcuisine.data.auth;

import android.content.Context;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class UserSessionSync {

    public static void syncFavorites(Context ctx, String uid) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {
                    SessionStore sm = new SessionStore(ctx);

                    Set<String> ids = new HashSet<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ids.add(doc.getId()); // foodId as string
                    }

                    sm.replaceFavoriteFoodIds(ids);
                });
    }
}
