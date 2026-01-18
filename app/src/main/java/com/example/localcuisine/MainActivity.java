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
import com.example.localcuisine.data.repository.UserRepository;
import com.example.localcuisine.data.user.UserProfile;
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

/**
 * MainActivity
 * <p>
 * - Bootstrap user role (ADMIN / USER) trước khi setup UI
 * - Admin: chỉ có Profile
 * - User: full navigation
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionStore session;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionStore(this);

        // ===== Auth check =====
        if (!session.isLoggedIn()) {
            redirectToLogin();
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

        setSupportActionBar(binding.toolbarHome);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setBottomNavText();

        bootstrapUserRole(savedInstanceState);
    }

    /**
     * Load UserProfile để xác định role
     * -> SAU KHI BIẾT ROLE mới setup navigation
     */
    private void bootstrapUserRole(Bundle savedInstanceState) {
        new UserRepository().loadMyProfile(new UserRepository.LoadProfileCallback() {
            @Override
            public void onSuccess(UserProfile profile) {
                isAdmin = profile.isAdmin;
                setupAfterRoleResolved(savedInstanceState);
            }

            @Override
            public void onNotFound() {
                isAdmin = false;
                setupAfterRoleResolved(savedInstanceState);
            }

            @Override
            public void onError(Exception e) {
                isAdmin = false;
                setupAfterRoleResolved(savedInstanceState);
            }
        });
    }

    /**
     * Chỉ chạy SAU KHI đã biết isAdmin
     */
    private void setupAfterRoleResolved(Bundle savedInstanceState) {
        // ===== Preload data (user only) =====
        if (!isAdmin) {
            FoodRepository.ensureLoaded(new FoodRepository.LoadCallback() {
                @Override
                public void onSuccess(List<Food> foods) {
                }

                @Override
                public void onError(Exception e) {
                }
            });
        }

        // ===== Setup navigation =====
        if (isAdmin) {
            setupAdminMode();
        } else {
            setupUserMode();
        }

        if (savedInstanceState == null) {
            switchTab(isAdmin
                    ? R.id.navigation_profile
                    : R.id.navigation_home);
        }

        updateNotificationBadge();
    }


    private void setupAdminMode() {
        Menu menu = binding.navView.getMenu();
        if (menu == null) return;

        menu.findItem(R.id.navigation_home).setVisible(false);
        menu.findItem(R.id.navigation_favorite).setVisible(false);
        menu.findItem(R.id.navigation_notifications).setVisible(false);
        menu.findItem(R.id.navigation_profile).setVisible(true);

        binding.navView.removeBadge(R.id.navigation_notifications);

        binding.navView.setOnItemSelectedListener(item -> {
            switchTab(R.id.navigation_profile);
            return true;
        });
    }

    private void setupUserMode() {
        binding.navView.setOnItemSelectedListener(item -> {
            switchTab(item.getItemId());
            return true;
        });
    }


    private void switchTab(int tabId) {
        if (isAdmin && tabId != R.id.navigation_profile) {
            return;
        }

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
    }

    public void openFoodDetail(int foodId) {
        if (isAdmin) return;

        Fragment fragment = FoodDetailFragment.newInstance(foodId);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nav_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void openNotificationDetail(String title, String content) {
        if (isAdmin) return;

        Fragment fragment = NotificationDetailFragment.newInstance(title, content);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_container, fragment)
                .addToBackStack(null)
                .commit();
    }


    public void updateNotificationBadge() {
        if (isAdmin) return;

        BadgeDrawable badge =
                binding.navView.getOrCreateBadge(R.id.navigation_notifications);

        // TODO: set number / visibility
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


    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
