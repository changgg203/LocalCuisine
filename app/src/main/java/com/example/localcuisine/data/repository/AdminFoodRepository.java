package com.example.localcuisine.data.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
 * AdminFoodRepository
 * <p>
 * - CRUD trực tiếp Firestore
 * - Cache cục bộ cho admin UI
 * - Không dùng fallback hardcode
 */
public class AdminFoodRepository {

    private static AdminFoodRepository INSTANCE;

    private final FirebaseFoodDataSource remote =
            new FirebaseFoodDataSource();

    /**
     * Cache admin (luôn phản ánh Firestore mới nhất)
     */
    private final List<Food> cache = new ArrayList<>();

    private boolean loaded = false;

    private AdminFoodRepository() {
    }

    public static synchronized AdminFoodRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AdminFoodRepository();
        }
        return INSTANCE;
    }

    // --------------------------------------------------
    // Load
    // --------------------------------------------------

    public void loadAll(@NonNull LoadCallback cb) {
        if (loaded) {
            cb.onSuccess(new ArrayList<>(cache));
            return;
        }

        remote.getAllFoods(new FirebaseFoodDataSource.Callback<List<FirestoreFoodDoc>>() {
            @Override
            public void onSuccess(List<FirestoreFoodDoc> docs) {
                cache.clear();

                if (docs != null) {
                    for (FirestoreFoodDoc d : docs) {
                        Food f = mapToFood(d);
                        if (f != null) cache.add(f);
                    }
                }

                loaded = true;
                cb.onSuccess(new ArrayList<>(cache));
            }

            @Override
            public void onError(@NonNull Exception e) {
                cb.onError(e);
            }
        });
    }

    // --------------------------------------------------
    // Query
    // --------------------------------------------------

    @Nullable
    public Food getById(int id) {
        for (Food f : cache) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    @NonNull
    public List<Food> getAllCached() {
        return new ArrayList<>(cache);
    }

    // --------------------------------------------------
    // Create / Update / Delete
    // --------------------------------------------------

    public void add(
            @NonNull Food food,
            @NonNull ActionCallback cb
    ) {
        FirestoreFoodDoc doc = mapToDoc(food);

        remote.addFood(doc, new FirebaseFoodDataSource.Callback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                cache.add(food);
                cb.onSuccess();
            }

            @Override
            public void onError(@NonNull Exception e) {
                cb.onError(e);
            }
        });
    }

    public void update(
            @NonNull Food food,
            @NonNull ActionCallback cb
    ) {
        FirestoreFoodDoc doc = mapToDoc(food);

        remote.updateFood(doc, new FirebaseFoodDataSource.Callback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                replaceCache(food);
                cb.onSuccess();
            }

            @Override
            public void onError(@NonNull Exception e) {
                cb.onError(e);
            }
        });
    }

    public void delete(
            int foodId,
            @NonNull ActionCallback cb
    ) {
        remote.deleteFood(foodId, new FirebaseFoodDataSource.Callback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                removeFromCache(foodId);
                cb.onSuccess();
            }

            @Override
            public void onError(@NonNull Exception e) {
                cb.onError(e);
            }
        });
    }

    // --------------------------------------------------
    // Cache helpers
    // --------------------------------------------------

    private void replaceCache(@NonNull Food food) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId() == food.getId()) {
                cache.set(i, food);
                return;
            }
        }
        cache.add(food);
    }

    private void removeFromCache(int id) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId() == id) {
                cache.remove(i);
                return;
            }
        }
    }

    // --------------------------------------------------
    // Mapping
    // --------------------------------------------------

    private Food mapToFood(@NonNull FirestoreFoodDoc d) {
        try {
            Region region = Region.valueOf(d.region);

            Set<FoodType> types = new HashSet<>();
            if (d.types != null) {
                for (String t : d.types) {
                    try {
                        types.add(FoodType.valueOf(t));
                    } catch (Exception ignored) {
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
        } catch (Exception e) {
            return null;
        }
    }

    private FirestoreFoodDoc mapToDoc(@NonNull Food f) {
        FirestoreFoodDoc d = new FirestoreFoodDoc();

        d.id = f.getId();
        d.name = f.getName();
        d.description = f.getDescription();
        d.region = f.getRegion().name();
        d.bestTime = f.getBestTime();
        d.location = f.getLocation();
        d.imageUrl = f.getImageUrl();

        d.tags = f.getTags();

        List<String> types = new ArrayList<>();
        for (FoodType t : f.getTypes()) {
            types.add(t.name());
        }
        d.types = types;

        return d;
    }

    // --------------------------------------------------
    // Callbacks
    // --------------------------------------------------

    public interface LoadCallback {
        void onSuccess(@NonNull List<Food> foods);

        void onError(@NonNull Exception e);
    }

    public interface ActionCallback {
        void onSuccess();

        void onError(@NonNull Exception e);
    }
}