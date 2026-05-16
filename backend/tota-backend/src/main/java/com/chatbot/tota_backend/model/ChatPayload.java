package com.chatbot.tota_backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatPayload {
    private String sessionId; // null or empty for first message
    private String userId;
    private String message;

    // getters/setters
}
