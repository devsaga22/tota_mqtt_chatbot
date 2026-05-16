package com.chatbot.tota_backend.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor
public class SessionSummaryDto {
    private String id;
    private Instant startTime;
    private Instant endTime;
    private String firstMessageSnippet;
}
