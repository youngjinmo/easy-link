import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('auth_token') || '',
    isLoggedIn: !!localStorage.getItem('auth_token'),
  }),
  actions: {
    setToken(token: string) {
      this.token = token;
      this.isLoggedIn = true;
      localStorage.setItem('auth_token', token);
    },
    clearToken() {
      this.token = '';
      this.isLoggedIn = false;
      localStorage.removeItem('auth_token');
    },
  },
}); 

