package com.example.knowledge.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.knowledge.entity.Note;
import com.example.knowledge.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    
    @Autowired
    private NoteService noteService;
    
    @GetMapping("/notes")
    public IPage<Note> searchNotes(
            @RequestParam Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        logger.info("搜索笔记: userId={}, keyword={}, folderId={}, tagId={}, pageNum={}, pageSize={}",
                userId, keyword, folderId, tagId, pageNum, pageSize);
        
        return noteService.getNotes(userId, folderId, tagId, keyword, pageNum, pageSize);
    }
}