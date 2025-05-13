# MCP Spring AI Service

## 1. 概述

本服务是一个基于 Spring Boot 和 Spring AI 实现的 Java 应用，提供模型上下文协议 (MCP) Server 能力。它能够集成 AI 大语言模型（通过 Spring AI）并与客户端进行 MCP 交互，同时还能根据 JSON 配置动态启动和管理其他外部 MCP Server 进程。

## 2. 功能特性

-   **MCP Server 实现**：基于 `mcp-java-sdk`（特别是 `mcp-spring-webflux`），提供标准的 MCP 端点（如 `/mcp/message`, `/mcp/capabilities`）。
-   **Spring AI 集成**：集成 Spring AI，可配置连接到 OpenAI GPT 模型（或其他兼容模型）进行智能对话处理。
-   **外部 MCP Server 集成**：通过读取 JSON 配置文件 (`mcp-servers.json`)，动态启动和管理多个外部 MCP Server 进程（例如，通过执行 `npx` 命令启动 Node.js 实现的 MCP Server）。
-   **响应式设计**：采用 Spring WebFlux 实现异步、非阻塞的请求处理。
-   **可配置性**：通过 `application.properties` 和外部 JSON 文件进行灵活配置。

## 3. 项目结构

```
.mcp-spring-ai-service/
├── pom.xml                   # Maven 项目配置文件
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/mcpspringaiservice/
│       │       ├── McpSpringAiServiceApplication.java  # Spring Boot 主应用类
│       │       ├── config/
│       │       │   └── ExternalMcpServersConfig.java # 外部MCP Server配置类
│       │       ├── controller/
│       │       │   └── McpController.java              # MCP协议端点控制器
│       │       └── service/
│       │           └── ExternalMcpServerManager.java # 外部MCP Server管理服务
│       └── resources/
│           └── application.properties    # Spring Boot 配置文件
├── mcp-servers.json          # 外部MCP Server配置文件示例 (项目根目录外，路径可配置)
├── architecture_design.md    # 项目架构设计文档
└── todo.md                   # 项目任务清单
```

## 4. 环境要求与构建

-   Java 17 或更高版本
-   Maven 3.6.x 或更高版本
-   Node.js 和 npm/npx (如果需要运行 `mcp-servers.json` 中配置的 Node.js 类型的外部 MCP Server)
-   一个有效的 OpenAI API Key (或其他 Spring AI 支持的模型提供商的凭证)

**构建项目：**

在项目根目录 (`mcp-spring-ai-service`) 下执行以下 Maven 命令：

```bash
mvn clean package
```

这会在 `target/` 目录下生成一个可执行的 JAR 文件，例如 `mcp-spring-ai-service-0.0.1-SNAPSHOT.jar`。

## 5. 配置

### 5.1. `application.properties`

文件位于 `src/main/resources/application.properties`。

关键配置项：

-   `spring.ai.openai.api-key`: **必须配置**。替换 `YOUR_OPENAI_API_KEY` 为您的 OpenAI API 密钥。
    ```properties
    spring.ai.openai.api-key=sk-YOUR_ACTUAL_OPENAI_API_KEY
    ```
-   `mcp.external.servers.config.path`: 外部 MCP Server 配置文件的绝对路径。默认为 `/home/ubuntu/mcp-servers.json`。您可以根据实际存放位置修改此路径。
    ```properties
    mcp.external.servers.config.path=/path/to/your/mcp-servers.json
    ```
-   `logging.level.*`: 可以调整日志级别进行调试。

### 5.2. `mcp-servers.json`

此文件定义了需要由本服务启动和管理的外部 MCP Server。路径由 `application.properties` 中的 `mcp.external.servers.config.path` 指定。

示例格式：

```json
{
  "mcpServers": {
    "ExamplePuppeteerServer": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-puppeteer",
        "--port",
        "8081"
      ],
      "env": {}
    },
    "AnotherExampleServer": {
      "command": "echo",
      "args": [
        "MCP Server AnotherExampleServer started with dummy command"
      ],
      "env": {}
    }
  }
}
```

-   `mcpServers`: 一个对象，键是您为外部服务器指定的名称，值是该服务器的详细信息。
    -   `command`: 启动服务器的主命令 (例如 `npx`, `java`, `python`)。
    -   `args`: 命令的参数列表。
    -   `env`: 需要为该进程设置的环境变量 (键值对)。

## 6. 运行服务

1.  确保已完成构建和配置步骤。
2.  将 `mcp-servers.json` 文件放置到 `application.properties` 中配置的路径下。
3.  运行 JAR 文件：
    ```bash
    java -jar target/mcp-spring-ai-service-0.0.1-SNAPSHOT.jar
    ```

服务默认启动在 `8080` 端口。外部 MCP Server (如示例中的 Puppeteer Server) 会根据其自身配置（或命令行参数）在其他端口启动 (例如 `8081`)。

## 7. 测试 MCP 端点

您可以使用任何支持发送 HTTP 请求的工具 (如 `curl`, Postman, 或 MCP 客户端库) 来测试 MCP 端点。

### 7.1. 获取 Capabilities

-   **URL**: `http://localhost:8080/mcp/capabilities`
-   **Method**: `POST` (MCP 规范通常使用 POST，即使是获取信息)
-   **Body**: (可选，可以为空 JSON `{}` 或无 body)

### 7.2. 发送消息 (与 AI 交互)

-   **URL**: `http://localhost:8080/mcp/message`
-   **Method**: `POST`
-   **Headers**: `Content-Type: application/x-mcp-json-stream` (MCP 规范)
-   **Body** (示例, application/x-mcp-json-stream 意味着每个 JSON 对象是一行):
    ```json
    {"type":"request","id":"req-1","message":{"id":"msg-1","conversationId":"conv-1","role":"user","text":"你好，世界！"}}
    ```

服务将以 Server-Sent Events (SSE) 的形式流式返回响应。

## 8. 架构与设计

详细的架构设计请参考项目中的 `architecture_design.md` 文件。

## 9. 任务清单

项目的开发任务和进度记录在 `todo.md` 文件中。

