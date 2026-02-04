package com.example.localcuisine.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.repository.AdminUserRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.example.localcuisine.ui.i18n.UiTextProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Admin - Create / Edit User Profile (Firestore document)
 */
public class AdminUserEditFragment extends Fragment {

    public static final String ARG_UID = "uid";

    private TextInputEditText edtDisplayName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPhone;
    private TextInputEditText edtBio;
    private CheckBox chkIsAdmin;

    private Button btnSave;
    private Button btnCancel;

    private UserProfile editingUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputLayout tilName = view.findViewById(R.id.tilName);
        TextInputLayout tilEmail = view.findViewById(R.id.tilEmail);
        TextInputLayout tilPhone = view.findViewById(R.id.tilPhone);
        TextInputLayout tilBio = view.findViewById(R.id.tilBio);

        edtDisplayName = view.findViewById(R.id.edtDisplayName);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtBio = view.findViewById(R.id.edtBio);
        chkIsAdmin = view.findViewById(R.id.chkIsAdmin);

        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        tilName.setHint(UiTextProvider.get(UiTextKey.EDIT_PROFILE_HINT_NAME));
        tilEmail.setHint(UiTextProvider.get(UiTextKey.EDIT_PROFILE_HINT_EMAIL));

        btnSave.setText(UiTextProvider.get(UiTextKey.ADMIN_SAVE));
        btnCancel.setText(UiTextProvider.get(UiTextKey.ADMIN_CANCEL));

        String uid = getArguments() != null ? getArguments().getString(ARG_UID) : null;

        if (uid != null) {
            editingUser = AdminUserRepository.getInstance().getByUid(uid);
            if (editingUser != null) bindData(editingUser);
        }

        btnSave.setOnClickListener(v -> onSave());
        btnCancel.setOnClickListener(v -> close());
    }

    private void bindData(@NonNull UserProfile u) {
        edtDisplayName.setText(u.displayName != null ? u.displayName : "");
        edtEmail.setText(u.email != null ? u.email : "");
        edtPhone.setText(u.phone != null ? u.phone : "");
        edtBio.setText(u.bio != null ? u.bio : "");
        chkIsAdmin.setChecked(u.isAdmin);
    }

    private void onSave() {
        String name = edtDisplayName.getText() != null ? edtDisplayName.getText().toString().trim() : "";
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
        String bio = edtBio.getText() != null ? edtBio.getText().toString().trim() : "";

        if (TextUtils.isEmpty(name)) {
            edtDisplayName.setError(UiTextProvider.get(UiTextKey.ADMIN_USER_NAME_REQUIRED));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError(UiTextProvider.get(UiTextKey.ADMIN_USER_EMAIL_REQUIRED));
            return;
        }

        btnSave.setEnabled(false);

        UserProfile p = new UserProfile();
        if (editingUser != null) p.uid = editingUser.uid;
        p.displayName = name;
        p.email = email;
        p.phone = phone;
        p.bio = bio;
        p.isAdmin = chkIsAdmin.isChecked();

        AdminUserRepository repo = AdminUserRepository.getInstance();

        if (editingUser == null) {
            repo.add(p, callback(UiTextProvider.get(UiTextKey.ADMIN_USER_ADD_SUCCESS)));
        } else {
            repo.update(p, callback(UiTextProvider.get(UiTextKey.ADMIN_USER_UPDATE_SUCCESS)));
        }
    }

    private AdminUserRepository.ActionCallback callback(String successMsg) {
        return new AdminUserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(requireContext(), successMsg, Toast.LENGTH_SHORT).show();
                close();
            }

            @Override
            public void onError(@NonNull Exception e) {
                btnSave.setEnabled(true);
                Toast.makeText(requireContext(), UiTextProvider.get(UiTextKey.ADMIN_ERROR_COMMON), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void close() {
        if (!isAdded()) return;

        Fragment parent = requireParentFragment();

        // Sau khi thêm / sửa user, cập nhật lại danh sách ngay từ cache
        // để AdminUserListFragment hiển thị dữ liệu mới.
        if (parent instanceof AdminUserListFragment) {
            ((AdminUserListFragment) parent).refreshFromCache();
        }

        parent.getChildFragmentManager().popBackStack();

        View root = parent.getView();
        if (root != null) {
            root.findViewById(R.id.admin_edit_container).setVisibility(View.GONE);
            root.findViewById(R.id.admin_list_container).setVisibility(View.VISIBLE);
        }
    }
}
