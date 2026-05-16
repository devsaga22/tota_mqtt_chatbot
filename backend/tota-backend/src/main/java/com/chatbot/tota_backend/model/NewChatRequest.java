package com.chatbot.tota_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewChatRequest {
    private String userId;
    // no sessionId here
    private String message; // optional, if first message is included
}
