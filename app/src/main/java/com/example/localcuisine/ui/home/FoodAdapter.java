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

    private final List<Food> originalList;
    private final List<Food> displayList;
    private final OnFoodClickListener listener;

    public FoodAdapter(List<Food> foodList, OnFoodClickListener listener) {
        this.originalList = new ArrayList<>(foodList);
        this.displayList = new ArrayList<>(foodList);
        this.listener = listener;
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

        // ===== IMAGE RENDER =====
        String imageUrl = food.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {

            if (imageUrl.startsWith("http")) {
                // load từ internet
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_food)
                        .error(R.drawable.placeholder_food)
                        .centerCrop()
                        .into(holder.imgFood);

            } else {
                // load từ assets
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

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFoodClick(food.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    // ===================== FILTER =====================

    @SuppressLint("NotifyDataSetChanged")
    public void filterByTypes(List<FoodType> types) {
        displayList.clear();

        if (types == null || types.isEmpty()) {
            displayList.addAll(originalList);
        } else {
            for (Food food : originalList) {
                if (!Collections.disjoint(food.getTypes(), types)) {
                    displayList.add(food);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void filterByQuery(String query) {
        displayList.clear();

        if (query == null || query.trim().isEmpty()) {
            displayList.addAll(originalList);
        } else {
            String q = query.toLowerCase();

            for (Food food : originalList) {
                boolean match =
                        food.getName().toLowerCase().contains(q)
                                || food.getLocation().toLowerCase().contains(q)
                                || food.getTags().stream()
                                .anyMatch(tag -> tag.toLowerCase().contains(q));

                if (match) {
                    displayList.add(food);
                }
            }
        }

        notifyDataSetChanged();
    }

    // ===================== DATA =====================

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Food> newData) {
        originalList.clear();
        originalList.addAll(newData);

        displayList.clear();
        displayList.addAll(newData);

        notifyDataSetChanged();
    }

    // ===================== VIEW HOLDER =====================

    public interface OnFoodClickListener {
        void onFoodClick(int foodId);
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView txtFoodName;
        TextView txtFoodRegion;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodRegion = itemView.findViewById(R.id.txtFoodRegion);
        }
    }
}
