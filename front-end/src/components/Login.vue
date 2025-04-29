<template>
  <div class="login">
    <div class="login-container">
      <h2>Login</h2>
      <div class="social-buttons">
        <el-button
          type="primary"
          class="kakao-login"
          @click="loginWithKakao"
          :loading="kakaoLoading"
        >
          Login with Kakao
        </el-button>
        
        <el-button
          type="danger"
          class="google-login"
          @click="loginWithGoogle"
          :loading="googleLoading"
        >
          Login with Google
        </el-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { ref } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const store = useStore()
    const router = useRouter()
    const kakaoLoading = ref<boolean>(false)
    const googleLoading = ref<boolean>(false)

    const loginWithKakao = async (): Promise<void> => {
      kakaoLoading.value = true
      try {
        // 카카오 로그인 URL로 리다이렉트
        window.location.href = `${process.env.VUE_APP_API_URL}/oauth2/authorization/kakao`
      } catch (error) {
        ElMessage.error('Failed to initialize Kakao login')
      } finally {
        kakaoLoading.value = false
      }
    }

    const loginWithGoogle = async (): Promise<void> => {
      googleLoading.value = true
      try {
        // 구글 로그인 URL로 리다이렉트
        window.location.href = `${process.env.VUE_APP_API_URL}/oauth2/authorization/google`
      } catch (error) {
        ElMessage.error('Failed to initialize Google login')
      } finally {
        googleLoading.value = false
      }
    }

    return {
      kakaoLoading,
      googleLoading,
      loginWithKakao,
      loginWithGoogle
    }
  }
}
</script>

<style scoped lang="scss">
.login {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f7fa;

  .login-container {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 400px;
    text-align: center;

    h2 {
      margin-bottom: 2rem;
      color: #303133;
    }

    .social-buttons {
      display: flex;
      flex-direction: column;
      gap: 1rem;

      .kakao-login {
        background-color: #FEE500;
        border-color: #FEE500;
        color: #000000;
        
        &:hover {
          background-color: #FDD800;
          border-color: #FDD800;
        }
      }

      .google-login {
        background-color: #DB4437;
        border-color: #DB4437;
        
        &:hover {
          background-color: #C53929;
          border-color: #C53929;
        }
      }
    }
  }
}
</style>
