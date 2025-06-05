package com.example.knowledge.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.knowledge.entity.Note;
import java.util.Map;

public interface SearchStatService {
    IPage<Note> searchNotes(Long userId, String keyword, Long folderId, Long tagId, int pageNum, int pageSize);
    Map<String, Object> getNoteStats(Long userId);
} 