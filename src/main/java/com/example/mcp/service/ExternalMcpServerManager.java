package com.example.mcp.service;

import com.example.mcp.config.ExternalMcpServersConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class ExternalMcpServerManager {

    private final ExternalMcpServersConfig externalMcpServersConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Process> runningServers = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool(); // For managing process output streams

    @Autowired
    public ExternalMcpServerManager(ExternalMcpServersConfig externalMcpServersConfig) {
        this.externalMcpServersConfig = externalMcpServersConfig;
    }

    @PostConstruct
    public void init() {
        loadAndStartServers();
    }

    private void loadAndStartServers() {
        String configPath = externalMcpServersConfig.getConfigPath();
        if (configPath == null || configPath.isBlank()) {
            log.warn("External MCP servers configuration path is not set. No external servers will be started.");
            return;
        }

        File configFile = new File(configPath);
        if (!configFile.exists() || !configFile.isFile()) {
            log.warn("External MCP servers configuration file not found at: {}. No external servers will be started.", configPath);
            return;
        }

        try {
            Map<String, ExternalMcpServersConfig.McpServerDetails> serverDetailsMap =
                    objectMapper.readValue(configFile, new TypeReference<Map<String, ExternalMcpServersConfig.McpServerDetails>>() {});
            
            if (serverDetailsMap == null || serverDetailsMap.isEmpty()) {
                log.info("No external MCP servers defined in the configuration file.");
                return;
            }
            externalMcpServersConfig.setServers(serverDetailsMap); // Populate the config object

            for (Map.Entry<String, ExternalMcpServersConfig.McpServerDetails> entry : serverDetailsMap.entrySet()) {
                String serverName = entry.getKey();
                ExternalMcpServersConfig.McpServerDetails details = entry.getValue();
                startServer(serverName, details);
            }
        } catch (IOException e) {
            log.error("Error reading or parsing external MCP servers configuration file: {}", configPath, e);
        }
    }

    public void startServer(String serverName, ExternalMcpServersConfig.McpServerDetails details) {
        if (runningServers.containsKey(serverName)) {
            log.warn("Server {} is already running.", serverName);
            return;
        }

        List<String> commandWithArgs = new ArrayList<>();
        commandWithArgs.add(details.getCommand());
        if (details.getArgs() != null) {
            commandWithArgs.addAll(details.getArgs());
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commandWithArgs);
        if (details.getEnv() != null) {
            processBuilder.environment().putAll(details.getEnv());
        }

        // Redirect output and error streams to the main process's streams for logging
        processBuilder.redirectOutput(Redirect.INHERIT);
        processBuilder.redirectError(Redirect.INHERIT);

        try {
            log.info("Starting external MCP server: {} with command: {}", serverName, String.join(" ", commandWithArgs));
            Process process = processBuilder.start();
            runningServers.put(serverName, process);
            log.info("External MCP server {} started successfully.", serverName);

            // Asynchronously monitor the process to log when it exits
            executorService.submit(() -> {
                try {
                    int exitCode = process.waitFor();
                    log.info("External MCP server {} has exited with code: {}.", serverName, exitCode);
                } catch (InterruptedException e) {
                    log.warn("Monitoring for external MCP server {} was interrupted.", serverName);
                    Thread.currentThread().interrupt();
                } finally {
                    runningServers.remove(serverName);
                }
            });

        } catch (IOException e) {
            log.error("Failed to start external MCP server: {}. Error: {}", serverName, e.getMessage(), e);
        }
    }

    public void stopServer(String serverName) {
        Process process = runningServers.get(serverName);
        if (process != null) {
            log.info("Stopping external MCP server: {}", serverName);
            process.destroy(); // Sends SIGTERM
            try {
                // Wait for a bit for graceful shutdown, then force kill if necessary
                if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.warn("External MCP server {} did not stop gracefully after 5 seconds, forcing kill.", serverName);
                    process.destroyForcibly(); // Sends SIGKILL
                }
                log.info("External MCP server {} stopped.", serverName);
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for server {} to stop.", serverName);
                process.destroyForcibly();
                Thread.currentThread().interrupt();
            }
            runningServers.remove(serverName);
        } else {
            log.warn("External MCP server {} is not running or not found.", serverName);
        }
    }

    @PreDestroy
    public void shutdownAllServers() {
        log.info("Shutting down all external MCP servers...");
        new ArrayList<>(runningServers.keySet()).forEach(this::stopServer); // Iterate over a copy to avoid ConcurrentModificationException
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("All external MCP servers have been requested to shut down.");
    }

    public Map<String, Process> getRunningServers() {
        return Map.copyOf(runningServers);
    }
}

