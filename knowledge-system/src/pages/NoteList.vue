<template>
  <div class="obsidian-note-list">
    <div class="obsidian-toolbar">
      <div class="search-container">
        <div class="search-icon">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path>
          </svg>
        </div>
        <el-input 
          v-model="search" 
          placeholder="搜索笔记..." 
          clearable 
          class="obsidian-search"
          @keyup.enter="fetchNotes" 
        />
      </div>
      <el-button type="primary" @click="showCreate = true" class="obsidian-btn create-btn">
        <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="btn-icon">
          <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"></path>
        </svg>
        新建笔记
      </el-button>
    </div>

    <div class="obsidian-filters">
      <div class="filter-group">
        <div class="filter-label">文件夹:</div>
        <el-select v-model="folderId" placeholder="选择文件夹" clearable class="obsidian-select" @change="fetchNotes">
          <el-option label="全部" :value="''" />
          <el-option v-for="f in folders" :key="f.id" :label="f.name" :value="f.id" />
        </el-select>
      </div>
      <div class="filter-group">
        <div class="filter-label">标签:</div>
        <el-select v-model="tagId" placeholder="选择标签" clearable class="obsidian-select" @change="fetchNotes">
          <el-option label="全部" :value="''" />
          <el-option v-for="t in tags" :key="t.id" :label="t.name" :value="t.id" />
        </el-select>
      </div>
      <div class="view-toggle">
        <el-tooltip content="卡片视图" placement="top">
          <div class="view-btn" :class="{ active: viewMode === 'grid' }" @click="viewMode = 'grid'">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M3 3v8h8V3H3zm6 6H5V5h4v4zm0 2H3v8h8v-8H9zm-4 6v-4h4v4H5zm10-16h-8v8h8V3zm-2 6h-4V5h4v4zm2 2v8h8v-8h-8zm6 6h-4v-4h4v4z"></path>
            </svg>
          </div>
        </el-tooltip>
        <el-tooltip content="列表视图" placement="top">
          <div class="view-btn" :class="{ active: viewMode === 'list' }" @click="viewMode = 'list'">
            <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
              <path d="M3 13h2v-2H3v2zm0 4h2v-2H3v2zm0-8h2V7H3v2zm4 4h14v-2H7v2zm0 4h14v-2H7v2zM7 7v2h14V7H7z"></path>
            </svg>
          </div>
        </el-tooltip>
      </div>
    </div>

    <div class="notes-container">
      <transition-group name="fade" tag="div" class="notes-transition-group">
        <div v-if="notes.length === 0" class="empty-state" key="empty">
          <div class="empty-icon">📝</div>
          <div class="empty-text">没有找到笔记</div>
          <el-button @click="showCreate = true" class="obsidian-btn">创建第一个笔记</el-button>
        </div>
        
        <div v-else-if="viewMode === 'grid'" class="notes-grid" key="grid">
          <div v-for="note in notes" :key="note.id" class="note-card" @click="goDetail(note.id)">
            <div class="note-card-header">
              <div class="note-title">{{ note.title || '无标题笔记' }}</div>
              <div class="note-actions">
                <span class="favorite-icon" @click.stop="toggleFavorite(note)">
                  <svg viewBox="0 0 24 24" width="16" height="16" :fill="note.isFavorite ? '#FFD700' : 'currentColor'">
                    <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"></path>
                  </svg>
                </span>
                <span class="delete-icon" @click.stop="delNote(note)">
                  <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
                    <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"></path>
                  </svg>
                </span>
              </div>
            </div>
            <div class="note-card-content">{{ truncateContent(note.content) }}</div>
            <div class="note-card-footer">
              <div class="note-date">{{ formatDate(note.createTime) }}</div>
              <div v-if="note.tags && note.tags.length > 0" class="note-tags">
                <span v-for="tagId in note.tags" :key="tagId" class="note-tag-indicator">
                  #{{ getTagName(tagId) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="notes-list" key="list">
          <div v-for="note in notes" :key="note.id" class="note-list-item" @click="goDetail(note.id)">
            <div class="note-list-left">
              <div class="note-favorite" v-if="note.isFavorite">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="#FFD700">
                  <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"></path>
                </svg>
              </div>
              <div class="note-list-title">{{ note.title || '无标题笔记' }}</div>
              <div class="note-list-excerpt">{{ truncateContent(note.content, 60) }}</div>
            </div>
            <div class="note-list-right">
              <div class="note-list-date">{{ formatDateFull(note.createTime) }}</div>
              <div class="note-list-actions">
                <span class="action-btn" @click.stop="toggleFavorite(note)">
                  {{ note.isFavorite ? '取消收藏' : '收藏' }}
                </span>
                <span class="action-btn delete" @click.stop="delNote(note)">
                  删除
                </span>
              </div>
            </div>
          </div>
        </div>
      </transition-group>
    </div>

    <el-pagination
      v-if="total > pageSize"
      class="obsidian-pagination"
      background
      layout="prev, pager, next"
      :current-page="pageNum"
      :page-size="pageSize"
      :total="total"
      @current-change="val => { pageNum = val; fetchNotes(); }"
    />

    <el-dialog v-model="showCreate" title="新建笔记" width="500px" class="obsidian-dialog">
      <el-form :model="newNote" class="create-note-form">
        <el-form-item label="标题">
          <el-input v-model="newNote.title" placeholder="输入笔记标题..." />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="newNote.content" :rows="8" placeholder="输入笔记内容..." />
        </el-form-item>
        <el-form-item label="文件夹" v-if="folders.length > 0">
          <el-select v-model="newNote.folderId" placeholder="选择文件夹" clearable class="folder-select">
            <el-option v-for="f in folders" :key="f.id" :label="f.name" :value="f.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false" class="obsidian-btn">取消</el-button>
        <el-button type="primary" @click="createNote" class="obsidian-btn">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const user = JSON.parse(localStorage.getItem('user') || '{}');
const isLogin = !!user.id;
const notes = ref([]);
const pageNum = ref(1);
const pageSize = ref(12);
const total = ref(0);
const search = ref('');
const folderId = ref('');
const tagId = ref('');
const folders = ref([]);
const tags = ref([]);
const showCreate = ref(false);
const newNote = ref({ title: '', content: '', folderId: '' });
const viewMode = ref(localStorage.getItem('viewMode') || 'grid');

// 监听视图模式变化，保存到本地存储
watch(viewMode, (newVal) => {
  localStorage.setItem('viewMode', newVal);
});

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
    month: 'short', 
    day: 'numeric'
  });
};

const formatDateFull = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric',
    month: 'short', 
    day: 'numeric'
  });
};

const truncateContent = (content, length = 100) => {
  if (!content) return '';
  return content.length > length ? content.substring(0, length) + '...' : content;
};

const getTagName = (tagId) => {
  const tag = tags.value.find(t => t.id === tagId);
  return tag ? tag.name : '';
};

const fetchNotes = async () => {
  if (isLogin) {
    try {
      console.log('获取笔记: 用户ID=', user.id);
      // 使用/api/notes接口代替/api/search/notes
      const res = await axios.get('/api/notes', {
        params: {
          userId: user.id,
          keyword: search.value,
          folderId: folderId.value || undefined,
          tagId: tagId.value || undefined,
          pageNum: pageNum.value,
          pageSize: pageSize.value
        }
      });
      console.log('笔记数据:', res.data);
      notes.value = res.data.records;
      total.value = res.data.total;
    } catch (error) {
      console.error('获取笔记失败:', error);
      notes.value = [];
      total.value = 0;
    }
  } else {
    // 游客模式
    let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
    // 搜索、筛选
    let filtered = guestNotes.filter(n => {
      let match = true;
      if (search.value) match = n.title.includes(search.value) || n.content.includes(search.value);
      if (folderId.value) match = match && n.folderId == folderId.value;
      if (tagId.value && n.tags) match = match && n.tags.includes(tagId.value);
      return match;
    });
    total.value = filtered.length;
    notes.value = filtered.slice((pageNum.value-1)*pageSize.value, pageNum.value*pageSize.value);
  }
};

const fetchFolders = async () => {
  if (isLogin) {
    const res = await axios.get('/api/folders', { params: { userId: user.id } });
    folders.value = res.data;
  } else {
    folders.value = JSON.parse(localStorage.getItem('guest_folders') || '[]');
  }
};
const fetchTags = async () => {
  if (isLogin) {
    const res = await axios.get('/api/tags', { params: { userId: user.id } });
    tags.value = res.data;
  } else {
    tags.value = JSON.parse(localStorage.getItem('guest_tags') || '[]');
  }
};

const createNote = async () => {
  if (isLogin) {
    try {
      const response = await axios.post('/api/notes', { ...newNote.value, userId: user.id });
      showCreate.value = false;
      const noteId = response.data.id || response.data;
      newNote.value = { title: '', content: '', folderId: '' };
      // 自动跳转到新建的笔记页面
      router.push(`/note/${noteId}`);
    } catch (error) {
      console.error('创建笔记失败:', error);
      alert('创建笔记失败，请重试');
      fetchNotes();
    }
  } else {
    // 游客模式
    const newNoteId = Date.now();
    let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
    const newNoteObj = { 
      ...newNote.value, 
      id: newNoteId, 
      createTime: new Date().toISOString(), 
      isFavorite: false, 
      isDeleted: false, 
      tags: [] 
    };
    
    guestNotes.unshift(newNoteObj);
    localStorage.setItem('guest_notes', JSON.stringify(guestNotes));
    showCreate.value = false;
    newNote.value = { title: '', content: '', folderId: '' };
    
    // 自动跳转到新建的笔记页面
    router.push(`/note/${newNoteId}`);
  }
};

const delNote = async (note) => {
  if (confirm('确定要删除这个笔记吗？')) {
    if (isLogin) {
      await axios.delete(`/api/notes/${note.id}`, { params: { userId: user.id } });
      fetchNotes();
    } else {
      let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
      guestNotes = guestNotes.filter(n => n.id !== note.id);
      localStorage.setItem('guest_notes', JSON.stringify(guestNotes));
      fetchNotes();
    }
  }
};

const toggleFavorite = async (note) => {
  if (isLogin) {
    await axios.put(`/api/notes/${note.id}/favorite`, null, { params: { favorite: !note.isFavorite } });
    fetchNotes();
  } else {
    let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
    guestNotes = guestNotes.map(n => n.id === note.id ? { ...n, isFavorite: !n.isFavorite } : n);
    localStorage.setItem('guest_notes', JSON.stringify(guestNotes));
    fetchNotes();
  }
};

const goDetail = (id) => {
  router.push(`/note/${id}`);
};

onMounted(() => {
  fetchFolders();
  fetchTags();
  fetchNotes();
});

watch([pageNum, pageSize], fetchNotes);

const totalPages = computed(() => Math.ceil(total.value / pageSize.value));
</script>

<style scoped>
.obsidian-note-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--background-primary);
}

.obsidian-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
}

.search-container {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--text-muted);
  z-index: 1;
}

.obsidian-search {
  width: 100%;
}

.obsidian-search :deep(.el-input__inner) {
  padding-left: 36px;
  background-color: var(--background-secondary) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: 6px;
}

.obsidian-btn {
  display: flex;
  align-items: center;
  gap: 6px;
}

.create-btn {
  background-color: var(--interactive-accent) !important;
  border-color: var(--interactive-accent) !important;
}

.btn-icon {
  margin-right: 4px;
}

.obsidian-filters {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  color: var(--text-muted);
  font-size: 14px;
  white-space: nowrap;
}

.obsidian-select {
  width: 140px;
}

.view-toggle {
  display: flex;
  margin-left: auto;
  background-color: var(--background-secondary);
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.view-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--text-muted);
  transition: background-color 0.2s;
}

.view-btn:hover {
  background-color: rgba(var(--interactive-accent-rgb), 0.05);
}

.view-btn.active {
  background-color: rgba(var(--interactive-accent-rgb), 0.1);
  color: var(--interactive-accent);
}

.notes-container {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 16px;
  min-height: 300px;
}

.notes-transition-group {
  height: 100%;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: var(--text-muted);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 18px;
  margin-bottom: 24px;
}

.notes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.note-card {
  background-color: var(--background-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--card-radius);
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  height: 220px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 1px 3px var(--shadow-color);
}

.note-card:hover {
  border-color: var(--interactive-accent);
  box-shadow: 0 3px 8px var(--shadow-color);
  transform: translateY(-2px);
}

.note-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.note-card .note-title {
  font-weight: 600;
  font-size: 18px;
  color: var(--text-normal);
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.note-card-content {
  color: var(--text-muted);
  font-size: 14px;
  line-height: 1.6;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 5;
  -webkit-box-orient: vertical;
}

.note-card-footer {
  margin-top: 12px;
  font-size: 12px;
  color: var(--text-muted);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.note-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.note-tag-indicator {
  font-size: 11px;
  color: var(--interactive-accent);
  background-color: rgba(var(--interactive-accent-rgb), 0.08);
  padding: 1px 6px;
  border-radius: 10px;
  white-space: nowrap;
}

.note-actions {
  display: flex;
  gap: 8px;
  opacity: 0.5;
}

.note-card:hover .note-actions {
  opacity: 1;
}

.favorite-icon, .delete-icon {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.favorite-icon:hover, .delete-icon:hover {
  background-color: var(--background-tertiary);
}

.delete-icon:hover {
  color: #ff4d4f;
}

/* 列表视图样式 */
.notes-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.note-list-item {
  display: flex;
  padding: 16px;
  background-color: var(--background-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--card-radius);
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 2px var(--shadow-color);
}

.note-list-item:hover {
  border-color: var(--interactive-accent);
  box-shadow: 0 2px 6px var(--shadow-color);
}

.note-list-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.note-list-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  margin-left: 16px;
  min-width: 120px;
}

.note-favorite {
  display: inline-flex;
  margin-bottom: 2px;
}

.note-list-title {
  font-weight: 600;
  font-size: 16px;
  color: var(--text-normal);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.note-list-excerpt {
  font-size: 14px;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.note-list-date {
  font-size: 12px;
  color: var(--text-muted);
}

.note-list-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  font-size: 12px;
  color: var(--interactive-accent);
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
}

.action-btn:hover {
  background-color: rgba(var(--interactive-accent-rgb), 0.1);
}

.action-btn.delete {
  color: #ff4d4f;
}

.action-btn.delete:hover {
  background-color: rgba(255, 77, 79, 0.1);
}

.obsidian-pagination {
  margin: 16px 0;
  display: flex;
  justify-content: center;
}

.folder-select {
  width: 100%;
}

.create-note-form :deep(.el-form-item__label) {
  padding-bottom: 8px;
}

@media (max-width: 768px) {
  .notes-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
  
  .note-card {
    height: 200px;
  }
  
  .note-list-right {
    min-width: 100px;
  }
}

@media (max-width: 576px) {
  .obsidian-filters {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .view-toggle {
    margin-left: 0;
    margin-top: 8px;
  }
  
  .search-container {
    max-width: 100%;
  }
  
  .note-list-actions {
    flex-direction: column;
    gap: 4px;
  }
}
</style> 