<template>
  <Header />
  <div class="signup-container">
    <div class="signup-content">
      <div class="signup-logo">회원가입</div>
      <div class="signup-ment">다양한 운동을 경험해보세요!</div>
      <div class="signupMenu">

        <!-- 아이디 입력 및 중복 확인 -->
        <div class="form-group">
          <label for="userId">아이디</label>
          <div class="input-group">
            <!-- 아이디 입력 -->
            <input type="text" id="userId" v-model="userId" class="rectangle-border" :placeholder="userIdPlaceholder"
              @focus="userIdPlaceholder = ''" @blur="userIdPlaceholder = '아이디를 입력하세요'" @input="resetCheckMessage" />
            <button class="check-button" @click="checkUserId">중복 확인</button>
          </div>
          <!-- 중복 확인 결과 메시지 -->
          <p v-if="checkedUserId !== null" class="check-message">
            <span v-if="isDuplicate">❌ 이미 사용 중인 아이디입니다.</span>
            <span v-else style="color: blue;">✔️ 사용 가능한 아이디입니다.</span>
          </p>
        </div>






        <!-- 비밀번호 입력 -->
<div class="form-group">
  <label for="password">비밀번호</label>
  <div class="input-group">
    <input
      :type="showPassword ? 'text' : 'password'"
      id="password"
      v-model="password"
      class="rectangle-border"
      :placeholder="passwordPlaceholder"
      @focus="passwordPlaceholder = ''"
      @blur="passwordPlaceholder = '비밀번호를 입력하세요'"
    />
    <img
      :src="showPassword ? eyeOpen : eyeClosed"
      class="show-password-icon"
      @click="togglePasswordVisibility"
      alt="Toggle Password Visibility"
    />
  </div>
  <p v-if="isPasswordValid !== null" class="error-message">
    <span v-if="!isPasswordValid">❌ 비밀번호는 8자 이상, 숫자, 영문자, 특수문자를 포함해야 합니다.</span>
    <span v-else style="color: blue;">✔️ 사용 가능한 비밀번호입니다.</span>
  </p>
</div>

<!-- 비밀번호 확인 -->
<div class="form-group">
  <label for="confirmPassword">비밀번호 확인</label>
  <div class="input-group">
    <input
      :type="showConfirmPassword ? 'text' : 'password'"
      id="confirmPassword"
      v-model="confirmPassword"
      class="rectangle-border"
      :placeholder="confirmPasswordPlaceholder"
      @focus="confirmPasswordPlaceholder = ''"
      @blur="confirmPasswordPlaceholder = '비밀번호를 다시 입력하세요'"
    />
    <img
      :src="showConfirmPassword ? eyeOpen : eyeClosed"
      class="show-password-icon"
      @click="toggleConfirmPasswordVisibility"
      alt="Toggle Password Visibility"
    />
  </div>
  <p v-if="passwordMatch !== null" class="password-match-message">
    <span v-if="!passwordMatch">❌ 비밀번호가 일치하지 않습니다.</span>
    <span v-else style="color: blue;">✔️ 비밀번호가 일치합니다.</span>
  </p>
</div>



        <div class="form-group">
          <label for="userName">이름</label>
          <div class="input-group">
            <input type="text" id="userName" v-model="userName" class="rectangle-border"
              :placeholder="userNamePlaceholder" @focus="userNamePlaceholder = ''"
              @blur="userNamePlaceholder = '이름을 입력하세요'" />
          </div>
        </div>

        <!-- 전화번호 입력 -->
        <div class="form-group">
          <label for="phone">전화번호</label>
          <div class="phone-input-group">
            <input list="phoneOptions" v-model="phoneFirst" class="phone-input" :placeholder="phoneFirstPlaceholder"
              @focus="phoneFirstPlaceholder = ''" @blur="phoneFirstPlaceholder = '010'" @change="clearPhoneFirst" />
            <datalist id="phoneOptions">
              <option value="010"></option>
              <option value="011"></option>
              <option value="016"></option>
              <option value="017"></option>
              <option value="019"></option>
              <option value="직접 입력"></option>
            </datalist>
            <span>-</span>
            <input type="text" v-model="phoneSecond" class="phone-input" :placeholder="phoneSecondPlaceholder"
              @focus="phoneSecondPlaceholder = ''" @blur="phoneSecondPlaceholder = 'XXXX'" maxlength="4" />
            <span>-</span>
            <input type="text" v-model="phoneThird" class="phone-input" :placeholder="phoneThirdPlaceholder"
              @focus="phoneThirdPlaceholder = ''" @blur="phoneThirdPlaceholder = 'XXXX'" maxlength="4" />
          </div>
        </div>


        <!-- 이메일 입력 -->
        <div class="form-group">
          <label for="email">이메일</label>
          <div class="email-input-group">
            <input type="text" v-model="emailFirst" class="email-input" :placeholder="emailFirstPlaceholder"
              @focus="emailFirstPlaceholder = ''" @blur="emailFirstPlaceholder = '예: marketkurly'" />
            <span>@</span>
            <input list="emailOptions" v-model="emailSecond" class="email-input" :placeholder="emailSecondPlaceholder"
              @focus="emailSecondPlaceholder = ''" @blur="emailSecondPlaceholder = 'gmail.com'"
              @change="clearEmailSecond" />
            <datalist id="emailOptions">
              <option value="gmail.com"></option>
              <option value="naver.com"></option>
              <option value="daum.net"></option>
              <option value="yahoo.com"></option>
              <option value="직접 입력"></option>
            </datalist>
          </div>
        </div>

      </div>

      <!-- 회원가입 및 취소 버튼 -->
      <div class="button-container">
        <button class="signup-button1" @click="signup">가입하기</button>
        <button class="signup-button2">취소</button>
      </div>
    </div>
  </div>
  <Footer />
</template>

<script setup>
import { ref, computed } from 'vue';
import router from '@/router';
import axios from 'axios';
import eyeOpen from '@/assets/img/user/open_eye.png';
import eyeClosed from '@/assets/img/user/closed_eye.png';
import Footer from '@/components/Footer.vue';
import Header from '@/components/Header.vue';

// Placeholder states
const userIdPlaceholder = ref('아이디를 입력하세요');
const passwordPlaceholder = ref('비밀번호를 입력하세요');
const confirmPasswordPlaceholder = ref('비밀번호를 다시 입력하세요');
const phoneFirstPlaceholder = ref('');
const phoneSecondPlaceholder = ref('XXXX');
const phoneThirdPlaceholder = ref('XXXX');
const userNamePlaceholder = ref('이름을 입력하세요');
const emailFirstPlaceholder = ref('예: marketkurly');
const emailSecondPlaceholder = ref('');

// State variables
const userId = ref('');
const password = ref('');
const confirmPassword = ref('');
const phoneFirst = ref('');
const phoneSecond = ref('');
const phoneThird = ref('');
const emailFirst = ref('');
const emailSecond = ref('');
const userName = ref('');
const isDuplicate = ref(null); // 초기값 null로 설정
const showPassword = ref(false);
const showConfirmPassword = ref(false);
const checkedUserId = ref(null); // 중복 확인 버튼 클릭 여부 상태

// Validations
// Computed properties for validations
const isPasswordValid = computed(() => {
  if (!password.value) return null; // 비밀번호가 없을 때는 null 반환
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
  return passwordRegex.test(password.value);
});

const passwordMatch = computed(() => {
  if (!confirmPassword.value) return null; // 비밀번호 확인 입력이 없을 때는 null 반환
  return password.value === confirmPassword.value;
});

// 아이디 입력값 변경 시 메시지 초기화
const resetCheckMessage = () => {
  checkedUserId.value = null; // 메시지 초기화
};

// 아이디 중복 확인 함수
const checkUserId = async () => {
  try {
    if (!userId.value.trim()) {
      alert('아이디를 입력해주세요.');
      return;
    }

    const response = await axios.get('/api/user/check-id', {
      params: { userId: userId.value },
    });

    isDuplicate.value = response.data.isDuplicate; // 중복 여부 업데이트
    checkedUserId.value = true; // 버튼 클릭 상태 업데이트

    if (isDuplicate.value) {
      alert('이미 사용 중인 아이디입니다.');
    } else {
      alert('사용 가능한 아이디입니다.');
    }
  } catch (error) {
    console.error('아이디 중복 확인 오류:', error);
    alert('아이디 확인 중 오류가 발생했습니다.');
  }
};

// 회원가입 함수
const signup = async () => {
  const phoneNum = `${phoneFirst.value}-${phoneSecond.value}-${phoneThird.value}`;
  const email = `${emailFirst.value}@${emailSecond.value}`;

  // 유효성 검사
  if (!userName.value || !userId.value || !password.value || !phoneNum || !email) {
    alert('모든 필드를 입력해주세요.');
    return;
  }

  try {
    console.log('회원가입 요청 데이터:', {
      userName: userName.value,
      userId: userId.value,
      password: password.value,
      phoneNum,
      email,
    });

    const response = await axios.post('/api/user/signup', {
      userName: userName.value,
      userId: userId.value,
      password: password.value,
      phoneNum,
      email,
    });

    console.log('서버 응답:', response.data);

    if (response.data.success) {
      alert('회원가입이 완료되었습니다!');
      router.push('/'); // 메인 페이지로 리다이렉트
    } else {
      alert(response.data.message || '회원가입에 실패했습니다.');
    }
  } catch (error) {
    console.error('회원가입 오류:', error);

    if (error.response) {
      alert(`오류: ${error.response.status} - ${error.response.data.message || error.response.statusText}`);
    } else {
      alert('회원가입 중 네트워크 오류가 발생했습니다.');
    }
  }
};

// Password toggle functions
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

const toggleConfirmPasswordVisibility = () => {
  showConfirmPassword.value = !showConfirmPassword.value;
};

const clearPhoneFirst = () => {
  if (phoneFirst.value === '직접 입력') {
    phoneFirst.value = ''; // "직접 입력" 선택 시 값 초기화
  }
};

const clearEmailSecond = () => {
  if (emailSecond.value === '직접 입력') {
    emailSecond.value = ''; // "직접 입력" 선택 시 값 초기화
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
  min-height: 70px; /* 고정 높이 추가 (알림 포함) */
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
  font-size: 20px;
  width: 100%;
  padding-right: 40px;
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

.check-message,
.password-match-message,
.error-message {
  font-size: 14px;
  text-align: left;
  margin-top: 4px;
  color: #d9534f;
  height: 20px; /* 고정 높이 추가 */
  line-height: 20px; /* 줄 높이 고정 */
  overflow: hidden; /* 텍스트 넘침 방지 */
  white-space: nowrap; /* 텍스트 줄 바꿈 방지 */
}

.success-message {
  font-size: 14px;
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

.phone-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  justify-content: space-between;
}

.phone-select {
  width: 120px;
  height: 40px;
  border: 2px solid #303030;
  border-radius: 20px;
  padding: 0 10px;
  text-align: center;
  font-size: 16px;
}

.phone-input {
  width: 120px;
  height: 40px;
  border: 2px solid #303030;
  border-radius: 20px;
  padding: 0 10px;
  text-align: center;
  font-size: 16px;
}

.phone-input-group span {
  font-size: 18px;
  font-weight: bold;
  margin: 0 5px;
}

.email-input-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  font-size: 18px;
}

.email-input {
  flex: 1;
  height: 40px;
  border: 2px solid #303030;
  border-radius: 20px;
  padding: 0 10px;
  text-align: center;
  font-size: 16px;
}

.email-select {
  flex: 1;
  height: 40px;
  border: 2px solid #303030;
  border-radius: 20px;
  padding: 0 10px;
  text-align: center;
  font-size: 16px;
}

.email-input-group span {
  flex: 0;
  font-size: 18px;
  font-weight: bold;
  margin: 0 5px;
}

.email-custom-group {
  margin-top: 10px;
}

.email-custom-group .email-input {
  flex: 1;
  height: 40px;
  border: 2px solid #303030;
  border-radius: 20px;
  padding: 0 10px;
  text-align: center;
  font-size: 16px;
}
</style>



