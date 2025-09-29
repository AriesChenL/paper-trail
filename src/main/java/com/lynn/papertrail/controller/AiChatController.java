package com.lynn.papertrail.controller;

import com.lynn.papertrail.dto.AiChatRequest;
import com.lynn.papertrail.dto.AiChatResponse;
import com.lynn.papertrail.dto.AiStreamChatResponse;
import com.lynn.papertrail.service.AiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * AI 聊天控制器
 *
 * @author lynn
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // 根据需要调整 CORS 设置
public class AiChatController {

    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    private final AiChatService aiChatService;

    @Autowired
    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 处理聊天请求
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        logger.info("Received AI chat request: {}", request.getMessage());

        try {
            AiChatResponse response = aiChatService.chat(request);
            logger.info("AI response generated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing AI chat request", e);
            return ResponseEntity.status(500).body(new AiChatResponse(
                    "抱歉，AI处理您的请求时出现了错误，请稍后再试。",
                    request.getSessionId(),
                    request.getModel()
            ));
        }
    }

    /**
     * 处理流式聊天请求 (Reactive - Alternative approach)
     *
     * @param request 聊天请求
     * @return 流式响应
     */
    @PostMapping(value = "/reactive-stream-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AiStreamChatResponse> reactiveStreamChat(@Valid @RequestBody AiChatRequest request) {
        logger.info("Received AI reactive stream chat request: {}", request.getMessage());

        try {
            return aiChatService.streamChat(request);
        } catch (Exception e) {
            logger.error("Error processing AI reactive stream chat request", e);
            return Flux.error(e);
        }
    }

    /**
     * 健康检查端点
     *
     * @return 简单响应
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI service is running");
    }
}