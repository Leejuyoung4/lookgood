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

            <span class="views">조회수 {{ formatViewsDetail(video.vviews) }}회</span>

 
          </div>

          <!-- 액션 버튼 -->
          <div class="action-bar">
            <div class="action-buttons">
              <button class="action-btn share">
                <i class="bi bi-share"></i>
                공유하기
              </button>
              <button class="action-btn save" @click="handleSave">
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
                <span class="views">조회수 {{ formatViewsDetail(recommendedVideo.vviews) }}회</span>
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
const isSaved = ref(false)

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

const formatViewsDetail = (views) => {
  if (!views) return '0';
  // 천 단위 콤마 추가
  return views.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
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

// 저장하기 함수 수정
const handleSave = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo || !userInfo.userNo) {
      alert('로그인이 필요한 서비스입니다.');
      return;
    }

    const response = await axios.post(`/api/saved-videos/${video.value.vno}`, null, {
      params: { userNo: userInfo.userNo }
    });

    if (response.data.success) {
      alert('영상이 저장되었습니다.');
      isSaved.value = true;
    }
  } catch (error) {
    console.error('저장 처리 중 오류 발생:', error);
    alert(error.response?.data?.message || '저장 처리 중 오류가 발생했습니다.');
  }
};

// 저장 상태 확인
const checkSavedStatus = async () => {
  try {
    if (!video.value?.vno) return;
    
    const userInfoStr = localStorage.getItem('userInfo');
    if (!userInfoStr) return;

    const userInfo = JSON.parse(userInfoStr);
    if (!userInfo || !userInfo.userNo) return;

    const response = await axios.get(`/api/saved-videos/${video.value.vno}/check`);
    console.log('저장 상태 확인 응답:', response.data);
    
    if (response.data.success) {
      isSaved.value = response.data.isSaved;
    }
  } catch (error) {
    console.error('저장 상태 확인 중 오류:', error);
  }
};

// 컴포넌트 마운트 시 저장 상태 확인
onMounted(() => {
  fetchVideoDetails();
  checkSavedStatus();
});

// 비디오 변경 시 저장 상태 다시 확인
watch(() => route.params.id, () => {
  checkSavedStatus();
});
</script>

<style scoped>
.video-detail {
  background: var(--bg-color);
  min-height: 100vh;
  transition: background-color 0.3s ease;
}

.content-wrapper {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 400px;
  gap: 40px;
  max-width: 1600px;
  margin: 0 auto;
  padding: 30px;
  background: var(--bg-color);
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
  color: var(--text-color);
  line-height: 1.3;
}

.video-meta {
  font-size: 18px;
  color: var(--text-color);
  opacity: 0.8;
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
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
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
  background: var(--button-bg, #DEB887);
  color: var(--button-text, white);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

:root.dark-mode .action-btn {
  background: #4a4a4a;
  color: #fff;
}

.action-btn:hover {
  background: var(--button-hover-bg, #CD853F);
  transform: translateY(-2px);
}

.action-btn i {
  font-size: 18px;
}

.description {
  font-size: 18px;
  line-height: 1.8;
  color: var(--text-color);
  margin: 30px 0;
  padding: 20px;
  background: var(--hover-color);
  border-radius: 8px;
}

.about-speaker {
  margin-top: 40px;
  padding: 30px;
  background: var(--hover-color);
  border-radius: 8px;
}

.about-speaker h2 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 25px;
  color: var(--text-color);
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

:root.dark-mode .speaker-avatar {
  filter: brightness(0.8);
}

.speaker-details h3 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
  color: var(--text-color);
}

.speaker-bio {
  font-size: 17px;
  line-height: 1.6;
  color: var(--text-color);
  opacity: 0.8;
}

.sidebar {
  padding: 20px;
  background: var(--hover-color);
  border-radius: 8px;
  height: fit-content;
}

.watch-next {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid var(--border-color);
  color: var(--text-color);
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
  background: var(--hover-color);
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

:root.dark-mode .thumbnail img {
  filter: brightness(0.8);
  transition: filter 0.3s ease;
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
  color: var(--text-color);
}

.speaker {
  font-size: 12px;
  color: var(--text-color);
  opacity: 0.7;
  margin-bottom: 4px;
}

.meta {
  font-size: 12px;
  color: var(--text-color);
  opacity: 0.7;
}

.meta span:not(:last-child)::after {
  content: "•";
  margin: 0 4px;
}

.views {
  color: var(--text-color);
  opacity: 0.7;
}

.date {
  color: var(--text-color);
  opacity: 0.7;
}

.no-recommendations {
  padding: 20px;
  text-align: center;
  color: var(--text-color);
}

@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .sidebar {
    margin-top: 30px;
  }
}

:root.dark-mode {
  --button-bg: #4a4a4a;
  --button-hover-bg: #666;
  --border-color: #444;
}

:root.dark-mode .action-btn i {
  color: #fff;
}

:root.dark-mode .video-item:hover {
  background: rgba(255, 255, 255, 0.1);
}
</style>