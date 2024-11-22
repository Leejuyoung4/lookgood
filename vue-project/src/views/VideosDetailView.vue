<template>
  <div class="video-detail">
    <div v-if="video" class="content-wrapper">
      <!-- 메인 비디오 섹션 -->
      <div class="primary-content">
        <div class="video-container">
          <iframe :src="`https://www.youtube.com/embed/${video.videoId}`" frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen></iframe>
        </div>

        <!-- 비디오 제목 및 정보 -->
        <div class="video-info">
          <h1 class="video-title">{{ video.vtitle }}</h1>
          <div class="video-meta">
            <span class="views">조회수 {{ formatViews(video.vviews) }}회</span>1
          </div>

          <!-- 액션 버튼 -->
          <div class="action-bar">
            <div class="action-buttons">
              <button class="action-btn share">
                <i class="bi bi-share"></i>
                공유하기
              </button>
              <button class="action-btn save">
                <i class="bi bi-bookmark"></i>
                저장하기
              </button>
              <button class="action-btn like">
                <i class="bi bi-hand-thumbs-up"></i>
                좋아요
              </button>
            </div>
          </div>

          <!-- 비디오 설명 -->
          <div class="description">
            <p>{{ video.vdescription }}</p>
          </div>

          <!-- 강사 정보 -->
          <div class="about-speaker">
            <h2>강사 소개</h2>
            <div class="speaker-info">
              <div class="speaker-avatar"></div>
              <div class="speaker-details">
                <h3>{{ video.vinstructor }}</h3>
                <p class="speaker-bio">{{ video.vinstructorIntro }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 사이드바 - 다음 시청 영상 -->
      <div class="sidebar">
        <h2 class="watch-next">다음 동영상</h2>
        <div v-if="recommendedVideos.length > 0" class="next-videos">
          <div v-for="recommendedVideo in recommendedVideos" :key="recommendedVideo.vno" class="video-item"
            @click="handleVideoClick(recommendedVideo.vno)">
            <div class="thumbnail">
              <img :src="`https://img.youtube.com/vi/${recommendedVideo.videoId}/mqdefault.jpg`"
                :alt="recommendedVideo.vtitle">
            </div>
            <div class="video-info">
              <h3 class="video-title">{{ recommendedVideo.vtitle }}</h3>
              <p class="speaker">{{ recommendedVideo.vinstructor }}</p>
              <p class="meta">
                <span class="views">조회수 {{ formatViews(recommendedVideo.vviews) }}회</span>
                <span class="dot">•</span>
                <span class="date">{{ formatDate(recommendedVideo.vuploadDate) }}</span>
              </p>
            </div>
          </div>
        </div>
        <div v-else class="no-recommendations">
          <p>추천 동영상을 불러오는 중...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const video = ref(null)
const recommendedVideos = ref([])

const fetchVideoDetails = async () => {
  try {
    const response = await axios.get(`/api/videos/${route.params.id}`);
    video.value = response.data;
    // 비디오 데이터를 받아온 후에 추천 비디오 호출
    await fetchRecommendedVideos();
  } catch (error) {
    console.error('비디오 로딩 실패:', error);
  }
};

const fetchRecommendedVideos = async () => {
  try {
    if (!video.value) return; // video가 없으면 리턴

    const response = await axios.get('/api/videos/recommended', {
      params: {
        currentVideoId: route.params.id,
        category: video.value.vCategoryName,
        limit: 5
      }
    });

    console.log('추천 비디오 응답:', response.data); // 디버깅용
    recommendedVideos.value = response.data;
  } catch (error) {
    console.error('추천 비디오 로딩 실패:', error);
    recommendedVideos.value = []; // 에러 시 빈 배열로 설정
  }
};

const formatViews = (views) => {
  if (!views) return '0';
  if (views >= 10000) {
    return `${Math.floor(views / 10000)}만`;
  } else if (views >= 1000) {
    return `${Math.floor(views / 1000)}천`;
  }
  return views.toString();
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const now = new Date();
  const diff = now - date;

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
  // 1달 이내
  if (diff < 30 * 24 * 60 * 60 * 1000) {
    const weeks = Math.floor(diff / (7 * 24 * 60 * 60 * 1000));
    return `${weeks}주 전`;
  }
  // 그 외
  return `${date.getFullYear()}. ${date.getMonth() + 1}. ${date.getDate()}`;
};

const handleVideoClick = async (videoNo) => {
  try {
    // 현재 페이지와 다른 경우에만 라우팅
    if (route.params.id !== videoNo.toString()) {
      await router.push(`/videos/${videoNo}`);
      // 페이지 새로고침 없이 데이터 갱신
      await fetchVideoDetails();
    }
  } catch (error) {
    console.error('비디오 이동 중 오류:', error);
  }
};

onMounted(() => {
  fetchVideoDetails()
})

// route params가 변경될 때마다 데이터 다시 가져오기
watch(
  () => route.params.id,
  async (newId) => {
    if (newId) {
      await fetchVideoDetails();
    }
  }
);
</script>

<style scoped>
.video-detail {
  background: #FFF5E6;
  min-height: 100vh;
}

.content-wrapper {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 400px;
  gap: 40px;
  max-width: 1600px;
  margin: 0 auto;
  padding: 30px;
  background: #FFF5E6;
}

.video-container {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
  overflow: hidden;
  background: #000;
  margin-bottom: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-container iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.video-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #4A4A4A;
  line-height: 1.3;
}

.video-meta {
  font-size: 18px;
  color: #8B4513;
  margin-bottom: 20px;
}

.bullet {
  margin: 0 8px;
  color: #DEB887;
}

.action-bar {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin: 25px 0;
  padding: 20px 0;
  border-top: 1px solid #DEB887;
  border-bottom: 1px solid #DEB887;
}

.action-buttons {
  display: flex;
  gap: 15px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  background: #DEB887;
  color: white;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #CD853F;
  transform: translateY(-2px);
}

.action-btn i {
  font-size: 18px;
}

.description {
  font-size: 18px;
  line-height: 1.8;
  color: #4A4A4A;
  margin: 30px 0;
  padding: 20px;
  background: #FAEBD7;
  border-radius: 8px;
}

.about-speaker {
  margin-top: 40px;
  padding: 30px;
  background: #FAEBD7;
  border-radius: 8px;
}

.about-speaker h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 25px;
  color: #8B4513;
}

.speaker-info {
  display: flex;
  gap: 25px;
}

.speaker-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: #DEB887;
}

.speaker-details h3 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #4A4A4A;
}

.speaker-bio {
  font-size: 17px;
  line-height: 1.6;
  color: #666;
}

.sidebar {
  padding: 20px;
  background: #FAEBD7;
  border-radius: 8px;
  height: fit-content;
}

.watch-next {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #DEB887;
  color: #8B4513;
}

.next-videos {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.video-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-radius: 8px;
}

.video-item:hover {
  background: rgba(0, 0, 0, 0.05);
}

.thumbnail {
  position: relative;
  width: 168px;
  height: 94px;
  border-radius: 8px;
  overflow: hidden;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-info {
  flex: 1;
  min-width: 0;
  /* 텍스트 오버플로우 방지 */
}

.video-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #0f0f0f;
}

.speaker {
  font-size: 12px;
  color: #606060;
  margin-bottom: 4px;
}

.meta {
  font-size: 12px;
  color: #606060;
}

.meta span:not(:last-child)::after {
  content: "•";
  margin: 0 4px;
}

.views {
  color: #606060;
}

.date {
  color: #606060;
}

.no-recommendations {
  padding: 20px;
  text-align: center;
  color: #8B4513;
}

@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .sidebar {
    margin-top: 30px;
  }
}
</style>