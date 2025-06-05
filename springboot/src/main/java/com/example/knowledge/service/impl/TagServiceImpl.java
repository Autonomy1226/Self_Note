package com.example.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.knowledge.entity.NoteTag;
import com.example.knowledge.entity.Tag;
import com.example.knowledge.mapper.NoteTagMapper;
import com.example.knowledge.mapper.TagMapper;
import com.example.knowledge.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private NoteTagMapper noteTagMapper;

    @Override
    public Tag createTag(Tag tag) {
        tagMapper.insert(tag);
        return tag;
    }

    @Override
    public Tag updateTag(Tag tag) {
        tagMapper.updateById(tag);
        return tagMapper.selectById(tag.getId());
    }

    @Override
    public boolean deleteTag(Long tagId, Long userId) {
        Tag tag = tagMapper.selectById(tagId);
        if (tag == null || !tag.getUserId().equals(userId)) return false;
        return tagMapper.deleteById(tagId) > 0;
    }

    @Override
    public Tag getTagById(Long tagId) {
        return tagMapper.selectById(tagId);
    }

    @Override
    public List<Tag> getTagsByUser(Long userId) {
        QueryWrapper<Tag> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        return tagMapper.selectList(query);
    }

    @Override
    public boolean addTagToNote(Long noteId, Long tagId) {
        NoteTag noteTag = new NoteTag();
        noteTag.setNoteId(noteId);
        noteTag.setTagId(tagId);
        return noteTagMapper.insert(noteTag) > 0;
    }

    @Override
    public boolean removeTagFromNote(Long noteId, Long tagId) {
        QueryWrapper<NoteTag> query = new QueryWrapper<>();
        query.eq("note_id", noteId).eq("tag_id", tagId);
        return noteTagMapper.delete(query) > 0;
    }

    @Override
    public List<Tag> getTagsByNoteId(Long noteId) {
        QueryWrapper<NoteTag> query = new QueryWrapper<>();
        query.eq("note_id", noteId);
        List<NoteTag> noteTags = noteTagMapper.selectList(query);
        List<Tag> tags = new ArrayList<>();
        for (NoteTag nt : noteTags) {
            Tag tag = tagMapper.selectById(nt.getTagId());
            if (tag != null) tags.add(tag);
        }
        return tags;
    }
} 