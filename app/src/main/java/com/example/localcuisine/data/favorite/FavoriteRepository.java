package com.example.localcuisine.data.favorite;

public interface FavoriteRepository {

    void isFavorite(String uid, int foodId, BooleanCallback cb);

    void addFavorite(String uid, int foodId, ActionCallback cb);

    void removeFavorite(String uid, int foodId, ActionCallback cb);

    void getAllFavoriteIds(String uid, ListCallback cb);

    interface BooleanCallback {
        void onResult(boolean value);

        void onError(Exception e);
    }

    interface ActionCallback {
        void onSuccess();

        void onError(Exception e);
    }

    interface ListCallback {
        void onResult(java.util.List<Integer> foodIds);

        void onError(Exception e);
    }
}
