package com.chatbot.tota_backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MqttService {
	  private final MqttClient mqttClient;
//	  private final ChatService openRouterService;
	 
	  private final Map<String, String> activeSessions = new ConcurrentHashMap();
		public MqttService(@Qualifier("backend_publisher")MqttClient mqttClient) {
	        this.mqttClient = mqttClient;
	        
	    }

//	public MqttService(@Qualifier("backend_publisher")MqttClient mqttClient,ChatService openRouterService) {
//        this.mqttClient = mqttClient;
//        this.openRouterService=openRouterService;
//    }

    public void publishMessage(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1); // QoS 1: At least once (can be tuned)
            mqttClient.publish(topic, mqttMessage);
            
            System.out.println("✅ Published to topic [" + topic + "] -> " + message);
        } catch (MqttException e) {
            System.err.println("❌ Failed to publish MQTT message: " + e.getMessage());
        }
    }
    /** Registers a session for a user. Replaces old session if any. */
    public void registerSession(String userId, String sessionId) {
    	 if (sessionId == null) {
    	        throw new IllegalArgumentException("SessionId is null in registerSession");
    	    }
        activeSessions.put(userId, sessionId);
        System.out.println("🔑 User [" + userId + "] active session set to: " + sessionId);
    }

    /** Checks if this session is the active one for the user. */
    public boolean isActiveSession(String userId, String sessionId) {
        return sessionId != null && sessionId.equals(activeSessions.get(userId));
    }

}
