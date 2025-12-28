package com.example.localcuisine.ui.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.localcuisine.R;

public class NavExt {

    public static void navigateToFoodDetail(Fragment fragment, int foodId) {
        Bundle args = new Bundle();
        args.putInt("food_id", foodId);

        NavHostFragment.findNavController(fragment)
                .navigate(R.id.action_home_to_detail, args);
    }


}
