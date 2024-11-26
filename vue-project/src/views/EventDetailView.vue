<template>
  <div class="event-detail">
    <!-- Cute Navigation Bubbles -->
    <div class="bubble-navigation animate__animated animate__bounceIn">
      <div class="bubble" 
           :class="{ active: activeSection === 'image' }"
           @click="scrollToSection('image-section')">
        <i class="bi bi-image"></i>
        <span>사진</span>
      </div>
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
      <!-- Main Image Section -->
      <div id="image-section" class="image-section animate__animated animate__fadeIn">
        <div class="main-image-container">
          <img :src="getEventImage(event.description)" alt="Event Main Image" />
        </div>
      </div>

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
            <span>주차 {{ event.parkingAvailable ? '가능' : '불가능' }}</span>
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
});

const isChatbotOpen = ref(false);

const activeSection = ref('image');

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

// 주소를 기반으로 구글 지도 URL 생성
const getMapUrl = (address) => {
  if (!address) {
    return 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3...';
  }
  return `https://www.google.com/maps/embed/v1/place?key=YOUR_GOOGLE_MAP_API_KEY&q=${encodeURIComponent(address)}`;
};

const getEventImage = (description) => {
  // 설명에 포함된 키워드에 따 이미지 반환
  if (!description) return 'https://via.placeholder.com/800x400';

  const keywords = description.toLowerCase();

  if (keywords.includes('마라톤') || keywords.includes('달리기')) {
    return 'https://images.unsplash.com/photo-1530143584546-02191bc84eb5?q=80&w=1000';
  }

  if (keywords.includes('철인') || keywords.includes('트라이애슬론')) {
    return 'https://images.unsplash.com/photo-1530549387789-4c1017266635?q=80&w=1000';
  }

  if (keywords.includes('수영')) {
    return 'https://images.unsplash.com/photo-1530549387789-4c1017266635?q=80&w=1000';
  }

  if (keywords.includes('자전거') || keywords.includes('사이클')) {
    return 'https://images.unsplash.com/photo-1517649763962-0c623066013b?q=80&w=1000';
  }

  if (keywords.includes('패션')) {
    return 'https://images.unsplash.com/photo-1509631179647-0177331693ae?q=80&w=1000';
  }

  // 기본 이미지
  return 'https://via.placeholder.com/800x400';
};

// 맵 관련 상태
const mapLoaded = ref(false);
const mapInstance = ref(null);

// 카카오맵 초기화 함수
const initializeKakaoMap = async () => {
  const container = document.getElementById('kakao-map');
  const options = {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 3
  };

  const map = new kakao.maps.Map(container, options);
  mapInstance.value = map;

  const geocoder = new kakao.maps.services.Geocoder();

  const searchAddressPromise = (address) => {
    return new Promise((resolve) => {
      geocoder.addressSearch(address, (result, status) => {
        resolve({ result, status });
      });
    });
  };

  if (event.value?.address) {
    const findLocation = async (address) => {
      // 1. 정확한 주소로 검색
      let response = await searchAddressPromise(address);
      if (response.status === kakao.maps.services.Status.OK) {
        return response.result[0];
      }

      // 2. 번지수 제거 후 검색
      const addressWithoutNumber = address.replace(/[0-9-]+/g, '').trim();
      response = await searchAddressPromise(addressWithoutNumber);
      if (response.status === kakao.maps.services.Status.OK) {
        return response.result[0];
      }

      // 3. 도로명/지번 주소 키워드 제거 후 검색
      const parts = address.split(' ');
      if (parts.length >= 3) {
        // 도로명이나 동네 이름에서 '길', '로', '동' 등의 키워드 제거
        const locationName = parts[parts.length - 1]
          .replace(/[0-9-]+/g, '')
          .replace(/(길|로|가|동|읍|면)$/, '');
        
        // 시/구/동 조합으로 검색
        const areaSearch = `${parts[0]} ${parts[1]} ${locationName}`;
        response = await searchAddressPromise(areaSearch);
        if (response.status === kakao.maps.services.Status.OK) {
          // 구청, 시청 등의 키워드가 포함된 결과 필터링
          const filteredResults = response.result.filter(item => 
            !item.address_name.includes('청사') &&
            !item.address_name.includes('구청') &&
            !item.address_name.includes('시청')
          );
          
          if (filteredResults.length > 0) {
            return filteredResults[0];
          }
          return response.result[0];
        }

        // 4. 더 넓은 지역으로 검색
        const broadArea = `${parts[0]} ${parts[1]}`;
        response = await searchAddressPromise(broadArea);
        if (response.status === kakao.maps.services.Status.OK) {
          // 구청, 시청 등의 키워드가 포함된 결과 필터링
          const filteredResults = response.result.filter(item => 
            !item.address_name.includes('청사') &&
            !item.address_name.includes('구청') &&
            !item.address_name.includes('시청')
          );
          
          if (filteredResults.length > 0) {
            return filteredResults[0];
          }
          return response.result[0];
        }
      }

      return null;
    };

    const searchResult = await findLocation(event.value.address);
    
    if (searchResult) {
      const coords = new kakao.maps.LatLng(searchResult.y, searchResult.x);
      
      // 마커 생성
      const marker = new kakao.maps.Marker({
        map: map,
        position: coords
      });

      // 인포윈도우 생성
      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;font-size:12px;">
                  ${event.value.address}
                  </div>`
      });

      // 마커 이벤트
      kakao.maps.event.addListener(marker, 'mouseover', () => infowindow.open(map, marker));
      kakao.maps.event.addListener(marker, 'mouseout', () => infowindow.close());

      // 지도 중심 이동
      map.setCenter(coords);
      map.setLevel(3);
    }
  }
};

onMounted(async () => {
  const eventId = route.params.id;
  
  try {
    // 1. 이벤트 데이터 가져오기
    const response = await axios({
      method: 'get',
      url: `/api/event-detail/${eventId}`,
      baseURL: `http://${currentIP}:8080`,
      withCredentials: false
    });
    
    event.value = response.data;
    console.log('서버 응답:', response.data);

    // 2. DOM 업데이트 대기
    await nextTick();

    // 3. 카카오맵 초기화
    if (window.kakao && window.kakao.maps) {
      initializeKakaoMap();
    } else {
      let attempts = 0;
      const maxAttempts = 20;
      
      const checkMap = setInterval(() => {
        if (window.kakao && window.kakao.maps) {
          clearInterval(checkMap);
          initializeKakaoMap();
        } else if (attempts >= maxAttempts) {
          clearInterval(checkMap);
          console.error('카카오맵 로드 타임아웃');
        }
        attempts++;
      }, 500);
    }

  } catch (error) {
    console.error("이벤트 상세 정보 가져오기 실패:", error);
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
  
  // URL이 http:// 또는 https://로 시작하지 않으면 추가
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

/* Main Image */
.main-image-container {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
  border: 3px solid #ebd03b;
}

.main-image-container img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.main-image-container:hover img {
  transform: scale(1.05);
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
</style>