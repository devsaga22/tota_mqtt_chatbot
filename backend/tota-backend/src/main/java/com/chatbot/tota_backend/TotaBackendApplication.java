package com.chatbot.tota_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.chatbot.tota_backend.repository")
public class TotaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TotaBackendApplication.class, args);
	}

}
