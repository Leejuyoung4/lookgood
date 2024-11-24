<template>
  <div class="video-list">
    <div class="video" 
         v-for="video in paginatedVideos" 
         :key="video.vNo"
         @click="goToVideo(video.vNo)">
      <div class="video-thumbnail">
        <img :src="video.image" :alt="video.title" />
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
  background: var(--bg-color);
  box-sizing: border-box;
  height: auto;
  position: relative;
  overflow: hidden;
  padding-bottom: 50px;
  transition: background-color 0.3s ease;
}

.video-menu {
  display: flex;
  justify-content: center;
  gap: 70px;
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  top: 100px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-item img {
  width: 60px;
  height: auto;
  transition: filter 0.3s ease;
}

.menu-item span {
  margin-top: 10px;
  font-size: 20px;
  font-family: "Inter-Medium", sans-serif;
  font-weight: 500;
  color: var(--text-color);
  transition: color 0.3s ease;
}

.video-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-top: 400px;
  padding: 0 50px;
}

.video {
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, background-color 0.3s ease;
  border: 1px solid var(--border-color);
}

.video:hover {
  transform: translateY(-4px);
}

.video-thumbnail {
  width: 100%;
  position: relative;
  padding-top: 56.25%; /* 16:9 비율 */
  overflow: hidden;
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
  background: var(--bg-color);
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: var(--text-color);
}

.video-description {
  font-size: 14px;
  color: var(--text-color);
  opacity: 0.8;
  margin-bottom: 12px;
}

.video-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.instructor {
  font-size: 14px;
  font-weight: 500;
  color: #1a73e8;
}

.instructor-intro {
  font-size: 13px;
  color: #666;
  margin: 0;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.stats {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: var(--text-color);
  opacity: 0.7;
  margin-top: 8px;
}

.views, .upload-date {
  color: var(--text-color);
  opacity: 0.7;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  background-color: var(--bg-color);
  color: var(--text-color);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.pagination button:hover {
  background-color: var(--hover-color);
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading, .error {
  text-align: center;
  margin-top: 200px;
  font-size: 18px;
  color: var(--text-color);
}

.error {
  color: #ff4444;
}

:root.dark-mode .video-thumbnail img {
  filter: brightness(0.9);
}

:root.dark-mode .video {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

:root.dark-mode .menu-item img {
  filter: invert(1) brightness(0.8);
}

.menu-item.active img {
  filter: brightness(1.2);
}

:root.dark-mode .menu-item.active img {
  filter: invert(1) brightness(1);
}

.menu-item:hover img {
  transform: scale(1.1);
}

:root.dark-mode .menu-item:hover img {
  filter: invert(1) brightness(1);
}
</style>
