package com.example.localcuisine.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.MainActivity;
import com.example.localcuisine.R;
import com.example.localcuisine.data.auth.SessionStore;
import com.example.localcuisine.data.chatbot.GeminiService;
import com.example.localcuisine.data.recommend.core.RecommendationContext;
import com.example.localcuisine.data.recommend.core.RecommendationResult;
import com.example.localcuisine.data.recommend.core.RecommenderEngine;
import com.example.localcuisine.data.recommend.signal.PreferenceTracker;
import com.example.localcuisine.data.repository.FoodRepository;
import com.example.localcuisine.data.user.UserProfile;
import com.example.localcuisine.model.ChatMessage;
import com.example.localcuisine.model.Food;
import com.example.localcuisine.model.FoodType;
import com.example.localcuisine.model.Region;
import com.example.localcuisine.ui.chatbot.ChatbotAdapter;
import com.example.localcuisine.ui.common.GridSpacingItemDecoration;
import com.example.localcuisine.ui.i18n.UiText;
import com.example.localcuisine.ui.i18n.UiTextKey;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeFragment extends Fragment {

    private final Map<Integer, RecommendationResult> explainMap = new HashMap<>();
    private FoodAdapter adapter;
    private ArrayAdapter<String> suggestAdapter;
    private RecommenderEngine recommenderEngine;
    
    // Chatbot components
    private GeminiService geminiService;
    private ChatbotAdapter chatbotAdapter;
    private View chatContainer;
    private RecyclerView recyclerChatMessages;
    private EditText edtChatInput;
    private View btnSendMessage;
    private ImageButton btnToggleChatbot;
    private ProgressBar progressChatbot;
    private boolean isChatbotExpanded = false; // Mặc định thu gọn

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarHome);
        toolbar.setSubtitle(UiText.t(UiTextKey.HOME_SUBTITLE));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerFood);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        int spacing = (int) (getResources().getDisplayMetrics().density * 6);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spacing));

        recommenderEngine = new RecommenderEngine();

        adapter = new FoodAdapter(
                new ArrayList<>(),
                foodId -> {
                    Food f = FoodRepository.getFoodById(foodId);
                    if (f != null) {
                        PreferenceTracker.onFoodClick(requireContext(), f);
                    }
                    ((MainActivity) requireActivity()).openFoodDetail(foodId);
                },
                foodId -> {
                    RecommendationResult r = explainMap.get(foodId);
                    return r != null ? r.getReasonText() : null;
                }
        );

        recyclerView.setAdapter(adapter);

        AutoCompleteTextView edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setHint(UiText.t(UiTextKey.HOME_SEARCH_HINT));

        List<String> suggestions = new ArrayList<>();
        suggestAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                suggestions
        );
        edtSearch.setAdapter(suggestAdapter);

        edtSearch.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            adapter.filterByQuery(selected);
        });

        ChipGroup chipGroupTypes = view.findViewById(R.id.chipGroupFilters);
        chipGroupTypes.removeAllViews();

        for (FoodType type : FoodType.values()) {
            Chip chip = new Chip(requireContext());
            chip.setText(type.getDisplayName());
            chip.setCheckable(true);
            chip.setTag(type);
            chipGroupTypes.addView(chip);
        }

        chipGroupTypes.setOnCheckedStateChangeListener((group, checkedIds) -> {
            List<FoodType> selectedTypes = new ArrayList<>();
            for (int id : checkedIds) {
                Chip chip = group.findViewById(id);
                if (chip != null && chip.getTag() instanceof FoodType) {
                    selectedTypes.add((FoodType) chip.getTag());
                }
            }
            adapter.filterByTypes(selectedTypes);
        });

        ChipGroup chipGroupRegions = view.findViewById(R.id.chipGroupRegions);
        chipGroupRegions.removeAllViews();

        for (Region region : Region.values()) {
            if (region != Region.ALL) {
                Chip chip = new Chip(requireContext());
                chip.setText(region.getDisplayName());
                chip.setCheckable(true);
                chip.setTag(region.name());
                chipGroupRegions.addView(chip);
                chip.setChipBackgroundColorResource(R.color.brand_orange_dark);
                chip.setChipStrokeColorResource(R.color.white);
                chip.setTextColor(getResources().getColor(R.color.white, null));
            }
        }

        chipGroupRegions.setOnCheckedStateChangeListener((group, checkedIds) -> {
            String region = null;
            if (!checkedIds.isEmpty()) {
                Chip chip = group.findViewById(checkedIds.get(0));
                if (chip != null && chip.getTag() instanceof String) {
                    region = (String) chip.getTag();
                }
            }
            adapter.filterByRegion(region);
        });

        FoodRepository.ensureLoaded(new FoodRepository.LoadCallback() {
            @Override
            public void onSuccess(@NonNull List<Food> loadedFoods) {
                if (!isAdded()) return;

                List<Food> recommendedFoods = recommendFoods(loadedFoods);
                adapter.setData(recommendedFoods);

                suggestions.clear();
                for (Food food : loadedFoods) {
                    suggestions.add(food.getName());
                }
                suggestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(@NonNull Exception e) {
                if (!isAdded()) return;

                List<Food> fallback = FoodRepository.getAllFoods();
                adapter.setData(fallback);

                suggestions.clear();
                for (Food food : fallback) {
                    suggestions.add(food.getName());
                }
                suggestAdapter.notifyDataSetChanged();
            }
        });

        // Chatbot widget removed from Home (use FAB to open full chat)

        return view;
    }
    
    // Chatbot widget removed from Home; logic moved to full-screen ChatFragment.
    // The setup method was removed to avoid referencing deleted resource IDs.

    private void sendChatMessage() {
        String message = edtChatInput.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }
        
        if (geminiService == null) {
            // Hiển thị thông báo lỗi
            ChatMessage errorMessage = new ChatMessage(
                "⚠️ Chatbot chưa được cấu hình. Vui lòng thêm GEMINI_API_KEY vào gradle.properties.",
                ChatMessage.MessageType.BOT
            );
            chatbotAdapter.addMessage(errorMessage);
            recyclerChatMessages.post(() -> {
                if (chatbotAdapter.getItemCount() > 0) {
                    recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
                }
            });
            edtChatInput.setText("");
            return;
        }

        // Add user message to chat
        ChatMessage userMessage = new ChatMessage(message, ChatMessage.MessageType.USER);
        chatbotAdapter.addMessage(userMessage);
        recyclerChatMessages.post(() -> {
            if (chatbotAdapter.getItemCount() > 0) {
                recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
            }
        });

        // Clear input
        edtChatInput.setText("");
        btnSendMessage.setEnabled(false);

        // Show loading
        progressChatbot.setVisibility(View.VISIBLE);

        // Send to Gemini
        geminiService.sendMessage(message, new GeminiService.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                if (!isAdded()) return;

                progressChatbot.setVisibility(View.GONE);

                // Add bot response
                ChatMessage botMessage = new ChatMessage(response, ChatMessage.MessageType.BOT);
                chatbotAdapter.addMessage(botMessage);
                recyclerChatMessages.post(() -> {
                    if (chatbotAdapter.getItemCount() > 0) {
                        recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;

                progressChatbot.setVisibility(View.GONE);

                // Show error message
                ChatMessage errorMessage = new ChatMessage(
                    "Xin lỗi, đã có lỗi xảy ra: " + error + ". Vui lòng thử lại sau.",
                    ChatMessage.MessageType.BOT
                );
                chatbotAdapter.addMessage(errorMessage);
                recyclerChatMessages.post(() -> {
                    if (chatbotAdapter.getItemCount() > 0) {
                        recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
                    }
                });
            }
        });
    }

    private List<Food> recommendFoods(List<Food> foods) {
        UserProfile user = buildUserProfile(requireContext(), foods);

        RecommendationContext ctx = RecommendationContext.nowDefault();
        ctx.intent = "explore";

        List<RecommendationResult> results =
                recommenderEngine.recommend(user, foods, ctx, 1000);

        explainMap.clear();
        for (RecommendationResult r : results) {
            explainMap.put(r.food.getId(), r);
        }

        List<Food> out = new ArrayList<>();
        for (RecommendationResult r : results) {
            out.add(r.food);
        }
        return out;
    }

    private UserProfile buildUserProfile(Context ctx, List<Food> foods) {
        SessionStore sm = new SessionStore(ctx);

        UserProfile user = new UserProfile();
        user.loggedIn = sm.isLoggedIn();
        user.region = sm.getUserRegion();

        Set<Integer> favoriteIds = sm.getFavoriteFoodIds();
        for (Food f : foods) {
            if (favoriteIds.contains(f.getId())) {
                if (f.getTypes() != null) {
                    for (FoodType type : f.getTypes()) {
                        user.preferredTypes.add(type.name());
                    }
                }
                if (f.getTags() != null) {
                    user.preferredTags.addAll(f.getTags());
                }
            }
        }

        user.preferredTypes.addAll(sm.getCachedPreferredTypes());
        user.preferredTags.addAll(sm.getCachedPreferredTags());

        return user;
    }
}
