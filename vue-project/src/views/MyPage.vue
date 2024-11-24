<template>
  <div class="mypage-container">
    <!-- 저장한 영상 섹션 -->
    <section class="saved-videos-section">
      <h2>저장한 영상</h2>
      <div v-if="isLoading" class="loading">
        로딩 중...
      </div>
      <div v-else-if="error" class="error">
        {{ error }}
      </div>
      <div v-else-if="savedVideos.length === 0" class="no-videos">
        저장된 영상이 없습니다.
      </div>
      <div v-else class="video-grid">
        <div v-for="video in savedVideos" :key="video.vNo" class="video-card">
          <router-link :to="`/videos/${video.vNo}`" class="video-link">
            <div class="thumbnail">
              <img 
                :src="`https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg`" 
                :alt="video.vTitle"
              >
            </div>
            <div class="video-info">
              <h3 class="video-title">{{ video.vTitle }}</h3>
              <p class="instructor">{{ video.vInstructor }}</p>
              <div class="video-meta">
                <span class="saved-date">저장일: {{ formatDate(video.savedDate) }}</span>
              </div>
            </div>
          </router-link>
          <button @click="unsaveVideo(video.vNo)" class="unsave-btn">
            저장 취소
          </button>
        </div>
      </div>
    </section>
  </div>
  
  <!-- 로그인 모달 추가 -->
  <div v-if="isLoginModalOpen" class="overlay">
    <LoginViewModal @close="closeLoginModal" @login-success="handleLoginSuccess" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import LoginViewModal from '@/views/LoginViewModal.vue';
import axios from 'axios';

const router = useRouter();
const savedVideos = ref([]);
const isLoginModalOpen = ref(false);
const isLoading = ref(false);
const error = ref(null);

// 저장된 영상 로드 수정
const fetchSavedVideos = async () => {
  try {
    isLoading.value = true;
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    
    if (!userInfo || !userInfo.userNo) {
      error.value = '로그인이 필요합니다.';
      return;
    }

    const response = await axios.get(`/api/saved-videos/user/${userInfo.userNo}`);
    console.log('저장된 비디오 응답:', response.data); // 디버깅용

    if (response.data.success) {
      savedVideos.value = response.data.videos;
    } else {
      error.value = response.data.message || '저장된 영상을 불러오는데 실패했습니다.';
    }
  } catch (err) {
    console.error('저장된 비디오 조회 중 오류:', err);
    error.value = '저장된 영상을 불러오는데 실패했습니다.';
  } finally {
    isLoading.value = false;
  }
};

// 로그인 모달 관련 함수 추가
const closeLoginModal = () => {
  isLoginModalOpen.value = false;
};

const handleLoginSuccess = () => {
  closeLoginModal();
  fetchSavedVideos(); // 로그인 성공 후 영상 목록 로드
};

// 저장 취소
const unsaveVideo = (vno) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('user'));
    if (!userInfo) return;
    
    const savedVideosData = JSON.parse(localStorage.getItem(`savedVideos_${userInfo.userId}`) || '[]');
    const updatedVideos = savedVideosData.filter(video => video.vno !== vno);
    localStorage.setItem(`savedVideos_${userInfo.userId}`, JSON.stringify(updatedVideos));
    fetchSavedVideos(); // 목록 새로고침
  } catch (error) {
    console.error('저장 취소 실패:', error);
    alert('저장 취소 중 오류가 발생했습니다.');
  }
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  const date = new Date(dateString);
  return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
};

onMounted(() => {
  fetchSavedVideos();
});
</script>

<style scoped>
.mypage-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

h2 {
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #DEB887;
}

.user-info p {
  margin: 10px 0;
  color: #666;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.video-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: transform 0.2s;
}

.video-card:hover {
  transform: translateY(-4px);
}

.video-link {
  text-decoration: none;
  color: inherit;
}

.thumbnail {
  width: 100%;
  aspect-ratio: 16/9;
  overflow: hidden;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-info {
  padding: 15px;
}

.video-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.instructor {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #888;
}

.unsave-btn {
  width: 100%;
  padding: 8px;
  background: #ff6b6b;
  color: white;
  border: none;
  cursor: pointer;
  transition: background 0.2s;
}

.unsave-btn:hover {
  background: #ff5252;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
}

@media (max-width: 768px) {
  .video-grid {
    grid-template-columns: 1fr;
  }
}

/* 모달 오버레이 스타일 추가 */
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
</style> 