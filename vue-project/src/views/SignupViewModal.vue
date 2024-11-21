<template>
  
<div v-if="isLoginModalOpen" class="overlay">
  <div class="signup-container">
    <div class="signup-content">
      <div class="signup-logo">회원가입</div>
      <div class="signup-ment">다양한 운동을 경험해보세요!</div>

      <div class="signupMenu">
        <!-- 아이디 입력 및 중복 확인 -->
        <div class="form-group">
          <label for="userId">아이디</label>
          <div class="input-group">
            <input
              type="text"
              id="userId"
              v-model="userId"
              class="rectangle-border"
              :placeholder="placeholders.userId"
              @focus="clearPlaceholder('userId')"
              @blur="restorePlaceholder('userId', '아이디를 입력하세요')"
            />
            <button class="check-button" @click="checkUserId">중복 확인</button>
          </div>
          <p v-if="isDuplicate !== null" class="check-message">
            <span v-if="isDuplicate">이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.</span>
            <span v-else style="color: blue;">사용 가능한 아이디입니다.</span>
          </p>
        </div>

        <!-- 비밀번호와 비밀번호 확인 입력 필드 -->
        <div v-for="field in fields" :key="field.id" class="form-group">
          <label :for="field.id">{{ field.label }}</label>
          <div class="input-group password-input-group">
            <input
              :type="field.id === 'password' ? (showPassword ? 'text' : 'password') : field.id === 'confirmPassword' ? (showConfirmPassword ? 'text' : 'password') : field.type"
              :id="field.id"
              v-model="field.model"
              class="rectangle-border password-input"
              :placeholder="placeholders[field.id]"
              :maxlength="field.id === 'password' || field.id === 'confirmPassword' ? field.maxLength : null"
              @focus="clearPlaceholder(field.id)"
              @blur="restorePlaceholder(field.id, field.defaultPlaceholder)"
            />
            <!-- 비밀번호와 비밀번호 확인 필드에만 눈 모양 이미지 추가 -->
            <img
              v-if="field.id === 'password'"
              :src="showPassword ? eyeOpen : eyeClosed"
              class="show-password-icon"
              @click="togglePasswordVisibility('password')"
              alt="Toggle Password Visibility"
            />
            <img
              v-if="field.id === 'confirmPassword'"
              :src="showConfirmPassword ? eyeOpen : eyeClosed"
              class="show-password-icon"
              @click="togglePasswordVisibility('confirmPassword')"
              alt="Toggle Password Visibility"
            />
          </div>
          <!-- 비밀번호 확인 입력 필드 아래에 비밀번호 일치 여부 메시지 표시 -->
          <p v-if="field.id === 'confirmPassword' && passwordMatch !== null" class="password-match-message">
            <span v-if="!passwordMatch" class="error-message">비밀번호가 일치하지 않습니다.</span>
            <span v-else class="success-message">비밀번호가 일치합니다.</span>
          </p>
        </div>
      </div>

      <!-- 회원가입 및 취소 버튼 -->
      <div class="button-container">
        <button class="signup-button1" @click="signup">가입하기</button>
        <button class="signup-button2">취소</button>
      </div>

      <!-- 추가 안내 및 약관 동의 -->
      <div class="signup-event-container">
        <label class="signup-event">
          <input type="checkbox" class="signup-agreement-check" />
          할인 혜택 및 다양한 소식을 받아볼래요.
        </label>
      </div>
      <div class="signup-agreement">
        회원 가입 시, 서비스 이용약관, 개인정보처리 방침에 동의하는 것으로 간주합니다.
      </div>
    </div>
  </div>
</div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import eyeOpen from '@/assets/img/user/open_eye.png';
import eyeClosed from '@/assets/img/user/closed_eye.png';


const userId = ref('');
const password = ref('');
const confirmPassword = ref('');
const name = ref('');
const phone = ref('');
const email = ref('');
const isDuplicate = ref(null);
const hasCheckedDuplicate = ref(false); // 중복 확인 여부를 저장하는 플래그
const passwordMatch = ref(null); // 비밀번호 일치 여부 저장
const showPassword = ref(false); // 비밀번호를 보여줄지 여부
const showConfirmPassword = ref(false); // 비밀번호 확인을 보여줄지 여부

// 필드 설정과 기본 placeholder 값 정의
const fields = reactive([
  { id: 'password', label: '비밀번호', type: 'password', model: password, defaultPlaceholder: '비밀번호를 입력하세요', maxLength: 60 },
  { id: 'confirmPassword', label: '비밀번호 확인', type: 'password', model: confirmPassword, defaultPlaceholder: '비밀번호 확인', maxLength: 60 },
  { id: 'name', label: '이름', type: 'text', model: name, defaultPlaceholder: '이름을 입력하세요' },
  { id: 'phone', label: '전화번호', type: 'text', model: phone, defaultPlaceholder: '전화번호를 입력하세요' },
  { id: 'email', label: '이메일', type: 'email', model: email, defaultPlaceholder: '이메일을 입력하세요' },
]);

// placeholder를 관리하는 객체
const placeholders = reactive({
  userId: '아이디를 입력하세요',
  password: '비밀번호를 입력하세요',
  confirmPassword: '비밀번호 확인',
  name: '이름을 입력하세요',
  phone: '전화번호를 입력하세요',
  email: '이메일을 입력하세요',
});

// placeholder를 비우는 함수
const clearPlaceholder = (fieldId) => {
  placeholders[fieldId] = '';
};

// placeholder를 복구하는 함수
const restorePlaceholder = (fieldId, defaultPlaceholder) => {
  const field = fields.find((field) => field.id === fieldId);
  if (!field || !field.model) {
    placeholders[fieldId] = defaultPlaceholder;
    return;
  }
  if (field.model.value === '') {
    placeholders[fieldId] = defaultPlaceholder;
  }
};

// 비밀번호와 비밀번호 확인이 일치하는지 확인하는 watch 메서드
watch([password, confirmPassword], () => {
  if (password.value && confirmPassword.value) {
    passwordMatch.value = password.value === confirmPassword.value;
  } else {
    passwordMatch.value = null; // 입력이 비어있을 경우 null로 설정
  }
});

// 비밀번호 보기/숨기기 토글 함수
const togglePasswordVisibility = (fieldId) => {
  if (fieldId === 'password') {
    showPassword.value = !showPassword.value;
  } else if (fieldId === 'confirmPassword') {
    showConfirmPassword.value = !showConfirmPassword.value;
  }
};

// 아이디 중복 확인 함수
const checkUserId = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/user/check-id`, {
      params: { userId: userId.value }
    });
    isDuplicate.value = response.data.isDuplicate;
    if (isDuplicate.value) {
      alert('이미 사용 중인 아이디입니다. 다른 아이디를 입력해주세요.');
      userId.value = '';
      hasCheckedDuplicate.value = false;
    } else {
      alert('사용 가능한 아이디입니다');
      hasCheckedDuplicate.value = true;
    }
  } catch (error) {
    console.error('중복 확인 중 오류가 발생했습니다.', error);
    alert('중복 확인 중 오류가 발생했습니다.');
  }
};

// 회원가입 함수
const signup = async () => {
  if (!hasCheckedDuplicate.value) {
    alert('아이디 중복 확인을 먼저 해주세요.');
    return;
  }
  if (passwordMatch.value === false) {
    alert('비밀번호가 일치하지 않습니다. 다시 확인해주세요.');
    return;
  }
  try {
    const response = await axios.post(`${API_BASE_URL}/api/user/signup`, {
      userId: userId.value,
      password: password.value,
      userName: name.value,
      phoneNum: phone.value,
      email: email.value
    });
    if (response.data.success) {
      alert(response.data.message || '회원가입이 성공적으로 완료되었습니다!');
      router.push('/');
    } else {
      alert(response.data.message || '회원가입에 실패했습니다.');
    }
  } catch (error) {
    console.error('회원가입 중 오류가 발생했습니다.', error);
    alert('회원가입 중 오류가 발생했습니다.');
  }
};
</script>

<style scoped>
.signup-container {
  display: flex;
  justify-content: center;
  padding-top: 100px;
  padding-bottom: 100px;
}

.signup-content {
  background: #f7f7f7;
  padding: 30px;
  border-radius: 30px;
  width: 100%;
  max-width: 600px;
  box-sizing: border-box;
  filter: none;
}

.signup-logo {
  font-size: 36px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 15px;
}

.signup-ment {
  font-size: 20px;
  font-weight: 500;
  text-align: center;
  margin-bottom: 30px;
}

.signupMenu {
  display: flex;
  flex-direction: column;
  gap: 20px;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 30px;
}

.form-group {
  padding-top: 0px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  position: relative;
}

label {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
}

.rectangle-border {
  background: #ffffff;
  border: 2px solid #303030;
  border-radius: 20px;
  height: 40px;
  padding: 0 15px;
  font-size: 18px;
  width: 100%;
  padding-right: 40px; /* 오른쪽 여백을 추가하여 아이콘이 입력 필드에 겹치지 않도록 */
}

.show-password-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  height: 20px;
  width: 20px;
  cursor: pointer;
}

.check-button {
  padding: 0 12px;
  font-size: 12px;
  background-color: #d9d9d9;
  border: none;
  border-radius: 10px;
  height: 40px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-message {
  font-size: 14px;
  color: #d9534f;
  text-align: left;
  margin-top: 5px;
  height: 20px; /* 높이를 고정하여 레이아웃이 변경되지 않도록 설정 */
}

.password-match-message {
  font-size: 14px;
  text-align: left;
  margin-top: 5px;
  height: 20px; /* 높이를 고정하여 레이아웃이 변경되지 않도록 설정 */
}

.error-message {
  color: #d9534f;
}

.success-message {
  color: #062af8;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

.signup-button1,
.signup-button2 {
  border-radius: 25px;
  width: 160px;
  height: 45px;
  font-size: 18px;
  border: none;
  cursor: pointer;
}

.signup-button1 {
  background: #ebd03b;
  color: #333;
}

.signup-button1:hover {
  background-color: #d1b031;
}

.signup-button2 {
  background: #d9d9d9;
  color: #333;
}

.signup-button2:hover {
  background-color: #b0b0b0;
}

.signup-agreement {
  font-size: 14px;
  color: #878787;
  text-align: center;
  margin-top: 20px;
}

.signup-event-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.signup-event {
  color: #383838;
  font-size: 14px;
  font-weight: 400;
}

.signup-agreement-check {
  transform: scale(1.3);
  cursor: pointer;
  margin-right: 8px;
}
</style>
