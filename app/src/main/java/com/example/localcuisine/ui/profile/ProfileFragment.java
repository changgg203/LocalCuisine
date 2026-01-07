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
import com.example.localcuisine.ui.i18n.LocaleStore;
import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;
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

        // ===== Bind views =====
        tvUsername = view.findViewById(R.id.tvUsername);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);

        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button btnEditProfile = view.findViewById(R.id.btnEditProfile);
        Button btnChangeLanguage = view.findViewById(R.id.btnChangeLanguage);
        btnEditProfile.setText(UiText.t(UiTextKey.PROFILE_EDIT));
        btnChangeLanguage.setText(UiText.t(UiTextKey.PROFILE_CHANGE_LANGUAGE));
        btnLogout.setText(UiText.t(UiTextKey.PROFILE_LOGOUT));

        userRepo = new UserRepository();

        // ===== Edit Profile =====
        btnEditProfile.setOnClickListener(v ->
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_container, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ===== Change Language (placeholder – UI i18n ready) =====
        btnChangeLanguage.setOnClickListener(v -> showLanguageDialog());

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
                                : UiText.t(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK)
                );
            }

            @Override
            public void onNotFound() {
                tvDisplayName.setText(
                        UiText.t(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK)
                );
            }

            @Override
            public void onError(Exception e) {
                tvDisplayName.setText(
                        UiText.t(UiTextKey.PROFILE_DISPLAY_NAME_FALLBACK)
                );
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

    // ===================== LANGUAGE (PLACEHOLDER) =====================

    private void showLanguageDialog() {
        String[] langs = {
                UiText.t(UiTextKey.LANG_VI),
                UiText.t(UiTextKey.LANG_EN)
        };

        String[] values = {"vi", "en"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(UiText.t(UiTextKey.LANG_SELECT_TITLE))
                .setItems(langs, (dialog, which) -> {
                    String selectedLang = values[which];
                    new LocaleStore(requireContext()).setLanguage(selectedLang);
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
