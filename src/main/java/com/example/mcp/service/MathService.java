package com.example.mcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class MathService {
    private static final Logger log = LoggerFactory.getLogger(MathService.class);
    @Tool(description = "加法方法")
    public Integer add(Integer a, Integer b) {
        log.info("===============add方法被调用: a={}, b={}", a, b);
        return a + b;
    }

    @Tool(description = "乘法方法")
    public Integer multiply(Integer a, Integer b) {
        log.info("===============multiply方法被调用: a={}, b={}", a, b);
        return a * b;
    }
}
