package com.example.mcp.controller;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mcpServer")
public class GaoDeMcpServerController {

    private static final Logger logger = LoggerFactory.getLogger(GaoDeMcpServerController.class);


    @Autowired
    List<McpAsyncClient> mcpAsyncClients;

    @RequestMapping("/gaodeWeather")
    public Mono<McpSchema.CallToolResult> gaodeWeather(String city) {
        var mcpClient = mcpAsyncClients.get(0);

        return mcpClient.listTools()
                .flatMap(tools -> {
                    logger.info("tools: {}", tools);

                    return mcpClient.callTool(
                            new McpSchema.CallToolRequest(
                                    "maps_weather",
                                    Map.of("city", city)
                            )
                    );
                });
    }

    @RequestMapping("/geo")
    public Mono<McpSchema.CallToolResult> geo(String address, String city) {
        var mcpClient = mcpAsyncClients.get(0);

        return mcpClient.listTools()
                .flatMap(tools -> {
                    logger.info("tools: {}", tools);

                    return mcpClient.callTool(
                            new McpSchema.CallToolRequest(
                                    "maps_geo",
                                    Map.of("address", address, "city", city)
                            )
                    );
                });
    }

    @RequestMapping("/regeo")
    public Mono<McpSchema.CallToolResult> regeo(String location) {
        var mcpClient = mcpAsyncClients.get(0);

        return mcpClient.listTools()
                .flatMap(tools -> {
                    logger.info("tools: {}", tools);

                    return mcpClient.callTool(
                            new McpSchema.CallToolRequest(
                                    "maps_regeocode",
                                    Map.of("location", location)
                            )
                    );
                });
    }
}
