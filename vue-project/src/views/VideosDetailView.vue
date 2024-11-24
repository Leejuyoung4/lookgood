<template>
  <div class="video-detail">
    <div v-if="loading" class="loading-state">
      로딩 중...
    </div>
    <div v-else-if="error" class="error-state">
      {{ error }}
    </div>
    <div v-else-if="video" class="content-wrapper">
      <!-- 메인 비디오 섹션 -->
      <div class="primary-content">
        <div class="video-container">
          <iframe :src="`https://www.youtube.com/embed/${video.videoId}`" frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen></iframe>
        </div>

        <!-- 비디오 제목 및 정보 -->
        <div class="video-info">
          <h1 class="video-title">{{ video.vTitle }}</h1>
          <div class="video-meta">
            <span class="views">조회수 {{ formatViewsDetail(video.vViews) }}회</span>
          </div>

          <!-- 액션 버튼 -->
          <div class="action-bar">
            <div class="action-buttons">
              <button class="action-btn share">
                <i class="bi bi-share"></i>
                공유하기
              </button>
              <button 
                class="action-btn save" 
                @click="handleSaveClick"
                :class="{ 'saved': isSaved }"
              >
                <i class="bi" :class="isSaved ? 'bi-bookmark-fill' : 'bi-bookmark'"></i>
                {{ isSaved ? '저장됨' : '저장하기' }}
              </button>
              <button class="action-btn like">
                <i class="bi bi-hand-thumbs-up"></i>
                좋아요
              </button>
            </div>
          </div>

          <!-- 비디오 설명 -->
          <div class="description">
            <p>{{ video.vDescription }}</p>
          </div>

          <!-- 강사 정보 -->
          <div class="about-speaker">
            <h2>강사 소개</h2>
            <div class="speaker-info">
              <div class="speaker-avatar"></div>
              <div class="speaker-details">
                <h3>{{ video.vInstructor }}</h3>
                <p class="speaker-bio">{{ video.vInstructorIntro }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 사이드바 - 다음 시청 영상 -->
      <div class="sidebar">
        <h2 class="watch-next">다음 동영상</h2>
        <div v-if="recommendedVideos.length > 0" class="next-videos">
          <div v-for="video in recommendedVideos" 
               :key="video.vNo" 
               class="video-item"
               @click="handleVideoClick(video.vNo)">
            <div class="thumbnail">
              <img :src="`https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg`"
                   :alt="video.vTitle">
            </div>
            <div class="video-info">
              <h3 class="video-title">{{ video.vTitle }}</h3>
              <p class="speaker">{{ video.vInstructor }}</p>
              <p class="meta">
                <span class="views">조회수 {{ formatViewsDetail(video.vViews) }}회</span>
                <span class="dot">•</span>
                <span class="date">{{ formatDate(video.vUploadDate) }}</span>
              </p>
            </div>
          </div>
        </div>
        <div v-else class="no-recommendations">
          <p>추천 동영상을 불러오는 중...</p>
        </div>
      </div>
    </div>

    <!-- 로인 모달 추가 -->
    <LoginViewModal 
      v-if="showLoginModal" 
      @close="showLoginModal = false"
      @login-success="handleLoginSuccess"
    />
  </div>
</template>
<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import LoginViewModal from '../views/LoginViewModal.vue'; // 경로 수정

const route = useRoute()
const router = useRouter()
const video = ref(null)
const recommendedVideos = ref([])
const isSaved = ref(false)
const showPlaylistModal = ref(false)
const playlists = ref([])
const newPlaylistName = ref('')
const loading = ref(false)
const error = ref(null)
const isLoggedIn = ref(false)
const showLoginModal = ref(false)

const fetchVideoDetails = async () => {
  const videoId = route.params.id;
  if (!videoId) {
    console.error('비디오 ID가 없습니다');
    router.push('/videos'); // 비디오 목록으로 리다이렉트
    return;
  }

  loading.value = true;
  error.value = null;

  try {
    console.log('가져올 비디오 ID:', videoId); // 디버깅용
    const response = await axios.get(`/api/videos/${videoId}`);
    video.value = response.data;
    await fetchRecommendedVideos();
  } catch (error) {
    console.error('비디오 로딩 실패:', error);
    error.value = '비디오를 불러오는데 실패했습니다.';
  } finally {
    loading.value = false;
  }
};

const fetchRecommendedVideos = async () => {
  try {
    if (!video.value?.vNo) return;

    console.log('추천 비디오 요청:', {
      currentVideoId: video.value.vNo,
      category: video.value.vCategoryName
    });

    const response = await axios.get('/api/videos/recommended', {
      params: {
        currentVideoId: video.value.vNo,
        category: video.value.vCategoryName,
        limit: 5
      }
    });

    console.log('추천 비디오 응답:', response.data);

    // 응답 데이터를 직접 할당
    recommendedVideos.value = response.data;

  } catch (error) {
    console.error('추천 비디오 로딩 실패:', error);
    recommendedVideos.value = [];
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
    if (route.params.id !== videoNo.toString()) {
      await router.push(`/videos/${videoNo}`);
      await fetchVideoDetails();
    }
  } catch (error) {
    console.error('비디오 이동 실패:', error);
  }
};

// 저장하기 관련 코드만 남기고 나머지 제거
const handleSaveClick = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo) {
      showLoginModal.value = true;  // 로그인 모달 표시
      return;
    }

    // 이미 저장된 경우 저장 취소
    if (isSaved.value) {
      const response = await axios.delete(`/api/saved-videos/${video.value.vNo}`, {
        params: { userNo: userInfo.userNo }
      });
      if (response.data.success) {
        isSaved.value = false;
        alert('저장이 취소되었습니다.');
      }
    } else {
      // 저장하기
      const response = await axios.post(`/api/saved-videos/${video.value.vNo}`, null, {
        params: { userNo: userInfo.userNo }
      });
      if (response.data.success) {
        isSaved.value = true;
        alert('영상이 저장되었습니다.');
        router.push('/mypage');  // 마이페이지로 이동
      }
    }
  } catch (error) {
    console.error('저장 처리 중 오류 발생:', error);
    alert('저장 처리 중 오류가 발생했습니다.');
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
    console.error('저장 상태 확 중 오류:', error);
  }
};

// 로그인 상태 체크
const checkLoginStatus = () => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo);
      isLoggedIn.value = !!user.userNo;
      console.log('로그인 상태:', isLoggedIn.value);
      return isLoggedIn.value;
    } catch (e) {
      console.error('사용자 정보 파싱 오류:', e);
    }
  }
  isLoggedIn.value = false;
  return false;
};

// 인증 헤더 가져오는 함수 수정
const getAuthHeader = () => {
  const userInfo = localStorage.getItem('userInfo');
  if (!userInfo) return {};
  
  const user = JSON.parse(userInfo);
  return {
    'Authorization': `Bearer ${user.token}`, // 또는 user.accessToken
    'Content-Type': 'application/json'
  };
};

// onMounted에서 플레이리스트 관련 코드 제거
onMounted(async () => {
  console.log('컴포넌트 마운트, route.params:', route.params);
  checkLoginStatus();
  await fetchVideoDetails();
  await checkSavedStatus();
});

// 로그인 상태 변경 감지를 위한 watch 추가
watch(() => checkLoginStatus(), async (isLoggedIn) => {
  if (isLoggedIn) {
    const returnUrl = localStorage.getItem('returnAfterLogin');
    if (returnUrl) {
      localStorage.removeItem('returnAfterLogin');
      router.push(returnUrl);
    }
  }
});

// route params가 변경될 때마다 데이터 다시 로딩
watch(() => route.params.id, async (newId) => {
  console.log('route params 변경:', newId); // 디버용
  if (newId) {
    await fetchVideoDetails();
    await checkSavedStatus();
  }
});

// 비디오를 저장하는 함수
const saveVideo = async () => {
  try {
    const response = await axios.post(`/api/videos/${video.value.vNo}/save`, {}, {
      withCredentials: true
    });

    if (response.status === 200) {
      alert('영상이 저장되었습니다.');
    }
  } catch (error) {
    if (error.response?.status === 401) {
      // 로그인 필요시 모달 표
      showLoginModal.value = true;
    } else {
      alert('영상 저장에 실패했습니다: ' + (error.response?.data || error.message));
    }
    console.error('비디오 저장 실패:', error);
  }
}

// 로그인 성공 핸들러
const handleLoginSuccess = () => {
  showLoginModal.value = false;
  checkLoginStatus();
  showPlaylistModal.value = true;
  fetchPlaylists();
};

// rememberMe 관련 경고를 해결하기 위해 추가
const rememberMe = ref(false);
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
  /* 텍스트 오버우 방지 */
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

.playlist-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.playlist-modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.playlist-modal-content h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}

.playlist-list {
  max-height: 300px;
  overflow-y: auto;
  margin: 20px 0;
  border: 1px solid #eee;
  border-radius: 8px;
}

.playlist-item {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  transition: background-color 0.2s;
}

.playlist-item:last-child {
  border-bottom: none;
}

.playlist-item:hover {
  background-color: #f8f8f8;
}

.playlist-item label {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  color: #333;
  font-size: 14px;
}

.create-playlist {
  display: flex;
  gap: 10px;
  margin-top: 20px;
  padding: 15px;
  border-top: 1px solid #eee;
}

.create-playlist input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  color: #333;
}

.create-btn {
  padding: 10px 20px;
  background-color: #DEB887;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.create-btn:hover:not(:disabled) {
  background-color: #CD853F;
}

.create-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #ccc;
}

/* 다크 모드 대응 */
:root.dark-mode .create-btn {
  background-color: #4a4a4a;
}

:root.dark-mode .create-btn:hover:not(:disabled) {
  background-color: #666;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.close-btn {
  padding: 8px 20px;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #e0e0e0;
}

:root.dark-mode .playlist-modal-content {
  background: #2d2d2d;
  color: #fff;
}

:root.dark-mode .playlist-modal-content h3 {
  color: #fff;
}

:root.dark-mode .playlist-item {
  border-color: #404040;
}

:root.dark-mode .playlist-item:hover {
  background-color: #3d3d3d;
}

:root.dark-mode .playlist-item label {
  color: #fff;
}

:root.dark-mode .create-playlist input {
  background-color: #3d3d3d;
  border-color: #404040;
  color: #fff;
}

:root.dark-mode .close-btn {
  background-color: #404040;
  color: #fff;
}

.loading-state, .error-state {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error-state {
  color: red;
}

.login-required {
  text-align: center;
  padding: 20px;
}

.login-required p {
  margin-bottom: 15px;
  color: var(--text-color);
}

.login-btn, .create-btn, .close-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.login-btn {
  background-color: #007bff;
  color: white;
}

.create-btn {
  background-color: var(--button-bg);
  color: white;
}

.close-btn {
  background-color: var(--button-bg);
  color: white;
}

.login-btn:hover, .create-btn:hover, .close-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.no-playlists {
  padding: 20px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.playlist-list {
  max-height: 300px;
  overflow-y: auto;
  margin: 20px 0;
  border: 1px solid #eee;
  border-radius: 8px;
}

.playlist-item {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.playlist-item label {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.playlist-item input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}
</style>
