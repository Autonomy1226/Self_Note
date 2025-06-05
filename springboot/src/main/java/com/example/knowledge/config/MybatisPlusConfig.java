package com.example.knowledge.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.knowledge.mapper")
public class MybatisPlusConfig {
} 