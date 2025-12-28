package com.example.localcuisine.ui.common;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;

    public GridSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state
    ) {
        outRect.left = spacing;
        outRect.right = spacing;
        outRect.bottom = spacing;

        // top spacing cho hàng đầu tiên
        if (parent.getChildAdapterPosition(view) < 2) {
            outRect.top = spacing;
        }
    }
}
