package com.example.localcuisine.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.data.repository.AdminUserRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.example.localcuisine.ui.i18n.UiTextProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class AdminUserListFragment extends Fragment {

    private final List<UserProfile> users = new ArrayList<>();
    private final AdminUserRepository repo = AdminUserRepository.getInstance();

    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private AdminUserAdapter adapter;

    private View errorContainer;
    private TextView tvError;
    private Button btnRetry;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvUsers = view.findViewById(R.id.rvUsers);
        progressBar = view.findViewById(R.id.progressBar);
        fabAdd = view.findViewById(R.id.fabAddUser);

        adapter = new AdminUserAdapter(
                users,
                user -> openEdit(user.uid),
                user -> confirmDelete(user)
        );

        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> openCreate());

        errorContainer = view.findViewById(R.id.errorContainer);
        tvError = view.findViewById(R.id.tvError);
        btnRetry = view.findViewById(R.id.btnRetry);

        btnRetry.setOnClickListener(v -> {
            errorContainer.setVisibility(View.GONE);
            loadData(true); // force refresh khi retry
        });

        loadData();
    }

    private void loadData() {
        loadData(false);
    }

    private void loadData(boolean forceRefresh) {
        progressBar.setVisibility(View.VISIBLE);
        repo.loadAll(forceRefresh, new AdminUserRepository.LoadCallback() {
            @Override
            public void onSuccess(@NonNull List<UserProfile> result) {
                if (!isAdded()) return;
                users.clear();
                users.addAll(result);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                if (errorContainer != null) errorContainer.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                // Log and show detailed error to help debugging Firestore rules / permission problems
                android.util.Log.e("AdminUserList", "Failed to load users", e);

                String msg = UiTextProvider.get(UiTextKey.ADMIN_LOAD_ERROR) +
                        (e.getMessage() != null ? (": " + e.getMessage()) : "");

                // set error UI
                if (tvError != null) tvError.setText(msg);
                if (errorContainer != null) errorContainer.setVisibility(View.VISIBLE);

                // quick toast for immediate feedback
                toast(msg);

                // If permission denied, show help dialog
                String em = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
                if (em.contains("permission-denied") || e instanceof com.google.firebase.firestore.FirebaseFirestoreException && ((com.google.firebase.firestore.FirebaseFirestoreException) e).getCode() == com.google.firebase.firestore.FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                    showPermissionHelpDialog(msg);
                }
            }
        });
    }

    /**
     * Gọi từ AdminUserEditFragment sau khi thêm / sửa user,
     * dùng cache hiện tại của AdminUserRepository để cập nhật list
     * mà không cần bấm nút reload hay mở lại màn hình.
     */
    public void refreshFromCache() {
        if (!isAdded()) return;
        users.clear();
        users.addAll(repo.getAllCached());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        if (errorContainer != null) {
            errorContainer.setVisibility(View.GONE);
        }
    }

    private void openCreate() {
        openEdit(null);
    }

    private void openEdit(String uid) {
        Bundle args = new Bundle();
        args.putString(AdminUserEditFragment.ARG_UID, uid);

        Fragment editFragment = new AdminUserEditFragment();
        editFragment.setArguments(args);

        View root = getView();
        if (root == null) return;

        root.findViewById(R.id.admin_list_container).setVisibility(View.GONE);
        View editContainer = root.findViewById(R.id.admin_edit_container);
        editContainer.setVisibility(View.VISIBLE);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_edit_container, editFragment)
                .addToBackStack("admin_edit")
                .commit();
    }

    private void confirmDelete(UserProfile user) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(UiTextProvider.get(UiTextKey.ADMIN_DELETE_TITLE))
                .setMessage(String.format(UiTextProvider.get(UiTextKey.ADMIN_DELETE_MESSAGE), user.displayName != null ? user.displayName : user.email))
                .setPositiveButton(UiTextProvider.get(UiTextKey.ADMIN_DELETE_CONFIRM), (d, w) -> deleteUser(user))
                .setNegativeButton(UiTextProvider.get(UiTextKey.ADMIN_DELETE_CANCEL), null)
                .show();
    }

    private void deleteUser(UserProfile user) {
        progressBar.setVisibility(View.VISIBLE);
        repo.delete(user.uid, new AdminUserRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;
                toast(UiTextProvider.get(UiTextKey.ADMIN_USER_DELETE_SUCCESS));
                loadData(true);
            }

            @Override
            public void onError(@NonNull Exception e) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                toast(UiTextProvider.get(UiTextKey.ADMIN_USER_DELETE_ERROR));
            }
        });
    }

    private void showPermissionHelpDialog(String errorMsg) {
        String helpMsg = "Firestore permission denied when listing users.\n\n" +
                "If you want admins to list users, set a custom claim 'admin' for the admin user and update Firestore rules to allow list only for admins.\n\n" +
                "Example (Node Admin SDK):\nadmin.auth().setCustomUserClaims('<UID>', { admin: true })\n\n" +
                "Then update rules:\nmatch /users/{userId} { allow list: if request.auth.token.admin == true; }\n\n" +
                "Error detail: " + errorMsg;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Failed to load users")
                .setMessage(helpMsg)
                .setPositiveButton("Copy snippet", (d, w) -> {
                    android.content.ClipboardManager cm = (android.content.ClipboardManager) requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("adminSnippet", "admin.auth().setCustomUserClaims('<UID>', { admin: true })");
                    if (cm != null) cm.setPrimaryClip(clip);
                    toast("Snippet copied to clipboard");
                })
                .setNegativeButton("Close", null)
                .setNeutralButton("Retry", (d, w) -> loadData(true))
                .show();
    }

    private void toast(String msg) {
        if (!isAdded()) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
