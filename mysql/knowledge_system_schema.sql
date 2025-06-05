-- 自动创建数据库并切换
CREATE DATABASE IF NOT EXISTS knowledge_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE knowledge_db;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '加密密码',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文件夹表
CREATE TABLE IF NOT EXISTS folder (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    name VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    parent_id BIGINT DEFAULT NULL COMMENT '父文件夹ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES folder(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 笔记表
CREATE TABLE IF NOT EXISTS note (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    folder_id BIGINT DEFAULT NULL COMMENT '所属文件夹',
    is_favorite TINYINT(1) DEFAULT 0 COMMENT '是否收藏',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (folder_id) REFERENCES folder(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签表
CREATE TABLE IF NOT EXISTS tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '所属用户',
    name VARCHAR(50) NOT NULL COMMENT '标签名',
    color VARCHAR(20) DEFAULT NULL COMMENT '标签颜色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_tag (user_id, name),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 笔记-标签关联表
CREATE TABLE IF NOT EXISTS note_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    note_id BIGINT NOT NULL COMMENT '笔记ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    FOREIGN KEY (note_id) REFERENCES note(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试数据
INSERT INTO user (id, username, email, password, create_time, update_time) VALUES
  (1, 'admin', 'admin@example.com', 'admin123', NOW(), NOW()),
  (2, 'user1', 'user1@example.com', 'user123', NOW(), NOW()),
  (3, 'user2', 'user2@example.com', 'user456', NOW(), NOW())
ON DUPLICATE KEY UPDATE username=VALUES(username);

INSERT INTO folder (id, user_id, name, parent_id, create_time, update_time) VALUES
  (1, 1, '默认文件夹', NULL, NOW(), NOW()),
  (2, 2, '学习', NULL, NOW(), NOW()),
  (3, 2, '工作', NULL, NOW(), NOW()),
  (4, 3, '生活', NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO tag (id, user_id, name, color, create_time) VALUES
  (1, 1, '重要', '#f56c6c', NOW()),
  (2, 2, '学习', '#67c23a', NOW()),
  (3, 2, '工作', '#409eff', NOW()),
  (4, 3, '生活', '#e6a23c', NOW())
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO note (id, user_id, title, content, folder_id, is_favorite, is_deleted, create_time, update_time) VALUES
  (1, 1, '系统介绍', '这是管理员的第一条笔记。', 1, 1, 0, NOW(), NOW()),
  (2, 2, 'Vue3 学习笔记', 'Vue3 是一套前端框架。', 2, 0, 0, NOW(), NOW()),
  (3, 2, '工作计划', '本周完成接口开发。', 3, 1, 0, NOW(), NOW()),
  (4, 3, '生活随笔', '今天心情不错。', 4, 0, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO note_tag (id, note_id, tag_id) VALUES
  (1, 1, 1),
  (2, 2, 2),
  (3, 3, 3),
  (4, 4, 4)
ON DUPLICATE KEY UPDATE note_id=VALUES(note_id), tag_id=VALUES(tag_id); 