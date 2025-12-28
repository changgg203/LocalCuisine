package com.example.localcuisine.ui.detail;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localcuisine.MainActivity;
import com.example.localcuisine.R;
import com.example.localcuisine.data.FoodRepository;
import com.example.localcuisine.data.NotificationHelper;
import com.example.localcuisine.data.SessionManager;
import com.example.localcuisine.data.favorite.FavoriteRepository;
import com.example.localcuisine.data.favorite.FirestoreFavoriteRepository;
import com.example.localcuisine.data.review.ReviewDataSource;
import com.example.localcuisine.data.review.ReviewRepository;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.model.Reply;
import com.example.localcuisine.model.Review;
import com.example.localcuisine.ui.home.FoodAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FoodDetailFragment extends Fragment {

    private final List<Review> reviewList = new ArrayList<>();
    private int foodId;
    private SessionManager sessionManager;
    private ReviewRepository reviewRepo;
    private FavoriteRepository favoriteRepo;
    private String uid;
    private ReviewAdapter reviewAdapter;

    private RatingBar ratingSummaryBar;
    private TextView txtRatingCount;
    private Button btnFavorite;

    // ===================== FACTORY =====================

    public static FoodDetailFragment newInstance(int foodId) {
        FoodDetailFragment f = new FoodDetailFragment();
        Bundle b = new Bundle();
        b.putInt("food_id", foodId);
        f.setArguments(b);
        return f;
    }

    // ===================== LIFECYCLE =====================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_food_detail, container, false);

        sessionManager = new SessionManager(requireContext());
        reviewRepo = ReviewRepository.getInstance(requireContext());
        favoriteRepo = new FirestoreFavoriteRepository();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        foodId = getArguments() != null
                ? getArguments().getInt("food_id", -1)
                : -1;

        setupReviewSection(view);

        FoodRepository.ensureLoaded(new FoodRepository.LoadCallback() {
            @Override
            public void onSuccess(@NonNull List<Food> foods) {
                if (!isAdded()) return;

                Food food = FoodRepository.getFoodById(foodId);
                if (food == null) return;

                bindFood(view, food);
                bindRecommend(view, foods, food);
            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    // ===================== FOOD INFO =====================

    @SuppressLint("SetTextI18n")
    private void bindFood(@NonNull View view, @NonNull Food food) {
        requireActivity().setTitle(food.getName());

        ImageView imgHeader = view.findViewById(R.id.imgHeader);
        String imageUrl = food.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load("file:///android_asset/" + imageUrl)
                    .placeholder(R.drawable.placeholder_food)
                    .error(R.drawable.placeholder_food)
                    .centerCrop()
                    .into(imgHeader);
        } else {
            imgHeader.setImageResource(R.drawable.placeholder_food);
        }

        TextView txtName = view.findViewById(R.id.txtFoodName);
        TextView txtMeta = view.findViewById(R.id.txtMeta);
        TextView txtDesc = view.findViewById(R.id.txtFoodDescription);
        ChipGroup chipGroupTags = view.findViewById(R.id.chipGroupTags);

        btnFavorite = view.findViewById(R.id.btnFavorite);
        Button btnReview = view.findViewById(R.id.btnReview);
        TextView txtRateHint = view.findViewById(R.id.txtRateHint);

        ratingSummaryBar = view.findViewById(R.id.ratingBar);
        txtRatingCount = view.findViewById(R.id.txtRatingCount);

        txtName.setText(food.getName());

        FoodType mainType = food.getTypes().stream().findFirst().orElse(null);
        txtMeta.setText(
                food.getRegion().getDisplayName()
                        + " • "
                        + (mainType != null ? mainType.getDisplayName() : "")
                        + " • "
                        + food.getLocation()
        );

        txtDesc.setText(food.getDescription());

        chipGroupTags.removeAllViews();
        for (String tag : food.getTags()) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(false);
            chipGroupTags.addView(chip);
        }

        updateFavoriteButton();

        btnFavorite.setOnClickListener(v -> toggleFavorite());

        txtRateHint.setOnClickListener(v -> showReviewDialog());
        btnReview.setOnClickListener(v -> showReviewDialog());

        loadReviews();
    }

    // ===================== FAVORITE =====================

    private void toggleFavorite() {
        if (uid == null) {
            Toast.makeText(requireContext(),
                    "Bạn cần đăng nhập",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        favoriteRepo.isFavorite(uid, foodId, new FavoriteRepository.BooleanCallback() {
            @Override
            public void onResult(boolean isFav) {
                if (isFav) {
                    favoriteRepo.removeFavorite(uid, foodId, new FavoriteRepository.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            updateFavoriteButton();
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    favoriteRepo.addFavorite(uid, foodId, new FavoriteRepository.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            updateFavoriteButton();
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updateFavoriteButton() {
        if (uid == null || btnFavorite == null) {
            btnFavorite.setText("❤️ Yêu thích");
            return;
        }

        favoriteRepo.isFavorite(uid, foodId, new FavoriteRepository.BooleanCallback() {
            @Override
            public void onResult(boolean isFavorite) {
                btnFavorite.setText(
                        isFavorite ? "💔 Bỏ yêu thích" : "❤️ Yêu thích"
                );
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ===================== REVIEW =====================

    private void setupReviewSection(@NonNull View view) {
        reviewAdapter = new ReviewAdapter(reviewList, this::showReplyDialog);

        RecyclerView rvReviews = view.findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReviews.setAdapter(reviewAdapter);
    }

    private void loadReviews() {
        reviewRepo.getDataSource()
                .loadReviews(foodId, new ReviewDataSource.LoadCallback() {
                    @Override
                    public void onSuccess(List<Review> reviews) {
                        reviewList.clear();
                        reviewList.addAll(reviews);
                        reviewAdapter.notifyDataSetChanged();
                        updateAverageRating();
                    }

                    @Override
                    public void onError(Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void showReviewDialog() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(),
                    "Bạn cần đăng nhập để đánh giá",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        View dialog = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_write_review, null);

        RatingBar ratingInput = dialog.findViewById(R.id.ratingInput);
        EditText edtComment = dialog.findViewById(R.id.edtComment);
        String currentUserId = sessionManager.getUserId();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đánh giá món ăn")
                .setView(dialog)
                .setPositiveButton("Gửi", (d, w) -> {
                    float rating = ratingInput.getRating();
                    String comment = edtComment.getText().toString().trim();
                    if (rating <= 0f) return;

                    Review review = new Review(foodId, rating, comment, currentUserId);
                    reviewRepo.getDataSource().addReview(review, new ReviewDataSource.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            loadReviews();
                        }

                        @Override
                        public void onError(Throwable t) {
                            t.printStackTrace();
                        }
                    });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void showReplyDialog(@NonNull Review review) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(requireContext(),
                    "Bạn cần đăng nhập để phản hồi",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        View dialog = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_reply, null);

        EditText edtReply = dialog.findViewById(R.id.edtReply);
        String currentUserId = sessionManager.getUserId();

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Phản hồi")
                .setView(dialog)
                .setPositiveButton("Gửi", (d, w) -> {
                    String content = edtReply.getText().toString().trim();
                    if (content.isEmpty()) return;

                    Reply reply = new Reply(review.getId(), content, currentUserId);
                    reviewRepo.getDataSource()
                            .addReply(foodId, review.getId(), review.getAuthorId(), reply, new ReviewDataSource.ActionCallback() {
                                @Override
                                public void onSuccess() {
                                    loadReviews();

                                    if (!currentUserId.equals(review.getAuthorId())) {
                                        Food food = FoodRepository.getFoodById(foodId);
                                        if (food != null) {
                                            NotificationHelper.notifyReply(
                                                    requireContext(),
                                                    food.getName(),
                                                    content
                                            );
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void updateAverageRating() {
        if (reviewList.isEmpty()) {
            ratingSummaryBar.setRating(0f);
            txtRatingCount.setText("(0)");
            return;
        }

        float sum = 0f;
        for (Review r : reviewList) sum += r.getRating();

        ratingSummaryBar.setRating(sum / reviewList.size());
        txtRatingCount.setText("(" + reviewList.size() + ")");
    }

    // ===================== RECOMMEND =====================

    private void bindRecommend(
            @NonNull View view,
            @NonNull List<Food> allFoods,
            @NonNull Food food
    ) {
        RecyclerView recyclerRecommend = view.findViewById(R.id.recyclerRecommend);
        recyclerRecommend.setLayoutManager(
                new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        );

        List<Food> recommendList = new ArrayList<>();
        for (Food f : allFoods) {
            if (f.getId() == foodId) continue;
            if (f.getRegion() == food.getRegion()
                    || !Collections.disjoint(f.getTypes(), food.getTypes())) {
                recommendList.add(f);
            }
            if (recommendList.size() >= 6) break;
        }

        recyclerRecommend.setAdapter(
                new FoodAdapter(
                        recommendList,
                        id -> ((MainActivity) requireActivity()).openFoodDetail(id)
                )
        );
    }
}
