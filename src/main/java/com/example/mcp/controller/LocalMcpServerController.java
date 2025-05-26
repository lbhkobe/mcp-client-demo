package com.example.mcp.controller;

import com.example.mcp.service.SampleClient;
import io.modelcontextprotocol.client.transport.WebFluxSseClientTransport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/localMcpServer")
public class LocalMcpServerController {

    @GetMapping("/hello")
    public String hello() {

        var transport = new WebFluxSseClientTransport(WebClient.builder().baseUrl("http://localhost:8082"));
        new SampleClient(transport).run();

        return "Hello, World!";
    }
}
