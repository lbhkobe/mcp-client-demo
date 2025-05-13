package com.example.mcp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class WeatherService {
    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);
    @Tool(description = "根据城市名称获取天气预报")
    public String getWeatherByCity(String city) {
        log.info("===============getWeatherByCity方法被调用：city="+city);
        Map<String, String> mockData = Map.of(
                "西安", "天气炎热",
                "北京", "晴空万里",
                "上海", "阴雨绵绵"
        );
        return mockData.getOrDefault(city, "抱歉：未查询到对应城市！");
    }

}