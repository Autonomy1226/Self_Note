package com.example.knowledge.controller;

import com.example.knowledge.entity.Tag;
import com.example.knowledge.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    @PostMapping
    public Tag createTag(@RequestBody Tag tag) {
        return tagService.createTag(tag);
    }

    @PutMapping
    public Tag updateTag(@RequestBody Tag tag) {
        return tagService.updateTag(tag);
    }

    @DeleteMapping("/{id}")
    public boolean deleteTag(@PathVariable Long id, @RequestParam Long userId) {
        return tagService.deleteTag(id, userId);
    }

    @GetMapping("/{id}")
    public Tag getTag(@PathVariable Long id) {
        return tagService.getTagById(id);
    }

    @GetMapping
    public List<Tag> getTagsByUser(@RequestParam Long userId) {
        return tagService.getTagsByUser(userId);
    }

    @PostMapping("/note/{noteId}/add")
    public boolean addTagToNote(@PathVariable Long noteId, @RequestParam Long tagId) {
        return tagService.addTagToNote(noteId, tagId);
    }

    @DeleteMapping("/note/{noteId}/remove")
    public boolean removeTagFromNote(@PathVariable Long noteId, @RequestParam Long tagId) {
        return tagService.removeTagFromNote(noteId, tagId);
    }

    @GetMapping("/note/{noteId}")
    public List<Tag> getTagsByNoteId(@PathVariable Long noteId) {
        return tagService.getTagsByNoteId(noteId);
    }
} 