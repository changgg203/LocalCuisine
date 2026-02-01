package com.example.localcuisine.data.chatbot;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.localcuisine.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {

    private static final String TAG = "GeminiService";

    // ✅ MODEL FREE TỐT NHẤT
    private static final String MODEL_NAME = "gemini-2.5-flash";
    private static final String API_BASE =
            "https://generativelanguage.googleapis.com/v1/models/";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    // ✅ FREE TIER CONFIG
    private static final int MAX_HISTORY = 6;              // chỉ giữ 6 message gần nhất
    private static final long MIN_REQUEST_INTERVAL = 2000; // 2 giây
    private static final int MAX_RETRIES = 2;
    private static final int INITIAL_RETRY_DELAY = 2000;   // 2s

    // ✅ SYSTEM PROMPT (chỉ gửi 1 lần)
    private static final String SYSTEM_PROMPT =
            "Bạn là chatbot hỗ trợ của ứng dụng Local Cuisine – ứng dụng về ẩm thực Việt Nam. "
                    + "Hãy trả lời bằng tiếng Việt, thân thiện, ngắn gọn, dễ hiểu, "
                    + "ưu tiên gợi ý món ăn, giải thích nguồn gốc, vùng miền ,nguyên liệu của món ăn và cách thưởng thức.";

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Gson gson = new Gson();

    private final String apiKey;
    private final List<JsonObject> chatHistory = new ArrayList<>();

    private long lastRequestTime = 0;
    private boolean systemPromptAdded = false;

    public interface ChatCallback {
        void onSuccess(String reply);
        void onError(String error);
    }

    public GeminiService() {
        apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.isEmpty() || "null".equals(apiKey)) {
            throw new IllegalStateException("GEMINI_API_KEY không hợp lệ");
        }
    }

    // ========================= PUBLIC API =========================

    public void sendMessage(String userMessage, ChatCallback callback) {
        executor.execute(() -> sendInternal(userMessage, callback, 0));
    }

    public void resetChat() {
        chatHistory.clear();
        systemPromptAdded = false;
    }

    // ========================= INTERNAL =========================

    private void sendInternal(String userMessage, ChatCallback callback, int retryCount) {
        try {
            // ⏱️ rate limit
            long delta = System.currentTimeMillis() - lastRequestTime;
            if (delta < MIN_REQUEST_INTERVAL) {
                Thread.sleep(MIN_REQUEST_INTERVAL - delta);
            }

            // 🧠 add system prompt (1 lần duy nhất)
            if (!systemPromptAdded) {
                chatHistory.add(buildMessage("user", SYSTEM_PROMPT));
                systemPromptAdded = true;
            }

            // 👤 add user message
            chatHistory.add(buildMessage("user", userMessage));
            trimHistory();

            JsonObject bodyJson = new JsonObject();
            JsonArray contents = new JsonArray();
            for (JsonObject msg : chatHistory) {
                contents.add(msg);
            }
            bodyJson.add("contents", contents);

            RequestBody body =
                    RequestBody.create(gson.toJson(bodyJson), JSON);

            String url = API_BASE + MODEL_NAME + ":generateContent?key=" + apiKey;

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            lastRequestTime = System.currentTimeMillis();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    postError(callback, "Lỗi kết nối mạng");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        int code = response.code();
                        Log.w(TAG, "API error " + code);

                        if (code == 429 && retryCount < MAX_RETRIES) {
                            int delay = INITIAL_RETRY_DELAY * (retryCount + 1);
                            mainHandler.postDelayed(
                                    () -> sendInternal(userMessage, callback, retryCount + 1),
                                    delay
                            );
                            return;
                        }

                        postError(callback,
                                code == 429
                                        ? "Bạn gửi quá nhanh, vui lòng chờ chút nhé"
                                        : "Lỗi API (" + code + ")"
                        );
                        return;
                    }

                    String res = response.body() != null
                            ? response.body().string()
                            : "";

                    JsonObject json = gson.fromJson(res, JsonObject.class);
                    String reply = extractText(json);

                    if (reply == null) {
                        postError(callback, "Không nhận được phản hồi từ AI");
                        return;
                    }

                    chatHistory.add(buildMessage("model", reply));
                    trimHistory();

                    mainHandler.post(() -> callback.onSuccess(reply));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "sendInternal error", e);
            postError(callback, "Có lỗi xảy ra");
        }
    }

    // ========================= HELPERS =========================

    private JsonObject buildMessage(String role, String text) {
        JsonObject msg = new JsonObject();
        msg.addProperty("role", role);

        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", text);
        parts.add(part);

        msg.add("parts", parts);
        return msg;
    }

    private void trimHistory() {
        while (chatHistory.size() > MAX_HISTORY) {
            chatHistory.remove(0);
        }
    }

    private String extractText(JsonObject json) {
        if (!json.has("candidates")) return null;

        JsonObject content =
                json.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content");

        if (content == null || !content.has("parts")) return null;

        return content.getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }

    private void postError(ChatCallback cb, String msg) {
        mainHandler.post(() -> {
            if (cb != null) cb.onError(msg);
        });
    }
}
