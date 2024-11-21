<template>
  <div>
    <Header />

    <!-- 로그인 모달 및 오버레이 -->
    <div v-if="isLoginModalOpen" class="overlay">
      <div class="login-div">
        <div class="login-logo">로그인</div>

        <div class="login-menu">
          <label class="login-text1">아이디</label>
          <input type="text" class="login-border" v-model="userId" />

          <label class="login-text2">비밀번호</label>
          <input type="password" class="login-border" v-model="password" />
        </div>

        <button class="login-button" @click="handleLogin">로그인</button>

        <div class="login-remember-container">
          <input type="checkbox" class="login-remember-check" v-model="rememberMe" />
          <label for="login-remember-check" class="login-remember">아이디 저장</label>
        </div>

        <div class="login-bar">
          <router-link to="/signup" style="cursor: pointer;">아이디 찾기</router-link>
          <router-link to="/signup" style="cursor: pointer;">비밀번호 찾기</router-link>
          <router-link to="/signup" style="cursor: pointer;">회원가입</router-link>
        </div>

        <!-- 닫기 버튼 -->
        <button class="close-button" @click="closeLoginModal">X</button>
      </div>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';

// 사용자 입력 데이터
const userId = ref('');
const password = ref('');
const rememberMe = ref(false);

// 로그인 모달 상태
const isLoginModalOpen = ref(true);

// 로그인 모달 닫기
const closeLoginModal = () => {
  isLoginModalOpen.value = false;
};

// 로그인 하기
const handleLogin = async () => {
  const loginData = {
    userId: userId.value,
    password: password.value,
  };

  try {
    const response = await axios.post('http://localhost:8080/api/login', loginData);

    if (response.data.success) {
      alert('로그인에 성공하였습니다!');
      isLoginModalOpen.value = false; // 로그인 성공 시 모달 닫기
    } else {
      alert('로그인에 실패하였습니다. 아이디 또는 비밀번호를 확인해주세요.');
    }
  } catch (error) {
    console.error('로그인 요청 중 오류가 발생했습니다.', error);
    alert('로그인 요청 중 오류가 발생했습니다.');
  }
};
</script>

<style scoped>
/* 페이지 전체 배경 블러 처리를 위한 오버레이 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5); /* 어두운 배경 */
  backdrop-filter: blur(8px); /* 배경 블러 처리 */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000; /* 로그인 모달이 다른 요소 위에 표시되도록 */
}

/* 로그인 모달 */
.login-div {
  background: #ffffff;
  border: 1px solid;
  border-radius: 28px;
  height: 740px;
  width: 580px;
  position: relative; /* 닫기 버튼 위치 설정을 위해 변경 */
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 닫기 버튼 스타일 */
.close-button {
  position: absolute;
  top: 20px;
  right: 20px;
  background: transparent;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.login-logo {
  color: #000000;
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 30px;
}

.login-menu {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
  align-items: center;
}

.login-text1,
.login-text2 {
  font-size: 26px;
  font-weight: 400;
  align-self: flex-start;
}

.login-border {
  background: #ffffff;
  border: 1px solid;
  border-radius: 10px;
  height: 43px;
  width: 100%;
  padding: 0 15px;
}

.login-button {
  background: #ebd03b;
  border-radius: 10px;
  width: 100%;
  height: 45px;
  font-size: 24px;
  border: none;
  transition: background-color 0.3s ease, transform 0.2s ease;
  cursor: pointer;
  margin-top: 30px;
}

.login-button:hover {
  background-color: #d1b031;
  transform: scale(1.05);
}

.login-remember-container {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.login-remember {
  color: #383838;
  font-size: 20px;
  font-weight: 400;
}

.login-remember-check {
  cursor: pointer;
  transform: scale(1.7);
}

.login-bar {
  display: flex;
  gap: 20px;
  margin-top: 30px;
  color: #ebd03b;
  font-size: 16px;
  justify-content: center;
  width: 100%;
}
</style>
