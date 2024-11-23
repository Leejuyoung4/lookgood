<template>
  <div class="mypage-container">
    <!-- 저장한 영상 섹션 -->
    <section class="saved-videos-section">
      <h2>저장한 영상</h2>
      <div v-if="savedVideos.length > 0" class="video-grid">
        <div v-for="video in savedVideos" :key="video.vno" class="video-card">
          <router-link :to="`/videos/${video.vno}`" class="video-link">
            <div class="thumbnail">
              <img 
                :src="`https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg`" 
                :alt="video.vtitle"
              >
            </div>
            <div class="video-info">
              <h3 class="video-title">{{ video.vtitle }}</h3>
              <p class="instructor">{{ video.vinstructor }}</p>
              <div class="video-meta">
                <span class="saved-date">저장일: {{ formatDate(video.savedAt) }}</span>
              </div>
            </div>
          </router-link>
          <button @click="unsaveVideo(video.vno)" class="unsave-btn">
            저장 취소
          </button>
        </div>
      </div>
      <div v-else class="empty-state">
        <p>저장한 영상이 없습니다.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const savedVideos = ref([]);

// 저장된 영상 로드
const loadSavedVideos = () => {
  try {
    const savedVideosData = localStorage.getItem('savedVideos');
    if (savedVideosData) {
      savedVideos.value = JSON.parse(savedVideosData);
    }
  } catch (error) {
    console.error('저장된 영상 로드 실패:', error);
    savedVideos.value = [];
  }
};

// 저장 취소
const unsaveVideo = (vno) => {
  try {
    const savedVideosData = JSON.parse(localStorage.getItem('savedVideos') || '[]');
    const updatedVideos = savedVideosData.filter(video => video.vno !== vno);
    localStorage.setItem('savedVideos', JSON.stringify(updatedVideos));
    loadSavedVideos(); // 목록 새로고침
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
  loadSavedVideos();
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
  -webkit-line-clamp: 2;
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
</style> 