package com.chatbot.tota_backend.utils;
public class TopicUtils {



	    public enum TopicType {
	        NEW_CHAT, CONTINUE_CHAT, SYSTEM
	    }

	    public static class TopicInfo {
	        private final TopicType type;
	        private final String userId;
	        private final String sessionId;

	        public TopicInfo(TopicType type, String userId, String sessionId) {
	            this.type = type;
	            this.userId = userId;
	            this.sessionId = sessionId;
	        }

	        public TopicType getType() { return type; }
	        public String getUserId() { return userId; }
	        public String getSessionId() { return sessionId; }
	    }

	    public static TopicInfo extractInfo(String topic) {
	        // Examples:
	        // chat/user/<userId>/new/in
	        // chat/user/<userId>/<sessionId>/in

	        String[] parts = topic.split("/");
	        if (parts.length < 4 || !"chat".equals(parts[0]) || !"user".equals(parts[1])) {
	            throw new IllegalArgumentException("Invalid topic format: " + topic);
	        }

	        String userId = parts[2];

	        if ("new".equals(parts[3])) {
	            return new TopicInfo(TopicType.NEW_CHAT, userId, null);
	        } else if (parts.length >= 5) {
	            String sessionId = parts[3];
	            return new TopicInfo(TopicType.CONTINUE_CHAT, userId, sessionId);
	        }

	        return new TopicInfo(TopicType.SYSTEM, userId, null);
	    }

	    public static String buildNewChatTopic(String userId) {
	        return "chat/user/" + userId + "/new/in";
	    }
	    public static String buildNewChatResponseTopic(String userId) {
	        return "chat/user/" + userId + "/new/out";
	    }

	    public static String buildContinueChatTopic(String userId, String sessionId) {
	        return "chat/user/" + userId + "/" + sessionId + "/in";
	    }

	    public static String buildResponseTopic(String userId, String sessionId) {
	        return "chat/user/" + userId + "/" + sessionId + "/out";
	    }
	}

