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
import com.example.localcuisine.data.FoodRepository;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.ui.common.GridSpacingItemDecoration;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FoodAdapter adapter;
    private ArrayAdapter<String> suggestAdapter;

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

        adapter = new FoodAdapter(new ArrayList<>(), foodId ->
                ((MainActivity) requireActivity()).openFoodDetail(foodId)
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

                adapter.setData(loadedFoods);

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
}
