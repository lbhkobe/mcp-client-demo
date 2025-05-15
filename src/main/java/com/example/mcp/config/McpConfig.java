package com.example.mcp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import org.springframework.ai.mcp.client.autoconfigure.NamedClientMcpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
