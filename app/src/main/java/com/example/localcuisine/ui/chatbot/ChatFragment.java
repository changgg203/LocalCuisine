package com.example.localcuisine.ui.chatbot;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localcuisine.R;
import com.example.localcuisine.data.chatbot.GeminiService;
import com.example.localcuisine.data.chatbot.ChatStorage;
import com.example.localcuisine.model.ChatMessage;

import java.util.List;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

public class ChatFragment extends Fragment {

    private GeminiService geminiService;
    private ChatbotAdapter chatbotAdapter;
    private ChatStorage chatStorage;
    private View chatContainer;
    private RecyclerView recyclerChatMessages;
    private EditText edtChatInput;
    private View btnSendMessage;
    private ImageButton btnToggleChatbot;
    private ProgressBar progressChatbot;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbarChat);
        // Set header title like the widget
        toolbar.setTitle("🤖 Trợ lý ẩm thực AI");

        // Hide activity FAB while full-screen chat is shown
        View hostFab = requireActivity().findViewById(R.id.fab_chat);
        if (hostFab != null) hostFab.setVisibility(View.GONE);

        toolbar.setNavigationOnClickListener(v -> {
            // Show FAB again when closing chat
            View fab = requireActivity().findViewById(R.id.fab_chat);
            if (fab != null) fab.setVisibility(View.VISIBLE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Toolbar menu for clearing history
        toolbar.inflateMenu(R.menu.menu_chat);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_clear_history) {
                clearHistory();
                return true;
            }
            return false;
        });

        setupChatbot(view);

        return view;
    }

    private void setupChatbot(View view) {
        View chatbotWidget = view.findViewById(R.id.chatbotFull);
        if (chatbotWidget == null) return;

        // Khởi tạo GeminiService nếu có
        try {
            geminiService = new GeminiService();
        } catch (Exception e) {
            geminiService = null;
        }

        chatContainer = view.findViewById(R.id.chatContainer);
        recyclerChatMessages = view.findViewById(R.id.recyclerChatMessages);
        edtChatInput = view.findViewById(R.id.edtChatInput);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);
        btnToggleChatbot = view.findViewById(R.id.btnToggleChatbot);
        progressChatbot = view.findViewById(R.id.progressChatbot);

        if (chatContainer == null || recyclerChatMessages == null || edtChatInput == null ||
                btnSendMessage == null || btnToggleChatbot == null) {
            return;
        }

        // Header clear button (nếu có) - gọi xác nhận xoá
        View btnClear = view.findViewById(R.id.btnClearHistory);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> clearHistory());
        }
        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        recyclerChatMessages.setLayoutManager(layoutManager);
        chatbotAdapter = new ChatbotAdapter();
        recyclerChatMessages.setAdapter(chatbotAdapter);

        // Load saved chat history
        chatStorage = new ChatStorage(requireContext());
        List<ChatMessage> saved = chatStorage.loadMessages();
        if (!saved.isEmpty()) {
            chatbotAdapter.setMessages(saved);
            recyclerChatMessages.post(() -> {
                if (chatbotAdapter.getItemCount() > 0) {
                    recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
                }
            });
        }

        // Welcome message if no history
        if (saved.isEmpty()) {
            String welcomeText;
            if (geminiService == null) {
                welcomeText = "⚠️ Chatbot chưa được cấu hình. Vui lòng thêm GEMINI_API_KEY vào gradle.properties để sử dụng tính năng này.";
            } else {
                // Restore original detailed greeting with bullets
                welcomeText = "Xin chào! 👋 Tôi là trợ lý ẩm thực AI của Local Cuisine.\n\n" +
                        "Tôi có thể giúp bạn:\n" +
                        "• Tìm hiểu về món ăn địa phương\n" +
                        "• Gợi ý món ăn phù hợp\n" +
                        "• Trả lời câu hỏi về ẩm thực Việt Nam\n\n" +
                        "Bạn muốn hỏi gì?";
            }
            ChatMessage welcomeMessage = new ChatMessage(welcomeText, ChatMessage.MessageType.BOT);
            chatbotAdapter.addMessage(welcomeMessage);
            chatStorage.saveMessages(chatbotAdapter.getMessages());
        } else {
            // Ensure storage is in sync
            chatStorage.saveMessages(chatbotAdapter.getMessages());
        }
        // Expand full chat by default
        chatContainer.setVisibility(View.VISIBLE);
        btnToggleChatbot.setImageResource(android.R.drawable.arrow_up_float);

        btnToggleChatbot.setOnClickListener(v -> {
            if (chatContainer.getVisibility() == View.VISIBLE) {
                chatContainer.setVisibility(View.GONE);
                btnToggleChatbot.setImageResource(android.R.drawable.arrow_down_float);
            } else {
                chatContainer.setVisibility(View.VISIBLE);
                btnToggleChatbot.setImageResource(android.R.drawable.arrow_up_float);
            }
        });

        btnSendMessage.setOnClickListener(v -> sendChatMessage());

        edtChatInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                btnSendMessage.setEnabled(s.toString().trim().length() > 0);
            }
        });
    }

    private void sendChatMessage() {
        String message = edtChatInput.getText().toString().trim();
        if (message.isEmpty()) return;

        if (geminiService == null) {
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

        // Add user message
        ChatMessage userMessage = new ChatMessage(message, ChatMessage.MessageType.USER);
        chatbotAdapter.addMessage(userMessage);
        // Persist history
        if (chatStorage != null) chatStorage.saveMessages(chatbotAdapter.getMessages());
        recyclerChatMessages.post(() -> {
            if (chatbotAdapter.getItemCount() > 0) {
                recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
            }
        });

        edtChatInput.setText("");
        btnSendMessage.setEnabled(false);
        progressChatbot.setVisibility(View.VISIBLE);

        geminiService.sendMessage(message, new GeminiService.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                if (!isAdded()) return;
                progressChatbot.setVisibility(View.GONE);
                ChatMessage botMessage = new ChatMessage(response, ChatMessage.MessageType.BOT);
                chatbotAdapter.addMessage(botMessage);
                if (chatStorage != null) chatStorage.saveMessages(chatbotAdapter.getMessages());
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
                ChatMessage errorMessage = new ChatMessage(
                        "Xin lỗi, đã có lỗi xảy ra: " + error + ". Vui lòng thử lại sau.",
                        ChatMessage.MessageType.BOT
                );
                chatbotAdapter.addMessage(errorMessage);
                if (chatStorage != null) chatStorage.saveMessages(chatbotAdapter.getMessages());
                recyclerChatMessages.post(() -> {
                    if (chatbotAdapter.getItemCount() > 0) {
                        recyclerChatMessages.smoothScrollToPosition(chatbotAdapter.getItemCount() - 1);
                    }
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        View fab = requireActivity().findViewById(R.id.fab_chat);
        if (fab != null) fab.setVisibility(View.VISIBLE);
    }

    private void clearHistory() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa lịch sử trò chuyện")
                .setMessage("Bạn có chắc muốn xóa toàn bộ lịch sử trò chuyện không?")
                .setPositiveButton("Xóa", (d, which) -> {
                    if (chatStorage != null) chatStorage.clear();
                    if (chatbotAdapter != null) chatbotAdapter.clearMessages();

                    // Add welcome back
                    String welcomeText = geminiService == null
                            ? "⚠️ Chatbot chưa được cấu hình. Vui lòng thêm GEMINI_API_KEY vào gradle.properties để sử dụng tính năng này."
                            : "Xin chào! 👋 Tôi là trợ lý ẩm thực AI của Local Cuisine.\n\n" +
                            "Tôi có thể giúp bạn:\n" +
                            "• Tìm hiểu về món ăn địa phương\n" +
                            "• Gợi ý món ăn phù hợp\n" +
                            "• Trả lời câu hỏi về ẩm thực Việt Nam\n\n" +
                            "Bạn muốn hỏi gì?";

                    ChatMessage welcome = new ChatMessage(welcomeText, ChatMessage.MessageType.BOT);
                    if (chatbotAdapter != null) chatbotAdapter.addMessage(welcome);
                    if (chatStorage != null) chatStorage.saveMessages(chatbotAdapter.getMessages());
                    Toast.makeText(requireContext(), "Đã xóa lịch sử trò chuyện", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}

