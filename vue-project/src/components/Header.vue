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
              <router-link to="/community/board" class="dropdown-item">자유 게시판</router-link>
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
        </div>
        <div v-else class="log-options-logged-in">
          <div class="welcome-text">{{ userInfo.userId }}님 환영합니다</div>
          <div class="user-menu">
            <router-link to="/mypage" class="log-item">마이페이지
              <img class="my-page-img" :src="fluentHomePersonImage" alt="My Page" />
            </router-link>
            <button class="log-item" @click="logout">로그아웃</button>
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
import LoginViewModal from '@/views/LoginViewModal.vue';

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

// 로그아웃
const logout = () => {
  isLoggedIn.value = false;
  userInfo.value = null;
  localStorage.removeItem('user'); // 로그인 상태 정보 삭제
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
}

.logo {
  display: flex;
  color: #ebd03b;
  font-size: 2.5vw; /* 크기 축소 */
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
  background: #f5f5f5;
  border-radius: 20px;
  padding: 5px 20px;
  border: 2px solid transparent;
  box-sizing: border-box;
}

.search-bar input {
  flex-grow: 1;
  border: none;
  background: transparent;
  font-size: 18px;
  outline: none; /* 기본 포커스 스타일 제거 */
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
    gap: 20px;
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
</style>
