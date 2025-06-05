<template>
  <el-card>
    <h2 style="margin-bottom: 18px;">标签管理</h2>
    <div class="tag-actions">
      <el-input v-model="newTagName" placeholder="新标签名称" style="width: 200px;" />
      <el-color-picker v-model="newTagColor" />
      <el-button type="primary" @click="createTag">新建</el-button>
    </div>
    
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32zm0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32zm448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32zm-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32zM195.2 195.2a32 32 0 0 1 45.248 0L376.32 331.008a32 32 0 0 1-45.248 45.248L195.2 240.448a32 32 0 0 1 0-45.248zm452.544 452.544a32 32 0 0 1 45.248 0L828.8 783.552a32 32 0 0 1-45.248 45.248L647.744 692.992a32 32 0 0 1 0-45.248zM828.8 195.264a32 32 0 0 1 0 45.184L692.992 376.32a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0zm-452.544 452.48a32 32 0 0 1 0 45.248L240.448 828.8a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0z"></path></svg></el-icon>
      加载中...
    </div>
    
    <div v-else-if="tags.length === 0" class="empty-tags">
      <el-empty description="暂无标签">
        <el-button type="primary" @click="newTagName = '新标签'; newTagColor = getRandomColor()">添加第一个标签</el-button>
      </el-empty>
    </div>
    
    <div v-else class="tag-list">
      <div v-for="tag in tags" :key="tag.id" class="tag-item">
        <div class="tag-color" :style="{ backgroundColor: tag.color }"></div>
        <div class="tag-name">{{ tag.name }}</div>
        <div class="tag-actions">
          <el-button 
            type="danger" 
            link
            @click="delTag(tag)"
          >
            删除
          </el-button>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const user = JSON.parse(localStorage.getItem('user') || '{}');
const isLogin = !!user.id;
const tags = ref([]);
const newTagName = ref('');
const newTagColor = ref('#67c23a');
const loading = ref(false);

const fetchTags = async () => {
  loading.value = true;
  try {
    if (isLogin) {
      console.log('获取用户标签:', user.id);
      const res = await axios.get('/api/tags', { params: { userId: user.id } });
      console.log('标签数据:', res.data);
      tags.value = res.data;
    } else {
      console.log('获取游客标签');
      tags.value = JSON.parse(localStorage.getItem('guest_tags') || '[]');
    }
  } catch (error) {
    console.error('获取标签失败:', error);
  } finally {
    loading.value = false;
  }
};

const getRandomColor = () => {
  const colors = ['#f56c6c', '#e6a23c', '#67c23a', '#409eff', '#9254de'];
  return colors[Math.floor(Math.random() * colors.length)];
};

const createTag = async () => {
  if (!newTagName.value.trim()) {
    return;
  }
  
  console.log('正在创建标签:', { name: newTagName.value, color: newTagColor.value });
  
  if (isLogin) {
    try {
      const response = await axios.post('/api/tags', {
        name: newTagName.value,
        color: newTagColor.value,
        userId: user.id
      });
      console.log('标签创建成功:', response.data);
      newTagName.value = '';
      newTagColor.value = getRandomColor();
      fetchTags();
    } catch (error) {
      console.error('创建标签失败:', error);
      alert('创建标签失败，请重试');
    }
  } else {
    // 游客模式
    const newTagId = Date.now();
    const tagObj = {
      id: newTagId,
      userId: 0,
      createTime: new Date().toISOString(),
      name: newTagName.value,
      color: newTagColor.value
    };
    
    const guestTags = JSON.parse(localStorage.getItem('guest_tags') || '[]');
    guestTags.push(tagObj);
    localStorage.setItem('guest_tags', JSON.stringify(guestTags));
    newTagName.value = '';
    newTagColor.value = getRandomColor();
    fetchTags();
  }
};

const delTag = async (tag) => {
  if (!confirm(`确定要删除标签 "${tag.name}" 吗？`)) {
    return;
  }
  
  try {
    if (isLogin) {
      await axios.delete(`/api/tags/${tag.id}`, { params: { userId: user.id } });
    } else {
      const guestTags = JSON.parse(localStorage.getItem('guest_tags') || '[]');
      const updatedTags = guestTags.filter(t => t.id !== tag.id);
      localStorage.setItem('guest_tags', JSON.stringify(updatedTags));
    }
    fetchTags();
  } catch (error) {
    console.error('删除标签失败:', error);
    alert('删除标签失败，请重试');
  }
};

onMounted(() => {
  newTagColor.value = getRandomColor();
  fetchTags();
});
</script>

<style scoped>
.tag-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  align-items: center;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 16px;
}

.tag-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.tag-color {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  margin-right: 8px;
}

.tag-name {
  font-size: 14px;
  margin-right: 16px;
}

.loading, .empty-tags {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 32px 0;
  color: #909399;
}

.empty-tags {
  flex-direction: column;
}
</style> 