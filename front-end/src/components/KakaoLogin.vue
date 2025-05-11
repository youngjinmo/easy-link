<template>
  <button class="kakao-login-button" @click="handleKakaoLogin">
    <img src="@/assets/kakao_login_logo.png" alt="카카오 로고" class="kakao-icon" />
    <span class="kakao-text">카카오로 시작하기</span>
  </button>
</template>

<script setup lang="ts">

const handleKakaoLogin = () => {
  // 카카오 로그인 페이지로 리다이렉트
  window.location.href = `${import.meta.env.VITE_KAKAO_OAUTH2_CODE_API}`
  + `?client_id=${import.meta.env.VITE_KAKAO_CLIENT_ID}`
  + `&redirect_uri=${import.meta.env.VITE_KAKAO_REDIRECT_URI}`
  + `&response_type=code`;
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