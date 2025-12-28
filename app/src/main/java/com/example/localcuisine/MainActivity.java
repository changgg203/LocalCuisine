package com.example.localcuisine;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.data.AppDatabase;
import com.example.localcuisine.data.FoodRepository;
import com.example.localcuisine.data.SessionManager;
import com.example.localcuisine.databinding.ActivityMainBinding;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.ui.auth.LoginActivity;
import com.example.localcuisine.ui.detail.FoodDetailFragment;
import com.example.localcuisine.ui.favorite.FavoriteFragment;
import com.example.localcuisine.ui.home.HomeFragment;
import com.example.localcuisine.ui.notification.NotificationFragment;
import com.example.localcuisine.ui.profile.ProfileFragment;
import com.google.android.material.badge.BadgeDrawable;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int currentTab = R.id.navigation_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbarHome);

        FoodRepository.ensureLoaded(new FoodRepository.LoadCallback() {
            @Override
            public void onSuccess(List<Food> foods) {
            }

            @Override
            public void onError(Exception e) {
            }
        });

        if (savedInstanceState == null) {
            switchTab(R.id.navigation_home);
        }

        updateNotificationBadge();

        binding.navView.setOnItemSelectedListener(item -> {
            switchTab(item.getItemId());
            return true;
        });
    }

    private void switchTab(int tabId) {
        Fragment fragment;

        if (tabId == R.id.navigation_home) {
            fragment = new HomeFragment();
        } else if (tabId == R.id.navigation_favorite) {
            fragment = new FavoriteFragment();
        } else if (tabId == R.id.navigation_notifications) {
            fragment = new NotificationFragment();
        } else if (tabId == R.id.navigation_profile) {
            fragment = new ProfileFragment();
        } else {
            return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_container, fragment)
                .commit();

        currentTab = tabId;
    }

    public void openFoodDetail(int foodId) {
        Fragment fragment = FoodDetailFragment.newInstance(foodId);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nav_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void updateNotificationBadge() {
        String userId = new SessionManager(this).getUserId();

        BadgeDrawable badge =
                binding.navView.getOrCreateBadge(R.id.navigation_notifications);

        new Thread(() -> {
            int count = AppDatabase.getInstance(this)
                    .notificationDao()
                    .getUnreadCount(userId);

            runOnUiThread(() -> {
                badge.setVisible(count > 0);
                badge.setNumber(count);
            });
        }).start();
    }
}
