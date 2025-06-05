package com.example.knowledge.controller;

import com.example.knowledge.entity.Folder;
import com.example.knowledge.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {
    @Autowired
    private FolderService folderService;

    @PostMapping
    public Folder createFolder(@RequestBody Folder folder) {
        return folderService.createFolder(folder);
    }

    @PutMapping
    public Folder updateFolder(@RequestBody Folder folder) {
        return folderService.updateFolder(folder);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFolder(@PathVariable Long id, @RequestParam Long userId) {
        return folderService.deleteFolder(id, userId);
    }

    @GetMapping("/{id}")
    public Folder getFolder(@PathVariable Long id) {
        return folderService.getFolderById(id);
    }

    @GetMapping
    public List<Folder> getFoldersByUser(@RequestParam Long userId) {
        return folderService.getFoldersByUser(userId);
    }

    @GetMapping("/tree")
    public List<Folder> getFolderTree(@RequestParam Long userId) {
        return folderService.getFolderTree(userId);
    }
} 