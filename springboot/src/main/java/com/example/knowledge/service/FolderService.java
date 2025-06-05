package com.example.knowledge.service;

import com.example.knowledge.entity.Folder;
import java.util.List;

public interface FolderService {
    Folder createFolder(Folder folder);
    Folder updateFolder(Folder folder);
    boolean deleteFolder(Long folderId, Long userId);
    Folder getFolderById(Long folderId);
    List<Folder> getFoldersByUser(Long userId);
    List<Folder> getFolderTree(Long userId);
} 