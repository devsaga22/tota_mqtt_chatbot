package com.chatbot.tota_backend.service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.chatbot.tota_backend.model.ChatPayload;
import com.chatbot.tota_backend.utils.TopicUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class MqttListener {

    private static final Logger logger = LoggerFactory.getLogger(MqttListener.class);

    private final MqttClient mqttClient;
    private final ChatService openRouterService;
    private final MqttService mqttService;
    private final ObjectMapper objectMapper;
    private final ChatSessionService chatSessionService;
    private boolean subscribed = false;
    public MqttListener(@Qualifier("backend_subscriber")MqttClient mqttClient, 
    		ChatService openRouterService, MqttService mqttService,ObjectMapper objectMapper
    		,ChatSessionService chatSessionService
    		) {
        this.mqttClient = mqttClient;
        this.openRouterService = openRouterService;
        this.mqttService = mqttService;
		this.objectMapper = objectMapper;
		 this.chatSessionService= chatSessionService;
    }
 // method is called only after all the required DI is done
//    run this code after spring finishes wiring
    @PostConstruct
    public void subscribeToUserInput() {
    	 try {
    	      
    	        	 MqttConnectOptions options = new MqttConnectOptions();
    	        	    options.setAutomaticReconnect(true);
//    	        	    options.setCleanSession(false);
    	        	    options.setCleanSession(true);
    	         // Set callback to handle reconnects and subscriptions
    	        	    // 2️⃣ Set callback BEFORE connecting, so events are handled from the start
    	            mqttClient.setCallback(new MqttCallbackExtended() {
    	                @Override
    	                public void connectComplete(boolean reconnect, String serverURI) {
    	                    logger.info("✅ MQTT connected to broker: {}", serverURI);
    	                    if (reconnect) {
    	                        logger.info("🔄 Reconnected to MQTT broker at {}", serverURI);
    	                    } else {
    	                        logger.info("✅ Connected to MQTT broker at {}", serverURI);
    	                    }
    	                    	subscribeToTopics();
    	                  
    	                }
//runs when the broker is down
    	                @Override
    	                public void connectionLost(Throwable cause) {
    	                    logger.warn("⚠️ MQTT connection lost: {}", cause.getMessage());
    	                }

    	              

						@Override
						public void messageArrived(String topic, MqttMessage message) throws Exception {
							// TODO Auto-generated method stub
							
						}

						@Override
						public void deliveryComplete(IMqttDeliveryToken token) {
							// TODO Auto-generated method stub
							
						}
    	            });
    	            logger.info("✅ MQTT client connected successfully.");

    	            // Connect to the broker — this triggers connectComplete() on success

    	      
    	        // 3️⃣ Connect to the broker — this triggers connectComplete() on success
    	        // Connect once
                if (!mqttClient.isConnected()) {
                    mqttClient.connect(options);
                    logger.info("✅ MQTT client connected successfully.");
                }

    	    } catch (MqttException e) {
    	        logger.error("❌ Failed to connect or subscribe to MQTT broker", e);
    	    }
    }

    private void subscribeToTopics() {
        try {
        	logger.info("🔁 Subscribing to topics...");
            // New session requests
            mqttClient.subscribe("chat/user/+/new/in", (topic, msg) -> {
                logger.info("🆕 New session requested on {}", topic);
                handleIncomingMessage(topic, msg);
            });

            // Ongoing session requests
            mqttClient.subscribe("chat/user/+/+/in", (topic, msg) -> {
            	TopicUtils.TopicInfo info = TopicUtils.extractInfo(topic);

                // Skip NEW_CHAT, only handle CONTINUE_CHAT
                if (info.getType() == TopicUtils.TopicType.NEW_CHAT) return;
                logger.info("📌 Ongoing session message on {}", topic);
                handleIncomingMessage(topic, msg);
            });

            logger.info("✅ Subscribed to chat topics successfully.");
        } catch (MqttException e) {
            logger.error("❌ Failed to subscribe to chat topics", e);
        }
    }

    private void handleIncomingMessage(String topic, MqttMessage message) {
        try {
            String jsonPayload = new String(message.getPayload(), StandardCharsets.UTF_8);
            logger.info("📥 Raw incoming payload: {}", jsonPayload);
            ChatPayload chatPayload = objectMapper.readValue(jsonPayload, ChatPayload.class);
            logger.info("topic:{}", topic);
            String userId = chatPayload.getUserId();
            String sessionId = chatPayload.getSessionId();
            String userMessage = chatPayload.getMessage();

            logger.info("📩 [{}:{}] {}", userId, sessionId, userMessage);
            TopicUtils.TopicInfo info = TopicUtils.extractInfo(topic);

            switch (info.getType()) {
                case NEW_CHAT:
                    // backend creates a new sessionId
                	  // Register session if it's the first message
                    // Create new sessionId
                    sessionId = UUID.randomUUID().toString();
                    chatSessionService.startSession(userId, sessionId);
                    mqttService.registerSession(userId, sessionId);
                 // Ask AI for a reply to the first message
                   
                    String newTopic = TopicUtils.buildNewChatResponseTopic(userId);
                    logger.info("out going topic:{}", newTopic);
                    publishAIReply(userId, sessionId, userMessage, newTopic);
                    
                    break;

                case CONTINUE_CHAT:
                    if (!mqttService.isActiveSession(userId, sessionId)) {
                        logger.warn("🚫 Ignoring message from inactive session: {} for user {}", sessionId, userId);
                        return;
                    }

                    logger.info("📩 [{}:{}] {}", userId, sessionId, userMessage);

                    String responseTopic = TopicUtils.buildResponseTopic(userId, sessionId);
                    publishAIReply(userId, sessionId, userMessage, responseTopic);
                    logger.info("✅ Published AI response to {}", responseTopic);
                    break;
                default:
                    logger.warn("⚠️ Unknown message type: {}", info.getType());
            }
           

        } catch (Exception ex) {
        	 logger.error("❌ Error processing MQTT message. incoming Topic={}, Payload={}", 
        		        topic, new String(message.getPayload(), StandardCharsets.UTF_8), ex);
        }
    }
    private void publishAIReply(String userId, String sessionId, String userMessage, String topic) throws Exception {
        String aiReply = openRouterService.processMessage(userId, sessionId, userMessage);

        ChatPayload response = new ChatPayload();
        response.setSessionId(sessionId);
        response.setUserId(userId);
        response.setMessage(aiReply);

        String responseJson = objectMapper.writeValueAsString(response);
        mqttService.publishMessage(topic, responseJson);

        logger.info("✅ Published AI response to {}", topic);
    }
    
//    private String extractUserInfoFromTopic(String topic) {
//        // Example topic: chat/user/user123/in
//        String[] parts = topic.split("/");
//        if (parts.length >= 4 && "chat".equals(parts[0]) && "user".equals(parts[1])) {
//            return parts[2]; // "user123"
//        }
//        throw new IllegalArgumentException("Invalid topic format: " + topic);
//    }
}
