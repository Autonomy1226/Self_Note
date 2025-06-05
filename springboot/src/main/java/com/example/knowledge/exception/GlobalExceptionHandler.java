package com.example.knowledge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        // 记录详细的异常信息
        logger.error("捕获到运行时异常: {}", e.getMessage(), e);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        response.put("error", "true");
        
        // 登录失败通常返回401状态码
        if (e.getMessage() != null && (
            e.getMessage().contains("用户名不存在") || 
            e.getMessage().contains("密码错误") || 
            e.getMessage().contains("用户名或密码错误") || 
            e.getMessage().contains("登录") || 
            e.getMessage().contains("认证"))) {
            logger.debug("返回401状态码 - 未授权: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // 其他运行时异常返回500状态码
        logger.debug("返回500状态码 - 服务器错误: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 