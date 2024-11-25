<template>
  <div>
    <!-- 헤더 -->
    <header class="header">
      <!-- 로고 -->
      <router-link to="/" class="logo" @click="clearSearchQuery">
        <div class="logo-container">
          <span class="logo">
            <span class="logo-item">L</span>
            <span class="logo-item logo-bold">oo</span>
            <span class="logo-item">k</span>
            <span class="logo-item">G</span>
            <span class="logo-item logo-bold">oo</span>
            <span class="logo-item">d</span>
          </span>
        </div>
      </router-link>

      <!-- 가운데 메뉴와 검색창 -->
      <div class="center-container">
        <!-- 메뉴 링크들 -->
        <div class="menu-bar">
          <router-link to="/videos" class="menu-item">
            영상
            <img class="menu-img" :src="groupImage" alt="Videos" />
          </router-link>
          <router-link to="/event" class="menu-item">
            대회 정보
            <img class="menu-img" :src="calendarImage" alt="Competition" />
          </router-link>
          <div class="menu-item dropdown">
            커뮤니티
            <img class="menu-img" :src="communityImage" alt="Community" />
            <!-- 드롭다운 메뉴 -->
            <div class="dropdown-menu">
              <router-link to="/community/group" class="dropdown-item">모임</router-link>
              <router-link to="/community/inquiry" class="dropdown-item">문의 게시판</router-link>
            </div>
          </div>
        </div>

        <!-- 검색 바 -->
        <div class="search-bar">
          <input
            type="text"
            placeholder="검색어를 입력하세요"
            v-model="searchQuery"
            @keyup.enter="handleSearch"
          />
          <button @click="handleSearch" class="search-bar-btn">
            <img :src="searchImage" alt="Search Icon" />
          </button>
        </div>

        <!-- 검색 결과 -->
        <div class="search-results" v-if="searchResults.length > 0">
          <ul>
            <li v-for="result in searchResults" :key="result.id">
              <p><strong>{{ result.source }}</strong>: {{ result.title }}</p>
              <p>{{ result.content }}</p>
            </li>
          </ul>
        </div>

      </div>

      <!-- 로그인 메뉴 -->
      <div class="log-menu">
        <div v-if="!isLoggedIn" class="log-options">
          <button class="log-item" @click="openLoginModal">
            로그인
            <img class="user-check" :src="userCheckImage" alt="Login" />
          </button>
          <button class="log-item" @click="openSignupModal">
            회원가입
            <img class="user-plus" :src="userPlusImage" alt="Signup" />
          </button>
          <button @click="themeStore.toggleDarkMode" class="theme-toggle log-item">
            <i :class="themeStore.isDarkMode ? 'bi bi-sun-fill' : 'bi bi-moon-fill'"></i>
          </button>
        </div>
        <div v-else class="log-options-logged-in">
          <div class="welcome-text" v-if="userInfo">{{ userInfo.userName }}님 환영합니다</div>
          <div class="user-menu">
            <router-link to="/mypage" class="log-item">
              마이페이지
              <img class="my-page-img" :src="fluentHomePersonImage" alt="My Page" />
            </router-link>
            <button class="log-item" @click="logout">
              로그아웃
              <img class="logout-img" :src="logoutImage" alt="Logout" />
            </button>
            <button @click="themeStore.toggleDarkMode" class="theme-toggle log-item">
              <i :class="themeStore.isDarkMode ? 'bi bi-sun-fill' : 'bi bi-moon-fill'"></i>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 모달 -->
    <div v-if="isLoginModalOpen" class="overlay">
      <LoginViewModal 
        @close="closeLoginModal" 
        @login-success="handleLoginSuccess"
        @open-signup="openSignupModal"
      />
    </div>
    <div v-if="isSignupModalOpen" class="overlay">
      <SignupViewModal 
        @close="closeSignupModal"
        @open-login="openLoginModal"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import axios from 'axios';
import { useThemeStore } from '@/stores/theme'
import { useRouter, useRoute } from 'vue-router';
import LoginViewModal from '@/views/LoginViewModal.vue';
import SignupViewModal from '@/views/SignupViewModal.vue';

// 이미지 imports
import searchImage from '@/assets/img/search1.svg';
import groupImage from '@/assets/img/group0.svg';
import calendarImage from '@/assets/img/calendar0.svg';
import communityImage from '@/assets/img/iconoir-community0.svg';
import userCheckImage from '@/assets/img/user-check0.svg';
import userPlusImage from '@/assets/img/user-plus0.svg';
import fluentHomePersonImage from '@/assets/img/fluent-home-person-24-regular0.svg';
import logoutImage from '@/assets/img/Logout.png';

// 상태 관리
const userInfo = ref(null);
const isLoggedIn = computed(() => {
  return userInfo.value !== null;
});
const isLoginModalOpen = ref(false);
const isSignupModalOpen = ref(false);
const showDropdown = ref(false);
const themeStore = useThemeStore();

const router = useRouter();
const route = useRoute();

const clearSearchQuery = () => {
  searchQuery.value = ''; // 검색어 초기화
};

// 로그인 체크
const checkLoginStatus = () => {
  try {
    const storedUserInfo = localStorage.getItem('userInfo');
    if (storedUserInfo) {
      userInfo.value = JSON.parse(storedUserInfo);
      return true;
    }
    return false;
  } catch (error) {
    console.error('로그인 상태 확인 중 오류:', error);
    return false;
  }
};

// 로그아웃
const logout = () => {
  try {
    // 모든 관련 데이터 삭제
    localStorage.removeItem('userInfo');
    localStorage.removeItem('token');
    userInfo.value = null;
    
    // 상태 초기화
    isLoggedIn.value = false;
    userInfo.value = null;
    
    // 로그아웃 알림
    alert('로그아웃 되었습니다.');
    
    // 홈으로 이동 (선택사항)
    router.push('/');
    
  } catch (error) {
    console.error('로그아웃 중 오류 발생:', error);
  }
};

// 모달 관리
const openLoginModal = () => {
  isSignupModalOpen.value = false; // 회원가입 모달이 열려있다면 닫기
  isLoginModalOpen.value = true;
};

const openSignupModal = () => {
  isLoginModalOpen.value = false; // 로그인 모달이 열려있다면 닫기
  isSignupModalOpen.value = true;
};

const closeLoginModal = () => isLoginModalOpen.value = false;
const closeSignupModal = () => isSignupModalOpen.value = false;

// 로그인 성공 처리
const handleLoginSuccess = (userData) => {
  if (userData) {
    isLoggedIn.value = true;
    userInfo.value = userData;
    localStorage.setItem('user', JSON.stringify(userData));
    closeLoginModal();
  }
};

// 컴포넌트 마운트 시 실행
onMounted(() => {
  checkLoginStatus();
});

// 로그인 상태 변경 감지를 위한 이벤트 리스너
window.addEventListener('storage', (e) => {
  if (e.key === 'userInfo') {
    checkLoginStatus();
  }
});

const searchQuery = ref(''); // 사용자가 입력한 검색어
const searchResults = ref([]); // 검색 결과 저장
const isSearching = ref(false); // 검색 로딩 상태

const handleSearch = () => {
  if (!searchQuery.value.trim()) return; // 빈 검색어 무시
  router.push({
    name: 'search', // SearchResultView.vue의 라우트 이름
    query: { query: searchQuery.value }, // 검색어를 query로 전달
  }).catch(() => {
    // 동일 경로일 경우 검색 결과 업데이트를 위한 처리
    router.replace({
      name: 'search',
      query: { query: searchQuery.value },
    });
  });
};


</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30px 40px;
  margin-top: 15px;
  width: 100%;
  max-width: 1920px;
  margin-left: auto;
  margin-right: auto;
  box-sizing: border-box;
  min-width: 1200px; /* 최소 너비 설정 */
  background-color: var(--bg-color);
  transition: background-color 0.3s;
}

.logo {
  display: flex;
  color: #ebd03b;
  /* font-size: 3vw; */
  font-size: 78px;
  font-weight: 400;
  text-decoration: none;
}

.logo-bold {
  font-weight: 600;
}

/* Apply rotation to each individual letter */
.logo-item:nth-child(1) {
  transform: rotate(-10deg);
}

/* L */
.logo-item:nth-child(2) {
  transform: rotate(10deg);
}

/* oo */
.logo-item:nth-child(3) {
  transform: rotate(-10deg);
}

/* k */
.logo-item:nth-child(4) {
  transform: rotate(10deg);
}

/* G */
.logo-item:nth-child(5) {
  transform: rotate(-10deg);
}

/* oo */
.logo-item:nth-child(6) {
  transform: rotate(10deg);
}

/* d */

.center-container {
  flex: 0 1 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25px;
  margin: 0 40px;
  min-width: 800px; /* 최소 너비 설정 */
  z-index: 1;
}

.menu-bar {
  display: flex;
  gap: 80px;
  justify-content: center;
  align-items: center;
  white-space: nowrap;
}

.menu-item {
  font-size: 22px;
  white-space: nowrap;
  display: flex;
  align-items: center;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: #333;
  text-decoration: none;
  position: relative;
  transition: transform 0.3s ease;
}

.menu-item::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #ebd03b;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.menu-item:hover {
  transform: translateY(-2px);
  color: #333;
}

.menu-item:hover::after {
  width: 100%;
}

.menu-img {
  margin-left: 7px;
  height: 24px;
}

.search-bar {
  width: 100%;
  max-width: 920px;
  min-width: 300px;
  height: 40px;
  display: flex;
  align-items: center;
  background: var(--hover-color);
  border-radius: 20px;
  padding: 5px 20px;
  border: 1px solid var(--border-color);
  box-sizing: border-box;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.search-bar:hover {
  transform: scale(1.01);
  box-shadow: 0 2px 8px rgba(235, 208, 59, 0.2);
}

.search-bar:focus-within {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(235, 208, 59, 0.3);
}

.search-bar input {
  flex: 1;
  min-width: 0;
  border: none;
  background: transparent;
  font-size: 18px;
  outline: none;
  color: var(--text-color);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.search-bar:focus-within {
  border: 2px solid #ebd03b; /* 사용자 정의 테두리 */
  border-radius: 20px; /* 테두리 모양 유지 */
}

.search-bar img {
  height: 24px;
  flex-shrink: 0;
}

.search-bar-btn {
  background: var(--hover-color);
  border: none;
}

.log-menu {
  flex: 0 0 350px;
  white-space: nowrap;
  position: relative;
  height: 80px;
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
}

.log-options {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-top: 35px;
  height: 40px;
  margin-right: 60px;
}

.log-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 40px;
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: var(--text-color);
  text-decoration: none;
  border: none;
  background: none;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.log-item::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(235, 208, 59, 0.1);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.5s ease;
}

.log-item:hover::before {
  width: 200px;
  height: 200px;
  opacity: 0;
}

.log-item:hover {
  transform: translateY(-2px);
  color: #333;
}

.user-check,
.user-plus,
.my-page-img,
.logout-img {
  height: 24px;
  transition: all 0.3s ease;
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.container {
  display: flex;
  flex-direction: row;
}

@media (max-width: 1600px) {
  .header {
    justify-content: space-between;
    gap: 20px;
  }

  .center-container {
    width: 35%;
  }

  .log-menu {
    min-width: 250px;
  }
}

@media (max-width: 1400px) {
  .header {
    padding: 20px;
  }

  .menu-bar {
    gap: 60px;
  }

  .center-container {
    min-width: 700px;
  }
}

@media (max-width: 1200px) {
  .header {
    flex-wrap: wrap;
    justify-content: center;
    min-width: 800px; /* 최소 너비 조정 */
  }

  .logo-container {
    flex: 0 0 100%;
    text-align: center;
    margin-bottom: 20px;
  }

  .center-container {
    flex: 0 0 100%;
    margin: 20px 0;
    min-width: 600px;
  }

  .menu-bar {
    gap: 40px;
  }

  .log-menu {
    flex: 0 0 100%;
    display: flex;
    justify-content: center;
  }
}

@media (max-width: 1000px) {
  .header {
    flex-direction: column;
    height: auto;
    padding: 20px 0;
  }

  .center-container {
    position: relative;
    left: 0;
    transform: none;
    width: 90%;
    min-width: unset;
  }

  .search-bar {
    width: 100%;
    min-width: unset;
  }

  .log-menu {
    min-width: unset;
  }
}

@media (max-width: 768px) {
  .menu-bar {
    gap: 30px;
  }

  .menu-item {
    font-size: 20px;
  }
}

@media (max-width: 576px) {
  .menu-bar {
    flex-direction: column;
    gap: 15px;
    align-items: center;
  }

  .search-bar {
    width: 90%;
  }

}

.welcome-text {
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: #333;
  position: relative;
  transition: all 0.3s ease;
}

.log-options-logged-in {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 30px;
}

.user-menu .log-item {
  display: flex;
  align-items: center;
  height: 40px; /* 높이 통일 */
  padding: 0 10px; /* 좌우 패딩 추가 */
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
}

.my-page-img {
  height: 24px;
  margin-left: 10px;
  vertical-align: middle; /* 이미지 수직 정렬 */
}

.user-menu button.log-item {
  background: none;
  border: none;
  cursor: pointer;
  line-height: 40px; /* 높이와 동일하게 설정 */
  padding: 0 10px;
}

.logo-container {
  flex: 0 0 300px; /* 고정 너비 */
}

/* 반응형 부분만 수정 */
@media (max-width: 1800px) {
  .center-container {
    width: 45%;
  }
}

@media (max-width: 1500px) {
  .logo-container {
    padding-left: 40px;
  }

  .log-menu {
    padding-right: 40px;
  }

  .menu-bar {
    gap: 120px;
  }
}

@media (max-width: 1300px) {
  .menu-bar {
    gap: 80px;
  }

  .center-container {
    width: 50%;
  }
}

@media (max-width: 1100px) {
  .header {
    flex-direction: column;
    padding: 20px 0;
  }

  .logo-container {
    padding-left: 0;
    margin-bottom: 30px;
    display: flex;
    justify-content: center;
  }

  .center-container {
    position: relative;
    left: 0;
    transform: none;
    width: 80%;
    margin: 0 auto;
  }

  .menu-bar {
    gap: 60px;
    margin-bottom: 20px;
  }

  .search-bar {
    width: 100%;
  }

  .log-menu {
    padding-right: 0;
    margin-top: 30px;
    display: flex;
    justify-content: center;
  }

  .log-options, .log-options-logged-in {
    justify-content: center;
  }

  .welcome-text {
    text-align: center;
  }
}

@media (max-width: 768px) {
  .menu-bar {
    gap: 40px;
  }

  .center-container {
    width: 90%;
  }

  .menu-item {
    font-size: 20px;
  }
}

@media (max-width: 576px) {
  .menu-bar {
    flex-direction: column;
    gap: 50px !important;
    align-items: center;
  }

  .log-options {
    flex-direction: column;
    gap: 15px;
  }

  .user-menu {
    flex-direction: column;
    gap: 15px;
    align-items: center;
  }
}

.dropdown {
  position: relative;
  padding-bottom: 20px;
  margin-bottom: -20px;
  z-index: 2000;
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 5px);
  left: -20px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 12px 0;
  min-width: 160px;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 드롭다운 표시 시 */
.dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  top: 100%;
}

/* 드롭다운 아이템 스타일 */
.dropdown-item {
  display: block;
  padding: 10px 24px;
  color: var(--text-color);
  text-decoration: none;
  transition: all 0.2s ease;
  text-align: left;
  white-space: nowrap;
  font-size: 20px;
  font-family: 'Noto Sans KR', sans-serif;
}

.dropdown-item:hover {
  background-color: var(--hover-color);
  transform: translateX(5px);
  color: #ebd03b;
}

/* 다크모드 대응 */
:root.dark-mode .dropdown-menu {
  background-color: var(--bg-color);
  border-color: var(--border-color);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 메뉴 아이템 */
.menu-bar {
  display: flex;
  gap: 200px;
  font-size: 30px;
}

.menu-item {
  display: flex;
  align-items: center;
  color: inherit;
  text-decoration: none;
  position: relative;
}

.menu-img {
  margin-left: 10px;
  height: 24px;
}

/* 드롭다운 메뉴 */
.dropdown-menu {
  position: absolute;
  top: 100%; /* 부모 요소 아래에 표시 */
  left: 0;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.dropdown-item {
  color: #333333;
  text-decoration: none;
  padding: 10px 20px;
  font-size: 18px;
  transition: background-color 0.3s ease;
}


/* 다크모드 토 버튼 스타일 */
.theme-toggle {
  position: fixed;
  right: 40px;
  top: 45px;
  font-size: 22px;
  color: var(--text-color);
  background: none;
  border: none;
  cursor: pointer;
  z-index: 2;
  user-select: none;
}

.theme-toggle:hover {
  transform: rotate(180deg);
  color: #ebd03b;
}

.theme-toggle i {
  font-size: 24px;
  pointer-events: none; /* 아이콘에 대한 마우스 이벤트 방지 */
}

/* 다모드 시 스타일 변경 */
:root {
  --bg-color: #fff;
  --text-color: #333;
}

.dark-mode {
  --bg-color: #1a1a1a;
  --text-color: #fff;
}

.header {
  background-color: var(--bg-color);
}

.menu-item, .log-item, .welcome-text {
  color: var(--text-color);
}

.search-bar {
  background: var(--bg-color);
  border: 1px solid var(--text-color);
}

.search-bar input {
  color: var(--text-color);
}

.dropdown-menu {
  background-color: var(--bg-color);
  border-color: var(--text-color);
}

.dropdown-item {
  color: var(--text-color);
}

/* 이미지 필터 관련 스타일 추가 */
.menu-img,
.user-check,
.user-plus,
.my-page-img,
.search-bar img {
  height: 24px;
  margin-left: 10px;
  transition: all 0.3s ease;
}

/* 로그아웃 이미지 별도 필터 용 */
.logout-img {
  height: 24px;
  margin-left: 10px;
  transition: all 0.3s ease;
  opacity: 0.7; /* 투명도 추가 */
}

/* 다크모드 미지 필터 */
:root.dark-mode .menu-img,
:root.dark-mode .user-check,
:root.dark-mode .user-plus,
:root.dark-mode .my-page-img,
:root.dark-mode .search-bar img,
:root.dark-mode .logout-img {
  filter: invert(0.7) brightness(1.5); /* 다그레이 톤으로 통일 */
}

/* 호버 효과 통일 */
.menu-item:hover .menu-img,
.log-item:hover .user-check,
.log-item:hover .user-plus,
.log-item:hover .my-page-img,
.log-item:hover .logout-img,
.search-bar:hover img {
  transform: scale(1.1);
  filter: brightness(0) saturate(100%) invert(83%) sepia(25%) saturate(638%) hue-rotate(332deg) brightness(101%) contrast(92%);
  /* 이 필터는 #ebd03b 색상을 만듭니다 */
}

/* 드롭다운 메뉴 다크모드 스타일 개선 */
.dropdown-menu {
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

:root.dark-mode .dropdown-menu {
  background-color: var(--bg-color);
  border-color: var(--border-color);
  box-shadow: 0 2px 5px rgba(255, 255, 255, 0.1);
}

/* 검색바 스타일 개선 */
.search-bar {
  background: var(--hover-color);
  border: 1px solid var(--border-color);
}

:root.dark-mode .search-bar {
  background: var(--hover-color);
  border-color: var(--border-color);
}

.search-bar:focus-within {
  border-color: #ebd03b;
}

/* 메뉴 아이템 호버 효과 */
.menu-item:hover,
.log-item:hover {
  color: #ebd03b;
}

/* 로고는 항상 란색 유지 */
.logo {
  color: #ebd03b !important;
}

/* 다크모드에서의 환영 텍스트 */
:root.dark-mode .welcome-text {
  color: var(--text-color);
}

/* 로고 관련 스타일 수정 및 추가 */
.logo-item {
  display: inline-block;
  transition: all 0.3s ease;
  position: relative;
}

/* 기본 회전 상태 */
.logo-item:nth-child(1) { transform: rotate(-10deg); }
.logo-item:nth-child(2) { transform: rotate(10deg); }
.logo-item:nth-child(3) { transform: rotate(-10deg); }
.logo-item:nth-child(4) { transform: rotate(10deg); }
.logo-item:nth-child(5) { transform: rotate(-10deg); }
.logo-item:nth-child(6) { transform: rotate(10deg); }

/* 호버 시 애니메이션 효과 */
.logo:hover .logo-item {
  animation: wiggleJump 0.8s ease;
  animation-fill-mode: both;
}

/* 각 글자별 애니메이션 딜레이 */
.logo:hover .logo-item:nth-child(1) { animation-delay: 0s; }
.logo:hover .logo-item:nth-child(2) { animation-delay: 0.1s; }
.logo:hover .logo-item:nth-child(3) { animation-delay: 0.2s; }
.logo:hover .logo-item:nth-child(4) { animation-delay: 0.3s; }
.logo:hover .logo-item:nth-child(5) { animation-delay: 0.4s; }
.logo:hover .logo-item:nth-child(6) { animation-delay: 0.5s; }

/* 귀여운 통통 튀는 애니이션 */
@keyframes wiggleJump {
  0% {
    transform: scale(1) rotate(var(--rotation));
  }
  10% {
    transform: scale(1.1) rotate(calc(var(--rotation) - 5deg));
  }
  20% {
    transform: scale(0.9) rotate(calc(var(--rotation) + 5deg));
  }
  30% {
    transform: translateY(-20px) scale(1.1) rotate(calc(var(--rotation) - 3deg));
  }
  50% {
    transform: translateY(0) scale(1.1) rotate(calc(var(--rotation) + 3deg));
  }
  70% {
    transform: translateY(-10px) scale(1.05) rotate(var(--rotation));
  }
  100% {
    transform: translateY(0) scale(1) rotate(var(--rotation));
  }
}

/* 각 자의 회전 각도 설정 */
.logo-item:nth-child(1) { --rotation: -10deg; }
.logo-item:nth-child(2) { --rotation: 10deg; }
.logo-item:nth-child(3) { --rotation: -10deg; }
.logo-item:nth-child(4) { --rotation: 10deg; }
.logo-item:nth-child(5) { --rotation: -10deg; }
.logo-item:nth-child(6) { --rotation: 10deg; }

/* 로고 컨테이너 효과 */
.logo-container {
  transition: all 0.3s ease;
}

/* 글자 색상 효과 */
.logo:hover .logo-item {
  text-shadow: 0 0 15px rgba(235, 208, 59, 0.5),
               0 0 25px rgba(235, 208, 59, 0.3);
}

/* oo 글자 특별 효과 */
.logo-item.logo-bold {
  transition: all 0.3s ease;
}

.logo:hover .logo-bold {
  animation: squishyBounce 0.8s ease;
}

@keyframes squishyBounce {
  0%, 100% { transform: scale(1) rotate(var(--rotation)); }
  30% { transform: scaleX(1.2) scaleY(0.8) rotate(calc(var(--rotation) - 5deg)); }
  50% { transform: scaleX(0.8) scaleY(1.2) rotate(calc(var(--rotation) + 5deg)); }
  70% { transform: scale(1.1) rotate(var(--rotation)); }
}

/* 로그아웃 이미지 스타일 추가 */
.logout-img {
  height: 24px; /* 다른 아이콘들 동일한 크기 */
  margin-left: 10px;
  vertical-align: middle;
}

/* 로그인 메뉴 아이템 스타일 통일 */
.user-menu .log-item {
  display: flex;
  align-items: center;
  height: 40px;
  padding: 0 10px;
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  white-space: nowrap;
}

/* 다크모드 대응을 위한 이미지 필터 추가 */
:root.dark-mode .logout-img {
  filter: invert(1) brightness(0.8);
}

/* 호버 효과 추가 */
.log-item:hover .logout-img {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}

/* 다크모드 스타일 */
:root[class='dark-mode'] {
  --bg-color: #1a1a1a;
  --text-color: #fff;
  --border-color: #444;
  --hover-color: #2a2a2a;
}

/* 다크모드 이미지 필터 */
:root[class='dark-mode'] .menu-img,
:root[class='dark-mode'] .user-check,
:root[class='dark-mode'] .user-plus,
:root[class='dark-mode'] .my-page-img,
:root[class='dark-mode'] .search-bar img,
:root[class='dark-mode'] .logout-img {
  filter: invert(0.7) brightness(1.5);
}

/* 드롭다운 메뉴 다크모드 */
:root[class='dark-mode'] .dropdown-menu {
  background-color: var(--bg-color);
  border-color: var(--border-color);
  box-shadow: 0 2px 5px rgba(255, 255, 255, 0.1);
}

/* 검색바 다크모드 */
:root[class='dark-mode'] .search-bar {
  background: var(--hover-color);
  border-color: var(--border-color);
}

/* 환영 텍스트 다크모드 */
:root[class='dark-mode'] .welcome-text {
  color: var(--text-color);
}

/* 로고 아이템 기본 스타일 */
.logo-item {
  display: inline-block;
  position: relative;
  animation: float 3s ease-in-out infinite;
  transform-origin: center;
}

/* 각 글자별 애니메이션 딜레이와 움직임 설정 */
.logo-item:nth-child(1) { /* L */
  animation: float 3s ease-in-out infinite;
  animation-delay: 0s;
}

.logo-item:nth-child(2) { /* oo */
  animation: bounce 3s ease-in-out infinite;
  animation-delay: 0.2s;
}

.logo-item:nth-child(3) { /* k */
  animation: wiggle 3s ease-in-out infinite;
  animation-delay: 0.4s;
}

.logo-item:nth-child(4) { /* G */
  animation: shake 3s ease-in-out infinite;
  animation-delay: 0.6s;
}

.logo-item:nth-child(5) { /* oo */
  animation: bounce 3s ease-in-out infinite;
  animation-delay: 0.8s;
}

.logo-item:nth-child(6) { /* d */
  animation: float 3s ease-in-out infinite;
  animation-delay: 1s;
}

/* 둥둥 떠다니는 애니메이션 */
@keyframes float {
  0%, 100% { transform: translateY(0) rotate(var(--rotation)); }
  50% { transform: translateY(-10px) rotate(var(--rotation)); }
}

/* 통통 튀는 애니메이션 */
@keyframes bounce {
  0%, 100% { transform: scale(1) rotate(var(--rotation)); }
  50% { transform: scale(1.1) rotate(var(--rotation)) translateY(-5px); }
}

/* 좌우 흔들림 애니메이션 */
@keyframes wiggle {
  0%, 100% { transform: rotate(calc(var(--rotation) - 5deg)); }
  50% { transform: rotate(calc(var(--rotation) + 5deg)); }
}

/* 떨림 애니메이션 */
@keyframes shake {
  0%, 100% { transform: translateX(0) rotate(var(--rotation)); }
  25% { transform: translateX(-2px) rotate(calc(var(--rotation) - 2deg)); }
  75% { transform: translateX(2px) rotate(calc(var(--rotation) + 2deg)); }
}

/* 호버 시 추가 효과 */
.logo:hover .logo-item {
  animation-play-state: paused; /* 호버 시 기존 애니메이션 일시 정지 */
  animation: wiggleJump 0.8s ease forwards;
}

/* 호버 시 각 글자 딜레이 */
.logo:hover .logo-item:nth-child(1) { animation-delay: 0s; }
.logo:hover .logo-item:nth-child(2) { animation-delay: 0.1s; }
.logo:hover .logo-item:nth-child(3) { animation-delay: 0.2s; }
.logo:hover .logo-item:nth-child(4) { animation-delay: 0.3s; }
.logo:hover .logo-item:nth-child(5) { animation-delay: 0.4s; }
.logo:hover .logo-item:nth-child(6) { animation-delay: 0.5s; }

/* 발광 효과 추가 */
.logo-item {
  transition: text-shadow 0.3s ease;
}

.logo:hover .logo-item {
  text-shadow: 0 0 15px rgba(235, 208, 59, 0.3),
               0 0 25px rgba(235, 208, 59, 0.2);
}

/* 이미지 기본 스타일 - 라이트모드 (검은색) */
.menu-img,
.user-check,
.user-plus,
.my-page-img,
.logout-img,
.search-bar img {
  transition: all 0.3s ease;
  height: 24px;
  margin-left: 10px;
  filter: brightness(0); /* 검은색으로 변경 */
}

/* 호버 시 노란색으로 변경 (라이트모드, 다크모드 모두) */
.menu-item:hover img,
.log-item:hover img,
.search-bar:hover img {
  transform: scale(1.1);
  filter: invert(72%) sepia(87%) saturate(481%) hue-rotate(358deg) brightness(96%) contrast(91%);
}

/* 다크모드 기본 이미지 스타일 (흰색) */
:root.dark-mode .menu-img,
:root.dark-mode .user-check,
:root.dark-mode .user-plus,
:root.dark-mode .my-page-img,
:root.dark-mode .logout-img,
:root.dark-mode .search-bar img {
  filter: invert(1) brightness(0.8);
}

/* 다크모드에서 호버 시 노란색으로 변경 */
:root.dark-mode .menu-item:hover img,
:root.dark-mode .log-item:hover img,
:root.dark-mode .search-bar:hover img {
  filter: invert(72%) sepia(87%) saturate(481%) hue-rotate(358deg) brightness(96%) contrast(91%);
}

/* 메뉴 아이템 기본 스타일 */
.menu-item, .log-item {
  color: #333;
  transition: transform 0.3s ease;
}

/* 호버 효과 - 라이트모드 */
.menu-item:hover, .log-item:hover {
  transform: translateY(-2px);
  color: #333 !important; /* 강제로 검은색 유지 */
}

/* 이미지 호버 효과 - 라이트모드 */
.menu-item:hover img,
.log-item:hover img,
.search-bar:hover img {
  transform: scale(1.1);
  filter: brightness(0) !important; /* 강제로 검은색 유지 */
}

/* 메뉴 밑줄 효과만 노란색으로 */
.menu-item::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 50%;
  width: 0;
  height: 2px;
  background-color: #ebd03b;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.menu-item:hover::after {
  width: 100%;
}

/* 다크모드 스타일 */
:root.dark-mode .menu-item,
:root.dark-mode .log-item {
  color: #fff;
}

:root.dark-mode .menu-item:hover,
:root.dark-mode .log-item:hover {
  color: #ebd03b !important;
}

:root.dark-mode .menu-item:hover img,
:root.dark-mode .log-item:hover img,
:root.dark-mode .search-bar:hover img {
  filter: invert(72%) sepia(87%) saturate(481%) hue-rotate(358deg) brightness(96%) contrast(91%) !important;
}

/* 로그인 메뉴 컨테이너 */
.log-menu {
  flex: 0 0 350px;
  white-space: nowrap;
  position: relative;
  height: 80px;
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
}

/* 다크모드 토글 버튼 */
.theme-toggle {
  position: fixed;
  right: 40px;
  top: 45px;
  width: 24px;
  height: 24px;
  font-size: 22px;
  color: var(--text-color);
  background: none;
  border: none;
  cursor: pointer;
  z-index: 1000;
  user-select: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 로그인 후 메뉴 - 위치 조정 */
.log-options-logged-in {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 5px; /* 상단 여백 줄임 */
  margin-right: 60px;
  width: 250px;
}

/* 환영 메시지 */
.welcome-text {
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: var(--text-color);
  margin-bottom: 8px;
  text-align: center;
}

/* 로그인 후 메뉴 버튼 컨테이너 */
.user-menu {
  display: flex;
  align-items: center;
  gap: 30px;
  height: 40px;
  justify-content: center;
  width: 100%;
}

/* 로그인/회원가입 버튼 컨테이너 */
.log-options {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-top: 35px;
  height: 40px;
  margin-right: 60px;
}

/* 버튼 스타일 통일 */
.log-item {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  height: 40px;
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: var(--text-color);
  text-decoration: none;
  border: none;
  background: none;
  cursor: pointer;
  transition: transform 0.3s ease;
}

</style>

