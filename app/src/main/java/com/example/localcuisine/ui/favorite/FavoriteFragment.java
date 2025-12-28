package com.example.localcuisine.ui.favorite;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.MainActivity;
import com.example.localcuisine.R;
import com.example.localcuisine.data.FoodRepository;
import com.example.localcuisine.data.favorite.FavoriteRepository;
import com.example.localcuisine.data.favorite.FirestoreFavoriteRepository;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.ui.home.FoodAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteFragment extends Fragment {

    private final List<Food> favoriteFoods = new ArrayList<>();

    private RecyclerView recyclerView;
    private TextView txtEmpty;
    private FoodAdapter adapter;

    private FavoriteRepository favoriteRepo;
    private String uid;

    // ===================== LIFECYCLE =====================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recyclerFood);
        txtEmpty = view.findViewById(R.id.txtEmpty);
        AutoCompleteTextView edtSearch = view.findViewById(R.id.edtSearch);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new FoodAdapter(
                new ArrayList<>(),
                foodId -> ((MainActivity) requireActivity()).openFoodDetail(foodId)
        );
        recyclerView.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterByQuery(s.toString());
                updateEmptyState();
            }
        });

        favoriteRepo = new FirestoreFavoriteRepository();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        loadFavorites();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    // ===================== DATA =====================

    private void loadFavorites() {
        if (uid == null) {
            favoriteFoods.clear();
            adapter.setData(favoriteFoods);
            updateEmptyState();
            return;
        }

        FoodRepository.ensureLoaded(new FoodRepository.LoadCallback() {
            @Override
            public void onSuccess(@NonNull List<Food> allFoods) {
                if (!isAdded()) return;

                favoriteRepo.getAllFavoriteIds(
                        uid,
                        new FavoriteRepository.ListCallback() {
                            @Override
                            public void onResult(@NonNull List<Integer> ids) {
                                favoriteFoods.clear();

                                Set<Integer> idSet = new HashSet<>(ids);
                                for (Food f : allFoods) {
                                    if (idSet.contains(f.getId())) {
                                        favoriteFoods.add(f);
                                    }
                                }

                                adapter.setData(favoriteFoods);
                                adapter.filterByQuery(null);
                                updateEmptyState();
                            }

                            @Override
                            public void onError(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ===================== UI =====================

    private void updateEmptyState() {
        boolean isEmpty = adapter.getItemCount() == 0;
        txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
