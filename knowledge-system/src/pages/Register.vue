<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-header">
        <div class="logo-container">
          <svg class="app-logo" viewBox="0 0 512 512" width="32" height="32">
            <!-- 空心拓扑三角形Logo -->
            <path stroke="currentColor" stroke-width="16" d="M256 74L96 330h320L256 74z" fill="none"/>
            <path stroke="currentColor" stroke-width="16" d="M256 200L176 330h160L256 200z" fill="none"/>
            <path stroke="currentColor" stroke-width="16" d="M176 330L256 200L336 330" fill="none"/>
          </svg>
          <h1 class="app-title">知识管理系统</h1>
        </div>
        <h2 class="register-title">用户注册</h2>
      </div>
      
      <el-form :model="form" @submit.prevent="handleRegister" class="register-form">
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
            v-model="form.email" 
            type="email" 
            placeholder="请输入邮箱" 
            clearable
            :class="{ 'is-error': formErrors.email }"
          >
            <template #prefix>
              <div class="input-icon-container">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="input-icon">
                  <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"></path>
                </svg>
              </div>
            </template>
          </el-input>
          <div v-if="formErrors.email" class="error-message">{{ formErrors.email }}</div>
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
        
        <el-form-item>
          <el-input 
            v-model="confirmPassword" 
            type="password" 
            placeholder="请确认密码" 
            show-password 
            clearable
            :class="{ 'is-error': formErrors.confirmPassword }"
          >
            <template #prefix>
              <div class="input-icon-container">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor" class="input-icon">
                  <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"></path>
                </svg>
              </div>
            </template>
          </el-input>
          <div v-if="formErrors.confirmPassword" class="error-message">{{ formErrors.confirmPassword }}</div>
        </el-form-item>
        
        <div class="terms-agreement">
          <el-checkbox v-model="agreeTerms" :class="{ 'is-error': formErrors.terms }">
            我已阅读并同意<span class="text-btn">服务条款</span>和<span class="text-btn">隐私政策</span>
          </el-checkbox>
          <div v-if="formErrors.terms" class="error-message">{{ formErrors.terms }}</div>
        </div>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="register-button" 
            @click="handleRegister" 
            :loading="loading"
          >
            注册
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="register-footer">
        <span>已有账号？</span>
        <router-link to="/login" class="login-link">立即登录</router-link>
      </div>
      
      <el-alert v-if="error" :title="error" type="error" show-icon class="register-error" />
      
      <el-alert 
        v-if="success" 
        title="注册成功！" 
        type="success" 
        show-icon 
        class="register-success"
      >
        <template #default>
          <p>注册成功，将在 {{ countdown }} 秒后跳转到登录页...</p>
        </template>
      </el-alert>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const form = ref({ username: '', email: '', password: '' });
const confirmPassword = ref('');
const agreeTerms = ref(false);
const formErrors = reactive({ 
  username: '', 
  email: '', 
  password: '',
  confirmPassword: '',
  terms: ''
});
const error = ref('');
const loading = ref(false);
const success = ref(false);
const countdown = ref(3);
let countdownTimer = null;

const validateForm = () => {
  let isValid = true;
  // 重置所有错误
  Object.keys(formErrors).forEach(key => formErrors[key] = '');
  
  // 用户名验证
  if (!form.value.username.trim()) {
    formErrors.username = '请输入用户名';
    isValid = false;
  } else if (form.value.username.length < 3) {
    formErrors.username = '用户名长度不能少于3个字符';
    isValid = false;
  }
  
  // 邮箱验证
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!form.value.email.trim()) {
    formErrors.email = '请输入邮箱';
    isValid = false;
  } else if (!emailRegex.test(form.value.email)) {
    formErrors.email = '请输入有效的邮箱地址';
    isValid = false;
  }
  
  // 密码验证
  if (!form.value.password) {
    formErrors.password = '请输入密码';
    isValid = false;
  } else if (form.value.password.length < 6) {
    formErrors.password = '密码长度不能少于6个字符';
    isValid = false;
  }
  
  // 确认密码验证
  if (!confirmPassword.value) {
    formErrors.confirmPassword = '请确认密码';
    isValid = false;
  } else if (confirmPassword.value !== form.value.password) {
    formErrors.confirmPassword = '两次输入的密码不一致';
    isValid = false;
  }
  
  // 服务条款验证
  if (!agreeTerms.value) {
    formErrors.terms = '请同意服务条款和隐私政策';
    isValid = false;
  }
  
  return isValid;
};

const handleRegister = async () => {
  if (!validateForm()) return;
  
  error.value = '';
  loading.value = true;
  
  try {
    await axios.post('/api/user/register', form.value);
    loading.value = false;
    success.value = true;
    
    // 启动倒计时
    countdownTimer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(countdownTimer);
        router.push('/login');
      }
    }, 1000);
  } catch (e) {
    loading.value = false;
    error.value = e.response?.data?.message || '注册失败，请稍后重试';
  }
};

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.register-page {
  height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--background-secondary);
}

.register-container {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background-color: var(--background-primary);
  border-radius: var(--card-radius);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.register-header {
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

.register-title {
  font-size: 20px;
  font-weight: 500;
  color: var(--text-normal);
  margin: 0;
}

.register-form {
  margin-bottom: 24px;
}

.register-form :deep(.el-input__inner) {
  padding-left: 40px;
  height: 44px;
  font-size: 15px;
}

.register-form :deep(.el-input.is-error .el-input__inner) {
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

.terms-agreement {
  margin-bottom: 24px;
  font-size: 14px;
  color: var(--text-muted);
}

.terms-agreement :deep(.el-checkbox.is-error .el-checkbox__label) {
  color: #f56c6c;
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

.register-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 6px;
}

.register-footer {
  text-align: center;
  font-size: 14px;
  color: var(--text-muted);
}

.login-link {
  color: var(--interactive-accent);
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.login-link:hover {
  text-decoration: underline;
}

.register-error, .register-success {
  margin-top: 20px;
}

@media (max-width: 576px) {
  .register-container {
    padding: 32px 24px;
    max-width: 100%;
    border-radius: 0;
    box-shadow: none;
    height: 100%;
  }
  
  .register-page {
    align-items: flex-start;
    background-color: var(--background-primary);
  }
}
</style> 
 