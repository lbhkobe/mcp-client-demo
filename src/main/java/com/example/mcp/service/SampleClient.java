package com.example.mcp.service;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class SampleClient {

    private final McpClientTransport transport;

    public SampleClient(McpClientTransport transport) {
        this.transport = transport;
    }

    public void run() {

        var client = McpClient.sync(this.transport).build();

        client.initialize();

        client.ping();

        // List and demonstrate tools
        McpSchema.ListToolsResult toolsList = client.listTools();
        System.out.println("Available Tools = " + toolsList);


        McpSchema.CallToolResult productInfo = client.callTool(new McpSchema.CallToolRequest(
                "queryProductInfo",
                Map.of("productName", "product2")));
       System.out.println("productInfo: " + productInfo);;


        client.closeGracefully();

    }

}