package com.example.localcuisine.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localcuisine.R;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<Food> originalList = new ArrayList<>();
    private final List<Food> displayList = new ArrayList<>();
    private final OnFoodClickListener listener;
    private final ExplainProvider explainProvider;
    private String currentQuery = null;
    private List<FoodType> selectedTypes = new ArrayList<>();
    private String selectedRegion = null;

    public FoodAdapter(
            List<Food> foodList,
            OnFoodClickListener listener,
            ExplainProvider explainProvider
    ) {
        if (foodList != null) {
            originalList.addAll(foodList);
            displayList.addAll(foodList);
        }
        this.listener = listener;
        this.explainProvider = explainProvider;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = displayList.get(position);

        holder.txtFoodName.setText(food.getName());
        holder.txtFoodRegion.setText(food.getRegion().getDisplayName());

        // ===== IMAGE =====
        String imageUrl = food.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .centerCrop()
                        .into(holder.imgFood);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load("file:///android_asset/" + imageUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .centerCrop()
                        .into(holder.imgFood);
            }
        } else {
            holder.imgFood.setImageResource(R.drawable.placeholder_food);
        }

        // ===== CLICK =====
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodClick(food.getId());
            }
        });

        // ===== EXPLAIN =====
        String explain = explainProvider != null
                ? explainProvider.getExplainText(food.getId())
                : null;

        if (explain != null && !explain.isEmpty()) {
            holder.txtExplain.setVisibility(View.VISIBLE);
            holder.txtExplain.setText(explain);
        } else {
            holder.txtExplain.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }


    @SuppressLint("NotifyDataSetChanged")
    private void applyFilters() {
        displayList.clear();

        for (Food food : originalList) {

            // ===== QUERY =====
            if (currentQuery != null && !currentQuery.trim().isEmpty()) {
                String q = currentQuery.toLowerCase();
                boolean match =
                        food.getName().toLowerCase().contains(q)
                                || food.getLocation().toLowerCase().contains(q)
                                || food.getTags().stream()
                                .anyMatch(tag -> tag.toLowerCase().contains(q));

                if (!match) continue;
            }

            // ===== TYPE =====
            if (selectedTypes != null && !selectedTypes.isEmpty()) {
                if (Collections.disjoint(food.getTypes(), selectedTypes)) {
                    continue;
                }
            }

            // ===== REGION =====
            if (selectedRegion != null) {
                if (!food.getRegion().name().equals(selectedRegion)) {
                    continue;
                }
            }

            displayList.add(food);
        }

        notifyDataSetChanged();
    }


    public void filterByQuery(String query) {
        this.currentQuery = query;
        applyFilters();
    }

    public void filterByTypes(List<FoodType> types) {
        this.selectedTypes = types != null ? types : new ArrayList<>();
        applyFilters();
    }

    public void filterByRegion(String region) {
        this.selectedRegion = region;
        applyFilters();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Food> newData) {
        originalList.clear();
        displayList.clear();
        if (newData != null) {
            originalList.addAll(newData);
            displayList.addAll(newData);
        }
        currentQuery = null;
        selectedTypes.clear();
        selectedRegion = null;

        notifyDataSetChanged();
    }

    public interface ExplainProvider {
        String getExplainText(int foodId);
    }

    public interface OnFoodClickListener {
        void onFoodClick(int foodId);
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView txtFoodName;
        TextView txtFoodRegion;
        TextView txtExplain;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodRegion = itemView.findViewById(R.id.txtFoodRegion);
            txtExplain = itemView.findViewById(R.id.txtExplain);
        }
    }
}
