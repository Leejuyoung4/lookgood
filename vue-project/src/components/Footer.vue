<template>
  <div class="footer-container">
    <RouterLink to="/" class="logo" @click="scrollToTop">
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
      </RouterLink>

    <!-- Social Media Icons -->
    <div class="social-icons">
      <a href="https://www.instagram.com/">
        <img class="icon" :src="instagram0" alt="Instagram" />
      </a>
      <a href="https://www.facebook.com/">
        <img class="icon" :src="facebook0" alt="Facebook" />
      </a>
      <a href="https://www.youtube.com/">
        <img class="icon" :src="youtube0" alt="YouTube" />
      </a>
      <a>
        <img class="icon" :src="share20" alt="Share" />
      </a>
    </div>

    <!-- Bottom Menu -->
    <div class="bottom-menu">
      <div>회사소개</div>
      <div>이용약관</div>
      <div class="highlight">개인정보처리방침</div>
      <div>이용안내</div>
    </div>

    <!-- Center Buttons -->
    <div class="center-buttons">
      <div class="button">
        <div class="button-background"></div>
        <RouterLink to="/community/inquiry" style="text-decoration: none;">
          <span class="button-text">고객센터</span>
        </RouterLink>
      </div>
      <div class="button">
        <div class="button-background"></div>
        <RouterLink to="/community/inquiry" style="text-decoration: none;">
          <span class="button-text">질문게시판</span>
        </RouterLink>
      </div>
    </div>

    <!-- Text -->
    <div class="copyright">
      <p>본 사이트의 콘텐츠는 저작권법의 보호를 받는 바 무단 전재, 복사, 배포 등을 금합니다.</p>
      <p>Copyright(c) SSAFY All rights reserved.</p>
    </div>
  </div>
</template>

<script setup>
import facebook0 from '@/assets/img/facebook0.svg';
import instagram0 from '@/assets/img/instagram0.svg';
import share20 from '@/assets/img/share-20.svg';
import youtube0 from '@/assets/img/youtube0.svg';


const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
};



</script>

<style scoped>
/* Footer 전체 컨테이너 수정 */
.footer-container {
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: all 0.3s ease;
  border-top: 1px solid var(--border-color);
  padding: 20px 0;
  width: 95%; /* 헤더와 동일한 너비 */
  margin: 0 auto;
}

/* 로고는 색상 유지 */
.logo {
  color: #ebd03b !important;
}

/* Bottom Menu 스타일 수정 */
.bottom-menu {
  display: flex;
  justify-content: center;
  gap: 60px;
  font-size: 18px;
  margin: 20px auto;
  padding: 0;
  color: var(--text-color);
}

.bottom-menu div {
  color: var(--text-color);
  transition: color 0.3s ease;
  font-size: 16px;
  font-weight: 400;
}

/* highlight 클래스는 색상 유지 */
.highlight {
  color: #c2ad31 !important;
}

/* 버튼 스타일 수정 */
.button {
  position: relative;
  width: 180px;
  height: 55px;
  transition: all 0.3s ease;
}

.button-background {
  background: var(--button-bg, #ddc434);
  border-radius: 30px;
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  transition: background-color 0.3s ease;
}

/* 다크모드 버튼 스타일 */
:root.dark-mode .button-background {
  background: var(--dark-button-bg, #4a4a4a);
}

:root.dark-mode .button:hover .button-background {
  background: var(--dark-button-hover-bg, #666);
}

/* 버튼 텍스트 */
.button-text {
  position: relative;
  color: var(--button-text-color, #fff) !important;
  font-size: 18px;
  line-height: 55px;
  font-weight: 500;
  z-index: 1;
  text-align: center;
  width: 100%;
  display: block;
  transition: color 0.3s ease;
}

/* 버튼 호버 효과 */
.button:hover {
  transform: translateY(-2px);
}

.button:hover .button-background {
  filter: brightness(1.1);
}

/* 다크모드 CSS 변수 추가 */
:root.dark-mode {
  --button-bg: #4a4a4a;
  --button-hover-bg: #666;
  --button-text-color: #fff;
}

/* Copyright Text */
.copyright {
  text-align: center;
  color: var(--text-color);
  font-size: 13px;
  line-height: 1.6;
  max-width: 800px;
  margin: 30px auto 20px;
  padding: 0;
}

.copyright p {
  color: var(--text-color);
  margin: 5px 0;
}

/* Social Icons */
.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px auto;
  padding: 0;
}

.icon {
  width: 40px;
  height: 40px;
  filter: var(--icon-filter);
}

/* 다크모드일 때 아이콘 색상 조정 */
:root.dark-mode .icon {
  filter: brightness(0.8) invert(1);
}

/* 나머지 기존 스타일 유지 */
.logo-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 20px auto 10px;
  padding: 0;
}
.logo {
  display: flex;
  color: #ebd03b;
  font-size: 38px;
  text-decoration: none;
  justify-content: center; /* Centers the logo content */
}
.logo-bold {
  font-weight: 600;
}

/* Apply rotation to each individual letter */
.logo-item:nth-child(1) { transform: rotate(-10deg); } /* L */
.logo-item:nth-child(2) { transform: rotate(10deg); }  /* oo */
.logo-item:nth-child(3) { transform: rotate(-10deg); } /* k */
.logo-item:nth-child(4) { transform: rotate(10deg); }  /* G */
.logo-item:nth-child(5) { transform: rotate(-10deg); } /* oo */
.logo-item:nth-child(6) { transform: rotate(10deg); }  /* d */

/* Social Media Icons */
.social-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px auto;
  padding: 0;
}
.icon {
  width: 40px;
  height: 40px;
}

/* Bottom Menu */
.bottom-menu {
  display: flex;
  justify-content: center;
  gap: 60px;
  font-size: 18px;
  margin: 20px auto;
  padding: 0;
}
.highlight {
  color: #c2ad31;
}

/* Center Buttons */
.center-buttons {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin: 30px auto;
  padding: 0;
}
.button {
  position: relative;
  width: 180px;
  height: 55px;
}
.button-background {
  background: #ddc434;
  border-radius: 30px;
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
}
.button-text {
  position: relative;
  color: #fff;
  font-size: 18px;
  line-height: 55px;
  font-weight: 500;
  z-index: 1;
  text-align: center;
  width: 100%;
  display: block;
}

/* Copyright Text */
.copyright {
  color: #878787;
  font-size: 14px;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
}

.footer {
  background-color: var(--bg-color);
  color: var(--text-color);
  border-top: 1px solid var(--border-color);
  transition: background-color 0.3s, color 0.3s;
}

.footer-content {
  background-color: var(--bg-color);
  color: var(--text-color);
}

.footer-section {
  color: var(--text-color);
}

.footer-section h3 {
  color: var(--text-color);
}

.footer-section p,
.footer-section a {
  color: var(--text-color);
}

.footer-section a:hover {
  color: #ebd03b;
}

.footer-bottom {
  background-color: var(--secondary-bg);
  color: var(--text-color);
  border-top: 1px solid var(--border-color);
}

/* 기존 스타일은 유지하고, 다크모드 관련 스타일만 추가 */
.social-links a {
  color: var(--text-color);
}

.social-links a:hover {
  color: #ebd03b;
}

.contact-info a {
  color: var(--text-color);
}

.contact-info a:hover {
  color: #ebd03b;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .footer-container {
    width: 100%;
    padding: 20px;
  }

  .bottom-menu {
    gap: 30px;
    padding: 0 10px;
  }

  .social-icons {
    gap: 15px;
  }
}
</style>