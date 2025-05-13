package com.example.mcp.config;

import com.example.mcp.service.MathService;
import com.example.mcp.service.WeatherService;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;


import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "mcp.external.servers")
public class ExternalMcpServersConfig {

    private String configPath; // Path to the JSON file

    // This field will be populated by reading and parsing the JSON file specified by configPath
    private Map<String, McpServerDetails> servers;

    @Data
    public static class McpServerDetails {
        private String command;
        private List<String> args;
        private Map<String, String> env;
    }

    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }

    @Bean
    public ToolCallbackProvider mathTools(MathService mathService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mathService).build();
    }
}

