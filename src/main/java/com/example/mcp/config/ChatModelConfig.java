package com.example.mcp.config;


import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelConfig {

    //@Bean
//    public OpenAiChatModel openAiChatModel() {
//        var openAiApi = OpenAiApi.builder()
//                .apiKey(System.getenv("vfiBiNt3JyUZBouSyOKterhIbq084kfu"))
//                .build();
//        var openAiChatOptions = OpenAiChatOptions.builder()
//                .model("gpt-3.5-turbo")
//                .temperature(0.4)
//                .maxTokens(200)
//                .build();
//        var chatModel = new OpenAiChatModel(openAiApi, openAiChatOptions);
//
//
//        return chatModel;
//    }
}
