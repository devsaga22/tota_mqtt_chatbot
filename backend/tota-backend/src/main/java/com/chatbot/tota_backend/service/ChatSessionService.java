package com.chatbot.tota_backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatbot.tota_backend.model.ChatMessage;
import com.chatbot.tota_backend.model.ChatSession;
import com.chatbot.tota_backend.repository.ChatSessionRepository;

@Service
public class ChatSessionService {

    
    private ChatSessionRepository chatSessionRepository;

    public ChatSessionService(ChatSessionRepository chatSessionRepository) {
		super();
		this.chatSessionRepository = chatSessionRepository;
	}



    public ChatSession startSession(String userId, String sessionId) {
        ChatSession session = new ChatSession();
        session.setId(sessionId); 
        session.setUserId(userId);
        session.setStartTime(Instant.now());
        return chatSessionRepository.save(session);
    }
       public ChatSession addMessage(String userId,String sessionId, ChatMessage message) {
        Optional<ChatSession> optionalSession = chatSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            ChatSession session = optionalSession.get();
            session.addMessage(message);
            return chatSessionRepository.save(session);
        }
        throw new RuntimeException("Session not found: " + sessionId);
    }

    public ChatSession endSession(String sessionId) {
        Optional<ChatSession> optionalSession = chatSessionRepository.findById(sessionId);
        if (optionalSession.isPresent()) {
            ChatSession session = optionalSession.get();
            session.setEndTime(Instant.now());
            return chatSessionRepository.save(session);
        }
        throw new RuntimeException("Session not found: " + sessionId);
    }
    public List<ChatSession> getUserSessions(String userId) {
        return chatSessionRepository.findByUserId(userId);
    }
    }

