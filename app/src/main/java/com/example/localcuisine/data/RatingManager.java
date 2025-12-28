package com.example.localcuisine.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.localcuisine.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RatingManager {

    private static final String PREF_NAME = "food_ratings_v2";
    private static final String KEY_REVIEWS_PREFIX = "reviews_";

    private static final Gson gson = new Gson();

    // ===================== INTERNAL =====================

    private static List<Review> loadReviews(Context ctx, int foodId) {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = pref.getString(KEY_REVIEWS_PREFIX + foodId, "[]");

        try {
            Type type = new TypeToken<List<Review>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static void saveReviews(Context ctx, int foodId, List<Review> reviews) {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        pref.edit()
                .putString(KEY_REVIEWS_PREFIX + foodId, gson.toJson(reviews))
                .apply();
    }

    // ===================== PUBLIC API =====================

    public static float getAverage(Context ctx, int foodId) {
        List<Review> list = loadReviews(ctx, foodId);
        if (list.isEmpty()) return 0f;

        float sum = 0f;
        for (Review r : list) {
            sum += r.getRating();
        }
        return sum / list.size();
    }

    public static int getCount(Context ctx, int foodId) {
        return loadReviews(ctx, foodId).size();
    }

    /**
     * Chỉ dùng khi chưa migrate hoàn toàn sang ReviewRepository
     * (không khuyến khích dùng song song lâu dài)
     */
    public static void addReview(Context ctx, Review review) {
        List<Review> list = loadReviews(ctx, review.getFoodId());
        list.add(0, review); // newest first
        saveReviews(ctx, review.getFoodId(), list);
    }
}
