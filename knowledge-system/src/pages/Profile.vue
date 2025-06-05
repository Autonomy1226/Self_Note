<template>
  <el-card style="max-width: 500px; margin: 0 auto;">
    <h2 style="margin-bottom: 18px;">个人信息</h2>
    <el-form :model="user" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="user.username" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="user.email" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updateUser">保存信息</el-button>
      </el-form-item>
    </el-form>
    <el-divider />
    <el-form label-width="80px">
      <el-form-item label="新密码">
        <el-input v-model="newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updatePassword">修改密码</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'));
const isLogin = !!user.value.id;
const newPassword = ref('');
const updateUser = async () => {
  if (isLogin) {
    const res = await axios.put('/api/user/update', user.value);
    localStorage.setItem('user', JSON.stringify(res.data));
    alert('保存成功');
  } else {
    alert('游客模式下无法修改信息');
  }
};
const updatePassword = async () => {
  if (!newPassword.value) return;
  if (isLogin) {
    await axios.put('/api/user/password', null, { params: { userId: user.value.id, newPassword: newPassword.value } });
    alert('密码修改成功');
    newPassword.value = '';
  } else {
    alert('游客模式下无法修改密码');
  }
};
</script>

<style scoped>
.profile-page {
  max-width: 500px;
  margin: 0 auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #eee;
  padding: 32px;
}
.profile-form {
  margin-bottom: 24px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
input {
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #ccc;
}
button {
  width: 120px;
  padding: 8px;
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  margin-top: 8px;
}
</style> 