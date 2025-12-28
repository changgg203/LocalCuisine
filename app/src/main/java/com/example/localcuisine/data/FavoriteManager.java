package com.example.localcuisine.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class FavoriteManager {

    private static final String PREF_NAME = "favorite_prefs";
    private static final String KEY_FAVORITES = "favorite_ids";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void addFavorite(Context context, int foodId) {
        SharedPreferences prefs = getPrefs(context);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
        favorites.add(String.valueOf(foodId));
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public static void removeFavorite(Context context, int foodId) {
        SharedPreferences prefs = getPrefs(context);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
        favorites.remove(String.valueOf(foodId));
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply();
    }

    public static boolean isFavorite(Context context, int foodId) {
        SharedPreferences prefs = getPrefs(context);
        Set<String> favorites = prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
        return favorites.contains(String.valueOf(foodId));
    }

    public static Set<String> getAllFavoriteIds(Context context) {
        SharedPreferences prefs = getPrefs(context);
        return prefs.getStringSet(KEY_FAVORITES, new HashSet<>());
    }
}
