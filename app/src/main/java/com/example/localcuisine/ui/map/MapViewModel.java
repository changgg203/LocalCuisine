package com.example.localcuisine.ui.map;

import android.content.Context;

import com.example.localcuisine.model.Region;
import com.example.localcuisine.recommend.RecommendContext;
import com.example.localcuisine.recommend.RecommendResult;
import com.example.localcuisine.recommend.RecommendationEngine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapViewModel {

    private final Context context;

    public MapViewModel(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<RecommendResult> recommend() {

        Set<String> prefs = new HashSet<>();
        prefs.add("món nước");
        prefs.add("cay");

        RecommendContext ctx = new RecommendContext(
                "Sáng",
                prefs,
                Region.NORTH
        );

        return RecommendationEngine.recommend(context, ctx);
    }
}
