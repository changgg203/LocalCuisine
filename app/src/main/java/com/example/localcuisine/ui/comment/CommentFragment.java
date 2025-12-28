package com.example.localcuisine.ui.comment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.data.AppDatabase;
import com.example.localcuisine.data.entity.CommentEntity;
import com.example.localcuisine.util.CommentNode;
import com.example.localcuisine.util.CommentTreeBuilder;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        RecyclerView rv = view.findViewById(R.id.rvComments);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        new Thread(() -> {
            List<CommentEntity> list =
                    AppDatabase.getInstance(requireContext())
                            .commentDao()
                            .getCommentsByFood(1); // foodId demo

            List<CommentNode> roots = CommentTreeBuilder.buildTree(list);
            List<CommentItem> items = new ArrayList<>();
            for (CommentNode r : roots) {
                CommentTreeBuilder.flatten(r, 0, items);
            }

            requireActivity().runOnUiThread(() ->
                    rv.setAdapter(new CommentAdapter(items))
            );
        }).start();
    }
}
