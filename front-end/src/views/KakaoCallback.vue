<template>
  <div class="kakao-callback">
    <p>카카오 로그인 처리 중...</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from '@/utils/axios-interceptor';
import { useAuthStore } from '@/utils/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

onMounted(async () => {
  try {
    // URL에서 인가 코드 추출
    const code = route.query.code as string;
    if (!code) {
      throw new Error('인가 코드가 없습니다.');
    }

    // REST API 서버에 인가 코드 전달하여 토큰 발급 요청
    const response = await axios.get('/api/oauth/kakao/callback?code='+code);
    const accessToken = response?.data;

    // 토큰 저장
    authStore.setToken(accessToken);

    // 홈으로 리다이렉트
    router.push('/');
  } catch (error) {
    console.error('카카오 로그인 처리 중 오류 발생:', error);
    router.push({
      path: '/',
      query: {
        errorCode: 'kakao_login_failed',
        message: '카카오 로그인에 실패했습니다. 다시 시도해 주세요.'
      }
    });
  }
});
</script>

<style scoped>
.kakao-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  font-size: 1.2rem;
  color: #333;
}
</style> 