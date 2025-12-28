package com.example.localcuisine.data.review;

import com.example.localcuisine.model.Reply;
import com.example.localcuisine.model.Review;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirestoreReviewDataSource implements ReviewDataSource {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadReviews(
            int foodId,
            LoadCallback cb
    ) {
        db.collection("foods")
                .document(String.valueOf(foodId))
                .collection("reviews")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {

                    List<Review> reviews = new ArrayList<>();
                    if (snapshots.isEmpty()) {
                        cb.onSuccess(reviews);
                        return;
                    }

                    AtomicInteger pending = new AtomicInteger(snapshots.size());

                    for (QueryDocumentSnapshot doc : snapshots) {
                        Review review = doc.toObject(Review.class);
                        review.setId(doc.getId());

                        loadReplies(foodId, review, () -> {
                            reviews.add(review);
                            if (pending.decrementAndGet() == 0) {
                                cb.onSuccess(reviews);
                            }
                        });
                    }
                })
                .addOnFailureListener(cb::onError);
    }

    private void loadReplies(
            int foodId,
            Review review,
            Runnable done
    ) {
        db.collection("foods")
                .document(String.valueOf(foodId))
                .collection("reviews")
                .document(review.getId())
                .collection("replies")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    List<Reply> replies = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Reply r = doc.toObject(Reply.class);
                        r.setId(doc.getId());
                        replies.add(r);
                    }
                    review.setReplies(replies);
                    done.run();
                })
                .addOnFailureListener(e -> {
                    review.setReplies(new ArrayList<>());
                    done.run();
                });
    }


    @Override
    public void addReview(Review review, ActionCallback cb) {
        Map<String, Object> data = new HashMap<>();
        data.put("authorId", review.getAuthorId());
        data.put("rating", review.getRating());
        data.put("comment", review.getComment());
        data.put("createdAt", FieldValue.serverTimestamp());

        db.collection("foods")
                .document(String.valueOf(review.getFoodId()))
                .collection("reviews")
                .add(data)
                .addOnSuccessListener(doc -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }

    @Override
    public void addReply(
            int foodId,
            String reviewId,
            String reviewAuthorId,
            Reply reply,
            ActionCallback cb
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("authorId", reply.getAuthorId());
        data.put("content", reply.getContent());
        data.put("createdAt", FieldValue.serverTimestamp());

        db.collection("foods")
                .document(String.valueOf(foodId))
                .collection("reviews")
                .document(reviewId)
                .collection("replies")
                .add(data)
                .addOnSuccessListener(doc -> {
                    // 🔔 sinh notification cho author của review
                    if (!reply.getAuthorId().equals(reviewAuthorId)) {
                        createNotificationForReviewOwner(reviewAuthorId);
                    }
                    cb.onSuccess();
                })
                .addOnFailureListener(cb::onError);
    }


    // ================= NOTIFICATION =================

    private void createNotificationForReviewOwner(String targetUserId) {
        Map<String, Object> noti = new HashMap<>();
        noti.put("title", "Phản hồi mới");
        noti.put("message", "Có người phản hồi đánh giá của bạn");
        noti.put("createdAt", FieldValue.serverTimestamp());
        noti.put("read", false);

        db.collection("users")
                .document(targetUserId)
                .collection("notifications")
                .add(noti);
    }

}
