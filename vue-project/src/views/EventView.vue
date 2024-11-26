<template>
  <div class="event-div">
    <div class="event-title">전체</div>
    <hr class="divider">

    <div class="event-top">
      <div>총 <span class="total-count">{{ filteredEvents.length }}</span> 건</div>

      <div class="board-tab">
        <button class="tab-item" :class="{ active: sortBy === 'latest' }" @click="setSortBy('latest')">최신순</button>
        <button class="tab-item" :class="{ active: sortBy === 'distance' }" @click="setSortBy('distance')">거리순</button>
      </div>
    </div>
    <hr class="divider thin">

    <div class="content-area">
      <div class="left-content">
        <div class="event-list-container">
          <transition-group name="fade" tag="div">
            <div v-for="event in paginatedEvents" :key="event.eNo" class="event-item" @click="navigateToEvent(event.eNo)">
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

        <div class="pagination" v-if="totalPages > 1">
          <button 
            class="page-button"
            :disabled="currentPage === 1"
            @click="currentPage = Math.max(1, currentPage - 1)"
          >
            이전
          </button>
          
          <div class="page-numbers">
            <button
              v-for="pageNum in displayedPages"
              :key="pageNum"
              @click="currentPage = pageNum"
              class="page-number"
              :class="{ active: currentPage === pageNum }"
            >
              {{ pageNum }}
            </button>
          </div>

          <button 
            class="page-button"
            :disabled="currentPage === totalPages"
            @click="currentPage = Math.min(totalPages, currentPage + 1)"
          >
            다음
          </button>
        </div>
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

// 상단에 현재 날짜 상태 추가
const currentDate = ref(new Date());

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
    const response = await axios({
      method: 'get',
      url: '/api/event',
      withCredentials: false
    });
    
    if (response.data) {
      // 원본 데이터 확인
      console.log('원본 데이터 첫번째 항목:', response.data[0]);
      
      eventList.value = response.data.map(event => ({
        ...event,
        eStartDate: new Date(event.eStartDate),
        eEndDate: new Date(event.eEndDate)
      }));
      
      // 변환된 데이터 확인
      console.log('변환된 데이터 첫번째 항목:', eventList.value[0]);
      console.log('시작일 타입:', typeof eventList.value[0].eStartDate);
      console.log('시작일이 Date 객체인지:', eventList.value[0].eStartDate instanceof Date);
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
    isLoading.value = true;

    let filteredList = [...eventList.value];
    console.log('전체 이벤트 수:', filteredList.length);

    switch (criteria) {
      case "distance":
        console.log('거리순 정렬 시작');
        await calculateEventDistances();
        filteredList.sort((a, b) => {
          const distanceA = eventDistances.value.get(a.eNo) || Infinity;
          const distanceB = eventDistances.value.get(b.eNo) || Infinity;
          return distanceA - distanceB;
        });
        break;

      case "latest":
        console.log('최신순 정렬 시작');
        filteredList.sort((a, b) => new Date(b.eStartDate) - new Date(a.eStartDate));
        break;
    }

    console.log('정렬 완료:', filteredList[0]?.eTitle);
    eventList.value = filteredList;

    if (criteria === "distance") {
      eventList.value = eventList.value.map(event => ({
        ...event,
        distance: eventDistances.value.get(event.eNo)
          ? `${eventDistances.value.get(event.eNo).toFixed(1)}km`
          : '거리 정보 없음'
      }));
    }
  } catch (error) {
    console.error('정렬 실패:', error);
  } finally {
    isLoading.value = false;
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

// 페이지네이션 련 상태
const currentPage = ref(1);
const itemsPerPage = 8;

// 페이지네이션된 이벤트 목록
const paginatedEvents = computed(() => {
  const startIndex = (currentPage.value - 1) * itemsPerPage;
  return filteredEvents.value.slice(startIndex, startIndex + itemsPerPage);
});

// 총 페이지 수 계산
const totalPages = computed(() => 
  Math.ceil(filteredEvents.value.length / itemsPerPage)
);

// 표시할 페이지 번호 계산
const displayedPages = computed(() => {
  const delta = 2;
  const range = [];
  const maxPages = totalPages.value;
  const currentPageValue = currentPage.value;

  for (
    let i = Math.max(2, currentPageValue - delta);
    i <= Math.min(maxPages - 1, currentPageValue + delta);
    i++
  ) {
    range.push(i);
  }

  if (currentPageValue - delta > 2) {
    range.unshift('...');
  }
  if (currentPageValue + delta < maxPages - 1) {
    range.push('...');
  }

  if (maxPages > 1) {
    range.unshift(1);
    if (maxPages > 1) {
      range.push(maxPages);
    }
  }

  return range;
});
</script>



<style scoped>
.event-div {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: var(--bg-color);
  color: var(--text-color);
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
  border-top: 2px solid var(--border-color);
  /* 상단 구분선 더 두껍게 */
  margin: 8px 0;
  width: 66%;
}

.divider.thin {
  border-top: 1px solid var(--border-color);
  /* 하단 구분선 얇게 */
}

/* 컨텐츠 영역 */
.content-area {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

/* 왼쪽 컨텐츠 영역 */
.left-content {
  width: 65%;
  display: flex;
  flex-direction: column;
}

/* 이벤트 리스트 컨테이너 */
.event-list-container {
  width: 100%;
  margin-bottom: 20px;
}

.event-item {
  cursor: pointer;
  /* 커서 스타일 추가 */
  display: flex;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid var(--border-color);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-decoration: none;
  color: inherit;
  background-color: var(--bg-color);
}

.event-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(255, 255, 255, 0.1);
  background-color: var(--hover-color);
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
  padding: 30px;
  background: var(--hover-color);
  border-radius: 15px;
  position: sticky;
  top: 20px;
  height: fit-content;
  border: 1px solid #FFE082;
  box-shadow: 0 2px 8px rgba(255, 193, 7, 0.1);
}

/* 지역 필터 섹션 */
.region-filter,
.subregion-filter {
  margin-bottom: 30px;
}

.region-filter h4,
.subregion-filter h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 15px;
  color: var(--text-color);
  padding-bottom: 10px;
  border-bottom: 2px solid #FFE082;
}

/* 태그 리스트 컨테이너 */
.tags-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  margin-top: 15px;
}

/* 태그 버튼 스타일 */
.tag-filter-item {
  width: 100%;
  padding: 10px;
  text-align: center;
  background: white;
  border: 1px solid #FFE082;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color);
  transition: all 0.2s ease;
  cursor: pointer;
}

.tag-filter-item:hover {
  background-color: #FFF8E1;
  border-color: #FFC107;
  transform: translateY(-2px);
}

.tag-filter-item.active {
  background-color: #FFC107;
  border-color: #FFC107;
  color: white;
  font-weight: 600;
}

/* 다크모드 대응 */
:root.dark-mode .tag-filter {
  background: #242424;
  border-color: #FFC107;
}

:root.dark-mode .tag-filter-item {
  background: #333;
  border-color: #FFC107;
  color: #fff;
}

:root.dark-mode .tag-filter-item:hover {
  background: rgba(255, 193, 7, 0.1);
}

:root.dark-mode .tag-filter-item.active {
  background: #FFC107;
  color: #242424;
}

/* 반응형 디자인 */
@media (max-width: 1200px) {
  .tags-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .tag-filter {
    width: 100%;
    margin-top: 20px;
    position: static;
  }

  .tags-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 480px) {
  .tags-list {
    grid-template-columns: repeat(2, 1fr);
  }
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
  color: var(--text-color-secondary, #aaa);
  transition: color 0.3s ease, transform 0.2s ease; /* 부드러운 전환 효과 */
}

.tab-item:hover {
  color: var(--text-color);
  transform: translateY(-2px); /* 살짝 위로 이동 */
}

.tab-item.active {
  color: var(--text-color);
  font-weight: bold; /* 두껍게 표시 */
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -5px; /* 하단 위치 */
  left: 10%; /* 양쪽 여백 */
  right: 10%; /* 양쪽 여백 */
  height: 3px; /* 밑줄 두께 */
  background-color: var(--accent-color, #ffd987); /* 활성화 밑줄 색상 */
  border-radius: 2px; /* 둥근 모서리 */
  transition: width 0.3s ease, background-color 0.3s ease; /* 부러운 효과 */
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

/* 반형 디자인 */
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

  .left-content,
  .tag-filter {
    width: 100%;
  }

  .event-image {
    width: 100px;
    height: 100px;
  }
}

/* 로딩 버레이 스타일 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #333;
  border-top: 4px solid var(--accent-color, #ffd987);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 거리 정보 스타일 */
.distance-info {
  color: var(--text-color-secondary, #666);
  font-size: 0.9em;
  margin-top: 5px;
}

/* 페이지네이션 스타일 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin: 40px 0;
  width: 100%;
}

.page-button {
  padding: 8px 16px;
  border: 2px solid #FFE082;
  background: white;
  color: #333;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
}

.page-button:hover:not(:disabled) {
  background: #FFF8E1;
  border-color: #FFC107;
  transform: translateY(-2px);
}

.page-button:disabled {
  background: #f5f5f5;
  border-color: #e0e0e0;
  color: #999;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 36px;
  height: 36px;
  border: 2px solid #FFE082;
  background: white;
  color: #333;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.page-number:hover:not(.active) {
  background: #FFF8E1;
  border-color: #FFC107;
  transform: translateY(-2px);
}

.page-number.active {
  background: #FFC107;
  border-color: #FFC107;
  color: white;
}

/* 다크모드 대응 */
:root.dark-mode .page-button,
:root.dark-mode .page-number {
  background: #242424;
  color: #fff;
  border-color: #FFC107;
}

:root.dark-mode .page-button:hover:not(:disabled),
:root.dark-mode .page-number:hover:not(.active) {
  background: rgba(255, 193, 7, 0.1);
}

:root.dark-mode .page-number.active {
  background: #FFC107;
  color: #242424;
}

:root.dark-mode .page-button:disabled {
  background: #1a1a1a;
  border-color: #333;
  color: #666;
}

/* 이벤트 아이템 스타일 수정 */
.event-item {
  border: 1px solid #FFE082;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.event-item:hover {
  border-color: #FFC107;
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.15);
}

/* 태그 필터 스타일 수정 */
.tag-filter-item.active {
  background-color: #FFC107;
  color: white;
}

.tag-filter-item:hover {
  background-color: #FFF8E1;
  color: #FFC107;
}
</style>
