package com.example.localcuisine.data.remote.review;

import com.example.localcuisine.model.Reply;
import com.example.localcuisine.model.Review;

import java.util.List;

public interface ReviewDataSource {

    void loadReviews(int foodId, LoadCallback callback);

    void addReview(Review review, ActionCallback callback);


    void addReply(
            int foodId,
            String reviewId,
            String reviewAuthorId,
            Reply reply,
            ActionCallback cb
    );


    interface LoadCallback {
        void onSuccess(List<Review> reviews);

        void onError(Throwable t);
    }

    interface ActionCallback {
        void onSuccess();

        void onError(Throwable t);
    }
}
