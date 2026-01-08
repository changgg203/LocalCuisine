package com.example.localcuisine;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.localcuisine.data.auth.SessionStore;
import com.example.localcuisine.data.auth.UserSessionSync;
import com.example.localcuisine.data.repository.FoodRepository;
import com.example.localcuisine.databinding.ActivityMainBinding;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.ui.auth.LoginActivity;
import com.example.localcuisine.ui.detail.FoodDetailFragment;
import com.example.localcuisine.ui.favorite.FavoriteFragment;
import com.example.localcuisine.ui.home.HomeFragment;
import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.example.localcuisine.ui.notification.NotificationDetailFragment;
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

        SessionStore session = new SessionStore(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (session.getUserRegion() == null) {
            session.setUserRegion(com.example.localcuisine.model.Region.ALL);
        }
        String uid = session.getUserId();
        if (uid != null) {
            UserSessionSync.syncFavorites(this, uid);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setBottomNavText();
        setSupportActionBar(binding.toolbarHome);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // ===== Preload food data =====
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
        String userId = new SessionStore(this).getUserId();

        BadgeDrawable badge =
                binding.navView.getOrCreateBadge(R.id.navigation_notifications);

    }

    public void openNotificationDetail(String title, String content) {
        Fragment f = NotificationDetailFragment.newInstance(title, content);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_container, f)
                .addToBackStack(null)
                .commit();
    }

    private void setBottomNavText() {
        Menu menu = binding.navView.getMenu();
        if (menu == null) return;

        MenuItem home = menu.findItem(R.id.navigation_home);
        MenuItem fav = menu.findItem(R.id.navigation_favorite);
        MenuItem noti = menu.findItem(R.id.navigation_notifications);
        MenuItem profile = menu.findItem(R.id.navigation_profile);

        if (home != null) {
            home.setTitle(UiText.t(UiTextKey.NAV_HOME));
        }
        if (fav != null) {
            fav.setTitle(UiText.t(UiTextKey.NAV_FAVORITE));
        }
        if (noti != null) {
            noti.setTitle(UiText.t(UiTextKey.NAV_NOTIFICATION));
        }
        if (profile != null) {
            profile.setTitle(UiText.t(UiTextKey.NAV_PROFILE));
        }
    }
}
