package com.example.localcuisine.ui.detail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.model.Reply;
import com.example.localcuisine.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> reviews;
    private final OnReplyClickListener replyListener;
    public ReviewAdapter(List<Review> reviews, OnReplyClickListener replyListener) {
        this.reviews = reviews;
        this.replyListener = replyListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Review review = reviews.get(position);

        // ===== Rating =====
        holder.ratingBar.setRating(review.getRating());
        holder.ratingBar.setIsIndicator(true);

        // ===== Comment =====
        String comment = review.getComment();
        holder.txtComment.setText(
                (comment == null || comment.trim().isEmpty())
                        ? "— Không có nhận xét —"
                        : comment
        );

        // ===== Replies =====
        holder.layoutReplies.removeAllViews();

        if (review.getReplies() != null) {
            for (Reply reply : review.getReplies()) {
                TextView tv = new TextView(holder.itemView.getContext());
                tv.setText("↳ " + reply.getContent());
                tv.setTextSize(13f);
                tv.setTextColor(0xFF555555);
                tv.setPadding(0, 4, 0, 0);
                holder.layoutReplies.addView(tv);
            }
        }

        // ===== Reply action =====
        holder.btnReply.setOnClickListener(v -> {
            if (replyListener != null) {
                replyListener.onReply(review);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews == null ? 0 : reviews.size();
    }

    public interface OnReplyClickListener {
        void onReply(Review review);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final RatingBar ratingBar;
        final TextView txtComment;
        final ViewGroup layoutReplies;
        final TextView btnReply;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txtComment = itemView.findViewById(R.id.txtComment);
            layoutReplies = itemView.findViewById(R.id.layoutReplies);
            btnReply = itemView.findViewById(R.id.btnReply);
        }
    }
}
