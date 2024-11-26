import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',  // 모든 네트워크 인터페이스에서 접근 허용
    port: 5173,       // 포트 번호
    hmr: {
      clientPort: 5173,
      // host: '192.168.210.84',
      // protocol: 'ws'
    },
    watch: {
      usePolling: true  // 파일 변경 감지를 위한 폴링 사용
    },
    optimizeDeps: {
      include: ['kakao.maps.sdk']  // 카카오맵 SDK를 사전 번들링에 포함
    },
    proxy: {
      '/youtube': {
        target: 'https://www.youtube.com',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/youtube/, '')
      }
    }
  }
})