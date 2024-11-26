<template>
  <div class="signup-modal">
    <button class="close-button" @click="$emit('close')">×</button>
    
    <div class="signup-header">
      <div class="signup-logo">회원가입</div>
      <div class="signup-ment">Look Good과 함께 운동해요! 💪</div>
    </div>

    <div class="signupMenu">
      <!-- 아이디 입력 및 중복 확인 -->
      <div class="form-group">
        <label for="userId">아이디 <span class="required">*</span></label>
        <div class="input-group">
          <input 
            type="text" 
            id="userId" 
            v-model="userId" 
            class="input-field" 
            placeholder="아이디를 입력하세요"
            @input="resetCheckMessage" 
          />
          <button class="check-button" @click="checkUserId">
            중복 확인 <span class="button-emoji">🔍</span>
          </button>
        </div>
        <p v-if="idCheckMessage" :class="['check-message', idCheckStatus]">
          {{ idCheckMessage }}
        </p>
      </div>

      <!-- 비밀번호 입력 -->
      <div class="form-group">
        <label for="password">비밀번호 <span class="required">*</span></label>
        <div class="input-group">
          <input 
            :type="showPassword ? 'text' : 'password'"
            id="password" 
            v-model="password"
            class="input-field"
            placeholder="비밀번호를 입력하세요" 
            @input="validatePassword"
          />
          <img 
            :src="showPassword ? eyeOpen : eyeClosed"
            class="eye-icon"
            @click="togglePasswordVisibility('password')"
            alt="비밀번호 보기" 
          />
        </div>
        <div class="password-validation-messages">
          <p :class="['validation-item', lengthValid ? 'valid' : 'invalid']">
            8자 이상
          </p>
          <span class="validation-divider">•</span>
          <p :class="['validation-item', letterValid ? 'valid' : 'invalid']">
            영문
          </p>
          <span class="validation-divider">•</span>
          <p :class="['validation-item', numberValid ? 'valid' : 'invalid']">
            숫자
          </p>
          <span class="validation-divider">•</span>
          <p :class="['validation-item', specialValid ? 'valid' : 'invalid']">
            특수문자
          </p>
        </div>
      </div>

      <!-- 비밀번호 확인 -->
      <div class="form-group">
        <label for="confirmPassword">비밀번호 확인 <span class="required">*</span></label>
        <div class="input-group">
          <input 
            :type="showConfirmPassword ? 'text' : 'password'"
            id="confirmPassword" 
            v-model="confirmPassword"
            class="input-field"
            placeholder="비밀번호를 다시 입력하세요" 
          />
          <img 
            :src="showConfirmPassword ? eyeOpen : eyeClosed"
            class="eye-icon"
            @click="togglePasswordVisibility('confirmPassword')"
            alt="비밀번호 보기" 
          />
        </div>
        <p v-if="passwordMatch !== null" :class="['check-message', passwordMatch ? 'success' : 'error']">
          {{ passwordMatch ? '✅ 비밀번호가 일치합니다.' : '❌ 비밀번호가 일치하지 않습니다.' }}
        </p>
      </div>

      <!-- 이름 입력 -->
      <div class="form-group">
        <label for="userName">이름 <span class="required">*</span></label>
        <div class="input-group">
          <input 
            type="text" 
            id="userName" 
            v-model="userName"
            class="input-field"
            placeholder="이름을 입력세요" 
          />
        </div>
      </div>

      <!-- 전화번호 입력 -->
      <div class="form-group">
        <label for="phone">전화번호 <span class="required">*</span></label>
        <div class="phone-input-group">
          <select v-model="phoneFirst" class="phone-select">
            <!-- 휴대폰 번호 -->
            <optgroup label="휴대폰">
              <option value="010">010</option>
              <option value="011">011</option>
              <option value="016">016</option>
              <option value="017">017</option>
              <option value="019">019</option>
            </optgroup>
            <!-- 지역 번호 -->
            <optgroup label="지역번호">
              <option value="02">02 (서울)</option>
              <option value="031">031 (경기)</option>
              <option value="032">032 (인천)</option>
              <option value="033">033 (강원)</option>
              <option value="041">041 (충남)</option>
              <option value="042">042 (대전)</option>
              <option value="043">043 (충북)</option>
              <option value="044">044 (세종)</option>
              <option value="051">051 (부산)</option>
              <option value="052">052 (울산)</option>
              <option value="053">053 (대구)</option>
              <option value="054">054 (경북)</option>
              <option value="055">055 (경남)</option>
              <option value="061">061 (전남)</option>
              <option value="062">062 (광주)</option>
              <option value="063">063 (전북)</option>
              <option value="064">064 (제주)</option>
            </optgroup>
          </select>
          <span class="phone-divider">-</span>
          <input 
            type="text" 
            v-model="phoneSecond" 
            class="phone-input" 
            maxlength="4"
            placeholder="0000" 
          />
          <span class="phone-divider">-</span>
          <input 
            type="text" 
            v-model="phoneThird" 
            class="phone-input" 
            maxlength="4"
            placeholder="0000" 
          />
        </div>
      </div>

      <!-- 이메일 입력 -->
      <div class="form-group">
        <label for="email">이메일 <span class="required">*</span></label>
        <div class="email-input-group">
          <input 
            type="text" 
            v-model="emailFirst" 
            class="email-input"
            placeholder="이메일" 
          />
          <span class="email-divider">@</span>
          <select v-model="emailSecond" class="email-select" @change="handleEmailDomainChange">
            <option value="">선택하세요</option>
            <option value="gmail.com">gmail.com</option>
            <option value="naver.com">naver.com</option>
            <option value="daum.net">daum.net</option>
            <option value="kakao.com">kakao.com</option>
            <option value="nate.com">nate.com</option>
            <option value="직접입력">직접입력</option>
          </select>
        </div>
      </div>
    </div>

    <div class="button-container">
      <button class="signup-button" @click="signup">
        가입하기 <span class="button-emoji">🎉</span>
      </button>
      <button class="cancel-button" @click="$emit('close')">
        취소 <span class="button-emoji">✖️</span>
      </button>
    </div>

    <div class="agreement-section">
      <label class="marketing-agree">
        <input type="checkbox" v-model="marketingAgree" class="agree-checkbox" />
        <span>가입 시 이용약관과 개인정보처방침에 동의하게 됩니다</span>
      </label>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import eyeOpen from '@/assets/img/user/open_eye.png';
import eyeClosed from '@/assets/img/user/closed_eye.png';

const router = useRouter();
const emit = defineEmits(['close']);

// State variables
const userId = ref('');
const password = ref('');
const confirmPassword = ref('');
const userName = ref('');
const phoneFirst = ref('');
const phoneSecond = ref('');
const phoneThird = ref('');
const emailFirst = ref('');
const emailSecond = ref('');
const marketingAgree = ref(false);

// 아이디 중복 체크 관련 상태
const idCheckMessage = ref('');
const idCheckStatus = ref('');
const hasCheckedDuplicate = ref(false);

// UI state
const showPassword = ref(false);
const showConfirmPassword = ref(false);

// 아이디 중복 확인 함수
const checkUserId = async () => {
  if (!userId.value?.trim()) {
    idCheckMessage.value = '❌ 아이디를 입력해주세요.';
    idCheckStatus.value = 'error';
    return;
  }

  try {
    const response = await axios.get(`/api/user/check-id`, {
      params: { userId: userId.value }
    });
    
    if (response.data.success) {
      if (response.data.isDuplicate) {
        idCheckMessage.value = '❌ 이미 사용 중인 아이디입니다.';
        idCheckStatus.value = 'error';
        hasCheckedDuplicate.value = false;
      } else {
        idCheckMessage.value = '✅ 사용 가능한 아이디입니다.';
        idCheckStatus.value = 'success';
        hasCheckedDuplicate.value = true;
      }
    }
  } catch (error) {
    console.error('중복 확인 중 오류가 발생했습니다.', error);
    idCheckMessage.value = '❌ 중복 확인 중 오류가 발생했습니다.';
    idCheckStatus.value = 'error';
  }
};

// 아이디 입력값 변경 시 메시지 초기화
const resetCheckMessage = () => {
  idCheckMessage.value = '';
  idCheckStatus.value = '';
  hasCheckedDuplicate.value = false;
};

// 비밀번호 유효성 검사 상태
const lengthValid = ref(false);
const letterValid = ref(false);
const numberValid = ref(false);
const specialValid = ref(false);

// 비밀번호 유효성 검사 함수
const validatePassword = () => {
  const pw = password.value;
  
  // 각 조건 검사
  lengthValid.value = pw.length >= 8;
  letterValid.value = /[A-Za-z]/.test(pw);
  numberValid.value = /[0-9]/.test(pw);
  specialValid.value = /[!@#$%^&*(),.?":{}|<>]/.test(pw);
};

// 비밀번호 입력값 변경 감지
watch(
  () => password.value,
  (newValue) => {
    validatePassword();
  },
  { immediate: true }
);

// 전체 비밀번호 유효성 여부를 반환하는 computed 속성
const isPasswordValid = computed(() => {
  return lengthValid.value && 
         letterValid.value && 
         numberValid.value && 
         specialValid.value;
});

const passwordMatch = computed(() => {
  if (!confirmPassword.value) return null;
  return password.value === confirmPassword.value;
});

// 전화번호와 이메일 조합을 위한 computed 속성 추가
const phone = computed(() => {
  return `${phoneFirst.value}-${phoneSecond.value}-${phoneThird.value}`;
});

const email = computed(() => {
  return `${emailFirst.value}@${emailSecond.value}`;
});

// Methods
const clearPlaceholder = (field) => {
  placeholders.value[field] = '';
};

const restorePlaceholder = (field, defaultValue) => {
  if (!eval(field).value) {
    placeholders.value[field] = defaultValue;
  }
};

const togglePasswordVisibility = (field) => {
  if (field === 'password') {
    showPassword.value = !showPassword.value;
  } else if (field === 'confirmPassword') {
    showConfirmPassword.value = !showConfirmPassword.value;
  }
};

// 회원가입 함수
const signup = async () => {
  try {
    // 필수 입력값 검증
    if (!userId.value?.trim()) {
      alert('아이디를 입력해주세요.');
      return;
    }

    // 아이디 중복 확인 검증
    if (!hasCheckedDuplicate.value) {
      alert('아이디 중복 확인이 필요합니다.');
      return;
    }

    // 나머지 필수 입력값 검증
    if (!password.value?.trim()) {
      alert('비밀번호를 입력해주세요.');
      return;
    }
    if (!userName.value?.trim()) {
      alert('이름을 입력해주세요.');
      return;
    }
    if (!phoneFirst.value || !phoneSecond.value || !phoneThird.value) {
      alert('전화번호를 입력해주세요.');
      return;
    }
    if (!emailFirst.value?.trim() || !emailSecond.value?.trim()) {
      alert('이메일을 입력해주세요.');
      return;
    }

    // 이용약관 동의 체크 검증 추가
    if (!marketingAgree.value) {
      alert('이용약관에 동의해주세요.');
      return;
    }

    const requestData = {
      userId: userId.value.trim(),
      password: password.value.trim(),
      userName: userName.value.trim(),
      phoneNum: `${phoneFirst.value}-${phoneSecond.value}-${phoneThird.value}`,
      email: `${emailFirst.value.trim()}@${emailSecond.value.trim()}`
    };

    const response = await axios.post('/api/user/signup', requestData);

    if (response.data.success) {
      alert('회원가입이 완료되었습니다!');
      emit('close');
      router.push('/');
    }
  } catch (error) {
    console.error('회원가입 오류:', error);
    // 전화번호 중복 에러 메시지 처리 추가
    if (error.response?.status === 400) {
      if (error.response.data.message.includes('전화번호')) {
        alert('이미 등록된 전화번호입니다.');
      } else {
        alert(error.response.data.message || '회원가입 처리 중 오류가 발생했습니다.');
      }
    } else {
      alert('회원가입 처리 중 오류가 발생했습니다.');
    }
  }
};

// 아이디 입력값 변경 감지
watch(userId, () => {
  resetCheckMessage();
});

// 서버 필수값 검증을 위한 computed 속성
const isValidForm = computed(() => {
  return (
    userId.value?.trim() &&
    password.value?.trim() &&
    userName.value?.trim() &&
    emailFirst.value?.trim() &&
    emailSecond.value?.trim()
  );
});

// 전화번호 입력 제한
const validatePhoneNumber = (value) => {
  if (!value) return '';
  return value.replace(/[^0-9]/g, '').slice(0, 4);
};

watch(phoneSecond, (newValue) => {
  phoneSecond.value = validatePhoneNumber(newValue);
});

watch(phoneThird, (newValue) => {
  phoneThird.value = validatePhoneNumber(newValue);
});

// 이메일 도메인 선택 처리
const handleEmailDomainChange = (event) => {
  const value = event.target.value;
  if (value === '직접입력') {
    emailSecond.value = '';
  } else {
    emailSecond.value = value;
  }
};
</script>

<style scoped>
.signup-modal {
  background: #ffffff;
  border-radius: 30px;
  width: 560px;
  min-height: 800px;
  padding: 50px 45px;
  position: relative;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  z-index: 2000;
}

.signup-header {
  text-align: center;
  margin-bottom: 30px;
}

.signup-logo {
  font-size: 38px;
  font-weight: 700;
  color: #333;
  margin-bottom: 10px;
  transition: all 0.3s ease;
  cursor: default;
}
.signup-logo:hover {
  color: #B7791F;
  transform: scale(1.05);
  text-shadow: 2px 2px 4px rgba(183, 121, 31, 0.2);
}

.signup-ment {
  font-size: 18px;
  color: #666;
  font-weight: 500;
  transition: all 0.3s ease;
  cursor: default;
}

.signup-ment:hover {
  color: #B7791F;
  transform: translateY(-2px);
}

.signup-ment:hover .emoji {
  display: inline-block;
  animation: shake 0.5s ease infinite;
}

/* 이모지 애니메이션 */
@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
}

/* Look Good 텍스트에 특별한 효과 추가 */
.signup-ment span {
  display: inline-block;
  transition: all 0.3s ease;
}

.form-group {
  position: relative;
  margin-bottom: 20px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
  gap: 12px;
  overflow: visible; /* 버튼 그림자가 보이도록 */
}

.input-field {
  flex: 1;
  height: 48px;
  padding: 0 45px 0 15px;
  border: 2px solid #eee;
  border-radius: 14px !important;
  font-size: 15px;
  transition: all 0.3s ease;
}

.input-field:focus {
  border-color: #FFE066;
  box-shadow: 0 0 0 3px rgba(255, 224, 102, 0.2);
  outline: none;
}

.eye-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 24px;
  height: 24px;
  cursor: pointer;
  padding: 3px;
  z-index: 1;
}

.eye-icon:hover {
  opacity: 0.7;
}

.check-message {
  font-size: 13px;
  margin-top: 6px;
  padding-left: 4px;
}

.success {
  color: #2ecc71;
}

.error {
  color: #e74c3c;
}

.info-message {
  font-size: 13px;
  color: #666;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.button-container {
  display: flex;
  gap: 14px;
  margin-top: 30px;
  padding: 0 10px;
}

.signup-button, .cancel-button {
  flex: 1;
  height: 54px;
  border: none;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.signup-button {
  background: #FFE082;
  color: #B7791F;
  box-shadow: 0 4px 8px rgba(255, 193, 7, 0.2);
}

.signup-button:hover {
  background: #FFD54F;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(255, 193, 7, 0.3);
}

.signup-button:active {
  transform: translateY(-1px);
}

.cancel-button {
  background: #F1F3F5;
  color: #868E96;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  border-radius: 16px;
}

.cancel-button:hover {
  background: #E9ECEF;
  transform: translateY(-3px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
}

.cancel-button:active {
  transform: translateY(-1px);
}

.required {
  color: #ff6b6b;
  margin-left: 4px;
}

.agreement-section {
  margin-top: 25px;
  text-align: center;
}

.marketing-agree {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
  cursor: pointer;
}

.agree-checkbox {
  width: 18px;
  height: 18px;
  accent-color: #ebd03b;
}

.terms-notice {
  font-size: 13px;
  color: #888;
}

/* 전화번호 입력 스타일 수정 */
.phone-input-group {
  display: flex;
  align-items: center;
  width: 100%;
  gap: 8px;
}

.phone-select {
  width: 135px;
  height: 48px;
  border: 2px solid #eee;
  border-radius: 14px;
  padding: 0 8px;
  font-size: 15px;
  background-color: white;
  flex-shrink: 0;
}

.phone-input {
  width: 135px;
  height: 48px;
  border: 2px solid #eee;
  border-radius: 14px;
  padding: 0 8px;
  font-size: 15px;
  text-align: center;
  flex-shrink: 0;
}

.phone-divider {
  font-size: 18px;
  color: #666;
  padding: 0;
  margin: 0 2px;
  user-select: none;
  flex-shrink: 0;
  text-align: center;
}

/* 이메일 입력 그룹과 동일한 너비 유지를 위한 스타일 */
.phone-input-group,
.email-input-group {
  max-width: 100%;
  box-sizing: border-box;
}

/* 이메일 입력 스타일 */
.email-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.email-input {
  flex: 1; /* flex: 1로 변경 */
  height: 48px;
  border: 2px solid #eee;
  border-radius: 14px;
  padding: 0 15px;
  font-size: 15px;
}

.email-select {
  flex: 1; /* flex: 1로 변경하여 입력창과 동일한 크기 */
  height: 48px;
  border: 2px solid #eee;
  border-radius: 14px;
  padding: 0 10px;
  font-size: 15px;
  background-color: white;
  cursor: pointer;
}

.email-divider {
  font-size: 18px;
  color: #666;
  padding: 0 8px;
  user-select: none;
}

/* optgroup 스타일 */
optgroup {
  font-weight: bold;
  color: #666;
}

option {
  font-weight: normal;
  color: #333;
}

/* 포커스 효과 */
.phone-select:focus,
.phone-input:focus,
.email-input:focus,
.email-select:focus {
  border-color: #ebd03b;
  box-shadow: 0 0 0 3px rgba(235, 208, 59, 0.2);
  outline: none;
}

/* 호버 효과 */
.phone-select:hover,
.phone-input:hover,
.email-input:hover,
.email-select:hover {
  border-color: #d1b031;
}

/* 추가 스타일은 필요에 따라 계속 작성 */

.password-validation-messages {
  position: static;
  margin-top: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.validation-item {
  transition: color 0.3s ease;
  white-space: nowrap;
}

.validation-divider {
  color: #ddd;
  font-size: 10px;
}

.valid {
  color: #2ecc71;
  font-weight: 500;
}

.invalid {
  color: #e74c3c;
  font-weight: 500;
}

/* 중복 확인 버튼 스타일 수정 */
.check-button {
  background: #FFF3BF;
  color: #B7791F;
  border: none;
  border-radius: 14px !important; /* 강제 적용 */
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 3px 6px rgba(183, 121, 31, 0.1);
  height: 42px;
  white-space: nowrap;
  flex-shrink: 0;
  z-index: 1; /* 버튼이 입력창 위에 오도록 */
}

.check-button:hover {
  background: #FFE066;
  transform: translateY(-2px);
  box-shadow: 0 5px 12px rgba(183, 121, 31, 0.2);
}

.check-button:active {
  transform: translateY(0);
}

/* 버튼 이모지 애니메이션 */
.button-emoji {
  transition: all 0.3s ease;
  display: inline-block;
}

.signup-button:hover .button-emoji {
  transform: rotate(10deg) scale(1.2);
}

.cancel-button:hover .button-emoji {
  transform: rotate(-10deg) scale(1.2);
}

.check-button:hover .button-emoji {
  transform: rotate(15deg) scale(1.2);
}

/* 입력 필드 라운드 통일 */
.input-field,
.phone-select,
.phone-input,
.email-input,
.email-select {
  border-radius: 14px;
}

/* 모달 닫기 버튼 스타일 수정 */
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

.close-button:active {
  transform: rotate(90deg) scale(0.95);
}

/* 크모드 스타일 */
:root[class='dark-mode'] .signup-modal {
  background: var(--bg-color);
  color: var(--text-color);
}

:root[class='dark-mode'] .signup-logo {
  color: #ebd03b; /* 로고는 항상 노란색 유지 */
}

:root[class='dark-mode'] .signup-ment {
  color: #999;
}

:root[class='dark-mode'] .signup-text,
:root[class='dark-mode'] label {
  color: var(--text-color);
}

:root[class='dark-mode'] .input-field,
:root[class='dark-mode'] .phone-select,
:root[class='dark-mode'] .phone-input,
:root[class='dark-mode'] .email-input,
:root[class='dark-mode'] .email-select {
  background-color: var(--bg-color);
  border-color: #444;
  color: var(--text-color);
}

:root[class='dark-mode'] .input-field::placeholder,
:root[class='dark-mode'] .phone-input::placeholder,
:root[class='dark-mode'] .email-input::placeholder {
  color: #666;
}

:root[class='dark-mode'] .phone-divider,
:root[class='dark-mode'] .email-divider,
:root[class='dark-mode'] .validation-divider {
  color: #666;
}

:root[class='dark-mode'] .terms-notice {
  color: #888;
}

:root[class='dark-mode'] option,
:root[class='dark-mode'] optgroup {
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* 다크모드에서 input 자동완성 배경색 제어 */
:root[class='dark-mode'] input:-webkit-autofill,
:root[class='dark-mode'] input:-webkit-autofill:hover,
:root[class='dark-mode'] input:-webkit-autofill:focus {
  -webkit-box-shadow: 0 0 0px 1000px var(--bg-color) inset !important;
  -webkit-text-fill-color: var(--text-color) !important;
  caret-color: var(--text-color) !important;
}

/* 다크모드에서 버튼 스타일 */
:root[class='dark-mode'] .check-button,
:root[class='dark-mode'] .signup-button,
:root[class='dark-mode'] .cancel-button {
  background: #2a2a2a;
  color: #ebd03b;
  border-color: #444;
}

:root[class='dark-mode'] .check-button:hover,
:root[class='dark-mode'] .signup-button:hover,
:root[class='dark-mode'] .cancel-button:hover {
  background: #333;
}

:root[class='dark-mode'] .close-button {
  background: #2a2a2a;
  color: var(--text-color);
}

:root[class='dark-mode'] .close-button:hover {
  background: #333;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 5;  /* 메모 입력 영역보다 위에 위치 */
}

.close-btn i {
  color: white;
  font-size: 20px;
}

.close-btn:hover {
  background: rgba(255, 0, 0, 0.7);
  transform: scale(1.1);
}

/* modal-memo의 높이를 나가기 버튼 영역 제외하도록 수정 */
.modal-memo {
  position: absolute;
  top: 60px;  /* 나가기 버튼 영역 제외 */
  left: 0;
  width: 100%;
  height: calc(100% - 140px);  /* 상단 60px + 하단 컨트롤바 80px 제외 */
  z-index: 3;
  pointer-events: auto;
}
</style>
