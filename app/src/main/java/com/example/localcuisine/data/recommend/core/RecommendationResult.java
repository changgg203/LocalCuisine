package com.example.localcuisine.data.recommend.core;

import android.text.TextUtils;

import com.example.localcuisine.model.Food;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * RecommendationResult
 * <p>
 * Đối tượng biểu diễn kết quả gợi ý cho một món ăn.
 *
 * <p>
 * Bao gồm:
 * <ul>
 *     <li>{@code food}: món ăn được đề xuất</li>
 *     <li>{@code score}: điểm phù hợp dùng để xếp hạng</li>
 *     <li>{@code reasons}: các lý do giải thích vì sao món này được gợi ý</li>
 * </ul>
 *
 * <p>
 * Class này là immutable ở mức logic:
 * <ul>
 *     <li>{@code food} là final</li>
 *     <li>{@code score} chỉ được set trong recommendation engine</li>
 *     <li>{@code reasons} chỉ được thêm (append) trong engine</li>
 * </ul>
 */
public class RecommendationResult {

    /**
     * Món ăn được đề xuất
     */
    public final Food food;

    /**
     * Danh sách lý do gợi ý (chỉ append, không expose mutable)
     */
    private final List<String> reasons = new ArrayList<>();

    /**
     * Điểm phù hợp của món ăn
     */
    public double score;

    /**
     * Khởi tạo kết quả gợi ý cho một món ăn
     *
     * @param food món ăn được đề xuất
     */
    public RecommendationResult(Food food) {
        this.food = food;
        this.score = 0.0;
    }

    /**
     * Thêm một lý do giải thích cho kết quả gợi ý
     *
     * @param reason Chuỗi mô tả lý do (bỏ qua nếu null hoặc rỗng)
     */
    public void addReason(String reason) {
        if (reason == null) return;
        if (reason.trim().isEmpty()) return;
        reasons.add(reason);
    }

    /**
     * Lấy danh sách lý do ở dạng chỉ đọc
     *
     * @return Danh sách reason (read-only)
     */
    public List<String> getReasons() {
        return Collections.unmodifiableList(reasons);
    }

    /**
     * Trả về chuỗi lý do gợi ý dùng trực tiếp cho UI
     *
     * <p>
     * Chỉ hiển thị một số lượng lý do giới hạn để tránh spam giao diện.
     *
     * <p>
     * Ví dụ:
     * <pre>
     * "Phù hợp buổi sáng • Đặc trưng miền Bắc"
     * </pre>
     *
     * @return Chuỗi lý do gợi ý
     */
    public String getReasonText() {
        if (reasons.isEmpty()) return "";
        return TextUtils.join(
                " • ",
                reasons.subList(0, Math.min(2, reasons.size()))
        );
    }

    @Override
    public String toString() {
        return "RecommendationResult{" +
                "food=" + (food != null ? food.getName() : "null") +
                ", score=" + score +
                ", reasons=" + reasons +
                '}';
    }
}
