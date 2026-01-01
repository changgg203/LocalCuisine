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
import com.example.localcuisine.data.auth.SessionStore;
import com.example.localcuisine.data.repository.UserRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvDisplayName;

    private UserRepository userRepo;

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
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        Button btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage);

        userRepo = new UserRepository();

        // ===== Edit Profile =====
        btnEditProfile.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_container, new EditProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // ===== Change Language (placeholder) =====
        btnChangeLanguage.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Thay đổi ngôn ngữ")
                    .setMessage("Tính năng này sẽ được cập nhật sau.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        // ===== Logout =====
        btnLogout.setOnClickListener(v -> showLogoutConfirm());

        bindUserInfo();

        return view;
    }

    // ===================== USER INFO =====================

    private void bindUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }

        // Email luôn lấy từ FirebaseAuth
        String email = user.getEmail();
        tvUsername.setText(email != null ? email : "—");

        // Display name lấy từ Firestore
        userRepo.loadMyProfile(new UserRepository.LoadProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                tvDisplayName.setText(
                        profile.displayName != null && !profile.displayName.isEmpty()
                                ? profile.displayName
                                : "Người dùng"
                );
            }

            @Override
            public void onNotFound() {
                // Chưa có profile
                tvDisplayName.setText("Người dùng");
            }

            @Override
            public void onError(Exception e) {
                // Lỗi backend → fallback an toàn
                tvDisplayName.setText("Người dùng");
            }
        });
    }

    // ===================== LIFECYCLE =====================

    @Override
    public void onResume() {
        super.onResume();
        // Khi quay lại từ EditProfileFragment → refresh profile
        bindUserInfo();
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
        new SessionStore(requireContext()).logout();
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
