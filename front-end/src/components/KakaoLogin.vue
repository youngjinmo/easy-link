<template>
  <button class="kakao-login-button" @click="handleKakaoLogin">
    <img src="@/assets/kakao_login_logo.png" alt="카카오 로고" class="kakao-icon" />
    <span class="kakao-text">카카오로 시작하기</span>
  </button>
</template>

<script setup lang="ts">
import axios from 'axios';
import router from '@/router/index';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();

const handleKakaoLogin = () => {
  axios.post('/api/login/oauth/kakao')
  .then((response) => {
    const { accessToken } = response.data;
    localStorage.setItem('auth_token', accessToken);
    authStore.setToken(accessToken);
    router.push('/');
  })
  .catch((err) => {
    router.push({
      path: '/',
      query: {
        errorCode: 'kakao_login_failed',
        message: '카카오 로그인에 실패했습니다. 다시 시도해 주세요.'
      }
    });
    console.error("카카오 로그인 실패", err);
  });
}
</script>

<style scoped>
.kakao-login-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 300px;
  height: 45px;
  background-color: #FEE500;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  padding: 0 15px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.kakao-icon {
  width: 22px;
  height: 22px;
  margin-right: 8px;
}

.kakao-text {
  color: #000;
  font-size: 15px;
  font-weight: 500;
}

@media (max-width: 768px) {
  .kakao-login-button {
    height: 40px;
  }
  
  .kakao-text {
    font-size: 16px;
  }

  .kakao-icon {
    width: 20px;
    height: 20px;
  }
}
</style> 