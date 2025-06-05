package com.example.knowledge.service;

import com.example.knowledge.entity.User;

public interface UserService {
    User register(User user);
    User login(String username, String password);
    User getByUsername(String username);
    User getById(Long id);
    User updateUser(User user);
    boolean updatePassword(Long userId, String newPassword);
} 