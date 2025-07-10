package com.example.mcp.controller;

import com.example.mcp.tools.DateTimeTools;
import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
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
import java.time.Duration;

@RestController
@RequestMapping("/chatModel")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final OpenAiChatModel chatModel;

    private final ChatClient chatClient;

    @Autowired
    ToolCallbackProvider toolCallbackProvider;


    @Autowired
    public ChatController(OpenAiChatModel chatModel, ChatClient.Builder chatClientBuilder, ToolCallbackProvider toolCallbackProvider) {
        this.chatModel = chatModel;
        this.chatClient = chatClientBuilder
                .defaultTools()
                .defaultSystem("你是一个百科助手，能够借助外部工具，使用中文回答用户问题.")
                .defaultToolCallbacks(toolCallbackProvider)
                //.defaultTools(toolCallbackProvider)
                //.defaultAdvisors(null)
                .build();
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

    /**
     * 大模型交互时使用定义的Tool
     * @param message
     * @return
     */
    @RequestMapping("/chatUseMcpTool")
    public String chatUseMcpTool(@RequestParam String message) {
        String response = ChatClient.create(chatModel)
                .prompt(message) //用户问题，也可以传系统/用户提示词
                .tools(new DateTimeTools()) //外部工具集合
                .call()
                .content();
        System.out.println(response);

        return response;
    }


    /**
     * 大模型交互时使用外部mcp server ------- 方式1
     * @param userQuestion
     * @return
     */
    @RequestMapping("/chatUseMcpServer")
    public String chatUseMcpServer(@RequestParam String userQuestion) {

        logger.info("开始处理chatUseMcpServer请求: {}", userQuestion);

        Prompt prompt = new Prompt(new UserMessage(userQuestion),new SystemMessage("你是一个百科助手，能够借助外部工具，使用中文回答用户问题."));
        String response = ChatClient.create(chatModel)
                .prompt(prompt) //用户问题，也可以传系统/用户提示词
                .user(userQuestion)
                .system("你是一个百科助手，能够借助外部工具，使用中文回答用户问题.")
                .toolCallbacks(toolCallbackProvider)
                .tools(new DateTimeTools()) //外部工具集合
                .call()
                .content();
        System.out.println(response);
        return response;
    }

    /**
     * 大模型交互时使用外部mcp server ------- 方式2
     * @param userQuestion
     * @return
     */
    @RequestMapping("/chatUseMcpServer2")
    public Flux<ChatResponse> chatUseMcpServer2(String userQuestion) {
        try {
            logger.info("开始处理chatUseMcpServer2请求: {}", userQuestion);
            return chatClient.prompt().user(userQuestion).stream().chatResponse()
                    .timeout(Duration.ofSeconds(100)) // 设置响应流的超时时间
                    .doOnError(e -> logger.error("处理chatUseMcpServer2请求时发生错误,", e))
                    .doOnComplete(() -> logger.info("chatUseMcpServer2请求处理完成"));
        } catch (Exception e) {
            logger.error("chatUseMcpServer2请求处理异常: {}", e.getMessage(), e);
            return Flux.error(e);
        }
    }
}
