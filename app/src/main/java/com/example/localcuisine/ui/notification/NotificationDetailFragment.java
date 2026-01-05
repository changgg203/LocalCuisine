package com.example.localcuisine.ui.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;

public class NotificationDetailFragment extends Fragment {

    private static final String ARG_TITLE = "arg_title";
    private static final String ARG_CONTENT = "arg_content";

    public static NotificationDetailFragment newInstance(
            String title,
            String content
    ) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);

        NotificationDetailFragment fragment = new NotificationDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(
                R.layout.fragment_notification_detail,
                container,
                false
        );

        TextView tvTitle = v.findViewById(R.id.tvTitle);
        TextView tvContent = v.findViewById(R.id.tvContent);

        Bundle args = getArguments();
        if (args != null) {
            tvTitle.setText(args.getString(ARG_TITLE, ""));
            tvContent.setText(args.getString(ARG_CONTENT, ""));
        }

        return v;
    }
}
