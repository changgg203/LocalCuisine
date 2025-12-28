package com.example.localcuisine.ui.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.recommend.RecommendResult;

import java.util.List;
import java.util.StringJoiner;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private List<RecommendResult> results;

    public MapAdapter(List<RecommendResult> results) {
        this.results = results;
    }

    public void update(List<RecommendResult> newResults) {
        this.results = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecommendResult r = results.get(position);

        holder.txtName.setText(r.food.getName());

        StringJoiner joiner = new StringJoiner(" • ");
        for (String reason : r.reasons) {
            joiner.add(reason);
        }
        holder.txtReason.setText(joiner.toString());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtReason;

        ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtFoodName);
            txtReason = itemView.findViewById(R.id.txtReason);
        }
    }
}
