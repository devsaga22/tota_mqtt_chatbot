package com.chatbot.tota_backend.model;

public class PromptRequest {
    private String prompt;


    public PromptRequest() {
        // No-arg constructor is required
    }
    
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
