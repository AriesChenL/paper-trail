package com.lynn.papertrail.service;

import com.lynn.papertrail.dto.AiChatRequest;
import com.lynn.papertrail.dto.AiChatResponse;
import com.lynn.papertrail.dto.AiStreamChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI 聊天服务类
 *
 * @author lynn
 */
@Service
public class AiChatService {

    private final ChatClient chatClient;
    private final OpenAiChatModel openAiChatModel;

    @Autowired
    public AiChatService(ChatClient.Builder chatClientBuilder, OpenAiChatModel openAiChatModel) {
        this.chatClient = chatClientBuilder.build();
        this.openAiChatModel = openAiChatModel;
    }

    /**
     * 处理 AI 聊天请求
     *
     * @param request 聊天请求
     * @return 聊天响应
     */
    public AiChatResponse chat(AiChatRequest request) {
        // 构建系统提示，提供上下文
        String systemPrompt = """
                你是一个有帮助的AI助手，专门用于Paper Trail文档管理系统。
                你可以帮助用户解答关于文档管理、处理、分析等方面的问题。
                请尽可能提供准确和有用的信息。
                如果问题与文档管理无关，请礼貌地告知用户。
                """;

        String userMessage = request.getMessage();

        // 使用 Spring AI ChatClient 构建并发送请求
        String response = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();

        return new AiChatResponse(response, request.getSessionId(), request.getModel());
    }

    /**
     * 处理流式 AI 聊天请求
     *
     * @param request 聊天请求
     * @return 流式响应
     */
    public Flux<AiStreamChatResponse> streamChat(AiChatRequest request) {
        // 构建系统提示，提供上下文
        String systemPrompt = """
                你是一个有帮助的AI助手，专门用于Paper Trail文档管理系统。
                你可以帮助用户解答关于文档管理、处理、分析等方面的问题。
                请尽可能提供准确和有用的信息。
                如果问题与文档管理无关，请礼貌地告知用户。
                """;

        String userMessage = request.getMessage();

        // 创建包含系统消息和用户消息的提示
        Prompt prompt = new Prompt(
                java.util.List.of(
                        new org.springframework.ai.chat.messages.SystemMessage(systemPrompt),
                        new UserMessage(userMessage)
                )
        );

        // 创建流式请求 - 使用 OpenAI 模型进行流式调用
        Flux<ChatResponse> chatResponseStream = openAiChatModel.stream(prompt);

        // 将响应转换为自定义格式 (正确的属性访问方法)
        return chatResponseStream.map(response -> {
            String content = response.getResult().getOutput().getText();
            // 对于流式响应，我们在这里不能准确判断是否完成，所以默认为 false
            // 实际完成状态将在调用方处理
            return new AiStreamChatResponse(content, false, request.getSessionId(), request.getModel());
        }).concatWith(Flux.just(new AiStreamChatResponse("", true, request.getSessionId(), request.getModel())));
    }
}