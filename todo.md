# 基于 Spring AI 实现 MCP Server 的 Java 服务 - 待办事项

## 阶段一：需求澄清与技术调研 (已完成)

-   [x] 澄清 MCP Server 在项目中的具体含义和主要功能。
-   [x] 明确 Spring AI 在服务中的角色及期望集成的 AI 大模型类型。
-   [x] 确认与其他开放 MCP Server 的集成方式和数据格式。
-   [x] 研究模型上下文协议 (MCP) 的官方文档和规范。
-   [x]调研 MCP Java SDK 及其与 Spring AI 的集成方案，特别是 `mcp-spring` 模块。
-   [x] 分析 `mcp-spring-webflux` 和 `mcp-spring-webmvc` 模块，了解其在 Spring Boot 中的集成方式。

## 阶段二：关键集成点总结与架构设计 (已完成)

-   [x] **总结 MCP 与 Spring AI 集成的关键技术点：**
    -   [x] 记录 MCP Java SDK 的核心依赖和配置方式 (例如 Maven/Gradle 依赖)。
    -   [x] 明确 `mcp-spring-webflux` 或 `mcp-spring-webmvc` 的选型依据和具体配置步骤。
    -   [x] 记录如何在 Spring Boot 应用中定义和暴露 MCP Server 端点。
    -   [x] 梳理通过 Spring AI 集成开源大模型的标准流程和配置项。
    -   [x] 明确解析和执行外部 MCP Server JSON 配置的实现思路。
-   [x] **设计 Java 服务整体架构：**
    -   [x] 绘制服务模块图，清晰展示 MCP Server 模块、Spring AI 集成模块、外部 MCP Server 调用模块等。
    -   [x] 定义各模块间的接口和数据交互格式。
    -   [x] 考虑服务的可配置性、可扩展性和错误处理机制。

## 阶段三：原型实现 (已完成)

-   [x] **搭建 Spring Boot 项目基础框架：**
    -   [x] 初始化 Spring Boot 项目，选择 Java 版本和构建工具。
    -   [x] 添加 Spring AI 和 MCP Java SDK (`mcp-spring`) 的相关依赖。
-   [x] **实现核心 MCP Server 功能：**
    -   [x] 根据 MCP 协议规范，实现基础的 MCP Server 端点。
    -   [x] 确保服务能够响应 MCP 客户端的请求。
-   [x] **集成 Spring AI 与大模型：**
    -   [x] 配置 Spring AI 连接到一个或多个开源 AI 大模型。
    -   [x] 实现通过 MCP Server 调用 AI 大模型进行处理的逻辑。
-   [x] **实现与其他 MCP Server 的集成：**
    -   [x] 开发解析用户提供的 JSON 配置的功能。
    -   [x] 实现根据配置动态调用外部 MCP Server 的逻辑 (例如通过执行命令 `npx -y @modelcontextprotocol/server-puppeteer`)。
    -   [x] 处理与外部 MCP Server 通信的成功与失败情况。

## 阶段四：验证与测试 (已完成)

-   [x] **单元测试：**
    -   [x] 针对核心模块编写单元测试用例。
-   [x] **集成测试：**
    -   [x] 测试 MCP Server 功能是否符合协议规范。
    -   [x] 测试与 AI 大模型的集成是否正常工作。
    -   [x] 测试与外部 MCP Server 的集成是否按预期执行。
-   [x] **功能验证：**
    -   [x] 模拟实际使用场景进行端到端的功能验证。

## 阶段五：文档编写与交付

-   [ ] **编写项目说明文档：**
    -   [ ] 描述项目架构、功能模块、配置方法和部署步骤。
    -   [ ] 提供 API 使用示例（如果适用）。
-   [ ] **整理并打包源代码。**
-   [ ] **向用户发送最终交付物。**

