package com.example.localcuisine.ui.notification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.data.remote.FirestoreNotification;

import java.util.List;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.NotiVH> {

    private final List<FirestoreNotification> list;
    private final OnNotificationClick listener;

    public NotificationAdapter(
            List<FirestoreNotification> list,
            OnNotificationClick listener
    ) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotiVH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotiVH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull NotiVH h,
            int position
    ) {
        FirestoreNotification n = list.get(position);
        
        if (n == null) return;

        h.txtTitle.setText(
                n.getTitle() != null ? n.getTitle() : ""
        );
        h.txtContent.setText(
                n.getContent() != null ? n.getContent() : ""
        );

        h.txtTime.setText(
                n.getCreatedAt() != null
                        ? n.getCreatedAt().toDate().toString()
                        : ""
        );

        h.itemView.setAlpha(n.isRead() ? 0.5f : 1f);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(n);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnNotificationClick {
        void onClick(FirestoreNotification n);
    }

    // ===================== VIEW HOLDER =====================

    static class NotiVH extends RecyclerView.ViewHolder {

        TextView txtTitle;
        TextView txtContent;
        TextView txtTime;

        NotiVH(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtContent = itemView.findViewById(R.id.txtContent);
            txtTime = itemView.findViewById(R.id.txtTime);

            if (txtTitle == null || txtContent == null || txtTime == null) {
                throw new RuntimeException(
                        "Notification item layout thiếu TextView ID"
                );
            }
        }
    }
}
