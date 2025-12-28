package com.example.localcuisine.recommend;

import com.example.localcuisine.model.Region;

import java.util.Set;

public class RecommendContext {

    public String timeOfDay;        // "Sáng", "Trưa", "Tối"
    public Set<String> preferences; // "cay", "món nước", ...
    public Region preferredRegion;  // có thể null

    public RecommendContext(String timeOfDay,
                            Set<String> preferences,
                            Region preferredRegion) {
        this.timeOfDay = timeOfDay;
        this.preferences = preferences;
        this.preferredRegion = preferredRegion;
    }
}
