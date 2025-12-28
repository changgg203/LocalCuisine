package com.example.localcuisine.recommend;

import com.example.localcuisine.data.FavoriteManager;
import com.example.localcuisine.data.FoodRepository;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.Region;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecommendationEngine {

    public static List<RecommendResult> recommend(Context context,
                                                  RecommendContext rc) {

        List<Food> foods = FoodRepository.getAllFoods();
        List<RecommendResult> results = new ArrayList<>();

        for (Food food : foods) {

            int score = 0;
            List<String> reasons = new ArrayList<>();

            // 1️⃣ Thời điểm trong ngày
            if (food.getBestTime().equalsIgnoreCase(rc.timeOfDay)) {
                score += 2;
                reasons.add("Phù hợp bữa " + rc.timeOfDay.toLowerCase());
            }

            // 2️⃣ Sở thích (tag match)
            for (String pref : rc.preferences) {
                if (food.getTags().contains(pref)) {
                    score += 1;
                    reasons.add("Phù hợp sở thích: " + pref);
                }
            }

            // 3️⃣ Vùng miền ưu tiên
            if (rc.preferredRegion != null &&
                    food.getRegion() == rc.preferredRegion) {
                score += 2;
                reasons.add("Thuộc vùng miền bạn quan tâm");
            }

            // 4️⃣ Món đã từng yêu thích → tăng điểm
            if (FavoriteManager.isFavorite(context, food.getId())) {
                score += 1;
                reasons.add("Bạn đã từng yêu thích món này");
            }

            // ❗ Loại bỏ món không liên quan
            if (score > 0) {
                results.add(new RecommendResult(food, reasons, score));
            }
        }

        // 5️⃣ Sort theo score giảm dần
        results.sort((a, b) -> b.score - a.score);

        return results;
    }
}
