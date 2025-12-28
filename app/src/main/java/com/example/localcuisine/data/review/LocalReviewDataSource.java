package com.example.localcuisine.data.review;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.localcuisine.model.Reply;
import com.example.localcuisine.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LocalReviewDataSource implements ReviewDataSource {

    private static final String PREF_NAME = "reviews_storage";
    private static final String KEY_REVIEWS = "reviews";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public LocalReviewDataSource(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private List<Review> getAll() {
        String json = prefs.getString(KEY_REVIEWS, null);
        if (json == null) return new ArrayList<>();

        Type type = new TypeToken<List<Review>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void saveAll(List<Review> list) {
        prefs.edit()
                .putString(KEY_REVIEWS, gson.toJson(list))
                .apply();
    }

    @Override
    public void addReply(int foodId, String reviewId, String reviewAuthorId, Reply reply, ActionCallback cb) {

    }

    @Override
    public void loadReviews(int foodId, LoadCallback callback) {
        try {
            List<Review> all = getAll();
            List<Review> result = new ArrayList<>();

            for (Review r : all) {
                if (r.getFoodId() == foodId) {
                    result.add(r);
                }
            }

            callback.onSuccess(result);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    @Override
    public void addReview(Review review, ActionCallback callback) {
        try {
            List<Review> all = getAll();
            review.setId(UUID.randomUUID().toString());
            all.add(0, review); // newest first
            saveAll(all);
            callback.onSuccess();
        } catch (Exception e) {
            callback.onError(e);
        }
    }

}
