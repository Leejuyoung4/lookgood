<template>
  <div class="login-modal">
    <button class="close-button" @click="$emit('close')">×</button>
    
    <div class="login-header">
      <div class="login-logo">로그인</div>
      <div class="login-subtitle">Look Good과 함께 운동해요! 🏃‍♂️</div>
    </div>

    <div class="login-menu">
      <div class="form-group">
        <label class="login-text">아이디 <span class="required">*</span></label>
        <input 
          type="text" 
          class="login-border" 
          v-model="userId"
          placeholder="아이디를 입력해주세요" 
        />
      </div>

      <div class="form-group">
        <label class="login-text">비밀번호 <span class="required">*</span></label>
        <input 
          type="password" 
          class="login-border" 
          v-model="password"
          placeholder="비밀번호를 입력해주세요" 
        />
      </div>

      <div class="login-options">
        <label class="login-remember-container">
          <input type="checkbox" class="login-remember-check" v-model="rememberMe" />
          <span class="login-remember">아이디 저장</span>
        </label>
      </div>
    </div>

    <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

    <button class="login-button" @click="handleLogin">
      로그인하기
      <span class="button-emoji">👊</span>
    </button>

    <div class="login-links">
      <a href="#" class="login-link">아이디 찾기</a>
      <div class="divider"></div>
      <a href="#" class="login-link">비밀번호 찾기</a>
      <div class="divider"></div>
      <a href="#" class="login-link" @click.prevent="switchToSignup">회원가입</a>
    </div>

    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const userId = ref('');
const password = ref('');
const errorMessage = ref('');
const isLoading = ref(false);

const emit = defineEmits(['close', 'login-success', 'open-signup']);

const handleLogin = async () => {
  try {
    if (!userId.value || !password.value) {
      errorMessage.value = '아이디와 비밀번호를 모두 입력해주세요.';
      return;
    }

    const response = await axios({
      method: 'POST',
      url: '/api/user/login',
      data: {
        userId: userId.value.trim(),
        password: password.value.trim()
      }
    });

    console.log('로그인 응답:', response.data);

    if (response.data && response.data.success) {
      // 토큰 저장
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
      }
      
      // 사용자 정보 저장 - response.data.user 구조 확인
      const userData = response.data.user;
      console.log('받은 사용자 데이터:', userData); // 디버깅용
      
      const userInfo = {
        id: userData.user_no || userData.userNo, // DB 컬럼명에 맞춰서
        userNo: userData.user_no || userData.userNo,
        userId: userData.user_id || userData.userId,
        userName: userData.user_name || userData.userName,
        email: userData.email,
        phoneNum: userData.phone_num || userData.phoneNum,
        joinDate: userData.join_date || userData.joinDate
      };
      
      console.log('저장할 사용자 정보:', userInfo); // 디버깅용
      
      // localStorage와 sessionStorage 모두에 저장
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
      sessionStorage.setItem('userInfo', JSON.stringify(userInfo));
      
      // 로그인 성공 알림
      alert('환영합니다! 로그인에 성공했습니다.');
      
      // 로그인 성공 이벤트 발생
      emit('login-success', userInfo);
      emit('close');
      
      // 페이지 새로고침
      window.location.reload();
    } else {
      errorMessage.value = response.data.message || '로그인에 실패했습니다.';
      alert('로그인에 실패했습니다. 다시 시도해주세요.');
    }
  } catch (error) {
    console.error('로그인 에러:', error);
    console.log('에러 응답:', error.response); // 디버깅용
    
    if (error.response?.status === 401) {
      errorMessage.value = '아이디 또는 비밀번호가 일치하지 않습니다.';
      alert('아이디 또는 비밀번호가 일치하지 않습니다.');
    } else {
      errorMessage.value = '서버와의 통신 중 오류가 발생했습니다.';
      alert('서버와의 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
  }
};

const showSuccessMessage = () => {
  const successElement = document.createElement('div');
  successElement.className = 'login-success-message';
  successElement.innerHTML = `
    <div class="success-content">
      <i class="bi bi-check-circle-fill"></i>
      <span>로그인 성공!</span>
    </div>
  `;
  document.body.appendChild(successElement);

  setTimeout(() => {
    successElement.remove();
  }, 1500);
};

const switchToSignup = () => {
  emit('open-signup');
  emit('close');
};
</script>

<style scoped>
.login-modal {
  background: #ffffff;
  border-radius: 30px;
  width: 460px;
  min-height: 600px;
  padding: 50px 40px;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2000;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-logo {
  color: #ebd03b;
  font-size: 48px;
  font-weight: 600;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.login-logo:hover {
  transform: scale(1.05);
  text-shadow: 0 0 15px rgba(235, 208, 59, 0.3);
}

.login-subtitle {
  font-size: 18px;
  color: #7c6e1b;
  transition: all 0.3s ease;
}

.login-subtitle:hover {
  transform: translateY(-2px);
  color: #ebd03b;
}

.login-menu {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  align-items: flex-start;
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
}

.login-border {
  background: #ffffff;
  border: 2px solid #eee;
  border-radius: 14px;
  height: 48px;
  width: 100%;
  padding: 0 15px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.login-border:focus {
  border-color: #FFE066;
  box-shadow: 0 0 0 3px rgba(255, 224, 102, 0.2);
  outline: none;
}

.login-button {
  background: #FFE082;
  color: #B7791F;
  border-radius: 16px;
  width: 100%;
  height: 54px;
  font-size: 24px;
  border: none;
  transition: all 0.3s ease;
  cursor: pointer;
  margin-top: 30px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 4px 8px rgba(255, 193, 7, 0.2);
}

.login-button:hover {
  background: #FFD54F;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 193, 7, 0.3);
}

.login-button:active {
  transform: translateY(-1px);
}

.login-remember-container {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 15px;
  cursor: pointer;
}

.login-remember-check {
  width: 18px;
  height: 18px;
  accent-color: #ebd03b;
}

.login-links {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
  font-size: 15px;
}

.login-link {
  color: #B7791F;
  text-decoration: none;
  transition: all 0.3s ease;
  padding: 5px 10px;
  border-radius: 8px;
}

.login-link:hover {
  color: #ebd03b;
  background: rgba(235, 208, 59, 0.1);
  transform: translateY(-2px);
}

.divider {
  width: 1px;
  height: 12px;
  background: #ddd;
}

.close-button {
  position: absolute;
  right: 25px;
  top: 25px;
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 12px;
  background: #FFF3BF;
  color: #B7791F;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 3px 6px rgba(183, 121, 31, 0.1);
}

.close-button:hover {
  background: #FFE066;
  transform: rotate(90deg) scale(1.1);
  box-shadow: 0 5px 12px rgba(183, 121, 31, 0.2);
}

:root[class='dark-mode'] .login-modal {
  background: var(--bg-color);
  color: var(--text-color);
}

:root[class='dark-mode'] .login-logo {
  color: #ebd03b;
}

:root[class='dark-mode'] .login-subtitle {
  color: #999;
}

:root[class='dark-mode'] .login-border {
  background: var(--bg-color);
  border-color: #444;
  color: var(--text-color);
}

:root[class='dark-mode'] .login-border::placeholder {
  color: #666;
}

:root[class='dark-mode'] .login-button {
  background: #2a2a2a;
  color: #ebd03b;
}

:root[class='dark-mode'] .login-button:hover {
  background: #333;
}

:root[class='dark-mode'] .login-link {
  color: #ebd03b;
}

:root[class='dark-mode'] .divider {
  background: #444;
}

:root[class='dark-mode'] .close-button {
  background: #2a2a2a;
  color: var(--text-color);
}

:root[class='dark-mode'] .close-button:hover {
  background: #333;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
  text-align: center;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #ebd03b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.login-success-message {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #4CAF50;
  color: white;
  padding: 15px 25px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  animation: slideIn 0.5s ease-out;
  z-index: 2000;
}

.success-content {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
}

.success-content i {
  font-size: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.error-message {
  color: #ff4444;
  margin-top: 10px;
  text-align: center;
  font-size: 14px;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}

</style>
