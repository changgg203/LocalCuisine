// ui/profile/ProfileFragment.java
package com.example.localcuisine.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.SessionManager;
import com.example.localcuisine.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvDisplayName;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        bindUserInfo();

        btnLogout.setOnClickListener(v -> showLogoutConfirm());

        return view;
    }

    // ===================== USER INFO =====================

    private void bindUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }

        String email = user.getEmail();
        String displayName = user.getDisplayName();

        tvUsername.setText(email != null ? email : "—");
        tvDisplayName.setText(
                displayName != null && !displayName.isEmpty()
                        ? displayName
                        : "Người dùng"
        );
    }

    // ===================== LOGOUT =====================

    private void showLogoutConfirm() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> doLogout())
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void doLogout() {
        FirebaseAuth.getInstance().signOut();
        new SessionManager(requireContext()).logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
