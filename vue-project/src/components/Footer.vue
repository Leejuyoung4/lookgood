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
.footer-container {
  background: #f5f5f5;
  padding: 40px 20px;
  text-align: center;
  font-family: "Inter-Regular", sans-serif;
}

.logo-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  margin-left: 20px;
  margin-bottom:10px; /* Adds spacing below logo */
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
  margin: 30px;
  margin-left: 50px;
}
.icon {
  width: 40px;
  height: 40px;
}

/* Bottom Menu */
.bottom-menu {
  display: flex;
  justify-content: center;
  gap: 50px;
  font-size: 18px;
  margin-bottom: 20px;
  margin-left: 25px;
}
.highlight {
  color: #c2ad31;
}

/* Center Buttons */
.center-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px 0;
  margin-left: 20px;
}
.button {
  position: relative;
  width: 150px;
  height: 60px;
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
  font-size: 20px;
  line-height: 60px;
  font-weight: 400;
  z-index: 1;
}

/* Copyright Text */
.copyright {
  color: #878787;
  font-size: 14px;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
}
</style>