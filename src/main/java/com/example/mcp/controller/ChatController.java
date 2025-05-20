package com.example.mcp.controller;

import com.example.mcp.tools.WeatherTools;
import com.example.mcp.tools.DateTimeTools;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatModel")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final OllamaChatModel chatModel;

    @Autowired
    public ChatController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.chatModel.call(message));
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }

    @RequestMapping("/chatUseMcpTool")
    public String chatUseMcpTool(String message) {

        String response = ChatClient.create(chatModel)
                .prompt(message) //用户问题，也可以传系统/用户提示词
                .tools(new DateTimeTools()) //外部工具集合
                .call()
                .content();
        System.out.println(response);

        return response;
    }

    @Autowired
    List<McpAsyncClient> mcpAsyncClients;

    @RequestMapping("/test")
    public Mono<McpSchema.CallToolResult> test() {
        var mcpClient = mcpAsyncClients.get(0);

        return mcpClient.listTools()
                .flatMap(tools -> {
                    logger.info("tools: {}", tools);
                    return mcpClient.callTool(
                            new McpSchema.CallToolRequest(
                                    "maps_text_search",
                                    Map.of("keywords", "烧烤",  "city", "青岛" ,"citylimit", true)
                            )
                    );
                });
    }

    @Autowired
    ToolCallbackProvider toolCallbackProvider;


    @RequestMapping("/chatUseMcpServer")
    public String chatUseMcpServer(String userQuestion) {

        Prompt prompt = new Prompt(new UserMessage(userQuestion),new SystemMessage("你是一个百科助手，能够借助外部工具，使用中文回答用户问题."));
        String response = ChatClient.create(chatModel)
                .prompt(prompt) //用户问题，也可以传系统/用户提示词
                .user(userQuestion)
                .system("你是一个百科助手，能够借助外部工具，使用中文回答用户问题.")
                .toolCallbacks(toolCallbackProvider)
                .tools(new DateTimeTools(),new WeatherTools()) //外部工具集合
                .call()
                .content();
        System.out.println(response);
        return response;
    }
}
