package com.example.localcuisine.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.localcuisine.model.Region;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * SessionStore
 * <p>
 * Lớp quản lý trạng thái phiên người dùng và các preference nhẹ
 * được lưu cục bộ trên thiết bị.
 * <p>
 * Phục vụ cho:
 * - Kiểm tra đăng nhập cục bộ
 * - Lưu ngữ cảnh người dùng (vùng miền)
 * - Lưu hành vi nhẹ (favorite, preference cache)
 * <p>
 * Không chứa logic xác thực hoặc xử lý server-side.
 */
public class SessionStore {

    /**
     * Tên file SharedPreferences
     */
    private static final String PREF_NAME = "user_session";

    private static final String KEY_UID = "firebase_uid";
    private static final String KEY_USER_REGION = "user_region";

    private static final String KEY_FAVORITE_FOOD_IDS = "favorite_food_ids";

    // Cached preference (lightweight history)
    private static final String KEY_CACHED_TYPES = "cached_preferred_types";
    private static final String KEY_CACHED_TAGS = "cached_preferred_tags";

    private final SharedPreferences prefs;

    public SessionStore(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public void saveUser(@Nullable String uid) {
        if (uid == null) {
            logout();
        } else {
            prefs.edit().putString(KEY_UID, uid).apply();
        }
    }

    @Nullable
    public String getUserId() {
        return prefs.getString(KEY_UID, null);
    }

    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    /**
     * Lấy vùng miền hiện tại của người dùng
     *
     * @return Region đã lưu, mặc định là Region.ALL nếu chưa có hoặc lỗi
     */
    public Region getUserRegion() {
        String raw = prefs.getString(KEY_USER_REGION, null);
        if (raw == null) return Region.CENTRAL; // mock

        try {
            return Region.valueOf(raw);
        } catch (Exception e) {
            return Region.CENTRAL;
        }
    }

    public void setUserRegion(Region region) {
        if (region == null) region = Region.ALL;
        prefs.edit().putString(KEY_USER_REGION, region.name()).apply();
    }

    /**
     * Lấy danh sách ID món ăn đã được favorite
     */
    public Set<Integer> getFavoriteFoodIds() {
        Set<String> raw =
                prefs.getStringSet(KEY_FAVORITE_FOOD_IDS, Collections.emptySet());

        Set<Integer> result = new HashSet<>();
        for (String s : raw) {
            try {
                result.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    /**
     * Thêm / gỡ favorite
     */
    public void toggleFavoriteFood(int foodId) {
        Set<String> raw =
                new HashSet<>(prefs.getStringSet(
                        KEY_FAVORITE_FOOD_IDS, Collections.emptySet()
                ));

        String key = String.valueOf(foodId);
        if (raw.contains(key)) {
            raw.remove(key);
        } else {
            raw.add(key);
        }

        prefs.edit().putStringSet(KEY_FAVORITE_FOOD_IDS, raw).apply();
    }


    public Set<String> getCachedPreferredTypes() {
        return new HashSet<>(
                prefs.getStringSet(KEY_CACHED_TYPES, Collections.emptySet())
        );
    }

    public Set<String> getCachedPreferredTags() {
        return new HashSet<>(
                prefs.getStringSet(KEY_CACHED_TAGS, Collections.emptySet())
        );
    }

    public void addCachedPreferredTypes(Set<String> types) {
        if (types == null || types.isEmpty()) return;

        Set<String> cached = getCachedPreferredTypes();
        cached.addAll(types);

        prefs.edit().putStringSet(KEY_CACHED_TYPES, cached).apply();
    }

    public void addCachedPreferredTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) return;

        Set<String> cached = getCachedPreferredTags();
        cached.addAll(tags);

        prefs.edit().putStringSet(KEY_CACHED_TAGS, cached).apply();
    }

    /**
     * Clear cache preference (optional, dùng khi cần reset)
     */
    public void clearCachedPreferences() {
        prefs.edit()
                .remove(KEY_CACHED_TYPES)
                .remove(KEY_CACHED_TAGS)
                .apply();
    }

    public void replaceFavoriteFoodIds(Set<String> ids) {
        if (ids == null) ids = Collections.emptySet();
        prefs.edit().putStringSet(KEY_FAVORITE_FOOD_IDS, ids).apply();
    }
}
