// data/recommend/RecommenderEngine.java
package com.example.localcuisine.data.recommend.core;

import com.example.localcuisine.data.recommend.rules.HardRule;
import com.example.localcuisine.data.recommend.rules.RegionRule;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RecommenderEngine
 * <p>
 * Engine gợi ý món ăn dựa trên ngữ cảnh và luật (context + rule based).
 *
 * <p>
 * Tổng quan pipeline:
 * <ol>
 *     <li>Hard filtering: loại bỏ các món không hợp điều kiện bắt buộc</li>
 *     <li>Scoring: chấm điểm theo vùng miền, thời điểm, khẩu vị, intent</li>
 *     <li>Re-ranking: đảm bảo tính đa dạng theo FoodType</li>
 *     <li>High-entropy phase: thêm yếu tố khám phá (exploration)</li>
 * </ol>
 *
 * <p>
 * Nguyên tắc thiết kế quan trọng:
 * <ul>
 *     <li>Lý do gợi ý (reason) phải xuất phát từ thuộc tính của món ăn</li>
 *     <li>Không sinh explanation chỉ dựa trên preference của user</li>
 *     <li>Explore chỉ là fallback khi không có lý do mạnh</li>
 * </ul>
 *
 * <p>
 * Engine này ưu tiên:
 * <ul>
 *     <li>Tính giải thích được (explainability)</li>
 *     <li>Trải nghiệm khám phá (không quá quyết định)</li>
 *     <li>Đa dạng kết quả</li>
 * </ul>
 */
public class RecommenderEngine {

    private static final String[] EXPLORE_REASONS = {
            "Khám phá ẩm thực địa phương",
            "Món ăn truyền thống đáng thử",
            "Đặc sản vùng miền nổi bật",
            "Gợi ý món ăn phổ biến",
            "Trải nghiệm hương vị Việt"
    };

    // Diversity caps
    private static final int DIVERSITY_TOP_N = 20;
    private static final int MAX_PER_TYPE_IN_TOP = 2;

    // Entropy knobs
    private static final int ENTROPY_SHUFFLE_TOP = 15;
    private static final int ENTROPY_INJECT_MAX = 5;

    private final List<HardRule> hardRules = new ArrayList<>();

    public RecommenderEngine() {
        // only truly hard rule(s)
        hardRules.add(new RegionRule());
    }

    /**
     * Sinh danh sách gợi ý món ăn
     *
     * @param user  Hồ sơ người dùng
     * @param foods Danh sách món ăn đầu vào
     * @param ctx   Ngữ cảnh gợi ý
     * @param k     Số lượng kết quả cần trả về
     * @return Danh sách RecommendationResult
     */
    public List<RecommendationResult> recommend(
            UserProfile user,
            List<Food> foods,
            RecommendationContext ctx,
            int k
    ) {

        if (foods == null || foods.isEmpty()) return Collections.emptyList();
        if (user == null) user = new UserProfile();
        if (ctx == null) ctx = RecommendationContext.nowDefault();
        if (k <= 0) k = 10;

        List<Food> candidates = new ArrayList<>();
        for (Food f : foods) {
            if (f == null) continue;

            boolean rejected = false;
            for (HardRule rule : hardRules) {
                if (rule.failReason(user, f, ctx) != null) {
                    rejected = true;
                    break;
                }
            }
            if (!rejected) candidates.add(f);
        }

        // absolute fallback: never empty
        if (candidates.isEmpty()) candidates.addAll(foods);

        List<RecommendationResult> scored = new ArrayList<>(candidates.size());
        for (Food f : candidates) scored.add(scoreFood(f, user, ctx));

        scored.sort((a, b) -> Double.compare(b.score, a.score));

        int topN = Math.min(DIVERSITY_TOP_N, scored.size());
        List<RecommendationResult> diversifiedTop =
                diversifyByType(new ArrayList<>(scored.subList(0, topN)));

        List<RecommendationResult> merged = new ArrayList<>(diversifiedTop.size() + (scored.size() - topN));
        merged.addAll(diversifiedTop);
        if (scored.size() > topN) merged.addAll(scored.subList(topN, scored.size()));


        // 5.1 Shuffle top slice
        int entropyTop = Math.min(ENTROPY_SHUFFLE_TOP, merged.size());
        if (entropyTop > 1) {
            Collections.shuffle(merged.subList(0, entropyTop));
        }

        // 5.2 Inject random items to the front (exploration)
        int injectCount = Math.min(ENTROPY_INJECT_MAX, merged.size() / 10); // ~10% but capped
        if (injectCount > 0) {
            List<RecommendationResult> injected = new ArrayList<>(injectCount);

            // choose unique indices by id
            Set<Integer> chosenFoodIds = new HashSet<>();
            int tries = 0;
            while (injected.size() < injectCount && tries < 50) {
                int idx = (int) (Math.random() * merged.size());
                RecommendationResult pick = merged.get(idx);
                if (pick != null && pick.food != null && chosenFoodIds.add(pick.food.getId())) {
                    injected.add(pick);
                }
                tries++;
            }

            if (!injected.isEmpty()) {
                merged.removeAll(injected);
                merged.addAll(0, injected);
            }
        }

        return merged.size() > k
                ? new ArrayList<>(merged.subList(0, k))
                : new ArrayList<>(merged);
    }


    private RecommendationResult scoreFood(
            Food food,
            UserProfile user,
            RecommendationContext ctx
    ) {
        RecommendationResult rr = new RecommendationResult(food);
        rr.score = 1.0;

        boolean hasStrongReason = false;

        /* 1) Region (soft, hard already filtered) */
        if (food.getRegion() != null && user.region != null && food.getRegion() == user.region) {
            rr.score += 0.6;
            rr.addReason("Đặc trưng vùng miền quen thuộc");
            hasStrongReason = true;
        }

        /* 2) Best time (soft) */
        if (ctx.timeOfDay != null && food.getBestTime() != null) {
            if (matchTime(ctx.timeOfDay, food.getBestTime())) {
                rr.score += 0.5;
                rr.addReason("Phù hợp thời điểm trong ngày");
                hasStrongReason = true;
            }
        }

        /* 3) Tag preference */
        if (food.getTags() != null && user.preferredTags != null && !user.preferredTags.isEmpty()) {
            int hit = 0;
            Set<String> tags = lowerSet(food.getTags());
            for (String pref : user.preferredTags) {
                if (pref == null) continue;
                if (tags.contains(pref.toLowerCase())) hit++;
            }
            if (hit > 0) {
                rr.score += Math.min(0.6, hit * 0.25);
                rr.addReason("Hợp khẩu vị của bạn");
                hasStrongReason = true;
            }
        }

        /* 4) FoodType preference */
        if (food.getTypes() != null && user.preferredTypes != null && !user.preferredTypes.isEmpty()) {

            int matchedCount = 0;
            for (FoodType t : food.getTypes()) {
                if (t == null) continue;
                if (user.preferredTypes.contains(t.name())) {
                    matchedCount++;
                }
            }

            if (matchedCount > 0) {
                rr.score += 0.4 + 0.1 * (matchedCount - 1);
                hasStrongReason = true; // strong signal exists, but silent
            }
        }


        /* 5) Intent (fallback for explore) */
        applyIntentBoost(rr, ctx, hasStrongReason);

        if (rr.score < 0) rr.score = 0;
        return rr;
    }


    private void applyIntentBoost(
            RecommendationResult rr,
            RecommendationContext ctx,
            boolean hasStrongReason
    ) {
        if (ctx == null || ctx.intent == null) return;

        Food f = rr.food;

        if ("comfort_food".equals(ctx.intent)) {
            if (hasTag(f, "warm") || hasTag(f, "soup")) {
                rr.score += 0.6;
                rr.addReason("Món ăn dễ chịu, hợp tâm trạng");
            }
            return;
        }

        if ("decide_fast".equals(ctx.intent)) {
            rr.score += 0.3;
            rr.addReason("Dễ chọn, phù hợp ăn nhanh");
            return;
        }

        // explore only when there is no strong reason
        if ("explore".equals(ctx.intent) && !hasStrongReason) {
            rr.score += 0.2;
            rr.addReason(randomExploreReason());
        }
    }

    private String randomExploreReason() {
        int idx = (int) (Math.random() * EXPLORE_REASONS.length);
        return EXPLORE_REASONS[idx];
    }

    private boolean matchTime(String ctxTime, String bestTime) {
        if (ctxTime == null || bestTime == null) return false;

        String ct = ctxTime.toLowerCase();
        String bt = bestTime.toLowerCase();

        if (ct.equals("morning") && bt.contains("sáng")) return true;
        if (ct.equals("noon") && bt.contains("trưa")) return true;
        if (ct.equals("evening") && bt.contains("tối")) return true;
        return false;
    }

    private boolean hasTag(Food f, String tag) {
        if (f == null || f.getTags() == null || tag == null) return false;
        for (String t : f.getTags()) {
            if (t != null && t.equalsIgnoreCase(tag)) return true;
        }
        return false;
    }

    private List<RecommendationResult> diversifyByType(List<RecommendationResult> list) {
        List<RecommendationResult> out = new ArrayList<>();
        Map<FoodType, Integer> count = new HashMap<>();

        for (RecommendationResult rr : list) {
            FoodType type = primaryType(rr.food);
            int c = count.getOrDefault(type, 0);
            if (c < MAX_PER_TYPE_IN_TOP) {
                out.add(rr);
                count.put(type, c + 1);
            }
        }

        // append skipped items (keep relative score order)
        if (out.size() < list.size()) {
            for (RecommendationResult rr : list) {
                if (!out.contains(rr)) out.add(rr);
            }
        }

        return out;
    }

    private FoodType primaryType(Food f) {
        if (f == null || f.getTypes() == null || f.getTypes().isEmpty()) return FoodType.OTHER;
        FoodType first = f.getTypes().iterator().next();
        return first != null ? first : FoodType.OTHER;
    }

    private Set<String> lowerSet(List<String> xs) {
        Set<String> s = new HashSet<>();
        if (xs == null) return s;
        for (String x : xs) if (x != null) s.add(x.toLowerCase());
        return s;
    }

    private String join(List<String> xs, String sep) {
        if (xs == null || xs.isEmpty()) return "";
        if (xs.size() == 1) return xs.get(0);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xs.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(xs.get(i));
        }
        return sb.toString();
    }
}
