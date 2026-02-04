package com.example.localcuisine.ui.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.R;
import com.example.localcuisine.data.repository.AdminFoodRepository;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.model.Region;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.example.localcuisine.ui.i18n.UiTextProvider;
import com.google.android.material.textfield.TextInputLayout;

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

    // =====================
    // Best time (internal)
    // =====================

    private static final String TIME_MORNING = "morning";
    private static final String TIME_NOON = "noon";
    private static final String TIME_EVENING = "evening";

    /**
     * internal value -> UiTextKey
     */
    private static final Map<String, UiTextKey> BEST_TIME_KEY_MAP = new HashMap<>();
    /**
     * Region -> UiTextKey
     */
    private static final Map<Region, UiTextKey> REGION_KEY_MAP = new HashMap<>();

    static {
        BEST_TIME_KEY_MAP.put(TIME_MORNING, UiTextKey.ADMIN_BEST_TIME_MORNING);
        BEST_TIME_KEY_MAP.put(TIME_NOON, UiTextKey.ADMIN_BEST_TIME_NOON);
        BEST_TIME_KEY_MAP.put(TIME_EVENING, UiTextKey.ADMIN_BEST_TIME_EVENING);
    }

    static {
        REGION_KEY_MAP.put(Region.NORTH, UiTextKey.REGION_NORTH);
        REGION_KEY_MAP.put(Region.CENTRAL, UiTextKey.REGION_CENTRAL);
        REGION_KEY_MAP.put(Region.SOUTH, UiTextKey.REGION_SOUTH);
    }

    // =====================
    // Views
    // =====================

    private final Map<FoodType, CheckBox> foodTypeCheckboxMap = new HashMap<>();

    private EditText edtName;
    private EditText edtDescription;
    private EditText edtTags;
    private EditText edtLocation;
    private EditText edtImageUrl;

    private RadioGroup rgRegion;
    private RadioGroup rgBestTime;

    private LinearLayout layoutFoodTypes;

    private Button btnSave;
    private Button btnCancel;

    private Food editingFood;

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
        return inflater.inflate(R.layout.fragment_admin_food_edit, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        bindViews(view);

        renderRegionRadios();
        renderBestTimeRadios();
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


    private void bindViews(@NonNull View v) {
        TextInputLayout tilName = v.findViewById(R.id.tilName);
        TextInputLayout tilDescription = v.findViewById(R.id.tilDescription);
        TextInputLayout tilLocation = v.findViewById(R.id.tilLocation);
        TextInputLayout tilTags = v.findViewById(R.id.tilTags);
        TextInputLayout tilImage = v.findViewById(R.id.tilImage);

        tilName.setHint(UiTextProvider.get(UiTextKey.ADMIN_FOOD_HINT_NAME));
        tilDescription.setHint(UiTextProvider.get(UiTextKey.ADMIN_FOOD_HINT_DESCRIPTION));
        tilLocation.setHint(UiTextProvider.get(UiTextKey.ADMIN_FOOD_HINT_LOCATION));
        tilTags.setHint(UiTextProvider.get(UiTextKey.ADMIN_FOOD_HINT_TAGS));
        tilImage.setHint(UiTextProvider.get(UiTextKey.ADMIN_FOOD_HINT_IMAGE));

        ((TextView) v.findViewById(R.id.tvRegionLabel))
                .setText(UiTextProvider.get(UiTextKey.ADMIN_REGION_LABEL));

        ((TextView) v.findViewById(R.id.tvBestTimeLabel))
                .setText(UiTextProvider.get(UiTextKey.ADMIN_BEST_TIME_LABEL));

        ((TextView) v.findViewById(R.id.tvFoodTypeLabel))
                .setText(UiTextProvider.get(UiTextKey.ADMIN_FOOD_TYPE_LABEL));

        ((TextView) v.findViewById(R.id.tvImageLabel))
                .setText(UiTextProvider.get(UiTextKey.ADMIN_IMAGE_LABEL));

        edtName = v.findViewById(R.id.edtName);
        edtDescription = v.findViewById(R.id.edtDescription);
        edtTags = v.findViewById(R.id.edtTags);
        edtLocation = v.findViewById(R.id.edtLocation);
        edtImageUrl = v.findViewById(R.id.edtImageUrl);
        rgRegion = v.findViewById(R.id.rgRegion);
        rgBestTime = v.findViewById(R.id.rgBestTime);

        layoutFoodTypes = v.findViewById(R.id.layoutFoodTypes);

        btnSave = v.findViewById(R.id.btnSave);
        btnCancel = v.findViewById(R.id.btnCancel);

        btnSave.setText(UiTextProvider.get(UiTextKey.ADMIN_SAVE));
        btnCancel.setText(UiTextProvider.get(UiTextKey.ADMIN_CANCEL));
    }

    // =====================
    // Render
    // =====================

    private void renderRegionRadios() {
        rgRegion.removeAllViews();

        for (Region r : Region.values()) {
            RadioButton rb = new RadioButton(getContext());

            UiTextKey key = REGION_KEY_MAP.get(r);
            rb.setText(
                    key != null
                            ? UiTextProvider.get(key)
                            : r.name()
            );

            rb.setTag(r);
            rgRegion.addView(rb);
        }
    }

    private void renderBestTimeRadios() {
        rgBestTime.removeAllViews();

        for (Map.Entry<String, UiTextKey> e : BEST_TIME_KEY_MAP.entrySet()) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(UiTextProvider.get(e.getValue()));
            rb.setTag(e.getKey());
            rgBestTime.addView(rb);
        }
    }

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

    // =====================
    // Bind data
    // =====================

    private void bindData(@NonNull Food f) {
        edtName.setText(f.getName());
        edtDescription.setText(f.getDescription());
        edtLocation.setText(f.getLocation());
        edtImageUrl.setText(f.getImageUrl());
        edtTags.setText(TextUtils.join(", ", f.getTags()));

        for (int i = 0; i < rgRegion.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgRegion.getChildAt(i);
            if (rb.getTag() == f.getRegion()) {
                rb.setChecked(true);
                break;
            }
        }

        for (int i = 0; i < rgBestTime.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rgBestTime.getChildAt(i);
            if (f.getBestTime().equals(rb.getTag())) {
                rb.setChecked(true);
                break;
            }
        }

        for (FoodType t : f.getTypes()) {
            CheckBox cb = foodTypeCheckboxMap.get(t);
            if (cb != null) cb.setChecked(true);
        }
    }

    // =====================
    // Save
    // =====================

    private void onSave() {
        String name = edtName.getText().toString().trim();
        if (name.isEmpty()) {
            toast(UiTextProvider.get(UiTextKey.ADMIN_FOOD_NAME_REQUIRED));
            return;
        }

        int regionId = rgRegion.getCheckedRadioButtonId();
        int timeId = rgBestTime.getCheckedRadioButtonId();

        if (regionId == -1 || timeId == -1) {
            toast(UiTextProvider.get(
                    UiTextKey.ADMIN_FOOD_REGION_TIME_REQUIRED));
            return;
        }

        RadioButton rbRegion = rgRegion.findViewById(regionId);
        RadioButton rbTime = rgBestTime.findViewById(timeId);

        Region region = (Region) rbRegion.getTag();
        String bestTimeValue = rbTime.getTag().toString();

        btnSave.setEnabled(false);

        Food newFood = new Food(
                editingFood != null ? editingFood.getId() : generateId(),
                name,
                edtDescription.getText().toString().trim(),
                region,
                collectFoodTypes(),
                collectTags(),
                bestTimeValue,
                edtLocation.getText().toString().trim(),
                edtImageUrl.getText().toString().trim()
        );

        AdminFoodRepository repo = AdminFoodRepository.getInstance();

        if (editingFood == null) {
            repo.add(newFood, callback(
                    UiTextProvider.get(UiTextKey.ADMIN_FOOD_ADD_SUCCESS)));
        } else {
            repo.update(newFood, callback(
                    UiTextProvider.get(UiTextKey.ADMIN_FOOD_UPDATE_SUCCESS)));
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
                toast(UiTextProvider.get(UiTextKey.ADMIN_ERROR_COMMON));
            }
        };
    }

    // =====================
    // Close
    // =====================

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

    // =====================
    // Helpers
    // =====================

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

    private int generateId() {
        return (int) System.currentTimeMillis();
    }

    private void toast(@NonNull String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
