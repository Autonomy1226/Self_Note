package com.example.knowledge.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.knowledge.entity.Note;
import com.example.knowledge.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @PostMapping
    public Note createNote(@RequestBody Note note) {
        return noteService.createNote(note);
    }

    @PutMapping
    public Note updateNote(@RequestBody Note note) {
        return noteService.updateNote(note);
    }

    @DeleteMapping("/{id}")
    public boolean deleteNote(@PathVariable Long id, @RequestParam Long userId) {
        return noteService.deleteNote(id, userId);
    }

    @GetMapping("/{id}")
    public Note getNote(@PathVariable Long id) {
        return noteService.getNoteById(id);
    }

    @GetMapping
    public IPage<Note> getNotes(@RequestParam Long userId,
                                @RequestParam(required = false) Long folderId,
                                @RequestParam(required = false) Long tagId,
                                @RequestParam(required = false) String keyword,
                                @RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "10") int pageSize) {
        return noteService.getNotes(userId, folderId, tagId, keyword, pageNum, pageSize);
    }

    @PutMapping("/{id}/favorite")
    public boolean favoriteNote(@PathVariable Long id, @RequestParam boolean favorite) {
        return noteService.favoriteNote(id, favorite);
    }

    @PutMapping("/{id}/restore")
    public boolean restoreNote(@PathVariable Long id) {
        return noteService.restoreNote(id);
    }

    @DeleteMapping("/{id}/real")
    public boolean realDeleteNote(@PathVariable Long id) {
        return noteService.realDeleteNote(id);
    }

    @GetMapping("/deleted")
    public List<Note> getDeletedNotes(@RequestParam Long userId) {
        return noteService.getDeletedNotes(userId);
    }
} 