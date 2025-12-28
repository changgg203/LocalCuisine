// data/NotificationRepository.java
package com.example.localcuisine.data;

import androidx.annotation.NonNull;

import com.example.localcuisine.data.remote.FirestoreNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadNotifications(@NonNull LoadCallback cb) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            cb.onError(new Exception("NOT_LOGGED_IN"));
            return;
        }

        db.collection("users")
                .document(uid)
                .collection("notifications")
                .orderBy("createdAt")
                .get()
                .addOnSuccessListener(snap -> {
                    List<FirestoreNotification> list = new ArrayList<>();
                    for (var doc : snap.getDocuments()) {
                        FirestoreNotification n = doc.toObject(FirestoreNotification.class);
                        if (n != null) {
                            n.setId(doc.getId());
                            list.add(n);
                        }
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(cb::onError);
    }


    public void markAsRead(String notiId) {
        if (notiId == null) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("notifications")
                .document(notiId)
                .update("isRead", true);
    }


    public interface LoadCallback {
        void onSuccess(List<FirestoreNotification> list);

        void onError(Exception e);
    }
}
