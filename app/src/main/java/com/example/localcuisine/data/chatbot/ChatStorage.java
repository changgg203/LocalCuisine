package com.example.localcuisine.data.chatbot;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.localcuisine.model.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatStorage {
    private static final String PREFS = "localcuisine_chat";
    private static final String KEY_MESSAGES = "chat_messages";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public ChatStorage(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<ChatMessage> loadMessages() {
        String json = prefs.getString(KEY_MESSAGES, null);
        if (json == null || json.isEmpty()) return new ArrayList<>();
        Type t = new TypeToken<List<ChatMessage>>(){}.getType();
        List<ChatMessage> list = gson.fromJson(json, t);
        return list != null ? list : new ArrayList<>();
    }

    public void saveMessages(List<ChatMessage> messages) {
        String json = gson.toJson(messages);
        prefs.edit().putString(KEY_MESSAGES, json).apply();
    }

    public void clear() {
        prefs.edit().remove(KEY_MESSAGES).apply();
    }
}