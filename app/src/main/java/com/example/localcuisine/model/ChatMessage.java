package com.example.localcuisine.model;

public class ChatMessage {
    public enum MessageType {
        USER,
        BOT
    }

    private String message;
    private MessageType type;
    private long timestamp;

    public ChatMessage(String message, MessageType type) {
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public MessageType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
