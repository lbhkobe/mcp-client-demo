# MCP服务器与AI大模型集成

这是一个基于Spring Boot和Spring AI的Java服务，实现了MCP（Machine Conversation Protocol）服务器功能，并集成了AI大模型和外部MCP服务器。

## 功能特点

- 提供标准MCP服务器接口，兼容OpenAI API格式
- 集成Spring AI，支持多种AI大模型
- 支持转发请求到外部MCP服务器
- 灵活的配置选项，便于扩展

## 技术栈

- Java 17
- Spring Boot 3.2.3
- Spring AI 0.8.0
- Spring WebFlux
- Lombok

## 快速开始

### 前提条件

- JDK 17或更高版本
- Maven 3.6或更高版本
- OpenAI API密钥（或其他兼容的AI服务提供商）

### 配置

1. 克隆项目到本地

2. 设置环境变量（或在`application.yml`中直接修改）

```bash
# 设置OpenAI API密钥
export OPENAI_API_KEY=your-api-key-here

# 可选：设置OpenAI API基础URL（如果使用代理或其他兼容服务）
export OPENAI_BASE_URL=https://api.openai.com

# 可选：设置外部MCP服务器地址（多个地址用逗号分隔）
export MCP_EXTERNAL_SERVERS=https://external-mcp-server1.com,https://external-mcp-server2.com
```

### 构建和运行

```bash
# 构建项目
mvn clean package

# 运行应用
java -jar target/mcp-server-0.0.1-SNAPSHOT.jar
```

或者使用Maven直接运行：

```bash
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动。

## API接口

### 本地AI处理

```
POST /v1/chat/completions
```

使用配置的AI模型（如OpenAI）处理请求。

### 外部MCP服务器处理

```
POST /v1/external/chat/completions
```

将请求转发到配置的外部MCP服务器处理。

## 请求示例

```json
{
  "model": "gpt-3.5-turbo",
  "messages": [
    {
      "role": "system",
      "content": "你是一个有用的助手。"
    },
    {
      "role": "user",
      "content": "你好，请介绍一下自己。"
    }
  ]
}
```

## 扩展和定制

- 添加新的AI模型支持：在`ApplicationConfig`中配置新的AI客户端
- 实现更复杂的负载均衡：修改`McpService`中的外部服务器选择逻辑
- 添加认证和授权：集成Spring Security

## 许可证

MIT