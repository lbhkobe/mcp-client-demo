package com.example.mcpserver.controller;

import com.example.mcpserver.model.McpRequest;
import com.example.mcpserver.model.McpResponse;
import com.example.mcpserver.service.McpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class McpController {

    private final McpService mcpService;
    
    @PostMapping("/chat/completions")
    public ResponseEntity<McpResponse> processChat(@RequestBody McpRequest request) {
        McpResponse response = mcpService.processMcpRequest(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/external/chat/completions")
    public ResponseEntity<McpResponse> processExternalChat(@RequestBody McpRequest request) {
        McpResponse response = mcpService.processExternalMcpRequest(request);
        return ResponseEntity.ok(response);
    }
}