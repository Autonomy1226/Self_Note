# 个人知识管理系统后端

本项目基于 Spring Boot，提供个人知识管理系统的后端服务。

## 主要技术栈
- Spring Boot 3
- Spring Data JPA
- Spring Security
- MySQL
- Lombok
- Swagger (OpenAPI)

## 目录结构
```
springboot/
├── src/main/java/com/example/knowledge/         # Java 源码
├── src/main/resources/                          # 配置文件
├── pom.xml                                      # Maven 依赖
└── README.md
```

## 启动方式
1. 配置 MySQL 数据库，修改 `application.yml` 中的数据库连接信息。
2. 执行 `mvn clean install` 编译项目。
3. 运行主类 `KnowledgeSystemApplication` 启动服务。

## 端口
默认端口：`8080`

---
后续将逐步完善用户、笔记、标签等核心功能。 