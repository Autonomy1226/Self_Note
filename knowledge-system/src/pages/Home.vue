<template>
  <el-container class="obsidian-layout">
    <el-header height="var(--header-height)" class="obsidian-header">
      <div class="logo">
        <svg class="obsidian-logo" viewBox="0 0 512 512" width="24" height="24">
          <!-- 空心拓扑三角形Logo -->
          <path stroke="currentColor" stroke-width="16" d="M256 74L96 330h320L256 74z" fill="none"/>
          <path stroke="currentColor" stroke-width="16" d="M256 200L176 330h160L256 200z" fill="none"/>
          <path stroke="currentColor" stroke-width="16" d="M176 330L256 200L336 330" fill="none"/>
        </svg>
        个人笔记管理系统
      </div>
      <div class="user-info">
        <router-link to="/profile" class="profile-link">个人中心</router-link>
        <el-button type="text" @click="logout" class="logout-btn">退出</el-button>
      </div>
    </el-header>
    <el-container class="obsidian-main">
      <el-aside width="var(--sidebar-width)" class="obsidian-sidebar">
        <el-menu :default-active="$route.path" router class="obsidian-menu">
          <el-menu-item index="/notes" class="obsidian-menu-item">
            <i class="obsidian-icon note-icon"></i>笔记
          </el-menu-item>
          <el-menu-item index="/folders" class="obsidian-menu-item">
            <i class="obsidian-icon folder-icon"></i>文件夹
          </el-menu-item>
          <el-menu-item index="/tags" class="obsidian-menu-item">
            <i class="obsidian-icon tag-icon"></i>标签
          </el-menu-item>
          <el-menu-item index="/stats" class="obsidian-menu-item">
            <i class="obsidian-icon stats-icon"></i>统计
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="obsidian-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
function logout() {
  localStorage.removeItem('user');
  router.push('/login');
}
</script>

<style scoped>
.obsidian-layout {
  min-height: 100vh;
  background-color: var(--background-primary);
}

.obsidian-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--background-primary);
  color: var(--text-normal);
  padding: 0 24px;
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.logo {
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--interactive-accent);
}

.obsidian-logo {
  color: var(--interactive-accent);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.profile-link {
  color: var(--text-normal);
  text-decoration: none;
}

.profile-link:hover {
  color: var(--interactive-accent);
}

.logout-btn {
  color: var(--text-muted) !important;
}

.logout-btn:hover {
  color: var(--interactive-accent) !important;
}

.obsidian-main {
  height: calc(100vh - var(--header-height));
}

.obsidian-sidebar {
  background: var(--background-secondary);
  border-right: 1px solid var(--border-color);
  padding: 0;
}

.obsidian-menu {
  border-right: none;
}

.obsidian-menu-item {
  height: 40px;
  line-height: 40px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.obsidian-content {
  padding: 24px;
  background: var(--background-primary);
  height: 100%;
  overflow-y: auto;
}

.obsidian-icon {
  width: 16px;
  height: 16px;
  display: inline-block;
  background-repeat: no-repeat;
  background-position: center;
  opacity: 0.8;
}

.note-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='16' height='16' fill='%235d23c2'%3E%3Cpath d='M14.06 9l.94.94L5.92 19H5v-.92L14.06 9zm3.6-6c-.25 0-.51.1-.7.29l-1.83 1.83 3.75 3.75 1.83-1.83c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.2-.2-.45-.29-.71-.29zm-3.6 3.19L3 17.25V21h3.75L17.81 9.94l-3.75-3.75z'%3E%3C/path%3E%3C/svg%3E");
}

.folder-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='16' height='16' fill='%235d23c2'%3E%3Cpath d='M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z'%3E%3C/path%3E%3C/svg%3E");
}

.tag-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='16' height='16' fill='%235d23c2'%3E%3Cpath d='M21.41 11.58l-9-9C12.05 2.22 11.55 2 11 2H4c-1.1 0-2 .9-2 2v7c0 .55.22 1.05.59 1.42l9 9c.36.36.86.58 1.41.58.55 0 1.05-.22 1.41-.59l7-7c.37-.36.59-.86.59-1.41 0-.55-.23-1.06-.59-1.42zM5.5 7C4.67 7 4 6.33 4 5.5S4.67 4 5.5 4 7 4.67 7 5.5 6.33 7 5.5 7z'%3E%3C/path%3E%3C/svg%3E");
}

.stats-icon {
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='16' height='16' fill='%235d23c2'%3E%3Cpath d='M9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4zm2.5 2.1h-15V5h15v14.1zm0-16.1h-15c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h15c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2z'%3E%3C/path%3E%3C/svg%3E");
}
</style> 