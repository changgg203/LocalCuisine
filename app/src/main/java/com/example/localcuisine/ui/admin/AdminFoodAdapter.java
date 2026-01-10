package com.example.localcuisine.ui.admin;

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
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.example.localcuisine.ui.i18n.UiTextProvider;

import java.util.List;

/**
 * Adapter danh sách món ăn cho Admin
 * <p>
 * - Click        → Edit
 * - Long press   → Delete (confirm ở Fragment)
 */
public class AdminFoodAdapter
        extends RecyclerView.Adapter<AdminFoodAdapter.FoodVH> {


    private final List<Food> foods;
    private final OnFoodClickListener clickListener;
    private final OnFoodLongClickListener longClickListener;


    public AdminFoodAdapter(
            List<Food> foods,
            OnFoodClickListener clickListener,
            OnFoodLongClickListener longClickListener
    ) {
        this.foods = foods;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public FoodVH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_food, parent, false);
        return new FoodVH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FoodVH h,
            int position
    ) {
        Food f = foods.get(position);

        h.tvName.setText(f.getName());

        h.tvRegion.setText(
                f.getRegion() != null
                        ? f.getRegion().getDisplayName()
                        : ""
        );

        if (f.getBestTime() != null && !f.getBestTime().isEmpty()) {
            String label = UiTextProvider.get(UiTextKey.ADMIN_BEST_TIME_LABEL);
            h.tvBestTime.setText(label + ": " + f.getBestTime());
            h.tvBestTime.setVisibility(View.VISIBLE);
        } else {
            h.tvBestTime.setVisibility(View.GONE);
        }


        h.tvLocation.setText(
                f.getLocation() != null
                        ? f.getLocation()
                        : ""
        );

        // ---------- IMAGE (assets) ----------
        String image = f.getImageUrl();
        if (image != null && !image.isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load("file:///android_asset/" + image)
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(h.ivFood);
        } else {
            h.ivFood.setImageResource(R.drawable.placeholder_food);
        }

        // ---------- EVENTS ----------
        h.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(f);
            }
        });

        h.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(f);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return foods == null ? 0 : foods.size();
    }

    public interface OnFoodClickListener {
        void onClick(Food food);
    }

    public interface OnFoodLongClickListener {
        void onLongClick(Food food);
    }

    static class FoodVH extends RecyclerView.ViewHolder {

        ImageView ivFood;
        TextView tvName;
        TextView tvRegion;
        TextView tvBestTime;
        TextView tvLocation;

        FoodVH(@NonNull View v) {
            super(v);
            ivFood = v.findViewById(R.id.ivFood);
            tvName = v.findViewById(R.id.tvName);
            tvRegion = v.findViewById(R.id.tvRegion);
            tvBestTime = v.findViewById(R.id.tvBestTime);
            tvLocation = v.findViewById(R.id.tvLocation);
        }
    }
}
