<template>
  <div :class="{ 'dark-mode': themeStore.isDarkMode }">
    <Header />
    <div class="app-container">
      <router-view></router-view>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import Header from './components/Header.vue';
import Footer from './components/Footer.vue';

const themeStore = useThemeStore()

onMounted(() => {
  themeStore.initializeTheme()
})
</script>

<style>
:root {
  /* 라이트 모드 (기본) */
  --bg-primary: #FFF5E6;
  --bg-secondary: #FAEBD7;
  --bg-tertiary: #fff;
  
  /* 텍스트 색상 */
  --text-primary: #4A4A4A;
  --text-secondary: #606060;
  --text-tertiary: #808080;
  
  /* 강조 색상 */
  --accent-primary: #8B4513;
  --accent-secondary: #DEB887;
  
  /* 테두리 색상 */
  --border-primary: #DEB887;
  --border-secondary: #E8E8E8;
}

/* 다크 모드 변수 */
.dark-mode {
  /* 배경 색상 */
  --bg-primary: #1a1a1a;
  --bg-secondary: #2d2d2d;
  --bg-tertiary: #333;
  
  /* 텍스트 색상 */
  --text-primary: #e0e0e0;
  --text-secondary: #b0b0b0;
  --text-tertiary: #808080;
  
  /* 강조 색상 */
  --accent-primary: #cd853f;
  --accent-secondary: #8B4513;
  
  /* 테두리 색상 */
  --border-primary: #404040;
  --border-secondary: #505050;
}

/* 전역 스타일 */
body {
  margin: 0;
  padding: 0;
  background-color: var(--bg-primary);
  color: var(--text-primary);
  transition: background-color 0.3s ease, color 0.3s ease;
}

.app-container {
  min-height: 100vh;
  background-color: var(--bg-primary);
  color: var(--text-primary);
}

/* 기본 링크 스타일 */
a {
  color: var(--text-primary);
  text-decoration: none;
  transition: color 0.3s ease;
}

a:hover {
  color: var(--accent-primary);
}

/* 기본 버튼 스타일 */
button {
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  border: 1px solid var(--border-primary);
  transition: all 0.3s ease;
}

button:hover {
  background-color: var(--accent-secondary);
}

/* 테마 토글 버튼 스타일 */
.theme-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-primary);
  color: var(--text-primary);
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-toggle:hover {
  background: var(--accent-secondary);
  color: var(--bg-primary);
}

.theme-toggle i {
  font-size: 1.2rem;
}

/* 네비게이션 바 스타일 */
.navbar {
  background-color: var(--bg-secondary);
  padding: 1rem;
  display: flex;
  justify-content: flex-end;
  border-bottom: 1px solid var(--border-primary);
}
</style>
