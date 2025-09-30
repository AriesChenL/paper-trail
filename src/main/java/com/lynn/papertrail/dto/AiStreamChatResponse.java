package com.lynn.papertrail.dto;

import lombok.Data;

/**
 * AI 流式聊天响应 DTO
 *
 * @author lynn
 */
@Data
public class AiStreamChatResponse {

    private String content;

    private boolean finished;  // 标记是否是最后一个响应

    private String sessionId;

    private String model;

    private long timestamp;

    public AiStreamChatResponse() {
        this.timestamp = System.currentTimeMillis();
        this.finished = false;
    }

    public AiStreamChatResponse(String content, boolean finished, String sessionId, String model) {
        this();
        this.content = content;
        this.finished = finished;
        this.sessionId = sessionId;
        this.model = model;
    }
}