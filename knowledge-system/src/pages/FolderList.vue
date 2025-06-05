<template>
  <el-card>
    <h2 style="margin-bottom: 18px;">文件夹管理</h2>
    <div class="folder-actions">
      <el-input v-model="newFolderName" placeholder="新文件夹名称" style="width: 200px;" />
      <el-button type="primary" @click="createFolder">新建</el-button>
    </div>
    <div v-if="loading" class="loading">
      <el-icon class="is-loading"><svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M512 64a32 32 0 0 1 32 32v192a32 32 0 0 1-64 0V96a32 32 0 0 1 32-32zm0 640a32 32 0 0 1 32 32v192a32 32 0 1 1-64 0V736a32 32 0 0 1 32-32zm448-192a32 32 0 0 1-32 32H736a32 32 0 1 1 0-64h192a32 32 0 0 1 32 32zm-640 0a32 32 0 0 1-32 32H96a32 32 0 0 1 0-64h192a32 32 0 0 1 32 32zM195.2 195.2a32 32 0 0 1 45.248 0L376.32 331.008a32 32 0 0 1-45.248 45.248L195.2 240.448a32 32 0 0 1 0-45.248zm452.544 452.544a32 32 0 0 1 45.248 0L828.8 783.552a32 32 0 0 1-45.248 45.248L647.744 692.992a32 32 0 0 1 0-45.248zM828.8 195.264a32 32 0 0 1 0 45.184L692.992 376.32a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0zm-452.544 452.48a32 32 0 0 1 0 45.248L240.448 828.8a32 32 0 0 1-45.248-45.248l135.808-135.808a32 32 0 0 1 45.248 0z"></path></svg></el-icon>
      加载中...
    </div>
    <div v-else>
      <el-empty v-if="treeData.length === 0" description="暂无文件夹">
        <el-button type="primary" @click="createFolder">新建文件夹</el-button>
      </el-empty>
      <el-tree
        v-else
        :data="treeData"
        :props="treeProps"
        node-key="id"
        default-expand-all
        highlight-current
        style="margin-top: 18px;"
        :expand-on-click-node="false"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span>
              <el-button 
                type="danger" 
                link 
                @click.stop="delFolder(data)">
                删除
              </el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
const user = JSON.parse(localStorage.getItem('user') || '{}');
const isLogin = !!user.id;
const folders = ref([]);
const newFolderName = ref('');
const treeData = ref([]);
const treeProps = { children: 'children', label: 'name' };
const loading = ref(false);

const fetchFolders = async () => {
  loading.value = true;
  try {
    if (isLogin) {
      console.log('获取用户文件夹:', user.id);
      const res = await axios.get('/api/folders', { params: { userId: user.id } });
      console.log('文件夹数据:', res.data);
      folders.value = res.data;
    } else {
      console.log('获取游客文件夹');
      folders.value = JSON.parse(localStorage.getItem('guest_folders') || '[]');
    }
    treeData.value = buildTree(folders.value, null);
  } catch (error) {
    console.error('获取文件夹失败:', error);
  } finally {
    loading.value = false;
  }
};

function buildTree(list, parentId) {
  return list.filter(f => {
    // parentId为null时表示根文件夹
    if (parentId === null) {
      return f.parentId === null || f.parentId === undefined;
    }
    return f.parentId === parentId;
  }).map(f => ({
    ...f,
    children: buildTree(list, f.id)
  }));
}

const createFolder = async () => {
  if (!newFolderName.value.trim()) {
    return;
  }
  
  console.log('正在创建文件夹:', { name: newFolderName.value, parentId: null });
  
  if (isLogin) {
    try {
      const response = await axios.post('/api/folders', {
        name: newFolderName.value,
        parentId: null,
        userId: user.id
      });
      console.log('文件夹创建成功:', response.data);
      newFolderName.value = '';
      fetchFolders();
    } catch (error) {
      console.error('创建文件夹失败:', error);
      alert('创建文件夹失败，请重试');
    }
  } else {
    // 游客模式
    const newFolderId = Date.now();
    const folderObj = {
      id: newFolderId,
      name: newFolderName.value,
      parentId: null,
      userId: 0,
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString()
    };
    let guestFolders = JSON.parse(localStorage.getItem('guest_folders') || '[]');
    guestFolders.push(folderObj);
    localStorage.setItem('guest_folders', JSON.stringify(guestFolders));
    newFolderName.value = '';
    fetchFolders();
  }
};

const delFolder = async (folder) => {
  if (!confirm(`确定要删除文件夹"${folder.name}"吗？`)) {
    return;
  }
  
  try {
    if (isLogin) {
      await axios.delete(`/api/folders/${folder.id}`, { params: { userId: user.id } });
    } else {
      let guestFolders = JSON.parse(localStorage.getItem('guest_folders') || '[]');
      guestFolders = guestFolders.filter(f => f.id !== folder.id);
      localStorage.setItem('guest_folders', JSON.stringify(guestFolders));
    }
    fetchFolders();
  } catch (error) {
    console.error('删除文件夹失败:', error);
    alert('删除文件夹失败，请重试');
  }
};

onMounted(() => {
  fetchFolders();
});
</script>

<style scoped>
.folder-list-page {
  max-width: 600px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #eee;
  padding: 32px;
}
.folder-actions {
  margin-bottom: 18px;
  display: flex;
  gap: 8px;
}
.folder-tree {
  list-style: none;
  padding-left: 0;
}
.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  color: #909399;
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}
</style> 