package com.example.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.knowledge.entity.Note;
import com.example.knowledge.mapper.NoteMapper;
import com.example.knowledge.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteMapper noteMapper;

    @Override
    public Note createNote(Note note) {
        note.setIsDeleted(false);
        note.setIsFavorite(false);
        noteMapper.insert(note);
        return note;
    }

    @Override
    public Note updateNote(Note note) {
        noteMapper.updateById(note);
        return noteMapper.selectById(note.getId());
    }

    @Override
    public boolean deleteNote(Long noteId, Long userId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null || !note.getUserId().equals(userId)) return false;
        note.setIsDeleted(true);
        return noteMapper.updateById(note) > 0;
    }

    @Override
    public Note getNoteById(Long noteId) {
        return noteMapper.selectById(noteId);
    }

    @Override
    public IPage<Note> getNotes(Long userId, Long folderId, Long tagId, String keyword, int pageNum, int pageSize) {
        QueryWrapper<Note> query = new QueryWrapper<>();
        query.eq("user_id", userId).eq("is_deleted", false);
        if (folderId != null) query.eq("folder_id", folderId);
        if (keyword != null && !keyword.isEmpty()) {
            query.and(q -> q.like("title", keyword).or().like("content", keyword));
        }
        // tagId 过滤可后续实现
        query.orderByDesc("update_time");
        return noteMapper.selectPage(new Page<>(pageNum, pageSize), query);
    }

    @Override
    public boolean favoriteNote(Long noteId, boolean favorite) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;
        note.setIsFavorite(favorite);
        return noteMapper.updateById(note) > 0;
    }

    @Override
    public boolean restoreNote(Long noteId) {
        Note note = noteMapper.selectById(noteId);
        if (note == null) return false;
        note.setIsDeleted(false);
        return noteMapper.updateById(note) > 0;
    }

    @Override
    public boolean realDeleteNote(Long noteId) {
        return noteMapper.deleteById(noteId) > 0;
    }

    @Override
    public List<Note> getDeletedNotes(Long userId) {
        QueryWrapper<Note> query = new QueryWrapper<>();
        query.eq("user_id", userId).eq("is_deleted", true);
        return noteMapper.selectList(query);
    }
} 