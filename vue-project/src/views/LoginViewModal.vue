<template>
  <div class="login-modal">
    <div class="login-logo">로그인</div>

    <div class="login-menu">
      <div class="form-group">
        <label class="login-text">아이디</label>
        <input type="text" class="login-border" v-model="userId" />
      </div>

      <div class="form-group">
        <label class="login-text">비밀번호</label>
        <input type="password" class="login-border" v-model="password" />
      </div>
    </div>

    <div class="login-remember-container">
      <input type="checkbox" class="login-remember-check" v-model="rememberMe" />
      <label for="login-remember-check" class="login-remember">아이디 저장</label>
    </div>

    <!-- 실패 메시지 표시 -->
    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <button class="login-button" @click="handleLogin">로그인</button>

    <div class="login-bar">
      <router-link to="/signup" class="login-link">아이디 찾기</router-link>
      <router-link to="/signup" class="login-link">비밀번호 찾기</router-link>
      <router-link to="/signup" class="login-link">회원가입</router-link>
    </div>

    <button class="close-button" @click="$emit('close')">X</button>
  </div>
</template>


<script setup>
import { ref } from 'vue';
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const userId = ref('');
const password = ref('');
const rememberMe = ref(false);
const errorMessage = ref(''); // 실패 메시지 상태

const emit = defineEmits(['close', 'login-success']);

const handleLogin = async () => {
  errorMessage.value = '';

  try {
    console.log('로그인 시도:', {
      userId: userId.value,
      password: password.value
    });

    const response = await axios.post('/api/user/login', {
      userId: userId.value,
      password: password.value
    });

    console.log('서버 응답:', response);

    if (response.data.success) {
      alert(`${response.data.user.userId}님 환영합니다!`);
      emit('login-success', response.data.user);
    } else {
      errorMessage.value = response.data.message || '아이디 또는 비밀번호가 잘못되었습니다.';
    }
  } catch (error) {
    console.error('로그인 에러 상세:', error);
    errorMessage.value = '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
  }
};



</script>


<style scoped>
.login-modal {
  background: #ffffff;
  border: 1px solid;
  border-radius: 28px;
  width: 400px;
  height: 700px;
  /* Increased height for a taller modal */
  padding: 50px 40px;
  /* Increased padding for a taller modal */
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2000;
}

.login-logo {
  color: #000000;
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 40px;
  /* Increased margin for more spacing */
  padding-bottom: 40px;
}

.login-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
  /* Increased gap for more spacing between fields */
  width: 100%;
  align-items: flex-start;
  /* Align labels and inputs to the left */
}

.form-group {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.login-text {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  /* Space between label and input */
}

.login-border {
  background: #ffffff;
  border: 1px solid;
  border-radius: 10px;
  height: 43px;
  width: 90%;
  /* Reduced width for smaller input fields */
  padding: 0 15px;
}

.login-button {
  background: #ebd03b;
  border-radius: 10px;
  width: 100%;
  height: 50px;
  /* Increased height for better visual balance */
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
  margin-top: 10px;
  margin-left: -285px;
}

.login-remember {
  font-size: 16px;
}

.login-bar {
  display: flex;
  gap: 20px;
  margin-top: 30px;
  font-size: 16px;
  justify-content: center;
  width: 100%;
  color: #7c6e1b;
}

.login-link {
  color: #6a5acd;
  text-decoration: none;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.login-link:visited {
  color: inherit;
}

.login-icons img {
    padding-top: 50px;
    padding-right: 20px;
    width: 80px;
    height: 80px;
    object-fit: cover; 
  }
  .error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
  text-align: center;
}

</style>
