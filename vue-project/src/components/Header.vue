<template>
  <div>
    <!-- 헤더 -->
    <header class="header">
      <!-- 로고 -->
      <router-link to="/" class="logo">
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

      <!-- 가운데 메뉴와 검색창 컨테이너 -->
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
          <div class="menu-item dropdown" 
               @mouseenter="showDropdown = true" 
               @mouseleave="showDropdown = false">
            커뮤니티
            <img class="menu-img" :src="communityImage" alt="Community" />
            <!-- 드롭다운 메뉴 -->
            <div v-if="showDropdown" class="dropdown-menu">
              <router-link to="/community/group" class="dropdown-item">모임</router-link>
              <router-link to="/community/inquiry" class="dropdown-item">문의 게시판</router-link>
            </div>
          </div>
        </div>

        <!-- 검색 바 -->
        <div class="search-bar">
          <input type="text" placeholder="검색어를 입력하세요" />
          <router-link to="/search">
            <img :src="searchImage" alt="Search Icon" />
          </router-link>
        </div>
      </div>

       <!-- 로그인 상태에 따른 메뉴 표시 -->
       <div class="log-menu">
        <div v-if="!isLoggedIn" class="log-options">
          <button class="log-item" @click="openLoginModal">로그인
            <img class="user-check" :src="userCheckImage" alt="Login" />
          </button>
          <router-link to="/signup" class="log-item">회원가입
            <img class="user-plus" :src="userPlusImage" alt="Signup" />
          </router-link>
          <!-- 다크모드 토글 버튼 추가 -->
          <button @click="themeStore.toggleDarkMode" class="theme-toggle log-item">
            <i :class="themeStore.isDarkMode ? 'bi bi-sun-fill' : 'bi bi-moon-fill'"></i>
          </button>
        </div>
        <div v-else class="log-options-logged-in">
          <div class="welcome-text">{{ userInfo.userId }}님 환영합니다</div>
          <div class="user-menu">
            <router-link to="/mypage" class="log-item">마이페이지
              <img class="my-page-img" :src="fluentHomePersonImage" alt="My Page" />
            </router-link>
            <button class="log-item" @click="logout">로그아웃</button>
            <!-- 다크모드 토글 버튼 추가 -->
            <button @click="themeStore.toggleDarkMode" class="theme-toggle log-item">
              <i :class="themeStore.isDarkMode ? 'bi bi-sun-fill' : 'bi bi-moon-fill'"></i>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 로그인 모달 -->
    <div v-if="isLoginModalOpen" class="overlay">
      <LoginViewModal @close="closeLoginModal" @login-success="handleLoginSuccess" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useThemeStore } from '@/stores/theme'
import LoginViewModal from '@/views/LoginViewModal.vue';
import { useRouter, useRoute } from 'vue-router';

import searchImage from '@/assets/img/search1.svg';
import groupImage from '@/assets/img/group0.svg';
import calendarImage from '@/assets/img/calendar0.svg';
import communityImage from '@/assets/img/iconoir-community0.svg';
import userCheckImage from '@/assets/img/user-check0.svg';
import userPlusImage from '@/assets/img/user-plus0.svg';
import fluentHomePersonImage from '@/assets/img/fluent-home-person-24-regular0.svg';

const isLoggedIn = ref(false); // 로그인 상태 관리
const userInfo = ref(null); // 로그인한 사용자 정보
const isLoginModalOpen = ref(false); // 로그인 모달 상태
const userId = ref(''); // 사용자 아이디
const rememberMe = ref(false); // 아이디 저장 체크박스 상태
const showDropdown = ref(false);
const themeStore = useThemeStore()

// router와 route 설정
const router = useRouter();
const route = useRoute();

// 로그아웃
const logout = () => {
  isLoggedIn.value = false;
  userInfo.value = null;
  localStorage.removeItem('user');
  localStorage.removeItem('savedVideos'); // 저장된 비디오 정보도 삭제
  
  // 현재 경로가 마이페이지인 경우 홈으로 이동
  if (route.path === '/mypage') {
    router.push('/');
  }
  
  alert('로그아웃되었습니다.');
};

// 로그인 모달 열기/닫기
const openLoginModal = () => {
  isLoginModalOpen.value = true;
};
const closeLoginModal = () => {
  isLoginModalOpen.value = false;
};

// 로그인 성공 처리
const handleLoginSuccess = (user) => {
  isLoggedIn.value = true;
  userInfo.value = user;
  localStorage.setItem('user', JSON.stringify(user)); // 사용자 정보 저장
  if (rememberMe.value) {
    localStorage.setItem('savedUserId', userId.value); // 아이디 저장
  }
  closeLoginModal();
};

// 아이디 저장 체크박스 상태 변경 처리
const handleRememberMeChange = () => {
  if (rememberMe.value) {
    localStorage.setItem('savedUserId', userId.value); // 아이디 저장
  } else {
    localStorage.removeItem('savedUserId'); // 저장된 아이디 삭제
  }
};

// 새로고침 시 로그인 상태와 아이디 저장 상태 복원
onMounted(() => {
  const storedUser = localStorage.getItem('user');
  if (storedUser) {
    userInfo.value = JSON.parse(storedUser);
    isLoggedIn.value = true;
  }

  const savedUserId = localStorage.getItem('savedUserId');
  if (savedUserId) {
    userId.value = savedUserId; // 아이디 복원
    rememberMe.value = true; // 체크박스 상태 유지
  }
});

</script>


<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 30px 0;
  margin-top: 15px;
  width: 95%;
  margin-left: auto;
  margin-right: auto;
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
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25px;
  width: 40%;
  min-width: 600px;
  z-index: 1;
}

.menu-bar {
  display: flex;
  gap: 160px;
  justify-content: center;
  align-items: center;
}

.menu-item {
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: #333;
  text-decoration: none;
}

.menu-img {
  margin-left: 7px;
  height: 24px;
}

.search-bar {
  width: 920px;
  height: 40px;
  display: flex;
  align-items: center;
  background: var(--hover-color);
  border-radius: 20px;
  padding: 5px 20px;
  border: 1px solid var(--border-color);
  box-sizing: border-box;
}

.search-bar input {
  flex-grow: 1;
  border: none;
  background: transparent;
  font-size: 18px;
  outline: none; /* 기본 포커스 스타일 제거 */
  color: var(--text-color);
}

.search-bar:focus-within {
  border: 2px solid #ebd03b; /* 사용자 정의 테두리 */
  border-radius: 20px; /* 테두리 모양 유지 */
}

.search-bar img {
  height: 24px;
}

.log-menu {
  flex: 1;
  display: flex;
  justify-content: flex-end;
  padding-right: 80px;
}

.log-options {
  display: flex;
  align-items: center;
  gap: 45px;
}

.log-item {
  font-size: 22px;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  color: #333;
  text-decoration: none;
  border: none;
  background: none;
  cursor: pointer;
}

.user-check,
.user-plus,
.my-page-img {
  height: 24px; /* 아이콘 크기도 통일 */
  margin-left: 10px;
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
  .center-container {
    width: 30%;
  }

  .search-bar {
    width: 100%;
  }
}

@media (max-width: 1200px) {
  .logo-container {
    min-width: 200px;
  }

  .center-container {
    width: 45%;
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
}

.log-options-logged-in {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  margin-right: 20px;
}

.user-menu {
  display: flex;
  gap: 35px;  /* 기존 간격 유지 */
}

.logo-container {
  flex: 1;
  display: flex;
  justify-content: flex-start;
  padding-left: 80px;
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
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 8px 0;
  min-width: 150px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  z-index: 1000;
}

.dropdown-item {
  display: block;
  padding: 8px 16px;
  color: #333;
  text-decoration: none;
  transition: background-color 0.2s;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
  color: #ebd03b;
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
  color: #333;
  text-decoration: none;
  padding: 10px 20px;
  font-size: 18px;
  transition: background-color 0.3s ease;
}

.dropdown-item:hover {
  background-color: #f1f1f1;
}

/* 다크모드 토글 버튼 스타일 */
.theme-toggle {
  display: flex;
  align-items: center;
  font-size: 22px;
  color: #333;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
}

.theme-toggle i {
  font-size: 24px;
}

/* 다크모드 시 스타일 변경 */
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

.dropdown-item:hover {
  background-color: var(--text-color);
  color: var(--bg-color);
}

/* 이미지 필터 관련 스타일 추가 */
.menu-img,
.user-check,
.user-plus,
.my-page-img,
.search-bar img {
  transition: filter 0.3s ease;
}

/* 다크모드일 때 이미지 필터 적용 */
:root.dark-mode .menu-img,
:root.dark-mode .user-check,
:root.dark-mode .user-plus,
:root.dark-mode .my-page-img,
:root.dark-mode .search-bar img {
  filter: invert(1) brightness(0.8);
}

/* 호버 효과 */
.menu-item:hover .menu-img,
.log-item:hover .user-check,
.log-item:hover .user-plus,
.log-item:hover .my-page-img {
  transform: scale(1.1);
  transition: transform 0.3s ease;
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

/* 로고는 항상 노란색 유지 */
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

/* 귀여운 통통 튀는 애니메이션 */
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

/* 각 글자의 회전 각도 설정 */
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
</style>
