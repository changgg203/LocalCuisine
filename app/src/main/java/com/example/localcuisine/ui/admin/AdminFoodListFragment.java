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
import com.example.localcuisine.data.repository.AdminFoodRepository;
import com.example.localcuisine.model.Food;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * AdminFoodListFragment
 * <p>
 * - List món ăn
 * - Click        → Edit
 * - Long press   → Delete
 */
public class AdminFoodListFragment extends Fragment {

    // =====================
    // Data
    // =====================

    private final List<Food> foods = new ArrayList<>();
    private final AdminFoodRepository repo = AdminFoodRepository.getInstance();

    // =====================
    // Views
    // =====================

    private RecyclerView rvFoods;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private AdminFoodAdapter adapter;

    // =====================
    // Lifecycle
    // =====================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_admin_food_list, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        bindViews(view);
        setupRecyclerView();
        setupActions();
        loadData();
    }

    // =====================
    // Setup
    // =====================

    private void bindViews(View v) {
        rvFoods = v.findViewById(R.id.rvFoods);
        progressBar = v.findViewById(R.id.progressBar);
        fabAdd = v.findViewById(R.id.fabAddFood);
    }

    private void setupRecyclerView() {
        adapter = new AdminFoodAdapter(
                foods,
                food -> openEdit(food.getId()),
                this::confirmDelete
        );

        rvFoods.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFoods.setAdapter(adapter);
    }

    private void setupActions() {
        fabAdd.setOnClickListener(v -> openCreate());
    }

    // =====================
    // Data
    // =====================

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        repo.loadAll(new AdminFoodRepository.LoadCallback() {
            @Override
            public void onSuccess(@NonNull List<Food> result) {
                if (!isAdded()) return;

                foods.clear();
                foods.addAll(result);
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull Exception e) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                toast("Không tải được danh sách món");
            }
        });
    }

    // =====================
    // Navigation
    // =====================

    private void openCreate() {
        openEdit(-1);
    }

    private void openEdit(int foodId) {
        Bundle args = new Bundle();
        args.putInt(AdminFoodEditFragment.ARG_FOOD_ID, foodId);

        Fragment editFragment = new AdminFoodEditFragment();
        editFragment.setArguments(args);

        View root = getView();
        if (root == null) return;

        root.findViewById(R.id.admin_list_container)
                .setVisibility(View.GONE);

        View editContainer = root.findViewById(R.id.admin_edit_container);
        editContainer.setVisibility(View.VISIBLE);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_edit_container, editFragment)
                .addToBackStack("admin_edit")
                .commit();
    }

    // =====================
    // Delete
    // =====================

    private void confirmDelete(@NonNull Food food) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xoá món ăn")
                .setMessage("Bạn có chắc muốn xoá \"" + food.getName() + "\"?")
                .setPositiveButton("Xoá", (d, w) -> deleteFood(food))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void deleteFood(@NonNull Food food) {
        progressBar.setVisibility(View.VISIBLE);

        repo.delete(food.getId(), new AdminFoodRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                if (!isAdded()) return;

                toast("Đã xoá món");
                loadData();
            }

            @Override
            public void onError(@NonNull Exception e) {
                if (!isAdded()) return;

                progressBar.setVisibility(View.GONE);
                toast("Không thể xoá món");
            }
        });
    }

    // =====================
    // Utils
    // =====================

    private void toast(String msg) {
        if (!isAdded()) return;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
