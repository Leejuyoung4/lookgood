<template>
  <div class="mypage-container">
    <!-- 사용자 정보 섹션 -->
    <section class="user-section">
      <h2>내 정보</h2>
      <div class="user-info" v-if="userInfo">
        <p><strong>이름:</strong> {{ userInfo.userName }}</p>
        <p><strong>이메일:</strong> {{ userInfo.email }}</p>
        <p><strong>가입일:</strong> {{ formatDate(userInfo.joinDate) }}</p>
      </div>
    </section>

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
                <span class="views">조회수 {{ video.vviews }}회</span>
                <span class="saved-date">{{ formatDate(video.savedDate) }}</span>
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
import axios from 'axios';

const router = useRouter();
const userInfo = ref(null);
const savedVideos = ref([]);

// 사용자 정보 로드
const loadUserInfo = async () => {
  try {
    const response = await axios.get('/api/users/info');
    userInfo.value = response.data;
  } catch (error) {
    console.error('사용자 정보 로드 실패:', error);
    if (error.response?.status === 401) {
      alert('로그인이 필요합니다.');
      router.push('/login');
    }
  }
};

// 저장된 영상 로드
const loadSavedVideos = async () => {
  try {
    const response = await axios.get('/api/videos/saved');
    savedVideos.value = response.data;
  } catch (error) {
    console.error('저장된 영상 로드 실패:', error);
  }
};

// 저장 취소
const unsaveVideo = async (vno) => {
  try {
    await axios.delete(`/api/videos/${vno}/save`);
    await loadSavedVideos(); // 목록 새로고침
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
  loadUserInfo();
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