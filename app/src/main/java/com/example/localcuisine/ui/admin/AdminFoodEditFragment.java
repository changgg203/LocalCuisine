package com.example.localcuisine.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.repository.AdminFoodRepository;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.model.Region;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Admin - Create / Edit Food
 */
public class AdminFoodEditFragment extends Fragment {

    public static final String ARG_FOOD_ID = "food_id";

    private static final String TIME_MORNING = "morning";
    private static final String TIME_NOON = "noon";
    private static final String TIME_EVENING = "evening";

    private static final Map<String, String> BEST_TIME_LABEL_MAP = new HashMap<>();
    private static final Map<String, String> BEST_TIME_VALUE_MAP = new HashMap<>();

    static {
        BEST_TIME_LABEL_MAP.put(TIME_MORNING, "Buổi sáng");
        BEST_TIME_LABEL_MAP.put(TIME_NOON, "Buổi trưa");
        BEST_TIME_LABEL_MAP.put(TIME_EVENING, "Buổi tối");

        // reverse map
        BEST_TIME_VALUE_MAP.put("Buổi sáng", TIME_MORNING);
        BEST_TIME_VALUE_MAP.put("Buổi trưa", TIME_NOON);
        BEST_TIME_VALUE_MAP.put("Buổi tối", TIME_EVENING);
    }

    private final Map<FoodType, CheckBox> foodTypeCheckboxMap = new HashMap<>();

    private EditText edtName;
    private EditText edtDescription;
    private EditText edtTags;
    private EditText edtLocation;
    private EditText edtImageUrl;

    private Spinner spRegion;
    private Spinner spBestTime;

    private LinearLayout layoutFoodTypes;

    private Button btnSave;
    private Button btnCancel;

    private Food editingFood;

    // --------------------------------------------------
    // Lifecycle
    // --------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_admin_food_edit, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        bindViews(view);
        setupSpinners();
        renderFoodTypeCheckboxes();

        int foodId = getArguments() != null
                ? getArguments().getInt(ARG_FOOD_ID, -1)
                : -1;

        if (foodId != -1) {
            editingFood = AdminFoodRepository.getInstance().getById(foodId);
            if (editingFood != null) {
                bindData(editingFood);
            }
        }

        btnSave.setOnClickListener(v -> onSave());
        btnCancel.setOnClickListener(v -> close());
    }

    // --------------------------------------------------
    // View binding
    // --------------------------------------------------

    private void bindViews(View v) {
        edtName = v.findViewById(R.id.edtName);
        edtDescription = v.findViewById(R.id.edtDescription);
        edtTags = v.findViewById(R.id.edtTags);
        edtLocation = v.findViewById(R.id.edtLocation);
        edtImageUrl = v.findViewById(R.id.edtImageUrl);

        spRegion = v.findViewById(R.id.spRegion);
        spBestTime = v.findViewById(R.id.spBestTime);

        layoutFoodTypes = v.findViewById(R.id.layoutFoodTypes);

        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);
    }

    // --------------------------------------------------
    // Spinner setup (QUAN TRỌNG)
    // --------------------------------------------------

    private void setupSpinners() {
        List<String> regions = new ArrayList<>();
        for (Region r : Region.values()) {
            regions.add(r.name());
        }

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                regions
        );
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRegion.setAdapter(regionAdapter);

        List<String> timeLabels = new ArrayList<>(BEST_TIME_VALUE_MAP.keySet());

        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                timeLabels
        );
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBestTime.setAdapter(timeAdapter);
    }


    // --------------------------------------------------
    // FoodType dynamic rendering
    // --------------------------------------------------

    private void renderFoodTypeCheckboxes() {
        layoutFoodTypes.removeAllViews();
        foodTypeCheckboxMap.clear();

        for (FoodType type : FoodType.values()) {
            CheckBox cb = new CheckBox(getContext());
            cb.setText(type.getDisplayName());
            cb.setTag(type);

            layoutFoodTypes.addView(cb);
            foodTypeCheckboxMap.put(type, cb);
        }
    }

    // --------------------------------------------------
    // Bind data (EDIT)
    // --------------------------------------------------

    private void bindData(Food f) {
        edtName.setText(f.getName());
        edtDescription.setText(f.getDescription());
        edtLocation.setText(f.getLocation());
        edtImageUrl.setText(f.getImageUrl());
        edtTags.setText(TextUtils.join(", ", f.getTags()));

        selectSpinner(spRegion, f.getRegion().name());
        String label = BEST_TIME_LABEL_MAP.get(f.getBestTime());
        if (label != null) {
            selectSpinner(spBestTime, label);
        }

        for (FoodType t : f.getTypes()) {
            CheckBox cb = foodTypeCheckboxMap.get(t);
            if (cb != null) cb.setChecked(true);
        }
    }

    // --------------------------------------------------
    // Save
    // --------------------------------------------------

    private void onSave() {
        String name = edtName.getText().toString().trim();
        if (name.isEmpty()) {
            toast("Tên món không được để trống");
            return;
        }

        Object regionObj = spRegion.getSelectedItem();
        Object timeObj = spBestTime.getSelectedItem();

        if (regionObj == null || timeObj == null) {
            toast("Vui lòng chọn khu vực và thời điểm");
            return;
        }

        // Map LABEL (VN) -> VALUE (internal)
        String bestTimeValue = BEST_TIME_VALUE_MAP.get(timeObj.toString());
        if (bestTimeValue == null) {
            toast("Thời điểm không hợp lệ");
            return;
        }

        btnSave.setEnabled(false);

        Food newFood = new Food(
                editingFood != null ? editingFood.getId() : generateId(),
                name,
                edtDescription.getText().toString().trim(),
                Region.valueOf(regionObj.toString()),
                collectFoodTypes(),
                collectTags(),
                bestTimeValue, // ✅ LƯU VALUE, KHÔNG PHẢI LABEL
                edtLocation.getText().toString().trim(),
                edtImageUrl.getText().toString().trim()
        );

        AdminFoodRepository repo = AdminFoodRepository.getInstance();

        if (editingFood == null) {
            repo.add(newFood, callback("Đã thêm món mới"));
        } else {
            repo.update(newFood, callback("Đã cập nhật món"));
        }
    }

    private AdminFoodRepository.ActionCallback callback(String successMsg) {
        return new AdminFoodRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                toast(successMsg);
                close();
            }

            @Override
            public void onError(@NonNull Exception e) {
                btnSave.setEnabled(true);
                toast("Có lỗi xảy ra");
            }
        };
    }

    // --------------------------------------------------
    // Close
    // --------------------------------------------------

    private void close() {
        if (!isAdded()) return;

        requireParentFragment()
                .getChildFragmentManager()
                .popBackStack();

        View root = requireParentFragment().getView();
        if (root != null) {
            root.findViewById(R.id.admin_edit_container)
                    .setVisibility(View.GONE);
            root.findViewById(R.id.admin_list_container)
                    .setVisibility(View.VISIBLE);
        }
    }

    // --------------------------------------------------
    // Helpers
    // --------------------------------------------------

    private Set<FoodType> collectFoodTypes() {
        Set<FoodType> set = EnumSet.noneOf(FoodType.class);
        for (Map.Entry<FoodType, CheckBox> e : foodTypeCheckboxMap.entrySet()) {
            if (e.getValue().isChecked()) {
                set.add(e.getKey());
            }
        }
        return set;
    }

    private List<String> collectTags() {
        List<String> list = new ArrayList<>();
        String raw = edtTags.getText().toString();
        if (raw.isEmpty()) return list;

        for (String t : raw.split(",")) {
            if (!t.trim().isEmpty()) {
                list.add(t.trim());
            }
        }
        return list;
    }

    private void selectSpinner(Spinner sp, String value) {
        for (int i = 0; i < sp.getCount(); i++) {
            if (value.equals(sp.getItemAtPosition(i))) {
                sp.setSelection(i);
                break;
            }
        }
    }

    private int generateId() {
        return (int) System.currentTimeMillis();
    }

    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
