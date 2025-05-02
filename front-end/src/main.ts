import './assets/main.css'

import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import App from './App.vue'
import Home from './views/Home.vue'
import KakaoLogin from './components/KakaoLogin.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login/oauth/kakao', component: KakaoLogin },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.use(createPinia())
app.mount('#app') 