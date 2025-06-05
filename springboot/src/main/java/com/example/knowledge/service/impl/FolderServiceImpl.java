package com.example.knowledge.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.knowledge.entity.Folder;
import com.example.knowledge.mapper.FolderMapper;
import com.example.knowledge.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class FolderServiceImpl implements FolderService {
    @Autowired
    private FolderMapper folderMapper;

    @Override
    public Folder createFolder(Folder folder) {
        folderMapper.insert(folder);
        return folder;
    }

    @Override
    public Folder updateFolder(Folder folder) {
        folderMapper.updateById(folder);
        return folderMapper.selectById(folder.getId());
    }

    @Override
    public boolean deleteFolder(Long folderId, Long userId) {
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null || !folder.getUserId().equals(userId)) return false;
        return folderMapper.deleteById(folderId) > 0;
    }

    @Override
    public Folder getFolderById(Long folderId) {
        return folderMapper.selectById(folderId);
    }

    @Override
    public List<Folder> getFoldersByUser(Long userId) {
        QueryWrapper<Folder> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        return folderMapper.selectList(query);
    }

    @Override
    public List<Folder> getFolderTree(Long userId) {
        List<Folder> all = getFoldersByUser(userId);
        return buildTree(all, 0L);
    }

    private List<Folder> buildTree(List<Folder> all, Long parentId) {
        List<Folder> tree = new ArrayList<>();
        for (Folder folder : all) {
            if ((folder.getParentId() == null && parentId == 0L) || (folder.getParentId() != null && folder.getParentId().equals(parentId))) {
                List<Folder> children = buildTree(all, folder.getId());
                // 可扩展children字段
                tree.add(folder);
            }
        }
        return tree;
    }
} 