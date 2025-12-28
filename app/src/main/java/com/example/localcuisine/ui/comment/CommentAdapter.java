package com.example.localcuisine.ui.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;

import java.util.List;

public class CommentAdapter
        extends RecyclerView.Adapter<CommentAdapter.VH> {

    private final List<CommentItem> items;

    public CommentAdapter(List<CommentItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        CommentItem item = items.get(pos);
        h.content.setText(item.comment.content);

        // indent theo level
        int padding = 32 * item.level;
        h.itemView.setPadding(padding, 16, 16, 16);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView content;

        VH(View v) {
            super(v);
            content = v.findViewById(R.id.tvContent);
        }
    }
}
