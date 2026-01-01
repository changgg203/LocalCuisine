// data/recommend/rules/HardRule.java
package com.example.localcuisine.data.recommend.rules;

import com.example.localcuisine.data.recommend.core.RecommendationContext;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.model.Food;

public interface HardRule {
    /**
     * @return null nếu PASS, hoặc String lý do nếu FAIL (bị loại)
     */
    String failReason(UserProfile user, Food item, RecommendationContext ctx);
}
