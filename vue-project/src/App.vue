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
  --bg-color: #ffffff;
  --text-color: #333333;
  --border-color: #dddddd;
  --hover-color: #f5f5f5;
  --secondary-bg: #f8f8f8;
}

:root.dark-mode {
  --bg-color: #2d2d2d;
  --text-color: #e0e0e0;
  --border-color: #4a4a4a;
  --hover-color: #3d3d3d;
  --secondary-bg: #363636;
}

body {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s, color 0.3s;
}

/* 공통 스타일 */
.card, .modal, .dropdown-menu {
  background-color: var(--bg-color);
  border-color: var(--border-color);
}

input, textarea, select {
  background-color: var(--bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

a {
  color: var(--text-color);
}

button {
  background-color: var(--bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
}

.app-container {
  min-height: 100vh;
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* 기본 링크 스타일 */
a {
  color: var(--text-color);
  text-decoration: none;
  transition: color 0.3s ease;
}

a:hover {
  color: var(--accent-primary);
}

/* 기본 버튼 스타일 */
button {
  background-color: var(--bg-color);
  color: var(--text-color);
  border-color: var(--border-color);
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
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-color);
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.theme-toggle:hover {
  background: var(--accent-secondary);
  color: var(--bg-color);
}

.theme-toggle i {
  font-size: 1.2rem;
}

/* 네비게이션 바 스타일 */
.navbar {
  background-color: var(--bg-color);
  padding: 1rem;
  display: flex;
  justify-content: flex-end;
  border-bottom: 1px solid var(--border-color);
}

/* 이미지 관련 스타일 추가 */
img {
  max-width: 100%;
  height: auto;
  display: block;
  opacity: 1 !important; /* 이미지 투명도 강제 설정 */
}
</style>
