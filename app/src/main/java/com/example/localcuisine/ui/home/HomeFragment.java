package com.example.localcuisine.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.MainActivity;
import com.example.localcuisine.R;
import com.example.localcuisine.data.auth.SessionStore;
import com.example.localcuisine.data.recommend.core.RecommendationContext;
import com.example.localcuisine.data.recommend.core.RecommendationResult;
import com.example.localcuisine.data.recommend.core.RecommenderEngine;
import com.example.localcuisine.data.recommend.signal.PreferenceTracker;
import com.example.localcuisine.data.repository.FoodRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.ui.common.GridSpacingItemDecoration;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private final Map<Integer, RecommendationResult> explainMap = new HashMap<>();
    private FoodAdapter adapter;
    private ArrayAdapter<String> suggestAdapter;
    private RecommenderEngine recommenderEngine;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // ===== RecyclerView =====
        RecyclerView recyclerView = view.findViewById(R.id.recyclerFood);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        int spacing = (int) (getResources().getDisplayMetrics().density * 6);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing));
        recommenderEngine = new RecommenderEngine();

        adapter = new FoodAdapter(
                new ArrayList<>(),
                foodId -> {
                    Food f = FoodRepository.getFoodById(foodId);
                    if (f != null) {
                        PreferenceTracker.onFoodClick(requireContext(), f);
                    }
                    ((MainActivity) requireActivity()).openFoodDetail(foodId);
                },
                foodId -> {
                    RecommendationResult r = explainMap.get(foodId);
                    return r != null ? r.getReasonText() : null;
                }
        );


        recyclerView.setAdapter(adapter);

        // ===== Search =====
        AutoCompleteTextView edtSearch = view.findViewById(R.id.edtSearch);
        List<String> suggestions = new ArrayList<>();

        suggestAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );
        edtSearch.setAdapter(suggestAdapter);

        edtSearch.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            adapter.filterByQuery(selected);
        });

        // ===== Filter Chips =====
        ChipGroup chipGroup = view.findViewById(R.id.chipGroupFilters);
        chipGroup.removeAllViews();

        for (FoodType type : FoodType.values()) {
            Chip chip = new Chip(requireContext());
            chip.setText(type.getDisplayName());
            chip.setCheckable(true);
            chip.setTag(type);
            chipGroup.addView(chip);
        }

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            List<FoodType> selectedTypes = new ArrayList<>();
            for (int id : checkedIds) {
                Chip chip = group.findViewById(id);
                if (chip != null && chip.getTag() instanceof FoodType) {
                    selectedTypes.add((FoodType) chip.getTag());
                }
            }
            adapter.filterByTypes(selectedTypes);
        });

        // ===== Load Data (Firebase + fallback) =====
        FoodRepository.ensureLoaded(new FoodRepository.LoadCallback() {
            @Override
            public void onSuccess(@NonNull List<Food> loadedFoods) {
                if (!isAdded()) return;

                List<Food> recommendedFoods = recommendFoods(loadedFoods);
                adapter.setData(recommendedFoods);


                suggestions.clear();
                for (Food food : loadedFoods) {
                    suggestions.add(food.getName());
                }
                suggestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NonNull Exception e) {
                if (!isAdded()) return;

                // fallback local
                List<Food> fallback = FoodRepository.getAllFoods();
                adapter.setData(fallback);

                suggestions.clear();
                for (Food food : fallback) {
                    suggestions.add(food.getName());
                }
                suggestAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private List<Food> recommendFoods(List<Food> foods) {
        SessionStore sm = new SessionStore(requireContext());

        UserProfile user = new UserProfile();
        user.region = sm.getUserRegion();
        user.loggedIn = sm.isLoggedIn();

        user.preferredTypes.addAll(
                PreferenceTracker.getPreferredTypes(requireContext())
        );


        RecommendationContext ctx = RecommendationContext.nowDefault();
        ctx.intent = "explore";

        List<RecommendationResult> results =
                recommenderEngine.recommend(user, foods, ctx, 1000);

        explainMap.clear();
        for (RecommendationResult r : results) {
            explainMap.put(r.food.getId(), r);
        }

        List<Food> out = new ArrayList<>();
        for (RecommendationResult r : results) {
            out.add(r.food);
        }
        return out;
    }


}
