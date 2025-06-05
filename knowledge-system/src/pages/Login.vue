<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo-container">
          <svg class="app-logo" viewBox="0 0 512 512" width="32" height="32">
            <path stroke="currentColor" stroke-width="16" d="M256 74L96 330h320L256 74z" fill="none"/>
            <path stroke="currentColor" stroke-width="16" d="M256 200L176 330h160L256 200z" fill="none"/>
            <path stroke="currentColor" stroke-width="16" d="M176 330L256 200L336 330" fill="none"/>
          </svg>
          <h1 class="app-title">知识管理系统</h1>
        </div>
        <h2 class="login-title">用户登录</h2>
      </div>
      
      <el-form :model="form" @submit.prevent="handleLogin" class="login-form">
        <el-form-item>
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            clearable 
            :class="{ 'is-error': formErrors.username }"
          >
            <template #prefix>
              <div class="input-icon-container">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="input-icon">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"></path>
                </svg>
              </div>
            </template>
          </el-input>
          <div v-if="formErrors.username" class="error-message">{{ formErrors.username }}</div>
        </el-form-item>
        
        <el-form-item>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password 
            clearable
            :class="{ 'is-error': formErrors.password }"
          >
            <template #prefix>
              <div class="input-icon-container">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="input-icon">
                  <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"></path>
                </svg>
              </div>
            </template>
          </el-input>
          <div v-if="formErrors.password" class="error-message">{{ formErrors.password }}</div>
        </el-form-item>
        
        <div class="form-options">
          <div class="guest-option">
            <el-checkbox v-model="rememberMe">记住我</el-checkbox>
          </div>
          <div class="guest-option">
            <span class="text-btn" @click="useGuestMode">游客模式</span>
          </div>
        </div>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-button" 
            @click="handleLogin" 
            :loading="loading"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="login-footer">
        <span>还没有账号？</span>
        <router-link to="/register" class="register-link">注册账号</router-link>
      </div>
      
      <el-alert v-if="error" :title="error" type="error" show-icon class="login-error" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const form = ref({ username: '', password: '' });
const formErrors = reactive({ username: '', password: '' });
const error = ref('');
const loading = ref(false);
const rememberMe = ref(false);

const validateForm = () => {
  let isValid = true;
  formErrors.username = '';
  formErrors.password = '';
  
  if (!form.value.username.trim()) {
    formErrors.username = '请输入用户名';
    isValid = false;
  }
  
  if (!form.value.password) {
    formErrors.password = '请输入密码';
    isValid = false;
  }
  
  return isValid;
};

const handleLogin = async () => {
  if (!validateForm()) return;
  
  error.value = '';
  loading.value = true;
  
  try {
    console.log('登录请求参数:', form.value);
    const res = await axios.post('/api/user/login', form.value);
    console.log('登录响应数据:', res.data);
    
    if (rememberMe.value) {
      localStorage.setItem('remembered_user', form.value.username);
    } else {
      localStorage.removeItem('remembered_user');
    }
    localStorage.setItem('user', JSON.stringify(res.data));
    router.push('/');
  } catch (e) {
    console.error('登录错误:', e);
    if (e.response) {
      console.error('错误状态:', e.response.status);
      console.error('错误数据:', e.response.data);
      error.value = e.response.data?.message || `登录失败 (${e.response.status})`;
    } else if (e.request) {
      console.error('无响应错误:', e.request);
      error.value = '服务器未响应，请检查网络连接';
    } else {
      console.error('请求错误:', e.message);
      error.value = `请求错误: ${e.message}`;
    }
  } finally {
    loading.value = false;
  }
};

const useGuestMode = () => {
  localStorage.removeItem('user');
  router.push('/');
};

// 初始化时检查是否有记住的用户
const initRememberedUser = () => {
  const rememberedUser = localStorage.getItem('remembered_user');
  if (rememberedUser) {
    form.value.username = rememberedUser;
    rememberMe.value = true;
  }
};

initRememberedUser();
</script>

<style scoped>
.login-page {
  height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--background-secondary);
}

.login-container {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background-color: var(--background-primary);
  border-radius: var(--card-radius);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  gap: 12px;
}

.app-logo {
  color: var(--interactive-accent);
}

.app-title {
  font-size: 22px;
  font-weight: 600;
  color: var(--interactive-accent);
  margin: 0;
}

.login-title {
  font-size: 20px;
  font-weight: 500;
  color: var(--text-normal);
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__inner) {
  padding-left: 40px;
  height: 44px;
  font-size: 15px;
}

.login-form :deep(.el-input.is-error .el-input__inner) {
  border-color: #f56c6c;
}

.input-icon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 100%;
  margin-left: 8px;
}

.input-icon {
  color: var(--text-muted);
}

.error-message {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
  padding-left: 4px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  align-items: center;
}

.guest-option {
  font-size: 14px;
  color: var(--text-muted);
}

.text-btn {
  color: var(--interactive-accent);
  cursor: pointer;
  transition: opacity 0.2s;
}

.text-btn:hover {
  opacity: 0.8;
  text-decoration: underline;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 6px;
}

.login-footer {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
}

.register-link {
  color: var(--interactive-accent);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.register-link:hover {
  text-decoration: underline;
}

.login-error {
  margin-top: 20px;
}

@media (max-width: 576px) {
  .login-container {
    padding: 32px 24px;
    max-width: 100%;
    border-radius: 0;
    box-shadow: none;
    height: 100%;
  }
  
  .login-page {
    align-items: flex-start;
    background-color: var(--background-primary);
  }
}
</style> 