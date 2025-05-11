<template>
  <div class="home-container">
    <div class="logo">Easy Link</div>
    
    <div v-if="shortenUrl" class="shorten-url">
      {{ shortenUrl }}
    </div>
    
    <form @submit.prevent="submitUrl" class="url-form">
      <input 
        v-model="originalUrl" 
        type="url" 
        class="url-input" 
        placeholder="줄이고 싶은 URL을 입력해주세요" 
        required 
      />
      
      <template v-if="isLoggedIn">
        <input 
          v-model="customPath" 
          type="text" 
          class="custom-path-input" 
          placeholder="Custom URL path (optional)" 
          :class="{ 'valid': isValid && customPath.length > 0, 'invalid': !isValid && customPath.length > 0 }"
          @input="checkPathAvailability"
        />
      </template>
      
      <button type="submit" class="submit-button" :disabled="!isValid && customPath.length > 0">
        URL 단축하기
      </button>
    </form>
    
    <div class="oauth-buttons">
      <KakaoLogin />
    </div>
    
    <modal-component 
      v-if="showModal" 
      :message="modalMessage" 
      @close="showModal = false" 
    />
    
    <ErrorModal
      :show="showErrorModal"
      :message="errorMessage"
      @close="closeErrorModal"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue';
import axios from '@/utils/axios-interceptor';
import { useRouter, useRoute } from 'vue-router';
import ModalComponent from '@/components/ModalComponent.vue';
import ErrorModal from '@/components/ErrorModal.vue';
import KakaoLogin from '@/components/KakaoLogin.vue';
import { API_URL, BASE_PROTOCOL, BASE_URL, BASE_PORT } from '@/config';

const router = useRouter();
const route = useRoute();
const originalUrl = ref('');
const customPath = ref('');
const shortenUrl = ref('');
const isValid = ref(true);
const showModal = ref(false);
const modalMessage = ref('');
const isLoggedIn = ref(false);
const showErrorModal = ref(false);
const errorMessage = ref('');

// URL 파라미터에서 에러 처리
const handleUrlError = () => {
  const errorCode = route.query.errorCode as string;
  const errorMsg = route.query.message as string;

  if (errorCode && errorMsg) {
    showError(errorMsg);
    // 에러 메시지를 표시한 후 URL에서 에러 파라미터 제거
    router.replace({ query: {} });
  }
};

// 컴포넌트 마운트 시 에러 처리
onMounted(() => {
  checkLoginStatus();
  handleUrlError();
});

// Check if user is logged in when component is mounted
// This would normally come from a user store or auth service
// For now, we'll simulate it
const checkLoginStatus = () => {
  const token = localStorage.getItem('auth_token');
  isLoggedIn.value = !!token;
};

const submitUrl = async () => {
  if (!originalUrl.value) {
    showError('URL을 입력해주세요.');
    return;
  }

  try {
    const response = await axios.post(`${API_URL}/link/free`, {
      originalUrl: originalUrl.value,
      shortPath: customPath.value || undefined
    })
    .catch((error) => {
      console.error('Error shortening URL:', error);
    });
    
    shortenUrl.value = `${BASE_PROTOCOL}${BASE_URL}:${BASE_PORT}/i/${response?.data.shortPath}`;
    
    if (!isLoggedIn.value) {
      showModal.value = true;
      modalMessage.value = '로그인하면 커스텀 URL 경로를 사용할 수 있습니다!';
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      const status = error.response?.status;
      const message = error.response?.data?.message || '알 수 없는 오류가 발생했습니다.';
      
      switch (status) {
        case 400:
          showError('잘못된 URL 형식입니다.');
          break;
        case 403:
          showError('접근이 거부되었습니다.');
          break;
        case 404:
          showError('요청한 리소스를 찾을 수 없습니다.');
          break;
        default:
          showError(message);
      }
    } else {
      showError('알 수 없는 오류가 발생했습니다.');
    }
  }
};

const checkPathAvailability = async () => {
  if (!customPath.value) {
    isValid.value = true;
    return;
  }
  
  try {
    const response = await axios.get(`${API_URL}/urls/check-availability/${customPath.value}`);
    isValid.value = response.data.available;
  } catch (error) {
    console.error('Error checking path availability:', error);
    isValid.value = false;
  }
};

const showError = (message: string) => {
  errorMessage.value = convertErrorMessage(message);
  showErrorModal.value = true;
};

const convertErrorMessage = (message: string) => {
  switch (message) {
    case 'Link expired':
      return '만료된 링크입니다.';
    case 'Exceeded the limit':
      return '최대 단축 URL 갯수를 초과하였습니다.';
    case 'Link not found':
      return '요청한 리소스를 찾을 수 없습니다.';
    case 'Invalid URL':
      return '잘못된 URL 형식입니다.';
    default:
      return '알 수 없는 오류가 발생했습니다.';
  }
};

const closeErrorModal = () => {
  showErrorModal.value = false;
};
</script>

<style lang="scss" scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.logo {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #333;
}

.url-form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.url-input, .custom-path-input, .submit-button, .oauth-buttons {
  width: 100%;
  max-width: 300px;
  margin: 0 auto;
}

.url-input, .custom-path-input {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s;

  &:focus {
    outline: none;
    border-color: #4a90e2;
  }
}

.custom-path-input {
  &.valid {
    border-bottom: 2px solid lightgreen;
  }
  
  &.invalid {
    border-bottom: 2px solid red;
  }
}

.submit-button {
  height: 45px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
}

.shorten-url {
  margin-bottom: 1.5rem;
  padding: 10px 15px;
  background-color: #f0f8ff;
  border-radius: 4px;
  font-weight: bold;
  word-break: break-all;
  width: 100%;
  text-align: center;
}

.oauth-buttons {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 10px;
}

@media (max-width: 768px) {
  .logo {
    font-size: 2rem;
  }
  
  .oauth-buttons {
    // flex-direction: column;
    width: 100%;
  }
  
  .submit-button {
    height: 40px;
    font-size: 16px;
  }
}
</style> 