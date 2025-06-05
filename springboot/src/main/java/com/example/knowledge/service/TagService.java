package com.example.knowledge.service;

import com.example.knowledge.entity.Tag;
import java.util.List;

public interface TagService {
    Tag createTag(Tag tag);
    Tag updateTag(Tag tag);
    boolean deleteTag(Long tagId, Long userId);
    Tag getTagById(Long tagId);
    List<Tag> getTagsByUser(Long userId);
    boolean addTagToNote(Long noteId, Long tagId);
    boolean removeTagFromNote(Long noteId, Long tagId);
    List<Tag> getTagsByNoteId(Long noteId);
} 