package com.example.localcuisine.data.repository;

import androidx.annotation.NonNull;

import com.example.localcuisine.data.remote.food.FirebaseFoodDataSource;
import com.example.localcuisine.data.remote.food.FirestoreFoodDoc;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.model.Region;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * FoodRepository
 * <p>
 * - Local fallback: FOOD_LIST (hardcode cũ)
 * - Remote source  : Firebase Firestore
 * - Strategy       : Prefetch + cache
 * <p>
 * UI chỉ nên gọi:
 * - ensureLoaded(...)
 * - getAllFoods()
 * - getFoodById(id)
 */
public class FoodRepository {

    /**
     * Local fallback (hardcoded data hiện có).
     */
    private static final List<Food> FOOD_LIST = new ArrayList<>();

    /**
     * Cache sau khi load từ Firebase
     */
    private static final List<Food> CACHE = new ArrayList<>();

    /**
     * Firebase data source
     */
    private static final FirebaseFoodDataSource remote =
            new FirebaseFoodDataSource();

    /**
     * Trạng thái load
     */
    private static boolean loaded = false;
    private static boolean loading = false;

    private FoodRepository() {
        // no instance
    }

    /**
     * Ensure data is loaded from Firebase (async).
     * Nếu đã load rồi → callback ngay bằng cache.
     * Nếu đang load → trả fallback tạm thời.
     */
    public static void ensureLoaded(@NonNull LoadCallback cb) {
        if (loaded) {
            cb.onSuccess(getAllFoods());
            return;
        }

        if (loading) {
            // Đơn giản: trả fallback ngay
            cb.onSuccess(getAllFoods());
            return;
        }

        loading = true;

        remote.getAllFoods(new FirebaseFoodDataSource.Callback<List<FirestoreFoodDoc>>() {
            @Override
            public void onSuccess(List<FirestoreFoodDoc> docs) {
                CACHE.clear();

                if (docs != null) {
                    for (FirestoreFoodDoc d : docs) {
                        Food food = mapToFood(d);
                        if (food != null) {
                            CACHE.add(food);
                        }
                    }
                }

                loaded = true;
                loading = false;

                cb.onSuccess(getAllFoods());
            }

            @Override
            public void onError(@NonNull Exception e) {
                loading = false;
                // Firebase fail → vẫn cho UI chạy bằng local fallback
                cb.onError(e);
            }
        });
    }

    /**
     * Get all foods (cache nếu có, không thì fallback local)
     */
    @NonNull
    public static List<Food> getAllFoods() {
        if (loaded && !CACHE.isEmpty()) {
            return new ArrayList<>(CACHE);
        }
        return new ArrayList<>(FOOD_LIST);
    }

    /**
     * Get food by id (cache ưu tiên)
     */
    public static Food getFoodById(int id) {
        List<Food> source = (loaded && !CACHE.isEmpty())
                ? CACHE
                : FOOD_LIST;

        for (Food food : source) {
            if (food.getId() == id) {
                return food;
            }
        }
        return null;
    }

    /**
     * Map Firestore document → domain model Food
     * Fail-safe: lỗi enum / null field → return null
     */
    private static Food mapToFood(@NonNull FirestoreFoodDoc d) {
        try {
            Region region = Region.valueOf(d.region);

            Set<FoodType> types = new HashSet<>();
            if (d.types != null) {
                for (String t : d.types) {
                    try {
                        types.add(FoodType.valueOf(t));
                    } catch (Exception ignored) {
                        // skip invalid enum
                    }
                }
            }

            List<String> tags = (d.tags != null)
                    ? d.tags
                    : new ArrayList<>();

            return new Food(
                    d.id,
                    d.name,
                    d.description,
                    region,
                    types,
                    tags,
                    d.bestTime,
                    d.location,
                    d.imageUrl
            );
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Callback cho ensureLoaded
     */
    public interface LoadCallback {
        void onSuccess(@NonNull List<Food> foods);

        void onError(@NonNull Exception e);
    }
}
