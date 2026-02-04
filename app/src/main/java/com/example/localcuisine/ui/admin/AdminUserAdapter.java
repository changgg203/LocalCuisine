package com.example.localcuisine.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.data.user.UserProfile;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserVH> {

    private final List<UserProfile> users;
    private final OnUserClickListener clickListener;
    private final OnUserLongClickListener longClickListener;

    public AdminUserAdapter(List<UserProfile> users, OnUserClickListener clickListener, OnUserLongClickListener longClickListener) {
        this.users = users;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new UserVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH h, int position) {
        UserProfile u = users.get(position);

        h.tvName.setText(u.displayName != null ? u.displayName : "—");
        h.tvEmail.setText(u.email != null ? u.email : "—");
        h.tvRole.setText(u.isAdmin ? "Admin" : "User");

        h.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onClick(u);
        });

        h.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(u);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public interface OnUserClickListener {
        void onClick(UserProfile user);
    }

    public interface OnUserLongClickListener {
        void onLongClick(UserProfile user);
    }

    static class UserVH extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvEmail;
        TextView tvRole;

        public UserVH(@NonNull View v) {
            super(v);
            ivAvatar = v.findViewById(R.id.ivAvatar);
            tvName = v.findViewById(R.id.tvName);
            tvEmail = v.findViewById(R.id.tvEmail);
            tvRole = v.findViewById(R.id.tvRole);
        }
    }
}
