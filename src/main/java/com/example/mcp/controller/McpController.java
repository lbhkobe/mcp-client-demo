//package com.example.mcp.controller;
//
//import io.modelcontextprotocol.client.MessageExchange;
//import io.modelcontextprotocol.client.Session;
//import io.modelcontextprotocol.mcp.McpModel;
//import io.modelcontextprotocol.mcp.McpModelConverter;
//import io.modelcontextprotocol.mcp.protocol.ContextUpdate;
//import io.modelcontextprotocol.mcp.protocol.McpMessage;
//import io.modelcontextprotocol.mcp.protocol.McpMessageHandler;
//import io.modelcontextprotocol.mcp.protocol.Request;
//import io.modelcontextprotocol.mcp.protocol.Response;
//import io.modelcontextprotocol.mcp.protocol.Root;
//import io.modelcontextprotocol.mcp.protocol.Tool;
//import org.springframework.ai.mcp.server.spring.webflux.McpSpringWebFluxController;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.ai.chat.ChatClient;
//import org.springframework.ai.chat.ChatResponse;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.chat.prompt.SystemPromptTemplate;
//import org.springframework.ai.openai.OpenAiChatClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.UUID;
//
//@Slf4j
//@RestController
//@RequestMapping("/mcp") // Base path for MCP endpoints
//public class McpController extends McpSpringWebFluxController {
//
//    private final ChatClient chatClient;
//    private final McpModelConverter mcpModelConverter = new McpModelConverter();
//
//    @Autowired
//    public McpController(OpenAiChatClient chatClient) { // Using OpenAiChatClient directly for simplicity
//        this.chatClient = chatClient;
//        // Define a simple McpMessageHandler
//        super.setMcpMessageHandler(new McpMessageHandler() {
//            @Override
//            public Flux<McpMessage> handle(Request request, Session session) {
//                log.info("Received MCP Request: {} for session: {}", request, session.getId());
//
//                // Example: Simple echo logic or AI interaction
//                if (request.getMessage() != null && request.getMessage().getText() != null) {
//                    String userText = request.getMessage().getText();
//                    log.info("User text: {}", userText);
//
//                    // Create a prompt for Spring AI
//                    // For a real application, you'd likely build a more complex context from the MCP request
//                    String systemMessage = "You are a helpful AI assistant responding to MCP requests.";
//                    SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemMessage);
//                    org.springframework.ai.chat.messages.Message systemAiMessage = systemPromptTemplate.createMessage();
//                    org.springframework.ai.chat.messages.UserMessage userAiMessage = new org.springframework.ai.chat.messages.UserMessage(userText);
//
//                    Prompt prompt = new Prompt(List.of(systemAiMessage, userAiMessage));
//
//                    return Flux.from(chatClient.stream(prompt))
//                            .map(chatResponse -> {
//                                String aiContent = chatResponse.getResult().getOutput().getContent();
//                                log.info("AI Response chunk: {}", aiContent);
//                                // Convert AI response to McpMessage (Response)
//                                Response mcpResponse = new Response();
//                                mcpResponse.setId(UUID.randomUUID().toString());
//                                mcpResponse.setConversationId(request.getMessage().getConversationId());
//                                mcpResponse.setCorrelationId(request.getId());
//                                McpModel.Message responseMessage = new McpModel.Message();
//                                responseMessage.setText(aiContent);
//                                responseMessage.setRole(McpModel.Message.Role.ASSISTANT);
//                                mcpResponse.setMessage(responseMessage);
//                                return McpMessage.response(mcpResponse);
//                            })
//                            .doOnComplete(() -> log.info("AI stream completed for request: {}", request.getId()))
//                            .doOnError(e -> log.error("Error processing AI stream for request: {}", request.getId(), e));
//                }
//
//                // Fallback or default response if no specific handling
//                Response defaultMcpResponse = new Response();
//                defaultMcpResponse.setId(UUID.randomUUID().toString());
//                if (request.getMessage() != null) {
//                    defaultMcpResponse.setConversationId(request.getMessage().getConversationId());
//                }
//                defaultMcpResponse.setCorrelationId(request.getId());
//                McpModel.Message defaultResponseMessage = new McpModel.Message();
//                defaultResponseMessage.setText("Received your request, but no specific action was taken.");
//                defaultResponseMessage.setRole(McpModel.Message.Role.ASSISTANT);
//                defaultMcpResponse.setMessage(defaultResponseMessage);
//                return Flux.just(McpMessage.response(defaultMcpResponse));
//            }
//
//            @Override
//            public Mono<List<Root>> getRoots(Session session) {
//                log.info("MCP getRoots called for session: {}", session.getId());
//                // Example: Return a static list of roots or dynamically generate them
//                Root exampleRoot = new Root();
//                exampleRoot.setId("example-root-id");
//                exampleRoot.setDisplayName("Example Root Context");
//                exampleRoot.setMimeType("text/plain");
//                // Add more properties to the root as needed
//                return Mono.just(List.of(exampleRoot));
//            }
//
//            @Override
//            public Mono<List<Tool>> getTools(Session session) {
//                log.info("MCP getTools called for session: {}", session.getId());
//                // Example: Return a static list of tools or dynamically generate them
//                // This is where you might list tools that your AI can use, potentially interacting with external MCP servers
//                return Mono.just(List.of()); // No tools defined for now
//            }
//
//            @Override
//            public Mono<ContextUpdate> updateContext(ContextUpdate contextUpdate, Session session) {
//                log.info("MCP updateContext called with: {} for session: {}", contextUpdate, session.getId());
//                // Handle context updates from the client
//                // For example, store the context in the session or a database
//                return Mono.just(contextUpdate); // Echo back the update for now
//            }
//        });
//    }
//
//    // The McpSpringWebFluxController provides the /message endpoint by default.
//    // You can add other MCP-related or custom endpoints if needed.
//
//    // Example of how you might expose capabilities (though McpSpringWebFluxController might handle some of this)
//    @PostMapping(value = "/capabilities", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Mono<McpModel.Capabilities> getCapabilities(@RequestBody(required = false) String body) {
//        log.info("Received request for /capabilities");
//        McpModel.Capabilities capabilities = new McpModel.Capabilities();
//        capabilities.setProtocolVersion("0.6.0"); // Example version
//        capabilities.setDisplayName("Spring AI MCP Service");
//        capabilities.setId(UUID.randomUUID().toString());
//        capabilities.setProviderName("Manus AI Team");
//        capabilities.setFeatures(new McpModel.Features());
//        capabilities.getFeatures().setContextImport(true);
//        capabilities.getFeatures().setContextExport(true);
//        capabilities.getFeatures().setRoots(true);
//        capabilities.getFeatures().setTools(false); // Set to true if you implement tools
//
//        // Define supported message roles
//        McpModel.MessageRoles messageRoles = new McpModel.MessageRoles();
//        messageRoles.setAssistant(true);
//        messageRoles.setUser(true);
//        messageRoles.setSystem(true);
//        messageRoles.setTool(false); // Set to true if you support tool messages
//        capabilities.setMessageRoles(messageRoles);
//
//        return Mono.just(capabilities);
//    }
//}
//
