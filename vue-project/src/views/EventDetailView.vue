<template>
  <div class="event-detail">
    <!-- 이미지 섹션 수정 -->
    <div v-if="event.imageUrls && event.imageUrls.length > 0" id="image-section" class="image-section animate__animated animate__fadeIn">
      <div class="image-bubble">
        <!-- 첫 번째 이미지만 표시 -->
        <img :src="getImageUrl(event.imageUrls[0])" :alt="event.title" class="event-image">
      </div>
    </div>

    <!-- Cute Navigation Bubbles -->
    <div class="bubble-navigation animate__animated animate__bounceIn">
      <div class="bubble" 
           :class="{ active: activeSection === 'info' }"
           @click="scrollToSection('info-section')">
        <i class="bi bi-info-circle"></i>
        <span>정보</span>
      </div>
      <div class="bubble" 
           :class="{ active: activeSection === 'map' }"
           @click="scrollToSection('map-section')">
        <i class="bi bi-map"></i>
        <span>지도</span>
      </div>
    </div>

    <!-- Main Content -->
    <div class="content-wrapper">
      <!-- Event Info Section -->
      <div id="info-section" class="info-section animate__animated animate__fadeIn">
        <div class="description-bubble">
          <p>{{ event.description }}</p>
        </div>

        <!-- Map Container -->
        <div id="map-section" class="map-section">
          <div class="map-bubble">
            <div id="kakao-map" style="width: 100%; height: 100%; position: relative;"></div>
          </div>
        </div>

        <!-- Details Bubbles -->
        <div class="details-container">
          <div class="detail-bubble">
            <i class="bi bi-telephone"></i>
            <span>{{ event.inquiryNumber }}</span>
          </div>
          <div class="detail-bubble">
            <i class="bi bi-geo-alt"></i>
            <span>{{ event.address }}</span>
          </div>
          <div class="detail-bubble">
            <i class="bi bi-clock"></i>
            <span>{{ event.checkInTime }} - {{ event.checkOutTime }}</span>
          </div>
          <div class="detail-bubble">
            <i class="bi bi-p-square"></i>
            <span>주차 {{ event.parkingAvailable ? '가능' : '불가' }}</span>
          </div>
          <div class="detail-bubble">
            <i class="bi bi-building"></i>
            <span>{{ event.facilities }}</span>
          </div>
          <div class="detail-bubble">
            <i class="bi bi-cash"></i>
            <span>{{ event.entryFee?.toLocaleString() }}원</span>
          </div>
          <div class="detail-bubble">
            <i class="bi bi-globe"></i>
            <a :href="formatWebsiteUrl(event.websiteUrl)" target="_blank">{{ event.websiteUrl }}</a>
          </div>
        </div>
      </div>
    </div>

    <!-- Chatbot Button -->
    <button class="chatbot-button" @click="toggleChatbot">
      <i class="bi bi-chat-dots-fill"></i>
    </button>

    <!-- Chatbot Window -->
    <div v-if="isChatbotOpen" class="chatbot-window animate__animated animate__fadeIn">
      <div class="chatbot-header">
        <h3>이벤트 도우미</h3>
        <button class="close-btn" @click="toggleChatbot">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
      <div class="chatbot-content">
        <Chatbot 
          :event="event" 
          @close="toggleChatbot"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
import { ref, onMounted, watch, nextTick, onUnmounted } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import Chatbot from '@/components/Chatbot.vue';


const currentIP = window.location.hostname;
console.log(' IP:', currentIP);

const route = useRoute();

// Comment handling
const event = ref({
  description: '',
  address: '',
  inquiryNumber: '',
  websiteUrl: '',
  checkInTime: '',
  checkOutTime: '',
  parkingAvailable: false,
  facilities: '',
  entryFee: 0,
  imageUrls: [],
});

const isChatbotOpen = ref(false);

const activeSection = ref('info');

const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId);
  if (element) {
    const offset = element.offsetTop - (window.innerHeight - element.offsetHeight) / 2;
    window.scrollTo({
      top: offset,
      behavior: 'smooth'
    });
    activeSection.value = sectionId.replace('-section', '');
  }
};

const updateActiveSection = () => {
  const sections = ['image-section', 'info-section', 'map-section'];
  const viewportMiddle = window.scrollY + window.innerHeight / 2;

  for (const sectionId of sections) {
    const element = document.getElementById(sectionId);
    if (element) {
      const elementMiddle = element.offsetTop + element.offsetHeight / 2;
      if (Math.abs(viewportMiddle - elementMiddle) < element.offsetHeight / 2) {
        activeSection.value = sectionId.replace('-section', '');
        break;
      }
    }
  }
};

onMounted(() => {
  window.addEventListener('scroll', updateActiveSection);
});

onUnmounted(() => {
  window.removeEventListener('scroll', updateActiveSection);
});



// 맵 련 상태
const mapLoaded = ref(false);
const mapInstance = ref(null);

// 카카오맵 초기화 함수 수정
const initializeKakaoMap = async () => {
  console.log('카카오맵 초기화 시작');
  
  // kakao 객체 확인
  if (typeof kakao === 'undefined') {
    console.error('Kakao Maps SDK not loaded');
    return;
  }

  const container = document.getElementById('kakao-map');
  if (!container) {
    console.error('Map container not found');
    return;
  }

  try {
    // 지도 생성
    const options = {
      center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청 기본 좌표
      level: 3
    };

    const map = new kakao.maps.Map(container, options);
    mapInstance.value = map;
    console.log('지도 생성 성공');

    // 주소로 좌표 검색
    if (event.value?.address) {
      console.log('검색할 주소:', event.value.address);
      
      // 주소-좌표 변환 객체 생성
      const geocoder = new kakao.maps.services.Geocoder();

      // 주소로 좌표 검색
      geocoder.addressSearch(event.value.address, function(result, status) {
        console.log('주소 검색 결과:', status, result);

        if (status === kakao.maps.services.Status.OK) {
          const coords = new kakao.maps.LatLng(result[0].y, result[0].x);

          // 마커 생성
          const marker = new kakao.maps.Marker({
            map: map,
            position: coords
          });

          // 인포윈도우 생성
          const infowindow = new kakao.maps.InfoWindow({
            content: `<div style="padding:5px;font-size:12px;text-align:center;">
                      <strong>${event.value.title || '행사 장소'}</strong><br>
                      ${event.value.address}
                    </div>`
          });

          // 마커에 마우스오버 이벤트 추가
          kakao.maps.event.addListener(marker, 'mouseover', function() {
            infowindow.open(map, marker);
          });

          // 마커에 마우스아웃 이벤트 추가
          kakao.maps.event.addListener(marker, 'mouseout', function() {
            infowindow.close();
          });

          // 지도 중심을 검색된 좌표로 이동
          map.setCenter(coords);
          map.setLevel(3); // 줌 레벨 설정
          
          console.log('지도 이동 완료:', coords);
        } else {
          console.error('주소 검색 실패:', status);
        }
      });
    } else {
      console.error('주소 정보가 없습니다.');
    }
  } catch (error) {
    console.error('지도 초기화 중 오류:', error);
  }
};

// 이미지 URL 생성 함수 추가
const getImageUrl = (filename) => {
  if (!filename) {
    console.log('파일명이 없음');
    return null;
  }
  const url = `http://${currentIP}:8080/uploads/${filename}`;
  console.log('생성된 이미지 URL:', url);
  return url;
};

onMounted(async () => {
  const eventId = route.params.id;
  
  try {
    const response = await axios({
      method: 'get',
      url: `/api/event-detail/${eventId}`,
      baseURL: `http://${currentIP}:8080`,
      withCredentials: false
    });
    
    if (response.data) {
      // 이벤트 데이터 설정
      event.value = response.data;
      
      // DOM 업데이트 대기
      await nextTick();
      
      // 카카오맵 초기화
      await initializeKakaoMap();
    }
  } catch (error) {
    console.error("API 요청 실패:", error);
  }
});

// 컴포넌트 언마운트 시 맵 인스턴스 정리
onUnmounted(() => {
  if (mapInstance.value) {
    mapInstance.value = null;
  }
});

const toggleChatbot = () => {
  isChatbotOpen.value = !isChatbotOpen.value;
};

// 웹사이트 URL 포맷팅 함수 추가
const formatWebsiteUrl = (url) => {
  if (!url) return '#';
  
  // URL이 http:// 또는 https://로 시작하지 않면 추가
  if (!url.startsWith('http://') && !url.startsWith('https://')) {
    return `https://${url}`;
  }
  return url;
};
</script>

<style scoped>
.event-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
  background-color: var(--bg-color);
  color: var(--text-color);
}

/* Bubble Navigation */
.bubble-navigation {
  display: flex;
  justify-content: center;
  gap: 25px;
  margin-bottom: 40px;
}

.bubble {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 25px;
  background-color: var(--bg-color);
  border: 2px solid #ebd03b;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.bubble.active {
  background-color: #ebd03b;
  color: #fff;
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(235, 208, 59, 0.3);
}

.bubble i {
  font-size: 24px;
  margin-bottom: 5px;
  color: #ebd03b;
}

.bubble.active i {
  color: #fff;
}

/* Info Bubbles */
.description-bubble, .detail-bubble, .comment-bubble {
  background-color: var(--bg-color);
  border: 2px solid #ebd03b;
  border-radius: 15px;
  padding: 20px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.detail-bubble {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 25px;
}

.detail-bubble i {
  font-size: 20px;
  color: #ebd03b;
}

.detail-bubble:hover {
  transform: translateY(-3px);
  background-color: rgba(235, 208, 59, 0.1);
}

/* Map Container */
.map-section {
  width: 100%;
  padding: 20px;
}

.map-bubble {
  width: 100%;
  height: 400px;
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: relative;
}

#kakao-map {
  width: 100%;
  height: 100%;
  position: relative;
}

/* 지도 관련 스타일 */
:deep(.map-bubble *) {
  width: 100%;
  height: 100%;
}

/* Comment Section */
.comment-bubble textarea {
  width: 100%;
  height: 120px;
  border: 2px solid #ebd03b;
  border-radius: 15px;
  padding: 15px;
  margin-bottom: 15px;
  font-size: 16px;
  resize: none;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: all 0.3s ease;
}

.comment-bubble textarea:focus {
  box-shadow: 0 0 10px rgba(235, 208, 59, 0.3);
  outline: none;
}

.submit-btn {
  background-color: #ebd03b;
  color: #fff;
  border: none;
  border-radius: 25px;
  padding: 12px 30px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #d4bb34;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(235, 208, 59, 0.3);
}

/* Animations */
.animate__animated {
  animation-duration: 0.8s;
}

/* Responsive Design */
@media (max-width: 768px) {
  .bubble-navigation {
    flex-wrap: wrap;
  }
  
  .bubble {
    flex: 1 1 calc(50% - 20px);
  }
}

.chatbot-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ebd03b;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(235, 208, 59, 0.3);
  transition: all 0.3s ease;
  z-index: 1000;
}

.chatbot-button:hover {
  transform: translateY(-3px);
  background-color: #d4bb34;
}

.chatbot-container {
  position: fixed;
  bottom: 100px;
  right: 30px;
  z-index: 1001;
}

.chatbot-window {
  width: 380px !important;
  height: 500px !important;
  background-color: var(--bg-color);
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  position: fixed;
  bottom: 100px;
  right: 30px;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  border: 2px solid #ebd03b;
}

.chatbot-header {
  padding: 15px 20px;
  background-color: #ebd03b;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.chatbot-header h3 {
  margin: 0;
  color: var(--text-color);
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  color: var(--text-color);
  cursor: pointer;
  font-size: 20px;
  padding: 5px;
}

.close-btn:hover {
  color: #BC8F8F;
}

.chatbot-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 애니메이션 추가 */
.animate__fadeIn {
  animation-duration: 0.3s;
}

@media (max-width: 768px) {
  .chatbot-window {
    width: 340px !important;
    height: 450px !important;
    bottom: 80px;
    right: 20px;
  }
}

/* 다크모드 변수 정의 */
:root {
  --bg-color: #fff;
  --text-color: #333;
  --hover-color: #FFE4B5;
  --border-color: #DEB887;
}

:root.dark-mode {
  --bg-color: #1a1a1a;
  --text-color: #e0e0e0;
  --hover-color: #4a3f35;
  --border-color: #5a4f45;
}

/* 링크 색상은 그대로 유지 */
a {
  color: #DEB887;
}

a:hover {
  color: #BC8F8F;
}

/* 아이콘 색상 */
.detail-bubble i {
  color: var(--border-color);
}

.detail-bubble a {
  text-decoration: none;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.detail-bubble a:hover {
  color: #ebd03b;
}

/* 이미지 섹션 스타일 추가 */
.image-section {
  width: 100%;
  margin-bottom: 30px;
}

.image-bubble {
  width: 100%;
  border-radius: 15px;
  overflow: hidden;
  border: 2px solid var(--border-color);
  background-color: var(--bg-color);
  box-shadow: 0 4px 12px var(--shadow-color);
}

.event-image {
  width: 100%;
  height: auto;
  object-fit: cover;
  display: block;
  max-height: 500px;
}

/* 다크모드에서의 이미지 스타일 */
:root.dark-mode .image-bubble {
  border-color: var(--border-color);
  background-color: var(--bg-color-lighter);
}

:root.dark-mode .event-image {
  filter: brightness(0.9);
}
</style>