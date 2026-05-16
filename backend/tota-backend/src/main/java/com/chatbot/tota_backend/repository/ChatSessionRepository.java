package com.chatbot.tota_backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.chatbot.tota_backend.model.ChatSession;

@Repository
public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {

	List<ChatSession> findByUserId(String userId);

}
