package com.example.mcpserver.config;

import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ApplicationConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${mcp.external-servers:}")
    private String[] externalMcpServers;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public OpenAiApi openAiApi(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builder(WebClientAdapter.forClient(webClient))
                .build();
        return factory.createClient(OpenAiApi.class);
    }

    @Bean
    public OpenAiChatOptions openAiChatOptions() {
        return OpenAiChatOptions.builder()
                .withModel("gpt-3.5-turbo")
                .withTemperature(0.7f)
                .build();
    }
}