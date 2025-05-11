<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/utils/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

onMounted(() => {
  // URL에서 토큰 파라미터 확인
  const token = route.query.token as string;
  if (token) {
    // 토큰 저장
    localStorage.setItem('auth_token', token);
    authStore.setToken(token);
    
    // 토큰 파라미터 제거하고 홈으로 리다이렉트
    router.replace('/');
  }
});
</script>

<style lang="scss">
html, body {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  height: 100%;
}

#app {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  height: 100%;
}
</style>
