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
          <iframe
            :src="getYouTubeEmbedUrl(video.videoId)"
            frameborder="0"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen
            id="youtube-player"
          ></iframe>
        </div>

        <!-- 비디오 제목 및 정보 -->
        <div class="video-info">
          <h1 class="video-title">{{ video.vTitle }}</h1>
          <div class="video-meta">
            <span class="views">조회수 {{ formatViewsDetail(video.vViews) }}회</span>
            <span v-if="isLoggedIn && progressRate !== null" class="progress-rate">
            </span>
          </div>

          <!-- 액션 버튼 -->
          <div class="action-bar">
            <div class="action-buttons">
              <button class="action-btn share" @click="handleShare">
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

    <!-- 토스트 메시지 추가 -->
    <div 
      v-if="showToast" 
      class="toast-message"
      :class="{ 'show': showToast }"
    >
      {{ toastMessage }}
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, watch, onUnmounted, computed } from 'vue'
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

// 플레이어 관련 변수들 추가
const player = ref(null);
const progressInterval = ref(null);
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);

// YouTube URL 생성 함수 추가
const getYouTubeEmbedUrl = (videoId) => {
  return `https://www.youtube.com/embed/${videoId}?enablejsapi=1&controls=1&modestbranding=1&rel=0&showinfo=0&iv_load_policy=3&playsinline=1`;
};

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

// toast 관련 ref 추가
const showToast = ref(false);
const toastMessage = ref('');

// toast 표시 함수
const displayToast = (message) => {
  toastMessage.value = message;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 저/저장취소 처리 함수 수정
const handleSaveClick = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo) {
      showLoginModal.value = true;
      return;
    }

    // 이미 저장된 상태면 저장 취소 처리
    if (isSaved.value) {
      const response = await axios.delete(
        `/api/saved-videos/${video.value.vNo}`,
        {
          params: {
            userNo: userInfo.userNo
          }
        }
      );

      if (response.data.success) {
        isSaved.value = false;
        displayToast('저장이 취소되었습니다.');
      }
      return;
    }

    // 저장 처리
    const response = await axios.post(
      `/api/saved-videos/${video.value.vNo}`,
      null,
      {
        params: {
          userNo: userInfo.userNo
        }
      }
    );

    if (response.data.success) {
      isSaved.value = true;
      displayToast('영상이 저장되었습니다.');
    }
  } catch (error) {
    console.error('저장/취소 처리 중 오류 발생:', error);
    
    if (error.response?.data?.message) {
      displayToast(error.response.data.message);
      // 이미 저장된 경우 태 업데이트
      if (error.response.data.message.includes('이미 저장된')) {
        isSaved.value = true;
      }
    } else {
      displayToast(isSaved.value ? '저장 취소에 실패했습니다.' : '저장에 실패했습니다.');
    }
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

// 저장 상태 인 함수 수정
const checkSavedStatus = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo || !video.value) return;

    const response = await axios.get(`/api/saved-videos/${video.value.vNo}/check`, {
      params: {
        userNo: userInfo.userNo
      }
    });

    console.log('저장 상태 확인 응답:', response.data);
    isSaved.value = response.data.isSaved;
  } catch (error) {
    console.error('저장 상태 확인 중 오류:', error);
    isSaved.value = false;
  }
};

// 컴포넌트 마운트 시 실행
onMounted(async () => {
  console.log('컴포넌트 마운트, route.params:', route.params);
  await fetchVideoDetails();
  if (checkLoginStatus()) {
    await checkSavedStatus();
  }
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

// 플레이어 초기화 함수 수정
const initPlayer = () => {
  if (!selectedVideo.value) return;
  
  try {
    player = new window.YT.Player(`video-player-${selectedVideo.value.vno}`, {
      events: {
        onReady: (event) => {
          isLoading.value = false;
          const videoDuration = event.target.getDuration();
          if (isFinite(videoDuration)) {
            duration.value = videoDuration;
          }
          player.setPlaybackQuality('hd720');
        },
        onStateChange: (event) => {
          if (event.data === window.YT.PlayerState.PLAYING) {
            isPlaying.value = true;
            hasStarted.value = true;
            startProgressTracking();
          } else if (event.data === window.YT.PlayerState.PAUSED) {
            isPlaying.value = false;
          } else if (event.data === window.YT.PlayerState.ENDED) {
            isPlaying.value = false;
            stopProgressTracking();
          }
        }
      },
      playerVars: {
        controls: 0,           // YouTube 컨트롤 숨기기
        disablekb: 1,         // 키보드 컨트롤 비활성화
        fs: 0,                // 전체화면 버튼 숨기기
        iv_load_policy: 3,    // 동영상 주석 숨기기
        modestbranding: 1,    // YouTube 로고 최소화
        rel: 0,               // 관련 동영상 숨기
        showinfo: 0,          // 영상 정보 숨기기
        autoplay: 0,          // 자동재생 비활성화
        playsinline: 1,       // iOS에서 인라인 재생
        enablejsapi: 1,       // JavaScript API 활성화
        origin: window.location.origin
      }
    });
  } catch (error) {
    console.error('플레이어 초기화 실패:', error);
    isLoading.value = false;
  }
};

// iframe 초기화 시 필요한 매개변수 추가
const initVideo = () => {
  const videoId = route.params.id;
  if (!videoId) return;

  const iframe = document.querySelector('.video-container iframe');
  if (iframe) {
    // YouTube API 매개변수 추가
    const updatedSrc = `https://www.youtube.com/embed/${video.value.videoId}?enablejsapi=1&controls=0&modestbranding=1&rel=0&showinfo=0&iv_load_policy=3&disablekb=1&playsinline=1&origin=${window.location.origin}`;
    iframe.src = updatedSrc;

    // YouTube API 초기화
    if (window.YT) {
      initYouTubePlayer();
    } else {
      // YouTube API가 아직 로드되지 않은 경우
      window.onYouTubeIframeAPIReady = initYouTubePlayer;
    }
  }
};

// YouTube 플레이어 초기화
const initYouTubePlayer = () => {
  const iframe = document.querySelector('.video-container iframe');
  if (!iframe) return;

  player.value = new window.YT.Player(iframe, {
    events: {
      'onReady': (event) => {
        console.log('Player ready');
        duration.value = event.target.getDuration();
      },
      'onStateChange': (event) => {
        console.log('Player state changed:', event.data);
        if (event.data === window.YT.PlayerState.PLAYING) {
          isPlaying.value = true;
          startProgressTracking();
        } else if (event.data === window.YT.PlayerState.PAUSED || 
                  event.data === window.YT.PlayerState.ENDED) {
          isPlaying.value = false;
          updateProgress();
          stopProgressTracking();
        }
      }
    }
  });
};

// 진도율 추적 시작
const startProgressTracking = () => {
  if (progressInterval.value) clearInterval(progressInterval.value);
  progressInterval.value = setInterval(async () => {
    if (!player.value) return;
    
    try {
      currentTime.value = await player.value.getCurrentTime();
      const progress = Math.floor((currentTime.value / duration.value) * 100);
      
      const userInfo = JSON.parse(localStorage.getItem('userInfo'));
      if (!userInfo) return;

      await axios.put(`/api/saved-videos/${video.value.vNo}/progress`, {
        progressRate: progress,
        userNo: userInfo.userNo,
        currentTime: currentTime.value
      });
      
      console.log('Progress updated:', progress);
    } catch (error) {
      console.error('Progress update failed:', error);
    }
  }, 5000); // 5초마다 업데이트
};

// 진도율 추적 중지
const stopProgressTracking = () => {
  if (progressInterval.value) {
    clearInterval(progressInterval.value);
    progressInterval.value = null;
  }
};

// 진도율 업데이트
const updateProgress = async () => {
  if (!player || !video.value) return;

  try {
    const currentTime = player.getCurrentTime();
    const duration = player.getDuration();
    const progress = Math.floor((currentTime / duration) * 100);

    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo) return;

    await axios.put(`/api/saved-videos/${video.value.vno}/progress`, {
      progressRate: progress,
      userNo: userInfo.userNo
    });

  } catch (error) {
    console.error('진도율 업데이트 실패:', error);
  }
};

// 컴포넌트 마운트 시 초기화
onMounted(() => {
  // YouTube API 스크립트 로드
  if (!window.YT) {
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    const firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
    
    window.onYouTubeIframeAPIReady = () => {
      console.log('YouTube API Ready');
      initVideo();
    };
  } else {
    initVideo();
  }
});

// 컴포넌트 언마운트 시 정리
onUnmounted(() => {
  stopProgressTracking();
  if (player.value) {
    player.value.destroy();
  }
});

// 비디오 변경 시 재초기화
watch(() => route.params.id, () => {
  stopProgressTracking();
  if (player.value) {
    player.value.destroy();
  }
  initVideo();
});

// 공유하기 함수 추가
const handleShare = async () => {
  try {
    const currentUrl = window.location.href;
    
    // 모바일 기기에서 Web Share API 지원하는 경우
    if (navigator.share) {
      await navigator.share({
        title: video.value.vTitle,
        text: video.value.vDescription,
        url: currentUrl,
      });
      return;
    }

    // 클립보드 API 지원 확인
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(currentUrl);
      displayToast('URL이 클립보드에 복사되었습니다.');
      return;
    }

    // 폴백: 임시 textarea 사용
    const textarea = document.createElement('textarea');
    textarea.value = currentUrl;
    textarea.style.position = 'fixed';
    textarea.style.left = '-9999px';
    document.body.appendChild(textarea);
    textarea.select();
    
    try {
      document.execCommand('copy');
      displayToast('URL이 클립보드에 복사되었습니다.');
    } catch (err) {
      displayToast('URL 복사에 실패했습니다.');
    } finally {
      document.body.removeChild(textarea);
    }

  } catch (error) {
    console.error('공유하기 실패:', error);
    displayToast('URL 복사에 실패했습니다.');
  }
};

// 메모 관련 함수들 수정
const saveMemoWithAnimation = async () => {
  if (!newMemoText.value.trim() || !selectedVideo.value) return;
  
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    
    // VideoMemo DTO와 정확히 일치하는 객체 생성
    const memo = {
      memoId: 0, // 새로운 메모의 경우 0
      userNo: parseInt(userInfo.userNo),
      vNo: parseInt(selectedVideo.value.vno),
      content: newMemoText.value.trim(),
      startTime: Math.floor(player.getCurrentTime()), // timestamp를 startTime으로 변경
      endTime: null, // 필요한 경우 설정
      createdAt: null // 서버에서 설정됨
    };
    
    console.log('Sending memo:', memo); // 디버깅용 로그
    
    const response = await axios.post('/api/memos', memo, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    if (response.data.success) {
      savedMemos.value.push(response.data.memo);
      showToast('메모가 저장되었습니다.');
      newMemoText.value = '';
      showMemoInput.value = false;
    }
  } catch (error) {
    console.error('메모 저장 실패:', error);
    console.error('에러 상세:', error.response?.data);
    showToast('메모 저장에 실패했습니다.');
  }
};

// computed 속성 수정 - 시간순 정렬
const sortedMemos = computed(() => {
  return [...savedMemos.value].sort((a, b) => a.startTime - b.startTime);
});

// 메모 시간으로 이동하는 함수 수정
const seekToTimestamp = (startTime) => {
  if (player) {
    player.seekTo(startTime);
    if (!isPlaying.value) {
      player.playVideo();
    }
  }
};
</script>

<style scoped>
/* 1. 변수 정의 */
:root {
  --primary-color: #FFD93D;
  --secondary-color: #FFF6BD;
  --accent-color: #FFB84C;
  --hover-color: #F5F5F5;
  --text-primary: #4F4F4F;
  --text-secondary: #6B6B6B;
  --border-radius: 16px;
  --shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

/* 2. 기본 레이아웃 */
.video-detail {
  background: #FFFEF8;
  min-height: 100vh;
  padding: 20px;
}

.content-wrapper {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 30px;
  max-width: 1600px;
  margin: 0 auto;
  padding: 20px;
}

/* 3. 비디오 플레이어 */
.video-container {
  position: relative;
  padding-bottom: 56.25%;
  height: 0;
  overflow: hidden;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  margin-bottom: 25px;
}

.video-container iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

/* 4. 비디오 정보 섹션 */
.video-title {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 15px;
  line-height: 1.4;
}

.video-meta {
  background: var(--secondary-color);
  padding: 12px 20px;
  border-radius: 25px;
  display: inline-block;
  font-size: 14px;
  color: var(--text-secondary);
}

/* 5. 액션 버튼 */
.action-bar {
  margin: 25px 0;
  padding: 20px 0;
  border-top: 2px dashed #FFE5A5;
  border-bottom: 2px dashed #FFE5A5;
}

.action-buttons {
  display: flex;
  gap: 15px;
  align-items: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 25px;
  border: none;
  background: var(--primary-color);
  color: var(--text-primary);
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 217, 61, 0.4);
}

.action-btn.save.saved {
  background: linear-gradient(135deg, #FFB84C, #FF9642);
  color: white;
}

.action-btn.like {
  background: linear-gradient(135deg, #FF6B6B, #FF5252);
  color: white;
}

.action-btn.like:hover {
  background: linear-gradient(135deg, #FF5252, #FF4141);
  box-shadow: 0 6px 20px rgba(255, 82, 82, 0.3);
}

/* 6. 설명 섹션 */
.description {
  background: white;
  padding: 25px;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  line-height: 1.7;
  color: var(--text-secondary);
  border: 1px solid #FFE5A5;
}

/* 7. 강사 정보 */
.about-speaker {
  background: white;
  padding: 25px;
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  margin-top: 30px;
  border: 1px solid #FFE5A5;
}

.speaker-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.speaker-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: var(--primary-color);
  border: 3px solid white;
  box-shadow: var(--shadow);
}

/* 8. 사이드바 - 다음 동영상 */
.sidebar {
  background: #FFFEF8;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(255, 217, 61, 0.15);
  height: fit-content;
  border: 1px solid #FFE5A5;
  width: 100%;
}

.watch-next {
  font-size: 22px;
  font-weight: 700;
  color: #4F4F4F;
  margin-bottom: 24px;
  padding-bottom: 15px;
  border-bottom: 2px dashed #FFE5A5;
  text-align: left;
  letter-spacing: -0.5px;
}

.next-videos {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
  padding-right: 8px;
}

.next-videos::-webkit-scrollbar {
  width: 6px;
}

.next-videos::-webkit-scrollbar-track {
  background: #FFF6BD;
  border-radius: 10px;
}

.next-videos::-webkit-scrollbar-thumb {
  background: #FFD93D;
  border-radius: 10px;
}

.video-item {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 12px;
  padding: 12px;
  margin-bottom: 16px;
  border-radius: 12px;
  background: white;
  transition: all 0.2s ease;
  border: 1px solid rgba(255, 184, 76, 0.1);
}

.video-item:hover {
  background: #FFFEF8;
}

.thumbnail {
  width: 120px;
  height: 68px;
  border-radius: 8px;
  overflow: hidden;
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.title {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #333;
}

.speaker {
  font-size: 13px;
  color: #666;
  margin: 4px 0;
}

.meta {
  font-size: 12px;
  color: #888;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  font-size: 8px;
}

/* 반응형 조정 */
@media (max-width: 768px) {
  .video-item {
    grid-template-columns: 100px 1fr;
    gap: 10px;
  }

  .thumbnail {
    width: 100px;
    height: 56px;
  }

  .title {
    font-size: 13px;
  }
}

/* 9. 토스트 메시지 */
.toast-message {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 6px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  z-index: 1000;
  transition: opacity 0.3s ease;
}

/* 10. 반응형 레이아웃 */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    margin-top: 30px;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    padding: 15px;
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
}

/* 다크모드 스타일 추가 */
:root.dark-mode .video-detail {
  background: #1a1a1a;
}

:root.dark-mode .content-wrapper {
  background: #1a1a1a;
}

:root.dark-mode .video-title {
  color: #fff;
}

:root.dark-mode .video-meta {
  background: #2a2a2a;
  color: #ddd;
}

:root.dark-mode .action-btn {
  background: #333;
  color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

:root.dark-mode .action-btn:hover {
  background: #444;
}

:root.dark-mode .action-btn.save.saved {
  background: #FFB84C;
  color: #1a1a1a;
}

:root.dark-mode .description,
:root.dark-mode .about-speaker {
  background: #242424;
  color: #ddd;
  border-color: #333;
}

:root.dark-mode .sidebar {
  background: #242424;
  border-color: #333;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

:root.dark-mode .watch-next {
  color: #fff;
  border-bottom-color: #333;
}

:root.dark-mode .video-item {
  background: #2a2a2a;
  border-color: #333;
}

:root.dark-mode .video-item:hover {
  border-color: #FFD93D;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
}

:root.dark-mode .video-info h3 {
  color: #fff;
}

:root.dark-mode .video-info .meta {
  color: #bbb;
}

:root.dark-mode .meta .views,
:root.dark-mode .meta .date {
  color: #999;
}

:root.dark-mode .no-recommendations {
  background: #2a2a2a;
  color: #bbb;
}

:root.dark-mode .next-videos::-webkit-scrollbar-track {
  background: #333;
}

:root.dark-mode .next-videos::-webkit-scrollbar-thumb {
  background: #555;
}

:root.dark-mode .action-bar {
  border-color: #333;
}
</style>
