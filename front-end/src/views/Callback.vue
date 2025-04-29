<template>
  <div class="callback-container">
    <div class="loading-spinner"></div>
    <p>Processing login...</p>
  </div>
</template>

<script lang="ts" setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { API_URL } from '@/config';

const router = useRouter();

onMounted(async () => {
  try {
    // Get code and state from URL
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const provider = window.location.pathname.includes('kakao') ? 'kakao' : 'google';
    
    if (code) {
      // Send the code to your backend
      const response = await axios.post(`${API_URL}/auth/oauth2/callback`, {
        code,
        provider
      });
      
      // Store the token in localStorage
      if (response.data.token) {
        localStorage.setItem('auth_token', response.data.token);
      }
    }
  } catch (error) {
    console.error('Error during OAuth callback:', error);
  }
  
  // Redirect to home page regardless of success/failure
  router.push('/');
});
</script>

<style lang="scss" scoped>
.callback-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border-left-color: #4a90e2;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style> 