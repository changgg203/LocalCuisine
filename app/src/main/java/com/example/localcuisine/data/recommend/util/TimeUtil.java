package com.example.localcuisine.data.recommend.util;

import java.util.Calendar;

/**
 * TimeUtil
 * <p>
 * Tiện ích xác định thời điểm trong ngày,
 * phục vụ cho recommendation context.
 *
 * <p>
 * Quy ước:
 * <ul>
 *     <li>"morning": 05h – 10h</li>
 *     <li>"noon":    11h – 15h</li>
 *     <li>"evening": các khung giờ còn lại</li>
 * </ul>
 *
 * <p>
 * Giá trị trả về là chuỗi đơn giản để dễ dùng trong rule-based logic.
 */
public class TimeUtil {

    /**
     * Lấy thời điểm trong ngày tại thời gian hiện tại
     *
     * @return "morning", "noon" hoặc "evening"
     */
    public static String getTimeOfDay() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h >= 5 && h <= 10) return "morning";
        if (h >= 11 && h <= 15) return "noon";
        return "evening";
    }
}
