import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import Home from '@/views/Home.vue'
import KakaoCallback from '@/views/KakaoCallback.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: 'URL 단축 서비스'
    }
  },
  {
    path: '/oauth/kakao/callback',
    name: 'KakaoOAuthCallback',
    component: KakaoCallback,
    meta: {
      title: '카카오 로그인'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 전역 네비게이션 가드
router.beforeEach((to, from, next) => {
  // 페이지 제목 설정
  document.title = to.meta.title as string || 'URL 단축 서비스'
  next()
})

export default router 