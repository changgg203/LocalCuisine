package com.example.localcuisine.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.auth.SessionStore;
import com.example.localcuisine.data.repository.UserRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.ui.admin.AdminFoodListFragment;
import com.example.localcuisine.ui.admin.AdminUserListFragment;
import com.example.localcuisine.ui.auth.LoginActivity;
import com.example.localcuisine.ui.i18n.LocaleStore;
import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.localcuisine.BuildConfig;

/**
 * ProfileFragment
 * <p>
 * - Hiển thị thông tin người dùng
 * - Cho phép chỉnh sửa profile
 * - Đổi ngôn ngữ
 * - Logout
 * - Hiển thị nút Admin nếu user có quyền
 */
public class ProfileFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvDisplayName;

    private Button btnEditProfile;
    private Button btnChangeLanguage;
    private Button btnChangePassword;
    private Button btnAdminManage;
    private Button btnDebugClaims;
    private Button btnLogout;

    private UserRepository userRepo;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        bindViews(view);
        setupTexts();
        setupActions();

        userRepo = new UserRepository();
        bindUserInfo();

        return view;
    }


    private void bindViews(View view) {
        tvUsername = view.findViewById(R.id.tvUsername);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnAdminManage = view.findViewById(R.id.btnAdminManage);
        // Use dynamic lookup to avoid compile-time dependency on R.id.btnDebugClaims
        int debugBtnId = view.getResources().getIdentifier("btnDebugClaims", "id", requireContext().getPackageName());
        btnDebugClaims = debugBtnId != 0 ? view.findViewById(debugBtnId) : null;
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupTexts() {
        btnEditProfile.setText(UiText.t(UiTextKey.PROFILE_EDIT));
        btnChangeLanguage.setText(UiText.t(UiTextKey.PROFILE_CHANGE_LANGUAGE));
        btnChangePassword.setText(UiText.t(UiTextKey.PROFILE_CHANGE_PASSWORD));
        btnAdminManage.setText(UiText.t(UiTextKey.ADMIN_MANAGEMENT));
        btnLogout.setText(UiText.t(UiTextKey.PROFILE_LOGOUT));
    }

    private void setupActions() {
        btnEditProfile.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_container, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnChangeLanguage.setOnClickListener(v -> showLanguageDialog());

        btnChangePassword.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_container, new ChangePasswordFragment())
                        .addToBackStack(null)
                        .commit()
        );

        btnAdminManage.setOnClickListener(v -> openAdmin());

        if (btnDebugClaims != null) {
            btnDebugClaims.setOnClickListener(v -> {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().getCurrentUser()
                        .getIdToken(true)
                        .addOnSuccessListener(result -> {
                            Object claims = result.getClaims();
                            String msg = claims != null ? claims.toString() : "{}";

                            Log.d("AdminClaim", "Claims: " + msg);

                            new MaterialAlertDialogBuilder(requireContext())
                                    .setTitle("Token claims")
                                    .setMessage(msg)
                                    .setPositiveButton("OK", null)
                                    .show();

                        })
                        .addOnFailureListener(e -> {
                            Log.e("AdminClaim", "getIdToken failed", e);
                            Toast.makeText(requireContext(), "Failed to refresh token: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        }

        btnLogout.setOnClickListener(v -> showLogoutConfirm());
    }


    private void bindUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            redirectToLogin();
            return;
        }

        // Email luôn lấy từ FirebaseAuth
        String email = user.getEmail();
        tvUsername.setText(email != null ? email : "—");

        // Profile từ Firestore
        userRepo.loadMyProfile(new UserRepository.LoadProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                tvDisplayName.setText(
                        profile.displayName != null && !profile.displayName.isEmpty()
                                ? profile.displayName
                                : UiText.t(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK)
                );

                // ===== ADMIN VISIBILITY =====
                btnAdminManage.setVisibility(
                        profile.isAdmin ? View.VISIBLE : View.GONE
                );

                // Show debug button when running debug build or if user is admin
                if (btnDebugClaims != null) {
                    btnDebugClaims.setVisibility((BuildConfig.DEBUG || profile.isAdmin) ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onNotFound() {
                applyFallbackProfile();
            }

            @Override
            public void onError(Exception e) {
                applyFallbackProfile();
            }
        });
    }

    private void applyFallbackProfile() {
        tvDisplayName.setText(
                UiText.t(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK)
        );
        btnAdminManage.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
        // Quay lại từ EditProfileFragment → refresh
        bindUserInfo();
    }


    private void openAdmin() {
        String[] options = {
                UiText.t(UiTextKey.ADMIN_MANAGE_FOODS),
                UiText.t(UiTextKey.ADMIN_MANAGE_USERS)
        };

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.nav_container, new AdminFoodListFragment())
                                .addToBackStack(null)
                                .commit();
                    } else {
                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.nav_container, new AdminUserListFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .show();
    }


    private void showLanguageDialog() {
        String[] langs = {
                UiText.t(UiTextKey.LANG_VI),
                UiText.t(UiTextKey.LANG_EN)
        };

        String[] values = {"vi", "en"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(UiText.t(UiTextKey.LANG_SELECT_TITLE))
                .setItems(langs, (dialog, which) -> {
                    new LocaleStore(requireContext())
                            .setLanguage(values[which]);
                    requireActivity().recreate();
                })
                .show();
    }

    // ===================== LOGOUT =====================

    private void showLogoutConfirm() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(UiText.t(UiTextKey.LOGOUT_TITLE))
                .setMessage(UiText.t(UiTextKey.LOGOUT_MESSAGE))
                .setPositiveButton(
                        UiText.t(UiTextKey.LOGOUT_CONFIRM),
                        (dialog, which) -> doLogout()
                )
                .setNegativeButton(
                        UiText.t(UiTextKey.LOGOUT_CANCEL),
                        null
                )
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
