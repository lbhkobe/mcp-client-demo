package com.example.mcp.controller;

import com.example.mcp.tools.DateTimeTools;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.tool.ToolCallback;
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
import java.util.function.Function;

@RestController
@RequestMapping("/chatModel")
public class ChatController {

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
                    return mcpClient.callTool(
                            new McpSchema.CallToolRequest(
                                    "maps_weather",
                                    Map.of("city", "北京")
                            )
                    );
                });
    }

    @Autowired
    ToolCallbackProvider toolCallbackProvider;


    @RequestMapping("/chatUseMcpServer")
    public String chatUseMcpServer(String userQuestion) {

        //var mcpClient = mcpAsyncClients.get(0);
        //var myToolCallbackProvider = toolCallbackProvider;
       // Mono<McpSchema.ListToolsResult> tools = mcpClient.listTools();
       // System.out.println(tools);

        Prompt prompt = new Prompt(new UserMessage(userQuestion),new SystemMessage("你是一个百科助手，能够借助外部工具，请用中文回答用户问题."));
        String response = ChatClient.create(chatModel)
                .prompt(prompt) //用户问题，也可以传系统/用户提示词
                //.toolCallbacks(toolCallbackProvider)
                //.tools(new DateTimeTools()) //外部工具集合
                .call()
                .content();
        System.out.println(response);
        return response;
    }
}
