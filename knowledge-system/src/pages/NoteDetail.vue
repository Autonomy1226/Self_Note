<template>
  <div class="obsidian-note-detail">
    <div class="note-toolbar">
      <div class="note-actions">
        <el-button type="primary" @click="saveNote" class="obsidian-btn save-btn">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="btn-icon">
            <path d="M17 3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V7l-4-4zm-5 16c-1.66 0-3-1.34-3-3s1.34-3 3-3 3 1.34 3 3-1.34 3-3 3zm3-10H5V5h10v4z"></path>
          </svg>
          保存
        </el-button>
        <el-button @click="goBack" class="obsidian-btn">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="btn-icon">
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"></path>
          </svg>
          返回
        </el-button>
      </div>
      <div class="note-tools">
        <div class="folder-select-container" v-if="isReady">
          <span class="tool-label">文件夹:</span>
          <el-select v-model="note.folderId" placeholder="选择文件夹" clearable size="small" class="folder-select">
            <el-option v-for="folder in folders" :key="folder.id" :label="folder.name" :value="folder.id" />
          </el-select>
        </div>
        <div class="note-meta" v-if="note && note.createTime">
          <span class="note-date">最后修改: {{ formatDate(note.updateTime || note.createTime) }}</span>
        </div>
      </div>
    </div>
    
    <div class="note-container" v-if="isReady">
      <div class="note-header">
        <input 
          v-model="note.title" 
          class="note-title" 
          placeholder="无标题笔记" 
          @focus="titleFocused = true" 
          @blur="titleFocused = false"
          :class="{ 'is-focused': titleFocused }" 
        />
      </div>
      
      <div class="note-content-wrapper">
        <textarea 
          v-model="note.content" 
          class="note-content" 
          placeholder="在此输入笔记内容..." 
          @focus="contentFocused = true" 
          @blur="contentFocused = false"
          :class="{ 'is-focused': contentFocused }"
          @keydown.tab.prevent="insertTab"
        ></textarea>
      </div>
      
      <div class="note-footer">
        <div class="note-tags">
          <div class="tags-label">标签:</div>
          <div class="tags-container">
            <div class="tags-list">
              <el-tag 
                v-for="tag in tags" 
                :key="tag.id" 
                closable 
                @close="removeTag(tag.id)" 
                class="obsidian-tag"
                effect="plain"
              >
                #{{ tag.name }}
              </el-tag>
              <div v-if="tags.length === 0" class="no-tags">无标签</div>
            </div>
            <div class="tag-selector">
              <el-select 
                v-model="selectedTagId" 
                placeholder="添加标签" 
                filterable
                allow-create
                default-first-option
                clearable
                class="tag-select"
                @change="onTagSelected"
              >
                <el-option v-for="t in allTags" :key="t.id" :label="t.name" :value="t.id" />
              </el-select>
            </div>
          </div>
        </div>
        
        <div class="note-actions-footer">
          <el-button 
            size="small" 
            plain 
            :type="note.isFavorite ? 'warning' : 'default'" 
            @click="toggleFavorite" 
            class="favorite-btn"
          >
            <svg viewBox="0 0 24 24" width="16" height="16" :fill="note.isFavorite ? '#FFD700' : 'currentColor'" class="btn-icon">
              <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"></path>
            </svg>
            {{ note.isFavorite ? '取消收藏' : '收藏' }}
          </el-button>
        </div>
      </div>
    </div>
    
    <div v-else class="note-loading">
      <div class="loading-spinner"></div>
      <div class="loading-text">加载中...</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const user = JSON.parse(localStorage.getItem('user') || '{}');
const isLogin = !!user.id;
const note = ref(null);
const tags = ref([]);
const allTags = ref([]);
const folders = ref([]);
const selectedTagId = ref('');
const titleFocused = ref(false);
const contentFocused = ref(false);
const isReady = computed(() => note.value !== null);

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const insertTab = (e) => {
  const target = e.target;
  const start = target.selectionStart;
  const end = target.selectionEnd;
  
  // 插入制表符
  const newValue = target.value.substring(0, start) + '    ' + target.value.substring(end);
  note.value.content = newValue;
  
  // 将光标移动到制表符之后
  setTimeout(() => {
    target.selectionStart = target.selectionEnd = start + 4;
  }, 0);
};

const fetchNote = async () => {
  if (isLogin) {
    try {
      const res = await axios.get(`/api/notes/${route.params.id}`);
      note.value = res.data;
      fetchTags();
      fetchFolders();
    } catch (error) {
      console.error('获取笔记失败:', error);
      router.push('/notes');
    }
  } else {
    let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
    note.value = guestNotes.find(n => n.id == route.params.id);
    if (!note.value) {
      router.push('/notes');
      return;
    }
    fetchTags();
    fetchFolders();
  }
};

const fetchTags = async () => {
  if (isLogin) {
    const res = await axios.get(`/api/tags/note/${route.params.id}`);
    tags.value = res.data;
    const all = await axios.get('/api/tags', { params: { userId: note.value.userId } });
    allTags.value = all.data.filter(t => !tags.value.some(tag => tag.id === t.id));
  } else {
    let guestTags = JSON.parse(localStorage.getItem('guest_tags') || '[]');
    tags.value = guestTags.filter(t => note.value.tags && note.value.tags.includes(t.id));
    allTags.value = guestTags.filter(t => !(note.value.tags && note.value.tags.includes(t.id)));
  }
};

const fetchFolders = async () => {
  if (isLogin) {
    const res = await axios.get('/api/folders', { params: { userId: note.value.userId } });
    folders.value = res.data;
  } else {
    folders.value = JSON.parse(localStorage.getItem('guest_folders') || '[]');
  }
};

const saveNote = async () => {
  try {
    if (isLogin) {
      // 添加更新时间
      note.value.updateTime = new Date().toISOString();
      await axios.put('/api/notes', note.value);
      showMessage('保存成功');
    } else {
      let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
      const updatedNote = { 
        ...note.value, 
        updateTime: new Date().toISOString() 
      };
      guestNotes = guestNotes.map(n => n.id === note.value.id ? updatedNote : n);
      localStorage.setItem('guest_notes', JSON.stringify(guestNotes));
      note.value = updatedNote;
      showMessage('保存成功（本地）');
    }
  } catch (error) {
    console.error('保存失败:', error);
    showMessage('保存失败，请重试', 'error');
  }
};

const onTagSelected = async (value) => {
  if (!value) return;
  
  // 处理新建标签的情况
  if (typeof value === 'string') {
    await createNewTag(value);
  } else {
    await addTag(value);
  }
  
  selectedTagId.value = '';
};

const createNewTag = async (tagName) => {
  if (isLogin) {
    try {
      const res = await axios.post('/api/tags', { name: tagName, userId: note.value.userId });
      const newTagId = res.data.id || res.data;
      await addTag(newTagId);
    } catch (error) {
      console.error('创建标签失败:', error);
      showMessage('创建标签失败', 'error');
    }
  } else {
    // 游客模式创建标签
    const newTagId = Date.now();
    const newTag = { id: newTagId, name: tagName };
    let guestTags = JSON.parse(localStorage.getItem('guest_tags') || '[]');
    guestTags.push(newTag);
    localStorage.setItem('guest_tags', JSON.stringify(guestTags));
    
    // 添加到笔记
    if (!note.value.tags) note.value.tags = [];
    note.value.tags.push(newTagId);
    saveNote();
    fetchTags();
  }
};

const addTag = async (tagId) => {
  if (isLogin) {
    await axios.post(`/api/tags/note/${note.value.id}/add`, null, { params: { tagId } });
    fetchTags();
  } else {
    if (!note.value.tags) note.value.tags = [];
    note.value.tags.push(Number(tagId));
    saveNote();
    fetchTags();
  }
};

const removeTag = async (tagId) => {
  if (isLogin) {
    await axios.delete(`/api/tags/note/${note.value.id}/remove`, { params: { tagId } });
    fetchTags();
  } else {
    note.value.tags = (note.value.tags || []).filter(id => id !== tagId);
    saveNote();
    fetchTags();
  }
};

const toggleFavorite = async () => {
  note.value.isFavorite = !note.value.isFavorite;
  
  if (isLogin) {
    await axios.put(`/api/notes/${note.value.id}/favorite`, null, { 
      params: { favorite: note.value.isFavorite } 
    });
  } else {
    let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
    guestNotes = guestNotes.map(n => n.id === note.value.id ? note.value : n);
    localStorage.setItem('guest_notes', JSON.stringify(guestNotes));
  }
  
  showMessage(note.value.isFavorite ? '已添加到收藏' : '已取消收藏');
};

const showMessage = (message, type = 'success') => {
  // 这里可以接入Element Plus的消息提示
  // 简单实现一个临时提示
  const messageEl = document.createElement('div');
  messageEl.className = `floating-message ${type}`;
  messageEl.textContent = message;
  document.body.appendChild(messageEl);
  
  setTimeout(() => {
    messageEl.classList.add('show');
    setTimeout(() => {
      messageEl.classList.remove('show');
      setTimeout(() => {
        document.body.removeChild(messageEl);
      }, 300);
    }, 2000);
  }, 0);
};

const goBack = () => {
  router.back();
};

onMounted(fetchNote);
</script>

<style>
/* 浮动消息提示 */
.floating-message {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%) translateY(-20px);
  background-color: var(--interactive-accent);
  color: white;
  padding: 10px 20px;
  border-radius: 4px;
  box-shadow: 0 3px 6px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  opacity: 0;
  transition: all 0.3s ease;
}

.floating-message.show {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
}

.floating-message.error {
  background-color: #f56c6c;
}
</style>

<style scoped>
.obsidian-note-detail {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--background-primary);
  border-radius: var(--card-radius);
  overflow: hidden;
  box-shadow: 0 1px 3px var(--shadow-color);
  border: 1px solid var(--border-color);
}

.note-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: var(--background-secondary);
  border-bottom: 1px solid var(--border-color);
}

.note-actions {
  display: flex;
  gap: 8px;
}

.btn-icon {
  margin-right: 4px;
}

.note-tools {
  display: flex;
  align-items: center;
  gap: 16px;
}

.folder-select-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tool-label {
  color: var(--text-muted);
  font-size: 13px;
}

.folder-select {
  width: 140px;
}

.note-meta {
  color: var(--text-muted);
  font-size: 12px;
}

.obsidian-btn {
  display: flex;
  align-items: center;
}

.save-btn {
  background-color: var(--interactive-accent) !important;
  border-color: var(--interactive-accent) !important;
}

.note-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  padding: 16px;
  height: calc(100% - 60px); /* 减去工具栏高度 */
}

.note-header {
  margin-bottom: 16px;
}

.note-title {
  width: 100%;
  font-size: 24px;
  font-weight: 600;
  padding: 10px 12px;
  background-color: var(--background-primary);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  color: var(--text-normal);
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.note-title.is-focused {
  border-color: var(--interactive-accent);
  box-shadow: 0 0 0 2px rgba(var(--interactive-accent-rgb), 0.15);
}

.note-content-wrapper {
  flex: 1;
  margin-bottom: 16px;
  overflow: hidden;
  min-height: 200px;
}

.note-content {
  width: 100%;
  height: 100%;
  min-height: 100%;
  padding: 12px 16px;
  background-color: var(--background-primary);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  color: var(--text-normal);
  font-family: var(--font-editor);
  font-size: 16px;
  line-height: 1.6;
  resize: none;
  outline: none;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.note-content.is-focused {
  border-color: var(--interactive-accent);
  box-shadow: 0 0 0 2px rgba(var(--interactive-accent-rgb), 0.15);
}

.note-footer {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.note-tags {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.tags-label {
  color: var(--text-muted);
  font-weight: 500;
  flex-shrink: 0;
  padding-top: 6px;
}

.tags-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.no-tags {
  color: var(--text-muted);
  font-style: italic;
  font-size: 13px;
}

.obsidian-tag {
  background-color: rgba(var(--interactive-accent-rgb), 0.08) !important;
  color: var(--interactive-accent) !important;
  border-color: rgba(var(--interactive-accent-rgb), 0.2) !important;
}

.tag-selector {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag-select {
  width: 100%;
  max-width: 240px;
}

.note-actions-footer {
  display: flex;
  justify-content: flex-end;
}

.favorite-btn {
  display: flex;
  align-items: center;
}

.favorite-btn.is-favorite {
  color: #FFD700;
}

.note-loading {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-muted);
  gap: 16px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(var(--interactive-accent-rgb), 0.2);
  border-radius: 50%;
  border-top-color: var(--interactive-accent);
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 14px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 768px) {
  .note-toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .note-tools {
    width: 100%;
    justify-content: space-between;
  }
  
  .folder-select {
    width: 120px;
  }
}

@media (max-width: 576px) {
  .note-container {
    padding: 12px;
  }
  
  .note-tools {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .tag-select {
    max-width: 100%;
  }
}
</style> 