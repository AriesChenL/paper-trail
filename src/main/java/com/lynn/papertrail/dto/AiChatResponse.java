package com.lynn.papertrail.dto;

import lombok.Data;

/**
 * AI 聊天响应 DTO
 */
@Data
public class AiChatResponse {
    
    private String response;
    
    private String sessionId;
    
    private String model;
    
    private long timestamp;
    
    public AiChatResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public AiChatResponse(String response, String sessionId, String model) {
        this();
        this.response = response;
        this.sessionId = sessionId;
        this.model = model;
    }
}