package com.example.localcuisine.data.remote.review;

import android.content.Context;


public class ReviewRepository {

    private static ReviewRepository instance;
    private final ReviewDataSource dataSource;

    private ReviewRepository() {
        dataSource = new FirestoreReviewDataSource();
    }

    public static ReviewRepository getInstance(Context ctx) {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }

    public ReviewDataSource getDataSource() {
        return dataSource;
    }
}
