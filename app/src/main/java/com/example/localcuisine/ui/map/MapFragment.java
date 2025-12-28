package com.example.localcuisine.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.recommend.RecommendResult;

import java.util.List;

public class MapFragment extends Fragment {

    private MapAdapter adapter;
    private MapViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerRecommend);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MapAdapter(List.of());
        recyclerView.setAdapter(adapter);

        Button btnRecommend = view.findViewById(R.id.btnRecommend);

        viewModel = new MapViewModel(requireContext());

        btnRecommend.setOnClickListener(v -> {
            List<RecommendResult> results = viewModel.recommend();
            adapter.update(results);
        });

        initMap();

        return view;
    }

    private void initMap() {
        // TODO: nhét Google Map vào đây
        // SupportMapFragment mapFragment = ...
    }
}
