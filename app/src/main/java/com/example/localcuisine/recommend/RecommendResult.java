package com.example.localcuisine.recommend;

import com.example.localcuisine.model.Food;

import java.util.List;

public class RecommendResult {

    public Food food;
    public List<String> reasons;
    public int score;

    public RecommendResult(Food food, List<String> reasons, int score) {
        this.food = food;
        this.reasons = reasons;
        this.score = score;
    }
}
