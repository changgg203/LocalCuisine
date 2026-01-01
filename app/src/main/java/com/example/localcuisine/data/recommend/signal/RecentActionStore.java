package com.example.localcuisine.data.recommend.signal;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RecentActionStore
 * <p>
 * Lưu trữ danh sách các hành động gần đây của người dùng
 * phục vụ cho recommendation (implicit signal).
 *
 * <p>
 * Đặc điểm:
 * <ul>
 *     <li>Lưu cục bộ bằng SharedPreferences</li>
 *     <li>Thứ tự: hành động mới nhất đứng trước</li>
 *     <li>Dữ liệu nhẹ, không yêu cầu backend</li>
 * </ul>
 *
 * <p>
 * Ví dụ action:
 * <ul>
 *     <li>"view:F123"</li>
 *     <li>"skip:F456"</li>
 *     <li>"favorite:F789"</li>
 * </ul>
 */
public class RecentActionStore {

    /**
     * Tên SharedPreferences
     */
    private static final String PREF = "rs_recent_actions";

    /**
     * Key lưu danh sách action
     */
    private static final String KEY = "actions";

    /**
     * Thêm một hành động mới vào danh sách
     *
     * <p>
     * Hành động mới nhất sẽ được đưa lên đầu danh sách.
     * Tổng số action được giới hạn để tránh phình dữ liệu.
     *
     * @param ctx    Context
     * @param action Chuỗi biểu diễn hành động
     */
    public static void addAction(Context ctx, String action) {
        if (ctx == null || action == null) return;
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        List<String> list = getActions(ctx);
        list.add(0, action); // newest first

        // Giới hạn tối đa 50 hành động gần nhất
        if (list.size() > 50) list = list.subList(0, 50);

        sp.edit().putString(KEY, join(list)).apply();
    }

    /**
     * Lấy danh sách các hành động gần đây
     *
     * @param ctx Context
     * @return Danh sách action (mới nhất đứng trước)
     */
    public static List<String> getActions(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String raw = sp.getString(KEY, "");
        if (raw == null || raw.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(raw.split("\\|\\|")));
    }

    /**
     * Ghép danh sách action thành chuỗi lưu trữ
     *
     * @param list Danh sách action
     * @return Chuỗi đã ghép
     */
    private static String join(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) sb.append("||");
        }
        return sb.toString();
    }
}
