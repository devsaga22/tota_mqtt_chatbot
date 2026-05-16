package com.chatbot.tota_backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.chatbot.tota_backend.model.ChatSession;
import com.chatbot.tota_backend.model.PromptRequest;
import com.chatbot.tota_backend.service.ChatService;
import com.chatbot.tota_backend.service.ChatSessionService;

@RestController
@RequestMapping("/api/test")

public class LlmConnectionTestController {
		@Autowired
		private ChatService chatService;
		@Autowired
		private ChatSessionService chatSessionService;
//	    @PostMapping("/ask-ai")
//	    public String testPrompt(@RequestParam String prompt) {
//	        return chatService.getResponse(prompt);
//	    }
	    @PostMapping("/ask-ai")
	    public String getResponse(@RequestParam String prompt){
	    	 System.out.println("========== Controller Called ==========");
	         System.out.println("Prompt received: " + prompt);
	        return chatService.getAiResponse(prompt);
	    }
	    @PostMapping("/ask-ai-json")
	    public String getResponseJson(@RequestBody PromptRequest body) {
	    	 
	        String prompt = body.getPrompt();
	        System.out.println("========== Controller Called ==========");
	        System.out.println("Prompt received: " + prompt);

	        if (prompt == null || prompt.trim().isEmpty()) {
	            return "Error: Prompt is empty!";
	        }
	        return chatService.getAiResponse(prompt);
	    }
	   

//	    public ChatController(ChatSessionService chatSessionService) {
//	        this.chatSessionService = chatSessionService;
//	    }

//	    @PostMapping("/start/{userId}")
//	    public ChatSession startChat(@PathVariable("userId") String userId) {
//	    	String sessionId = UUID.randomUUID().toString();
//	        return chatSessionService.startSession(userId);
//	    }

//	    @PostMapping("/{sessionId}/message")
//	    public ChatSession addMessage(@PathVariable String sessionId, @RequestBody ChatMessage message) {
//	        return chatSessionService.addMessage(sessionId, message);
//	    }

	    @PostMapping("/{sessionId}/end")
	    public ChatSession endChat(@PathVariable String sessionId) {
	        return chatSessionService.endSession(sessionId);
	    }
}
