<template>
  <el-card>
    <h2>统计信息</h2>
    
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32zm0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32zm448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32zm-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32zM195.2 195.2a32 32 0 0 1 45.248 0L376.32 331.008a32 32 0 0 1-45.248 45.248L195.2 240.448a32 32 0 0 1 0-45.248zm452.544 452.544a32 32 0 0 1 45.248 0L828.8 783.552a32 32 0 0 1-45.248 45.248L647.744 692.992a32 32 0 0 1 0-45.248zM828.8 195.264a32 32 0 0 1 0 45.184L692.992 376.32a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0zm-452.544 452.48a32 32 0 0 1 0 45.248L240.448 828.8a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0z"></path></svg></el-icon>
      加载中...
    </div>
    
    <div v-else-if="error" class="error-container">
      <el-alert type="error" :title="error" show-icon :closable="false" />
      <el-button style="margin-top: 20px;" @click="fetchStats">重新加载</el-button>
    </div>
    
    <el-row v-else :gutter="32" style="margin-top: 24px;">
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="stat-header">
              <span>笔记总数</span>
              <el-icon><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M832 384H576V128H192v768h640V384zm-26.496-64L640 154.496V320h165.504zM160 64h480l256 256v608a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32zm160 448h384v64H320v-64zm0-192h160v64H320v-64zm0 384h384v64H320v-64z"></path></svg></el-icon>
            </div>
          </template>
          <div class="stat-value">{{ stats.total }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="stat-header">
              <span>收藏数量</span>
              <el-icon><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="m512 747.84 228.16 119.936a6.4 6.4 0 0 0 9.28-6.72l-43.52-254.08 184.512-179.904a6.4 6.4 0 0 0-3.52-10.88l-255.104-37.12L517.76 147.904a6.4 6.4 0 0 0-11.52 0L392.192 379.072l-255.104 37.12a6.4 6.4 0 0 0-3.52 10.88L318.08 606.976l-43.584 254.08a6.4 6.4 0 0 0 9.28 6.72L512 747.84zM313.6 924.48a70.4 70.4 0 0 1-102.144-74.24l37.888-220.928L88.96 472.96A70.4 70.4 0 0 1 128 352.896l221.76-32.256 99.2-200.96a70.4 70.4 0 0 1 126.208 0l99.2 200.96 221.824 32.256a70.4 70.4 0 0 1 39.04 120.064L774.72 629.376l37.888 220.928a70.4 70.4 0 0 1-102.144 74.24L512 820.096l-198.4 104.32z"></path></svg></el-icon>
            </div>
          </template>
          <div class="stat-value highlight">{{ stats.favorite }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="stat-header">
              <span>已删除</span>
              <el-icon><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M352 192V95.936a32 32 0 0 1 32-32h256a32 32 0 0 1 32 32V192h256a32 32 0 1 1 0 64H96a32 32 0 0 1 0-64h256zm64 0h192v-64H416v64zM192 960a32 32 0 0 1-32-32V256h704v672a32 32 0 0 1-32 32H192zm224-192a32 32 0 0 0 32-32V416a32 32 0 0 0-64 0v320a32 32 0 0 0 32 32zm192 0a32 32 0 0 0 32-32V416a32 32 0 0 0-64 0v320a32 32 0 0 0 32 32z"></path></svg></el-icon>
            </div>
          </template>
          <div class="stat-value muted">{{ stats.deleted }}</div>
        </el-card>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

const user = JSON.parse(localStorage.getItem('user') || '{}');
const isLogin = !!user.id;
const stats = ref({ total: 0, favorite: 0, deleted: 0 });
const loading = ref(false);
const error = ref('');

const fetchStats = async () => {
  loading.value = true;
  error.value = '';
  
  try {
    if (isLogin) {
      console.log('获取用户统计信息:', user.id);
      const res = await axios.get('/api/search/stats', { params: { userId: user.id } });
      console.log('统计数据:', res.data);
      stats.value = res.data;
    } else {
      console.log('获取游客统计信息');
      let guestNotes = JSON.parse(localStorage.getItem('guest_notes') || '[]');
      stats.value = {
        total: guestNotes.length,
        favorite: guestNotes.filter(n => n.isFavorite).length,
        deleted: guestNotes.filter(n => n.isDeleted).length
      };
      console.log('游客统计数据:', stats.value);
    }
  } catch (err) {
    console.error('获取统计信息失败:', err);
    error.value = '获取统计信息失败: ' + (err.response?.data?.message || err.message);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-normal);
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-normal);
  text-align: center;
  padding: 20px 0;
}

.stat-value.highlight {
  color: var(--interactive-accent);
}

.stat-value.muted {
  color: #f56c6c;
}
</style> 