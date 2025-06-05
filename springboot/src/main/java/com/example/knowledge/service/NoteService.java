 package com.example.knowledge.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.knowledge.entity.Note;

public interface NoteService {
    Note createNote(Note note);
    Note updateNote(Note note);
    boolean deleteNote(Long noteId, Long userId);
    Note getNoteById(Long noteId);
    IPage<Note> getNotes(Long userId, Long folderId, Long tagId, String keyword, int pageNum, int pageSize);
    boolean favoriteNote(Long noteId, boolean favorite);
    boolean restoreNote(Long noteId);
    boolean realDeleteNote(Long noteId);
    java.util.List<Note> getDeletedNotes(Long userId);
}