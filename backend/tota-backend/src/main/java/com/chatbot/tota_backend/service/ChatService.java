package com.chatbot.tota_backend.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.chatbot.tota_backend.model.ChatMessage;

import java.util.*;

@Service
public class ChatService {

	@Value("${spring.ai.openai.api-key}")
	private String openRouterApiKey;

	@Value("${spring.ai.openai.base-url}")
	private String openRouterUrl;
	@Autowired
	private ChatSessionService chatSessionService;
	@Autowired
	private MqttService mqttService;
	
	  public String processMessage(String userId, String sessionId, String message) {

	        // End session command
	        if ("__END_SESSION__".equalsIgnoreCase(message)) {
	            chatSessionService.endSession(sessionId);
	            mqttService.publishMessage("chat/user/" + userId + "/out", 
	                "✅ Chat session ended and saved.");
	            return "Session ended.";
	        }

//	        // Ensure a session exists
	if (sessionId == null || sessionId.isEmpty()) {
	        throw new IllegalArgumentException("SessionId must be provided by MQTT listener");
	    }

	        // Save user message in session history
	        ChatMessage userMsg = new ChatMessage("user", message, System.currentTimeMillis());
	        chatSessionService.addMessage(userId, sessionId, userMsg);

	        // Get AI reply
	        String aiReply = getAiResponse( message);

	        // Save AI reply
	        ChatMessage aiMsg = new ChatMessage("ai", aiReply, System.currentTimeMillis());
	        chatSessionService.addMessage(userId, sessionId, aiMsg);

	        return aiReply;
	    }
	
// Attention !!!- spring Ai has less room for custom headers for OpenRouter so we dont use it-
//	less customization
    public String getAiResponse(String promptMsg) {
        RestTemplate restTemplate = new RestTemplate();
       

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openRouterApiKey);

        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "mistralai/mistral-small-3.2-24b-instruct:free");

        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
//     
        userMessage.put("role", "system");
        userMessage.put("content", "You are a helpful assistant. Limit your responses to 200 words unless explicitly told otherwise.");
        userMessage.put("content", promptMsg);
        messages.add(userMessage);
       

        requestBody.put("messages", messages);

        // Final entity
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(openRouterUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

        return "No response received.";
    }
   
}
