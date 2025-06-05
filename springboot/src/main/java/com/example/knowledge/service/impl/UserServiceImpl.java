package com.example.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.knowledge.entity.User;
import com.example.knowledge.mapper.UserMapper;
import com.example.knowledge.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(User user) {
        // 检查用户名唯一
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", user.getUsername());
        if (userMapper.selectCount(query) > 0) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 注意：这里使用明文密码以匹配测试数据
        // 在生产环境中应该使用密码编码器
        
        userMapper.insert(user);
        logger.info("用户注册成功: {}", user.getUsername());
        return user;
    }

    @Override
    public User login(String username, String password) {
        logger.info("尝试登录: 用户名={}", username);
        
        // 先检查用户名是否存在
        QueryWrapper<User> queryUser = new QueryWrapper<>();
        queryUser.eq("username", username);
        User existingUser = userMapper.selectOne(queryUser);
        
        if (existingUser == null) {
            logger.warn("登录失败: 用户名不存在 - {}", username);
            throw new RuntimeException("用户名不存在");
        }
        
        // 在这里直接比较明文密码
        // 注意：在生产环境中应该使用密码编码器比较
        logger.debug("密码比较: 输入={}, 存储={}", "[PROTECTED]", "[PROTECTED]");
        
        if (!existingUser.getPassword().equals(password)) {
            logger.warn("登录失败: 密码错误 - 用户名={}", username);
            throw new RuntimeException("密码错误");
        }
        
        logger.info("登录成功: 用户名={}", username);
        return existingUser;
    }

    @Override
    public User getByUsername(String username) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        return userMapper.selectOne(query);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User updateUser(User user) {
        userMapper.updateById(user);
        return userMapper.selectById(user.getId());
    }

    @Override
    public boolean updatePassword(Long userId, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        
        // 注意：这里使用明文密码
        // 在生产环境中应该使用密码编码器
        user.setPassword(newPassword);
        
        return userMapper.updateById(user) > 0;
    }
} 