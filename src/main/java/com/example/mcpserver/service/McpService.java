package com.example.mcpserver.service;

import com.example.mcpserver.model.McpRequest;
import com.example.mcpserver.model.McpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class McpService {

    private final ChatClient chatClient;
    private final WebClient webClient;
    
    @Value("${mcp.external-servers:}")
    private String[] externalMcpServers;
    
    /**
     * 处理MCP请求并使用Spring AI与大模型交互
     */
    public McpResponse processMcpRequest(McpRequest request) {
        log.info("Processing MCP request: {}", request.getId());
        
        // 转换MCP请求消息为Spring AI消息
        List<Message> messages = convertToSpringAiMessages(request.getMessages());
        
        // 调用AI模型
        ChatResponse chatResponse = chatClient.call(messages);
        
        // 构建MCP响应
        return buildMcpResponse(request, chatResponse);
    }
    
    /**
     * 处理外部MCP服务器请求
     */
    public McpResponse processExternalMcpRequest(McpRequest request) {
        if (externalMcpServers == null || externalMcpServers.length == 0) {
            log.warn("No external MCP servers configured");
            return processMcpRequest(request); // 回退到本地处理
        }
        
        // 选择一个外部MCP服务器（这里简单实现，可以扩展为负载均衡策略）
        String externalServer = externalMcpServers[0];
        
        log.info("Forwarding request to external MCP server: {}", externalServer);
        
        // 调用外部MCP服务器
        return webClient.post()
                .uri(externalServer + "/v1/chat/completions")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(McpResponse.class)
                .onErrorResume(e -> {
                    log.error("Error calling external MCP server: {}", e.getMessage());
                    // 回退到本地处理
                    return Mono.just(processMcpRequest(request));
                })
                .block();
    }
    
    /**
     * 将MCP请求消息转换为Spring AI消息
     */
    private List<Message> convertToSpringAiMessages(List<McpRequest.Message> mcpMessages) {
        List<Message> aiMessages = new ArrayList<>();
        
        for (McpRequest.Message mcpMessage : mcpMessages) {
            switch (mcpMessage.getRole()) {
                case "system":
                    aiMessages.add(new SystemMessage(mcpMessage.getContent()));
                    break;
                case "user":
                    aiMessages.add(new UserMessage(mcpMessage.getContent()));
                    break;
                case "assistant":
                    aiMessages.add(new org.springframework.ai.chat.messages.AssistantMessage(mcpMessage.getContent()));
                    break;
                default:
                    log.warn("Unsupported message role: {}", mcpMessage.getRole());
            }
        }
        
        return aiMessages;
    }
    
    /**
     * 构建MCP响应
     */
    private McpResponse buildMcpResponse(McpRequest request, ChatResponse chatResponse) {
        String content = chatResponse.getResult().getOutput().getContent();
        
        McpRequest.Message assistantMessage = McpRequest.Message.builder()
                .role("assistant")
                .content(content)
                .build();
        
        McpResponse.Choice choice = McpResponse.Choice.builder()
                .index(0)
                .message(assistantMessage)
                .finishReason("stop")
                .build();
        
        List<McpResponse.Choice> choices = List.of(choice);
        
        McpResponse.Usage usage = McpResponse.Usage.builder()
                .promptTokens(100) // 简化实现，实际应计算真实token数
                .completionTokens(content.length() / 4) // 简化实现
                .totalTokens(100 + content.length() / 4) // 简化实现
                .build();
        
        return McpResponse.builder()
                .id(UUID.randomUUID().toString())
                .model(request.getModel() != null ? request.getModel() : "default-model")
                .object("chat.completion")
                .created(Instant.now().getEpochSecond())
                .choices(choices)
                .usage(usage)
                .build();
    }
}