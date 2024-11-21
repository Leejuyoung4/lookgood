<template>
  <div class="event-detail">
    <!-- Cute Navigation Bubbles -->
    <div class="bubble-navigation animate__animated animate__bounceIn">
      <div class="bubble active">
        <i class="bi bi-image"></i>
        <span>사진</span>
      </div>
      <div class="bubble">
        <i class="bi bi-info-circle"></i>
        <span>정보</span>
      </div>
      <div class="bubble">
        <i class="bi bi-list-check"></i>
        <span>준비물</span>
      </div>
      <div class="bubble">
        <i class="bi bi-wallet2"></i>
        <span>비용</span>
      </div>
    </div>

    <!-- Main Content -->
    <div class="content-wrapper">
      <!-- Main Image Section -->
      <div class="image-section animate__animated animate__fadeIn">
        <div class="main-image-container">
          <img :src="getEventImage(event.description)" alt="Event Main Image" />
        </div>
      </div>

      <!-- Event Info Section -->
      <div class="info-section animate__animated animate__fadeIn">
        <div class="description-bubble">
          <p>{{ event.description }}</p>
        </div>

        <!-- Map Container -->
        <div class="map-bubble">
          <div id="kakao-map"></div>
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
            <a :href="event.websiteUrl" target="_blank">사이트 방문</a>
          </div>
        </div>

        <!-- Comment Section -->
        <div class="comment-bubble">
          <textarea 
            v-model="comment" 
            placeholder="귀여운 댓글을 남겨주세요 ✨"
          ></textarea>
          <button @click="submitComment" class="submit-btn">
            <i class="bi bi-send-fill"></i> 등록하기
          </button>
        </div>
      </div>
    </div>

    <!-- Chatbot Button -->
    <button class="chatbot-button" @click="toggleChatbot">
      <i class="bi bi-chat-dots-fill"></i>
    </button>

    <!-- Chatbot Window -->
    <div v-if="isChatbotOpen" class="chatbot-window">
      <div class="chatbot-header">
        <h3>이벤트 도우미</h3>
        <button class="close-btn" @click="toggleChatbot">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
      <Chatbot :event="event" />
    </div>
  </div>
</template>

<script setup>
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import axios from "axios";
import Chatbot from '@/components/Chatbot.vue';


const currentIP = window.location.hostname;
console.log('현재 IP:', currentIP);

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

const comment = ref('');
const isChatbotOpen = ref(false);

// Fetch event details based on the ID in the route
onMounted(async () => {
  const eventId = route.params.id;
  console.log('요청할 ID:', eventId);

  try {
    const response = await axios({
      method: 'get',
      url: `/api/event-detail/${eventId}`,
      baseURL: `http://${currentIP}:8080`,
      withCredentials: false
    });
    
    console.log('서버 응답:', response.data);
    event.value = response.data;
  } catch (error) {
    console.error("이벤트 상세 정보 가져오기 실패:", error);
  }
});

// Submit a comment
const submitComment = () => {
  if (comment.value.trim()) {
    console.log("Comment submitted:", comment.value);
    comment.value = ""; // Clear the comment field after submission
  } else {
    alert("댓글을 작성해주세요.");
  }
};

// 주소를 기반으로 구글 지도 URL 생성
const getMapUrl = (address) => {
  if (!address) {
    return 'https://www.google.com/maps/embed?pb=!1m18!1m12!1m3...';
  }
  return `https://www.google.com/maps/embed/v1/place?key=YOUR_GOOGLE_MAP_API_KEY&q=${encodeURIComponent(address)}`;
};

const getEventImage = (description) => {
  // 설명에 포함된 키워드에 따라 이미지 반환
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

// 주소로 지도 표시
const showMap = async (address) => {
  console.log('지도 표시 시작:', address);
  if (!address) return;

  // 명동로 5와 같은 도로명 주소를 지역명으로 변환
  const getSearchableAddress = (addr) => {
    const parts = addr.split(' ');
    // 도로명 주소인 경우 구까지만 사용
    if (addr.includes('로') || addr.includes('길')) {
      return parts.slice(0, 2).join(' ');
    }
    return addr;
  };

  try {
    const container = document.getElementById('kakao-map');
    if (!container) {
      console.error('지도를 표시할 div를 찾을 수 없습니다.');
      return;
    }

    const searchAddress = getSearchableAddress(address);
    console.log('검색할 주소:', searchAddress);

    const ps = new kakao.maps.services.Places();
    
    const searchResult = await new Promise((resolve) => {
      ps.keywordSearch(searchAddress, (data, status) => {
        resolve({ data, status });
      });
    });

    if (searchResult.status === kakao.maps.services.Status.OK) {
      const place = searchResult.data[0];
      const options = {
        center: new kakao.maps.LatLng(place.y, place.x),
        level: 3
      };

      const map = new kakao.maps.Map(container, options);
      
      const marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
      });

      const infowindow = new kakao.maps.InfoWindow({
        content: `<div style="padding:5px;font-size:12px;">${place.place_name}</div>`
      });
      infowindow.open(map, marker);

      console.log('지도 표시 완료:', place.place_name);
    } else {
      // 검색 실패 시 구 중심점으로 표시
      const districtCoords = {
        '중구': { lat: 37.5640907, lng: 126.9979403 },
        '종로구': { lat: 37.5728924, lng: 126.9793169 },
        '강남구': { lat: 37.5172363, lng: 127.0473248 },
        // ... 필요한 구 좌표 추가
      };

      const district = address.split(' ')[1];
      const defaultCoords = districtCoords[district] || { lat: 37.5665, lng: 126.9780 };

      const options = {
        center: new kakao.maps.LatLng(defaultCoords.lat, defaultCoords.lng),
        level: 5
      };

      const map = new kakao.maps.Map(container, options);
      console.log('기본 위치로 지도 표시');
    }

  } catch (error) {
    console.error('지도 표시 중 오류:', error);
  }
};

// API 호출 및 데이터 로드
onMounted(async () => {
  const eventId = route.params.id;
  console.log('요청할 ID:', eventId);

  try {
    const response = await axios({
      method: 'get',
      url: `/api/event-detail/${eventId}`,
      baseURL: `http://${currentIP}:8080`,
      withCredentials: false
    });
    
    event.value = response.data;
    console.log('이벤트 데이터 로드:', event.value);

    // SDK 로드 확인 후 지도 표시
    if (event.value.address) {
      // SDK가 로드될 때까지 기다림
      const waitForKakaoMaps = setInterval(() => {
        if (window.kakao && window.kakao.maps) {
          clearInterval(waitForKakaoMaps);
          showMap(event.value.address);
        }
      }, 100);

      // 10초 후에도 로드되지 않으면 중단
      setTimeout(() => {
        clearInterval(waitForKakaoMaps);
        console.error('카카오맵 SDK 로드 타임아웃');
      }, 10000);
    }
  } catch (error) {
    console.error("이벤트 상세 정보 가져오기 실패:", error);
  }
});

// 주소가 변경될 때마다 지도 업데이트
watch(() => event.value.address, (newAddress) => {
  if (newAddress) {
    console.log('주소 변경 감지:', newAddress);
    showMap(newAddress);
  }
});


// 주소가 변경될 때마다 지도 업데이트
watch(() => event.value.address, (newAddress) => {
  if (newAddress) {
    console.log('주소 변경 감지:', newAddress);
    showMap(newAddress);
  }
});

const toggleChatbot = () => {
  isChatbotOpen.value = !isChatbotOpen.value;
  console.log('챗봇 상태:', isChatbotOpen.value);
};
</script>

<style scoped>
.event-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px;
  background-color: #FFF5E6;
}

/* Bubble Navigation */
.bubble-navigation {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 40px;
}

.bubble {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px 25px;
  background-color: white;
  border-radius: 30px;
  box-shadow: 0 4px 15px rgba(222, 184, 135, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.bubble.active {
  background-color: #FFE4B5;
  transform: translateY(-5px);
}

.bubble i {
  font-size: 24px;
  margin-bottom: 5px;
  color: #DEB887;
}

/* Main Image */
.main-image-container {
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(222, 184, 135, 0.3);
  margin-bottom: 30px;
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
  background-color: white;
  border-radius: 25px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(222, 184, 135, 0.2);
  transition: transform 0.3s ease;
}

.detail-bubble {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px 25px;
}

.detail-bubble i {
  font-size: 20px;
  color: #DEB887;
}

.detail-bubble:hover {
  transform: translateY(-3px);
  background-color: #FFE4B5;
}

/* Map Container */
#kakao-map {
  height: 400px;
  border-radius: 25px;
  overflow: hidden;
  margin: 20px 0;
  border: 3px solid #FFE4B5;
}

/* Comment Section */
.comment-bubble textarea {
  width: 100%;
  height: 120px;
  border: 2px solid #FFE4B5;
  border-radius: 20px;
  padding: 15px;
  margin-bottom: 15px;
  font-size: 16px;
  resize: none;
}

.submit-btn {
  background-color: #DEB887;
  color: white;
  border: none;
  border-radius: 25px;
  padding: 12px 30px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn:hover {
  background-color: #BC8F8F;
  transform: translateY(-2px);
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
  background-color: #DEB887;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(222, 184, 135, 0.3);
  transition: all 0.3s ease;
  z-index: 1000;
}

.chatbot-button:hover {
  transform: translateY(-3px);
  background-color: #BC8F8F;
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
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  position: fixed;
  bottom: 100px;
  right: 30px;
  display: flex;
  flex-direction: column;
}

.chatbot-header {
  padding: 15px 20px;
  background-color: #FFE4B5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chatbot-header h3 {
  margin: 0;
  color: #8B4513;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  color: #8B4513;
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
  min-height: 0;
  max-height: 100%;
}

@media (max-width: 768px) {
  .chatbot-window {
    width: 340px !important;
    height: 450px !important;
    bottom: 80px;
    right: 20px;
  }
}
</style>