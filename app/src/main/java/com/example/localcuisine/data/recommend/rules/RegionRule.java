package com.example.localcuisine.data.recommend.rules;

import com.example.localcuisine.data.recommend.core.RecommendationContext;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.Region;

/**
 * RegionRule
 * <p>
 * Hard rule dùng để loại bỏ các món ăn không thuộc vùng miền phù hợp
 * với người dùng.
 *
 * <p>
 * Nguyên tắc:
 * <ul>
 *     <li>Nếu vùng miền của người dùng hoặc món ăn không xác định → không loại</li>
 *     <li>{@link Region#ALL} được xem là không giới hạn → không loại</li>
 *     <li>Chỉ loại khi cả hai đều xác định và khác nhau</li>
 * </ul>
 *
 * <p>
 * Đây là hard rule:
 * nếu fail, món ăn sẽ bị loại khỏi danh sách ứng viên ngay từ đầu.
 */
public class RegionRule implements HardRule {

    /**
     * Kiểm tra điều kiện vùng miền cho một món ăn
     *
     * @param user Hồ sơ người dùng
     * @param item Món ăn cần kiểm tra
     * @param ctx  Ngữ cảnh gợi ý (không sử dụng trong rule này)
     * @return Lý do loại nếu không hợp vùng miền, null nếu pass
     */
    @Override
    public String failReason(UserProfile user, Food item, RecommendationContext ctx) {
        return null;
    }
}
