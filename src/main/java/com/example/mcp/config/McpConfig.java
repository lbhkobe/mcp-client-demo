package com.example.mcp.config;

import com.example.mcp.tools.DateTimeTools;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.client.autoconfigure.NamedClientMcpTransport;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Configuration
public class McpConfig {

    /**
     * 配置高德MCP Client
     *
     * @return McpClientTransport
     */
//    @Bean
//    public List<NamedClientMcpTransport> mcpClientTransport() {
//        McpClientTransport transport = HttpClientSseClientTransport
//                .builder("https://mcp.amap.com")
//                .sseEndpoint("/sse?key=2549fa069752fb0ec89ed9e409690044")
//                .objectMapper(new ObjectMapper())
//                .build();
//
//        return Collections.singletonList(new NamedClientMcpTransport("amap", transport));
//    }


//    @Bean
//    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
//                                                 ConfigurableApplicationContext context) {
//
//
//        return args -> {
//
//            var chatClient = chatClientBuilder
//                    .defaultToolCallbacks(tools)
//                    .build();
//            var transport = new WebFluxSseClientTransport(WebClient.builder().baseUrl("http://localhost:8080"));
//            var client = McpClient.sync(transport).build();
//            //String userInput = "请帮我规划一下从青岛市李沧andy和达和城到潍坊市高密市凤凰景苑的路线";
//            //String userInput = "帮我将这个网页内容进行fetch https://www.shuaijiao.cn/news/view/68320.html";
//            String userInput = "Go to google.com and search for Browser MCP";
//            System.out.println("\n>>> QUESTION: " + userInput);
//            System.out.println("\n>>> ASSISTANT: " + chatClient.prompt(userInput).call().content());
//
//            context.close();
//        };
//    }
}
