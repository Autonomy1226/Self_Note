package com.example.knowledge.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.knowledge.entity.Note;
import com.example.knowledge.mapper.NoteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计数据控制器
 */
@RestController
@RequestMapping("/api/search")
public class StatsController {
    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);
    
    @Autowired
    private NoteMapper noteMapper;
    
    /**
     * 获取用户的笔记统计信息
     * @param userId 用户ID
     * @return 统计数据
     */
    @GetMapping("/stats")
    public Map<String, Integer> getStats(@RequestParam Long userId) {
        logger.info("获取统计信息: userId={}", userId);
        
        Map<String, Integer> stats = new HashMap<>();
        
        // 查询总笔记数
        QueryWrapper<Note> totalQuery = new QueryWrapper<>();
        totalQuery.eq("user_id", userId);
        Long totalCount = noteMapper.selectCount(totalQuery);
        stats.put("total", totalCount.intValue());
        
        // 查询收藏笔记数
        QueryWrapper<Note> favoriteQuery = new QueryWrapper<>();
        favoriteQuery.eq("user_id", userId).eq("is_favorite", 1);
        Long favoriteCount = noteMapper.selectCount(favoriteQuery);
        stats.put("favorite", favoriteCount.intValue());
        
        // 查询已删除笔记数
        QueryWrapper<Note> deletedQuery = new QueryWrapper<>();
        deletedQuery.eq("user_id", userId).eq("is_deleted", 1);
        Long deletedCount = noteMapper.selectCount(deletedQuery);
        stats.put("deleted", deletedCount.intValue());
        
        logger.info("统计结果: 总数={}, 收藏={}, 已删除={}", totalCount, favoriteCount, deletedCount);
        
        return stats;
    }
} 