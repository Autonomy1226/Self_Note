package com.example.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.knowledge.entity.Note;
import com.example.knowledge.entity.NoteTag;
import com.example.knowledge.mapper.NoteMapper;
import com.example.knowledge.mapper.NoteTagMapper;
import com.example.knowledge.service.SearchStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SearchStatServiceImpl implements SearchStatService {
    @Autowired
    private NoteMapper noteMapper;
    @Autowired
    private NoteTagMapper noteTagMapper;

    @Override
    public IPage<Note> searchNotes(Long userId, String keyword, Long folderId, Long tagId, int pageNum, int pageSize) {
        QueryWrapper<Note> query = new QueryWrapper<>();
        query.eq("user_id", userId).eq("is_deleted", false);
        if (folderId != null) query.eq("folder_id", folderId);
        if (keyword != null && !keyword.isEmpty()) {
            query.and(q -> q.like("title", keyword).or().like("content", keyword));
        }
        if (tagId != null) {
            List<NoteTag> noteTags = noteTagMapper.selectList(new QueryWrapper<NoteTag>().eq("tag_id", tagId));
            if (!noteTags.isEmpty()) {
                query.in("id", noteTags.stream().map(NoteTag::getNoteId).collect(Collectors.toList()));
            } else {
                query.eq("id", -1);
            }
        }
        query.orderByDesc("update_time");
        return noteMapper.selectPage(new Page<>(pageNum, pageSize), query);
    }

    @Override
    public Map<String, Object> getNoteStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        QueryWrapper<Note> base = new QueryWrapper<Note>().eq("user_id", userId);
        stats.put("total", noteMapper.selectCount(base));
        stats.put("favorite", noteMapper.selectCount(new QueryWrapper<Note>().eq("user_id", userId).eq("is_favorite", true)));
        stats.put("deleted", noteMapper.selectCount(new QueryWrapper<Note>().eq("user_id", userId).eq("is_deleted", true)));
        return stats;
    }
} 