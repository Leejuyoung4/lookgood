<template>
  <div class="video-div">
    <!-- 메뉴바 추가 -->
    <div class="video-menu">
      <div class="menu-item" 
           v-for="(icon, index) in icons" 
           :key="index" 
           @click="filterVideos(icon.category)"
           :class="{ active: selectedCategory === icon.category }">
        <img :src="icon.src" />
        <span>{{ icon.name }}</span>
      </div>
    </div>

    <!-- 로딩 상태 표시 -->
    <div v-if="loading" class="loading">
      데이터를 불러오는 중...
    </div>

    <!-- 에러 메시지 표시 -->
    <div v-if="error" class="error">
      {{ error }}
    </div>

    <!-- 비디오 리스트 -->
    <div v-else class="video-list">
      <div class="video" 
           v-for="video in paginatedVideos" 
           :key="video.vNo"
           @click="goToVideo(video.vNo)">
        <div class="video-thumbnail">
          <img :src="`https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg`" :alt="video.title" />
        </div>
        <div class="video-content">
          <h3 class="video-title">{{ video.title || '제목 없음' }}</h3>
          <div class="video-info">
            <span class="instructor">{{ video.instructor || '강사 미정' }}</span>
            <div class="stats">
              <span class="views">조회수 {{ formatViews(video.views) }}회</span>
              <span class="upload-date">{{ formatDate(video.uploadDate) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 버튼 -->
    <div class="pagination" v-if="filteredVideos.length > itemsPerPage">
      <button @click="prevPage" :disabled="currentPage === 1">이전</button>
      <button @click="nextPage" :disabled="currentPage === totalPages">다음</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import allIcon from '@/assets/img/video/all-icon.png';
import healthIcon from '@/assets/img/video/health-icon.png';
import yogaIcon from '@/assets/img/video/yoga-icon.png';
import pilatesIcon from '@/assets/img/video/pilates-icon.png';
import dancingIcon from '@/assets/img/video/dancing-icon.png';
import boxingIcon from '@/assets/img/video/boxing-icon.png';
import ex from '@/assets/img/video/ex.png';

const router = useRouter();
const videos = ref([]);
const loading = ref(true);
const error = ref(null);

// 메뉴 아이콘 리스트
const icons = [
  { src: allIcon, name: '전체', category: 'all' },
  { src: healthIcon, name: '헬스', category: '헬스' },
  { src: yogaIcon, name: '요가', category: '요가' },
  { src: pilatesIcon, name: '필라테스', category: '필라테스' },
  { src: dancingIcon, name: '댄스', category: '댄스' },
  { src: boxingIcon, name: '복싱', category: '복싱' }
];

// 비디오 데이터 가져오기
const fetchVideos = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/videos');
    console.log('원본 데이터:', response.data);
    
    videos.value = response.data.map(video => ({
      id: video.vNo,
      vNo: video.vNo,
      title: video.vTitle,
      description: video.vDescription,
      image: `https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg`,
      category: video.vCategoryName,
      uploadDate: video.vUploadDate,
      views: video.vViews,
      instructor: video.vInstructor,
      instructorIntro: video.vInstructorIntro,
      likes: video.vLikes,
      hates: video.vHates,
      savedNo: video.savedNo,
      videoId: video.videoId
    }));

    console.log('변환된 데이터:', videos.value);
  } catch (err) {
    error.value = '비디오 데이터를 불러오는데 실패했습니다.';
    console.error('Error fetching videos:', err);
  } finally {
    loading.value = false;
  }
};

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return '날짜 없음';
  
  // 이미 "~전" 형식으로 온 경우 그대로 반환
  if (dateString.includes('전')) {
    return dateString;
  }
  
  try {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;

    // 유효한 날짜가 아닌 경우
    if (isNaN(date.getTime())) {
      return dateString; // 원본 문자열 반환
    }

    // 1일 이내
    if (diff < 24 * 60 * 60 * 1000) {
      const hours = Math.floor(diff / (60 * 60 * 1000));
      return `${hours}시간 전`;
    }
    
    // 7일 이내
    if (diff < 7 * 24 * 60 * 60 * 1000) {
      const days = Math.floor(diff / (24 * 60 * 60 * 1000));
      return `${days}일 전`;
    }
    
    // 30일 이내
    if (diff < 30 * 24 * 60 * 60 * 1000) {
      const weeks = Math.floor(diff / (7 * 24 * 60 * 60 * 1000));
      return `${weeks}주 전`;
    }

    // 1년 이내
    if (diff < 365 * 24 * 60 * 60 * 1000) {
      const months = Math.floor(diff / (30 * 24 * 60 * 60 * 1000));
      return `${months}개월 전`;
    }

    // 1년 이상
    const years = Math.floor(diff / (365 * 24 * 60 * 60 * 1000));
    return `${years}년 전`;

  } catch (e) {
    console.log('날짜 처리:', dateString);
    return dateString; // 에러 발생 시 원본 반환
  }
};

// 조회수 포맷팅 함수 추가
const formatViews = (views) => {
  if (!views) return '0';
  if (views >= 10000) {
    return `${Math.floor(views / 10000)}만`;
  } else if (views >= 1000) {
    return `${Math.floor(views / 1000)}천`;
  }
  return views.toString();
};

// 비디오 상세 페이지로 이동
const goToVideo = (videoNo) => {
  console.log('이동할 비디오 번호:', videoNo); // 디버깅용
  router.push(`/videos/${videoNo}`);
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(() => {
  fetchVideos();
});

// 페이지네이션 상태
const currentPage = ref(1);
const itemsPerPage = 8;
const selectedCategory = ref('all');

const filteredVideos = computed(() => {
  console.log('Current selected category:', selectedCategory.value);
  console.log('Available categories:', [...new Set(videos.value.map(v => v.category))]);
  
  if (selectedCategory.value === 'all') {
    return videos.value;
  }
  
  const filtered = videos.value.filter(video => {
    const videoCategory = video.category;
    const selectedCat = selectedCategory.value;
    console.log(`Comparing video category: ${videoCategory} with selected: ${selectedCat}`);
    return videoCategory === selectedCat;
  });

  console.log('Filtered results:', filtered.length);
  return filtered;
});

const totalPages = computed(() => Math.ceil(filteredVideos.value.length / itemsPerPage));

const paginatedVideos = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredVideos.value.slice(start, end);
});

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

const filterVideos = (category) => {
  console.log('Filtering by category:', category);
  selectedCategory.value = category;
  currentPage.value = 1;
};

// formatDate 함수를 template에서 사용할 수 있도록 내보내기
defineExpose({
  formatDate
});

// 템플릿에서 현재 선택된 카테고리 시각적 표시를 위한 computed 속성 추가
const isActiveCategory = (category) => {
  return selectedCategory.value === category;
};
</script>

<style scoped>
.video-div {
  background: #FFFDF7;
  min-height: 100vh;
  padding: 40px;
  position: relative;
  max-width: 1400px;
  margin: 0 auto;
}

.video-menu {
  display: flex;
  justify-content: center;
  gap: 30px;
  background: white;
  padding: 25px 40px;
  margin-bottom: 40px;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #FFE082;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 10px 20px;
  border-radius: 12px;
  min-width: 100px;
}

.menu-item img {
  width: 40px;
  height: 40px;
  transition: all 0.3s ease;
  filter: brightness(1.2);
}

.menu-item span {
  margin-top: 8px;
  font-size: 15px;
  font-weight: 500;
  color: #666;
}

.menu-item:hover {
  background: #FFF8E1;
}

.menu-item.active {
  background: #FFF8E1;
  border-bottom: 3px solid #FFC107;
}

.video-list {
  margin-top: 200px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.video {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.2s ease;
  border: 1px solid #FFE082;
}

.video:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.2);
}

.video-thumbnail {
  position: relative;
  padding-top: 56.25%;
  background: #f5f5f5;
}

.video-thumbnail img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-content {
  padding: 16px;
}

.video-title {
  color: #333;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.instructor {
  color: #FFA000;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.stats {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 13px;
}

.pagination {
  margin-top: 40px;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  gap: 12px;
}

.pagination button {
  background: white;
  color: #333;
  border: 2px solid #FFE082;
  padding: 8px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.pagination button:hover:not(:disabled) {
  background: #FFF8E1;
  border-color: #FFC107;
}

.pagination button:disabled {
  background: #f5f5f5;
  border-color: #e0e0e0;
  color: #999;
}

/* 다크모드 */
:root.dark-mode .video-div {
  background: #1a1a1a;
}

:root.dark-mode .video-menu {
  background: #242424;
  border-bottom-color: #FFC107;
}

:root.dark-mode .video {
  background: #242424;
  border-color: #FFC107;
}

:root.dark-mode .video-title {
  color: #fff;
}

:root.dark-mode .stats {
  color: #bbb;
}

:root.dark-mode .menu-item span {
  color: #fff;
}

:root.dark-mode .menu-item:hover,
:root.dark-mode .menu-item.active {
  background: rgba(255, 193, 7, 0.1);
}
</style>
