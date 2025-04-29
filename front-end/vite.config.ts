import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 8080,
    proxy: {
      '/i': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path,
        // TODO 추후 운영 환경 보안 처리 필요
        secure: false,
      },
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        // TODO 추후 운영 환경 보안 처리 필요
        secure: false,
      }
    },
    cors: true,
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: process.env.NODE_ENV !== 'production',
  }
}) 