package com.example.localcuisine.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.auth.AuthRepository;
import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends Fragment {

    private TextInputEditText edtCurrent;
    private TextInputEditText edtNew;
    private TextInputEditText edtConfirm;
    private Button btnSave;

    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        edtCurrent = view.findViewById(R.id.edtCurrentPassword);
        edtNew = view.findViewById(R.id.edtNewPassword);
        edtConfirm = view.findViewById(R.id.edtConfirmPassword);
        btnSave = view.findViewById(R.id.btnSavePassword);

        btnSave.setText(UiText.t(UiTextKey.CHANGE_PASSWORD_SAVE));
        edtCurrent.setHint(UiText.t(UiTextKey.CHANGE_PASSWORD_CURRENT_HINT));
        edtNew.setHint(UiText.t(UiTextKey.CHANGE_PASSWORD_NEW_HINT));
        edtConfirm.setHint(UiText.t(UiTextKey.CHANGE_PASSWORD_CONFIRM_HINT));

        authRepository = new AuthRepository(requireContext());

        btnSave.setOnClickListener(v -> doChangePassword());

        return view;
    }

    private void doChangePassword() {
        String current = edtCurrent.getText() != null ? edtCurrent.getText().toString().trim() : "";
        String nw = edtNew.getText() != null ? edtNew.getText().toString().trim() : "";
        String conf = edtConfirm.getText() != null ? edtConfirm.getText().toString().trim() : "";

        if (current.isEmpty() || nw.isEmpty() || conf.isEmpty()) {
            Toast.makeText(requireContext(), UiText.t(UiTextKey.ERROR_COMMON), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nw.equals(conf)) {
            Toast.makeText(requireContext(), UiText.t(UiTextKey.CHANGE_PASSWORD_MISMATCH), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(requireContext(), UiText.t(UiTextKey.CHANGE_PASSWORD_ERROR), Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        authRepository.changePassword(user.getEmail(), current, nw, new AuthRepository.PasswordChangeCallback() {
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), UiText.t(UiTextKey.CHANGE_PASSWORD_SUCCESS), Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }

            @Override
            public void onError(String message) {
                requireActivity().runOnUiThread(() -> {
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), UiText.t(UiTextKey.CHANGE_PASSWORD_ERROR) + ": " + message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}