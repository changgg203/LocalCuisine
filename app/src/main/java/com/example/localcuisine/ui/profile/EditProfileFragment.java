package com.example.localcuisine.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.repository.UserRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditProfileFragment extends Fragment {

    private TextInputEditText edtDisplayName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPhone;
    private TextInputEditText edtBio;

    private View btnSave;
    private UserRepository userRepo;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        edtDisplayName = view.findViewById(R.id.edtDisplayName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtBio = view.findViewById(R.id.edtBio);
        btnSave = view.findViewById(R.id.btnSave);

        userRepo = new UserRepository();

        TextInputLayout tilDisplayName = view.findViewById(R.id.tilDisplayName);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);
        TextInputLayout tilBio = view.findViewById(R.id.tilBio);

        tilDisplayName.setHint(
                UiText.t(UiTextKey.EDIT_PROFILE_HINT_NAME)
        );
        tilEmail.setHint(
                UiText.t(UiTextKey.EDIT_PROFILE_HINT_EMAIL)
        );
        tilPhone.setHint(
                UiText.t(UiTextKey.EDIT_PROFILE_HINT_PHONE)
        );
        tilBio.setHint(
                UiText.t(UiTextKey.EDIT_PROFILE_HINT_BIO)
        );
        if (btnSave instanceof android.widget.Button) {
            ((android.widget.Button) btnSave)
                    .setText(UiText.t(UiTextKey.EDIT_PROFILE_SAVE));
        }

        btnSave.setOnClickListener(v -> onSave());

        loadProfile();

        return view;
    }


    private void loadProfile() {
        setLoading(true);

        userRepo.loadMyProfile(new UserRepository.LoadProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                bindProfile(profile);
                setLoading(false);
            }

            @Override
            public void onNotFound() {
                edtDisplayName.setText(
                        UiText.t(UiTextKey.EDIT_PROFILE_NAME_FALLBACK)
                );
                edtEmail.setText("");
                edtPhone.setText("");
                edtBio.setText("");
                setLoading(false);
            }

            @Override
            public void onError(Exception e) {
                setLoading(false);
                Toast.makeText(
                        requireContext(),
                        UiText.t(UiTextKey.EDIT_PROFILE_LOAD_ERROR),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void bindProfile(UserProfile profile) {
        edtDisplayName.setText(
                profile.displayName != null ? profile.displayName : ""
        );
        edtEmail.setText(
                profile.email != null ? profile.email : ""
        );
        edtPhone.setText(
                profile.phone != null ? profile.phone : ""
        );
        edtBio.setText(
                profile.bio != null ? profile.bio : ""
        );
    }


    private void onSave() {
        String name = edtDisplayName.getText() != null
                ? edtDisplayName.getText().toString().trim()
                : "";

        String phone = edtPhone.getText() != null
                ? edtPhone.getText().toString().trim()
                : "";

        String bio = edtBio.getText() != null
                ? edtBio.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(name)) {
            edtDisplayName.setError(
                    UiText.t(UiTextKey.EDIT_PROFILE_NAME_REQUIRED)
            );
            return;
        }

        setLoading(true);

        userRepo.saveMyProfile(
                name,
                phone,
                bio,
                new com.example.localcuisine.ui.i18n.LocaleStore(requireContext())
                        .getLanguage(),
                new UserRepository.SaveProfileCallback() {
                    @Override
                    public void onSuccess() {
                        setLoading(false);
                        Toast.makeText(
                                requireContext(),
                                UiText.t(UiTextKey.EDIT_PROFILE_SAVE_SUCCESS),
                                Toast.LENGTH_SHORT
                        ).show();

                        requireActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
                    }

                    @Override
                    public void onError(Exception e) {
                        setLoading(false);
                        Toast.makeText(
                                requireContext(),
                                UiText.t(UiTextKey.EDIT_PROFILE_SAVE_ERROR),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
    }
}
