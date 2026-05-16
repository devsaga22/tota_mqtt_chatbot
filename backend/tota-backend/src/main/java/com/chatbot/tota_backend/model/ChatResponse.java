package com.chatbot.tota_backend.model;
public class ChatResponse {
    private String sessionId;
    private String reply;

    public ChatResponse(String sessionId, String reply) {
        this.sessionId = sessionId;
        this.reply = reply;
    }

    // getters/setters
}
