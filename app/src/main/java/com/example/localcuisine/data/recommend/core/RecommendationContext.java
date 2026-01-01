package com.example.localcuisine.data.recommend.core;

import android.os.Build;

import com.example.localcuisine.data.recommend.util.TimeUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RecommendationContext
 * <p>
 * Đối tượng ngữ cảnh dùng làm đầu vào cho hệ thống gợi ý món ăn.
 * Đây là data holder thuần, chỉ chứa logic khởi tạo mặc định nhẹ.
 */
public class RecommendationContext {

    /**
     * Thời điểm trong ngày: "morning", "noon", "evening"
     */
    public String timeOfDay;

    /**
     * Mùa trong năm: "spring", "summer", "autumn", "winter"
     */
    public String season;

    /**
     * Vị trí / vùng miền (tạm thời bỏ qua)
     */
    public String location;

    /**
     * Mục đích hiện tại của người dùng
     * Ví dụ: "explore", "decide_fast", "comfort_food"
     */
    public String intent;

    /**
     * Danh sách hành động gần đây của người dùng
     * Ví dụ: "view:F123", "skip:F456"
     */
    public List<String> recentActions = new ArrayList<>();

    /**
     * Tạo context mặc định tại thời điểm hiện tại
     */
    public static RecommendationContext nowDefault() {
        RecommendationContext c = new RecommendationContext();
        c.timeOfDay = TimeUtil.getTimeOfDay();
        c.season = resolveSeason();
        c.intent = "explore";
        return c;
    }

    /**
     * Xác định mùa hiện tại dựa trên tháng trong năm.
     * Heuristic đơn giản, đủ dùng cho recommendation.
     */
    private static String resolveSeason() {
        int month = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            month = LocalDate.now().getMonthValue();
        }

        // Northern hemisphere assumption (OK cho VN)
        if (month == 12 || month <= 2) return "winter";
        if (month <= 5) return "spring";
        if (month <= 8) return "summer";
        return "autumn";
    }
}
