package com.chatbot.tota_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String sender; // "user" or "ai"
    private String text;
    private long timestamp; // System.currentTimeMillis()
}