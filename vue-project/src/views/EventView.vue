<template>
  <div class="event-div">
    <div class="event-title">전체</div>
    <hr class="divider">

    <div class="event-top">
      <div>총 <span class="total-count">{{ filteredEvents.length }}</span> 건</div>

      <div class="board-tab">
        <button class="tab-item" :class="{ active: sortBy === 'latest' }" @click="setSortBy('latest')">최신순</button>
        <!-- <span class="divider-vertical">|</span> -->
        <button class="tab-item" :class="{ active: sortBy === 'distance' }" @click="setSortBy('distance')">거리순</button>
        <!-- <span class="divider-vertical">|</span> -->
        <button class="tab-item" :class="{ active: sortBy === 'likes' }" @click="setSortBy('likes')">인기순</button>
      </div>
    </div>
    <hr class="divider thin">

    <div class="content-area">
      <div class="event-list-container">
        <transition-group name="fade" tag="div">
          <div v-for="event in filteredEvents" :key="event.eNo" class="event-item" @click="navigateToEvent(event.eNo)">
            <img :src="event.image || 'path/to/default-image.jpg'" alt="이벤트 이미지" class="event-image" />
            <div class="event-info">
              <h3>{{ event.eTitle }}</h3>
              <p>{{ event.eBigCity }} - {{ event.eSmallCity }}</p>
              <p>{{ event.eShort }}</p>
              <p>{{ formatDate(event.eStartDate) }} ~ {{ formatDate(event.eEndDate) }}</p>
              <p v-if="sortBy === 'distance'" class="distance-info">
                현재 위치에서 {{ event.distance }}
              </p>
            </div>
          </div>
        </transition-group>
      </div>

      <div class="tag-filter">
        <div class="region-filter">
          <h4>지역 선택</h4>
          <div class="tags-list">
            <button v-for="region in uniqueRegions" :key="region" class="tag-filter-item"
              :class="{ active: selectedRegion === region }" @click="filterByRegion(region)">
              {{ region }}
            </button>
          </div>
        </div>

        <div v-if="selectedRegion && selectedRegion !== '전체'" class="subregion-filter">
          <h4>세부 지역 선택</h4>
          <div class="tags-list">
            <button v-for="subregion in filteredSubregions" :key="subregion" class="tag-filter-item"
              :class="{ active: selectedSubregion === subregion }" @click="filterBySubregion(subregion)">
              {{ subregion }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 로딩 인디케이터 -->
    <div v-if="isLoading" class="loading-overlay">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import { ref, computed, onMounted } from "vue";
import { useRouter } from 'vue-router';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
console.log('API URL:', API_BASE_URL); // URL 확인
const router = useRouter();

// 이벤트 상세 페이지로 이동하는 함수
const navigateToEvent = (e_no) => {
  console.log('이동할 이벤트 번호:', e_no); // 디버깅용
  if (!e_no) {
    console.error('이벤트 번호가 없습니다');
    return;
  }
  router.push({
    name: 'EventDetail',
    params: { id: String(e_no) }
  });
};

// 상태 관리를 위한 ref 추가
const eventList = ref([]);
const sortBy = ref("latest");
const selectedRegion = ref("");
const selectedSubregion = ref("");
const eventDistances = ref(new Map());
const isLoading = ref(false); // 로딩 상태 추가
const userLocation = ref({
  lat: 36.3503354,  // 대전 유성구 동서대로 98-39
  lng: 127.3848378
});

// 거리 계산 함수
const calculateDistance = (lat1, lon1, lat2, lon2) => {
  const R = 6371; // 지구의 반경 (km)
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
    Math.sin(dLon/2) * Math.sin(dLon/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  return R * c; // 거리 (km)
};

// 이벤트 거리 계산
const calculateEventDistances = async () => {
  try {
    for (const event of eventList.value) {
      const geocoder = new kakao.maps.services.Geocoder();
      const address = `${event.eBigCity} ${event.eSmallCity}`;
      
      await new Promise((resolve) => {
        geocoder.addressSearch(address, (result, status) => {
          if (status === kakao.maps.services.Status.OK) {
            const distance = calculateDistance(
              userLocation.value.lat,
              userLocation.value.lng,
              parseFloat(result[0].y),
              parseFloat(result[0].x)
            );
            eventDistances.value.set(event.eNo, distance);
          }
          resolve();
        });
      });
    }
  } catch (error) {
    console.error('거리 계산 중 오류:', error);
  }
};

// 이벤트 데이터 가져오기
onMounted(async () => {
  try {
    // axios 인스턴스 생성하여 credentials 설정 제거
    const response = await axios({
      method: 'get',
      url: '/api/event',
      withCredentials: false
    });
    
    console.log('받아온 데이터:', response.data);
    
    if (response.data) {
      eventList.value = response.data.map((event) => ({
        ...event,
        eStartDate: new Date(event.eStartDate),
        eEndDate: new Date(event.eEndDate),
      }));
    }
    
  } catch (error) {
    console.error("데이터를 가져오는 중 오류 발생:", error);
    eventList.value = [];
  }

  // 카카오맵 API 로드
  if (!window.kakao?.maps) {
    const script = document.createElement('script');
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=YOUR_KAKAO_API_KEY&libraries=services`;
    script.async = true;
    await new Promise((resolve) => {
      script.onload = resolve;
      document.head.appendChild(script);
    });
  }
});

// 날짜 포맷 변경 함수
const formatDate = (date) => {
  return new Intl.DateTimeFormat("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" }).format(date);
};

// 거리 계산 및 정렬 함수
const setSortBy = async (criteria) => {
  try {
    sortBy.value = criteria;
    isLoading.value = true; // 로딩 시작

    if (criteria === "distance") {
      await calculateEventDistances();
      eventList.value.sort((a, b) => {
        const distanceA = eventDistances.value.get(a.eNo) || Infinity;
        const distanceB = eventDistances.value.get(b.eNo) || Infinity;
        return distanceA - distanceB;
      });

      // 거리 정보 표시 추가
      eventList.value = eventList.value.map(event => ({
        ...event,
        distance: eventDistances.value.get(event.eNo)
          ? `${eventDistances.value.get(event.eNo).toFixed(1)}km`
          : '거리 정보 없음'
      }));
    } else if (criteria === "latest") {
      eventList.value.sort((a, b) => b.eStartDate - a.eStartDate);
    } else if (criteria === "likes") {
      eventList.value.sort((a, b) => (b.likes || 0) - (a.likes || 0));
    }
  } catch (error) {
    console.error('거리순 정렬 실패:', error);
  } finally {
    isLoading.value = false; // 로딩 종료
  }
};

// 지역 필터링 로직
const uniqueRegions = computed(() => {
  return ["전체", ...new Set(eventList.value.map((item) => item.eBigCity))];
});

const filteredSubregions = computed(() => {
  if (!selectedRegion.value || selectedRegion.value === "전체") return [];
  return ["전체", ...new Set(eventList.value.filter((item) => item.eBigCity === selectedRegion.value).map((item) => item.eSmallCity))];
});

const filteredEvents = computed(() => {
  let events = eventList.value;

  if (selectedRegion.value && selectedRegion.value !== "전체") {
    events = events.filter((item) => item.eBigCity === selectedRegion.value);
  }
  if (selectedSubregion.value && selectedSubregion.value !== "전체") {
    events = events.filter((item) => item.eSmallCity === selectedSubregion.value);
  }

  return events;
});

const filterByRegion = (region) => {
  selectedRegion.value = region === selectedRegion.value ? "" : region;
  selectedSubregion.value = "";
};

const filterBySubregion = (subregion) => {
  selectedSubregion.value = subregion === selectedSubregion.value ? "" : subregion;
};
</script>



<style scoped>
.event-div {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.event-title {
  font-size: 32px;
  margin-bottom: 15px;
  font-weight: 600;
}

.event-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0;
  width: 66%;
}

/* 구분선 스타일 */
.divider {
  border: none;
  border-top: 2px solid #333;
  /* 상단 구분선 더 두껍게 */
  margin: 8px 0;
  width: 66%;
}

.divider.thin {
  border-top: 1px solid #ddd;
  /* 하단 구분선 얇게 */
}

/* 컨텐츠 영역 */
.content-area {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

/* 이벤트 리스트 */
.event-list-container {
  width: 65%;
}

.event-item {
  cursor: pointer;
  /* 커서 스타일 추가 */
  display: flex;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-decoration: none;
  color: inherit;
}

.event-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.event-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

/* 태그 필터 개선 */
.tag-filter {
  width: 25%;
  padding: 25px;
  background: #f8f9fa;
  border-radius: 15px;
  position: sticky;
  top: 20px;
  height: fit-content;
}

.tag-filter-item {
  background: none;
  border: none;
  display: inline-block;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-size: 14px;
  color: #666;
}

.tag-filter-item:hover {
  background-color: f8cd71(0, 123, 255, 0.1);
  color: #f8cd71;
}

.tag-filter-item.active {
  background-color: #ffd987;
  color: white;
}



/* 정렬 탭 */
.board-tab {
  display: flex;
  align-items: center;
  gap: 25px; /* 간격 유지 */
}

.tab-item {
  background: none;
  border: none;
  padding: 10px 15px; /* 버튼 내부 여백 */
  font-size: 18px; /* 폰트 크기 */
  cursor: pointer;
  position: relative;
  color: #aaa; /* 기본 색상 */
  transition: color 0.3s ease, transform 0.2s ease; /* 부드러운 전환 효과 */
}

.tab-item:hover {
  color: #555; /* 호버 색상 */
  transform: translateY(-2px); /* 살짝 위로 이동 */
}

.tab-item.active {
  color: #000; /* 활성화된 상태의 색상 */
  font-weight: bold; /* 두껍게 표시 */
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -5px; /* 하단 위치 */
  left: 10%; /* 양쪽 여백 */
  right: 10%; /* 양쪽 여백 */
  height: 3px; /* 밑줄 두께 */
  background-color: #ffd987; /* 활성화 밑줄 색상 */
  border-radius: 2px; /* 둥근 모서리 */
  transition: width 0.3s ease, background-color 0.3s ease; /* 부드러운 효과 */
}


/* .divider-vertical {
  color: #ddd;
} */

/* 애니메이션 */
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .event-top {
    width: 100%;
  }

  .divider {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .content-area {
    flex-direction: column;
  }

  .event-list-container,
  .tag-filter {
    width: 100%;
  }

  .event-image {
    width: 100px;
    height: 100px;
  }
}

/* 로딩 오버레이 스타일 */
.loading-overlay {
  position: fixed;
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
  border-top: 4px solid #ffd987;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 거리 정보 스타일 */
.distance-info {
  color: #666;
  font-size: 0.9em;
  margin-top: 5px;
}
</style>
