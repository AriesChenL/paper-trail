package com.lynn.papertrail.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 聊天请求 DTO
 */
@Data
public class AiChatRequest {
    
    @NotBlank(message = "消息内容不能为空")
    private String message;
    
    private String sessionId;  // 用于维护对话上下文的可选字段
    
    private String model;      // 可选的模型选择
}