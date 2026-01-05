// ui/notification/NotificationFragment.java
package com.example.localcuisine.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.MainActivity;
import com.example.localcuisine.R;
import com.example.localcuisine.data.remote.notification.FirestoreNotification;
import com.example.localcuisine.data.remote.notification.NotificationTextBuilder;
import com.example.localcuisine.data.repository.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private final List<FirestoreNotification> data = new ArrayList<>();
    private NotificationRepository repo;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        repo = new NotificationRepository();

        RecyclerView rv = view.findViewById(R.id.rvNotifications);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new NotificationAdapter(
                data,
                this::onNotificationClick
        );
        rv.setAdapter(adapter);

        loadNotifications();
    }

    // ===================== DATA =====================

    private void loadNotifications() {
        repo.loadNotifications(new NotificationRepository.LoadCallback() {
            @Override
            public void onSuccess(List<FirestoreNotification> list) {
                if (!isAdded()) return;

                data.clear();
                data.addAll(list);
                adapter.notifyDataSetChanged();

                // cập nhật badge ở MainActivity
                ((MainActivity) requireActivity())
                        .updateNotificationBadge();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ===================== CLICK =====================

    private void onNotificationClick(@NonNull FirestoreNotification n) {

        if (!n.isRead() && n.getId() != null) {
            repo.markAsRead(n.getId());
            n.setRead(true);
            adapter.notifyDataSetChanged();

            ((MainActivity) requireActivity())
                    .updateNotificationBadge();
        }

        MainActivity main = (MainActivity) requireActivity();

        String type = n.getType();
        if (type == null) {
            type = "REPLY";
        }

        switch (type) {
            case "REPLY":
            case "REVIEW":
                if (n.getFoodId() > 0) {
                    main.openFoodDetail(n.getFoodId());
                } else {
                    main.openNotificationDetail(
                            NotificationTextBuilder.buildTitle(n),
                            buildDetailContent(n)
                    );
                }
                break;

            case "FAVORITE":
            case "UNKNOWN":
            default:
                main.openNotificationDetail(
                        NotificationTextBuilder.buildTitle(n),
                        buildDetailContent(n)
                );
                break;
        }
    }

    private String buildDetailContent(FirestoreNotification n) {
        StringBuilder sb = new StringBuilder();

        sb.append(NotificationTextBuilder.buildContent(n));

        // Nếu có raw content (reply thật) → show như quote
        if (n.getContent() != null && !n.getContent().isEmpty()) {
            sb.append("\n\n\"");
            sb.append(n.getContent());
            sb.append("\"");
        }

        return sb.toString();
    }


}
