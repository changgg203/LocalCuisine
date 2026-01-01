package com.example.localcuisine.data.recommend.signal;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * PreferenceTracker
 * <p>
 * Theo dõi sở thích ngầm (implicit preference) của người dùng
 * dựa trên hành vi tương tác với món ăn.
 *
 * <p>
 * Đặc điểm:
 * <ul>
 *     <li>Dựa trên FoodType (không theo từng món cụ thể)</li>
 *     <li>Nhẹ, lưu cục bộ bằng SharedPreferences</li>
 *     <li>Không cần login, không cần backend</li>
 * </ul>
 *
 * <p>
 * Đây là dạng tracking hành vi đơn giản, phục vụ cho recommendation,
 * không phải hệ analytics hay logging chi tiết.
 */
public class PreferenceTracker {

    /**
     * Tên SharedPreferences dùng để lưu preference
     */
    private static final String PREF_NAME = "rs_pref";

    /**
     * Prefix cho key theo FoodType
     */
    private static final String KEY_PREFIX = "type_";

    /**
     * Lấy SharedPreferences instance
     */
    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Ghi nhận hành vi click vào món ăn
     *
     * @param ctx  Context
     * @param food Món ăn được click
     */
    public static void onFoodClick(Context ctx, Food food) {
        updateTypes(ctx, food, 1.0);
    }

    /**
     * Ghi nhận hành vi thêm món ăn vào yêu thích
     *
     * @param ctx  Context
     * @param food Món ăn được favorite
     */
    public static void onFavorite(Context ctx, Food food) {
        updateTypes(ctx, food, 2.0);
    }

    /**
     * Ghi nhận hành vi đánh giá / review món ăn
     *
     * @param ctx  Context
     * @param food Món ăn được review
     */
    public static void onReview(Context ctx, Food food) {
        updateTypes(ctx, food, 3.0);
    }

    /**
     * Cập nhật điểm preference cho các FoodType của món ăn
     *
     * @param ctx   Context
     * @param food  Món ăn
     * @param delta Giá trị tăng thêm cho mỗi FoodType
     */
    private static void updateTypes(Context ctx, Food food, double delta) {
        if (food == null || food.getTypes() == null) return;

        SharedPreferences.Editor ed = prefs(ctx).edit();

        for (FoodType t : food.getTypes()) {
            String key = KEY_PREFIX + t.name();
            double cur = Double.longBitsToDouble(
                    prefs(ctx).getLong(key, Double.doubleToRawLongBits(0.0))
            );
            double next = cur + delta;
            ed.putLong(key, Double.doubleToRawLongBits(next));
        }

        ed.apply();
    }

    /**
     * Lấy danh sách FoodType người dùng có xu hướng ưa thích
     *
     * <p>
     * Nguyên tắc:
     * <ul>
     *     <li>Chọn top-K FoodType theo score</li>
     *     <li>Chỉ lấy những type vượt ngưỡng tối thiểu</li>
     * </ul>
     *
     * @param ctx Context
     * @return Tập các FoodType name (String)
     */
    public static Set<String> getPreferredTypes(Context ctx) {
        final int TOP_K = 3;
        final double MIN_SCORE = 2.0;

        Map<FoodType, Double> scores = new HashMap<>();

        for (FoodType t : FoodType.values()) {
            String key = KEY_PREFIX + t.name();
            double score = Double.longBitsToDouble(
                    prefs(ctx).getLong(key, Double.doubleToRawLongBits(0.0))
            );
            if (score > 0) {
                scores.put(t, score);
            }
        }

        List<Map.Entry<FoodType, Double>> sorted =
                new ArrayList<>(scores.entrySet());

        sorted.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        Set<String> result = new HashSet<>();
        for (int i = 0; i < sorted.size() && result.size() < TOP_K; i++) {
            Map.Entry<FoodType, Double> e = sorted.get(i);
            if (e.getValue() >= MIN_SCORE) {
                result.add(e.getKey().name());
            }
        }

        return result;
    }

    /**
     * Dump toàn bộ điểm preference (phục vụ debug)
     *
     * @param ctx Context
     * @return Map FoodType → score
     */
    public static Map<FoodType, Double> dumpScores(Context ctx) {
        Map<FoodType, Double> m = new HashMap<>();
        for (FoodType t : FoodType.values()) {
            String key = KEY_PREFIX + t.name();
            double score = Double.longBitsToDouble(
                    prefs(ctx).getLong(key, Double.doubleToRawLongBits(0.0))
            );
            if (score > 0) m.put(t, score);
        }
        return m;
    }
}
