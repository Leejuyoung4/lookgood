<template>
  <div class="mypage-container">
    <!-- 저장된 영상 섹션 -->
    <section class="saved-videos-section">
      <h2>저장된 영상</h2>
      
      <!-- 탭 메뉴 -->
      <div class="video-tabs">
        <button 
          :class="{ active: activeTab === 'all' }" 
          @click="activeTab = 'all'"
        >
          전체 영상
        </button>
        <button 
          :class="{ active: activeTab === 'inProgress' }" 
          @click="activeTab = 'inProgress'"
        >
          학습 중
        </button>
        <button 
          :class="{ active: activeTab === 'completed' }" 
          @click="activeTab = 'completed'"
        >
          완료
        </button>
      </div>

      <!-- 영상 그리드 -->
      <div class="video-grid">
        <div 
          v-for="video in filteredVideos" 
          :key="video.vno" 
          class="video-card"
        >
          <div class="thumbnail-wrapper">
            <img 
              v-if="video.image"
              :src="video.image"
              :alt="video.vTitle"
              @click="goToVideo(video.vno)"
            >
            <div 
              v-else 
              class="thumbnail-placeholder"
              @click="goToVideo(video.vno)"
            >
              <i class="bi bi-play-circle"></i>
            </div>
            <div class="progress-bar">
              <div 
                class="progress" 
                :style="{ width: `${video.progressRate || 0}%` }"
                :class="{ 'completed': video.completed }"
              ></div>
            </div>
          </div>

          <div class="video-info">
            <h3>{{ video.vTitle || '제목 없음' }}</h3>
            <p class="progress-text">
              진도율: {{ video.progressRate || 0 }}%
            </p>
            <div class="video-actions">
              <button 
                class="remove-btn"
                @click="unsaveVideo(video.vno)"
              >
                저장 취소
              </button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 비디오 모달 -->
    <div v-if="selectedVideo" class="video-modal" @click.self="closeVideo">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ selectedVideo.vTitle }}</h2>
          <button class="close-btn" @click="closeVideo">×</button>
        </div>
        
        <div class="custom-player-container">
          <!-- YouTube 플레이어를 숨기고 커스텀 오버레이 추가 -->
          <div class="player-wrapper">
            <iframe
              :id="`video-player-${selectedVideo.vno}`"
              :src="`https://www.youtube.com/embed/${selectedVideo.videoId}?enablejsapi=1&controls=0&modestbranding=1&rel=0&showinfo=0&autoplay=0`"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
            ></iframe>
            
            <!-- 커스텀 컨기 화면 오버레이 -->
            <div v-if="!hasStarted" class="custom-overlay" @click="startVideo">
              <div class="thumbnail-container">
                <img 
                  :src="`https://img.youtube.com/vi/${selectedVideo.videoId}/maxresdefault.jpg`" 
                  :alt="selectedVideo.vTitle"
                >
                <div class="play-overlay">
                  <div class="play-button">
                    <i class="bi bi-play-fill"></i>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 기존 컨트롤 -->
            <div class="custom-controls" v-show="hasStarted">
              <button class="play-btn" @click="togglePlay">
                <i :class="isPlaying ? 'bi bi-pause-fill' : 'bi bi-play-fill'"></i>
              </button>
              
              <div class="progress-bar-container" @click="seekVideo">
                <div class="progress-bar">
                  <div class="progress" :style="{ width: `${currentProgress}%` }"></div>
                  <div class="progress-handle" 
                       :style="{ left: `${currentProgress}%` }"></div>
                </div>
                <div class="time-display">
                  {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
                </div>
              </div>
              
              <div class="volume-control">
                <button class="volume-btn" @click="toggleMute">
                  <i :class="isMuted ? 'bi bi-volume-mute-fill' : 'bi bi-volume-up-fill'"></i>
                </button>
                <input 
                  type="range" 
                  class="volume-slider" 
                  :value="volume" 
                  @input="updateVolume" 
                  min="0" 
                  max="100"
                  :style="{ '--volume-percentage': volume + '%' }"
                >
                <div class="volume-tooltip">
                  볼륨: {{ volume }}%
                </div>
              </div>

              <!-- 전체화면 버튼 추가 -->
              <button class="fullscreen-btn" @click="toggleFullscreen">
                <i class="bi" :class="isFullscreen ? 'bi-fullscreen-exit' : 'bi-fullscreen'"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 볼륨 변경 알림 -->
    <div class="volume-notification" :class="{ show: showVolumeNotification }">
      볼륨: {{ volume }}%
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const savedVideos = ref([]);
const activeTab = ref('all');
const selectedVideo = ref(null);
const currentProgress = ref(0);
let player = null;
let progressInterval = null;
const isLoading = ref(true);
const isPlaying = ref(false);
const currentTime = ref(0);
const duration = ref(0);
const volume = ref(100);
const isMuted = ref(false);
const hasStarted = ref(false);
const showVolumeNotification = ref(false);
let volumeNotificationTimeout;
const isFullscreen = ref(false);
const videoContent = ref(null);

// YouTube API 키 설정
const YOUTUBE_API_KEY = 'YOUR_API_KEY';

// 필터링된 디오 목록
const filteredVideos = computed(() => {
  switch (activeTab.value) {
    case 'completed':
      return savedVideos.value.filter(video => video.isCompleted);
    case 'inProgress':
      return savedVideos.value.filter(video => !video.isCompleted);
    default:
      return savedVideos.value;
  }
});

// 저장된 비디오 목록 조회
const fetchSavedVideos = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo) return;

    const response = await axios.get(`/api/saved-videos/user/${userInfo.userNo}`);
    console.log('API 응답:', response.data);
    
    if (response.data.success && response.data.videos) {
      // 각 저장된 비디오에 대해 상세 정보 가져오기
      const videosWithDetails = await Promise.all(
        response.data.videos.map(async (savedVideo) => {
          try {
            // vno를 사용하여 비디오 상세 정보 가져오기
            const videoResponse = await axios.get(`/api/videos/${savedVideo.vno}`);
            console.log('비디오 상세 정보:', videoResponse.data);
            
            return {
              ...savedVideo,
              ...videoResponse.data,
              image: videoResponse.data.videoId 
                ? `https://img.youtube.com/vi/${videoResponse.data.videoId}/mqdefault.jpg`
                : null,
              progressRate: savedVideo.progressRate || 0,
              isCompleted: savedVideo.isCompleted || false
            };
          } catch (error) {
            console.error(`비디오 ${savedVideo.vno} 상세 정보  실패:`, error);
            return savedVideo;
          }
        })
      );

      savedVideos.value = videosWithDetails;
      console.log('최종 변환된 데이터:', savedVideos.value);
    }
  } catch (error) {
    console.error('저장된 영상 로딩 실패:', error);
  }
};

// 비디오 저장 취소
const unsaveVideo = async (videoNo) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    const response = await axios.delete(`/api/saved-videos/${videoNo}`, {
      params: { userNo: userInfo.userNo }
    });
    if (response.data.success) {
      await fetchSavedVideos();
    }
  } catch (error) {
    console.error('영상 저장 취소 실패:', error);
  }
};

// 썸네일 URL 생성
const getThumbUrl = (videoId) => {
  return `https://img.youtube.com/vi/${videoId}/mqdefault.jpg`;
};

// 진도율 추적 시작
const startProgressTracking = () => {
  if (progressInterval) clearInterval(progressInterval);
  progressInterval = setInterval(updateProgress, 1000);
};

// 진도율 추 중지
const stopProgressTracking = () => {
  if (progressInterval) {
    clearInterval(progressInterval);
    updateProgress(); // 마지막 상태 저장
  }
};

// 진도율 업데이트
const updateProgress = async () => {
  if (!player || !player.getCurrentTime || !selectedVideo.value) return;
  
  try {
    const videoDuration = player.getDuration();
    const videoCurrentTime = player.getCurrentTime();
    duration.value = videoDuration;
    currentTime.value = videoCurrentTime;
    const progress = Math.floor((videoCurrentTime / videoDuration) * 100);
    
    // 1% 이상 변경이 있을 때만 UI 업데이트
    if (Math.abs(progress - currentProgress.value) >= 1) {
      currentProgress.value = progress;
      
      // 5% 단위로 서버 업데이트 (선택사항)
      if (progress % 5 === 0) {
        await axios.put(`/api/saved-videos/${selectedVideo.value.vno}/progress`, null, {
          params: {
            userNo: JSON.parse(localStorage.getItem('userInfo')).userNo,
            progressRate: progress
          }
        });
      }
    }
  } catch (error) {
    console.error('진도율 업데이트 실패:', error);
  }
};

// 스크롤바 너비 계산 함수 추가
const getScrollbarWidth = () => {
  const outer = document.createElement('div');
  outer.style.visibility = 'hidden';
  outer.style.overflow = 'scroll';
  document.body.appendChild(outer);

  const inner = document.createElement('div');
  outer.appendChild(inner);

  const scrollbarWidth = outer.offsetWidth - inner.offsetWidth;
  outer.parentNode.removeChild(outer);

  return scrollbarWidth;
};

// 비디오 선택 및 모달 열기 함수 수정
const goToVideo = async (vno) => {
  try {
    const video = savedVideos.value.find(v => v.vno === vno);
    if (video && video.videoId) {
      selectedVideo.value = video;
      
      // 비디오 내용 가져오기
      const content = await fetchVideoContent(video.videoId);
      if (content) {
        // 내용을 상태에 저장
        videoContent.value = content;
      }
      
      currentProgress.value = video.progressRate || 0;
      isLoading.value = true;
      
      // body에 modal-open 클래스 추가
      document.body.classList.add('modal-open');
      // 스크롤바 너비 계산하여 CSS 변수로 설정
      document.documentElement.style.setProperty(
        '--scrollbar-width',
        `${getScrollbarWidth()}px`
      );
      
      await nextTick();
      if (window.YT) {
        initPlayer();
      } else {
        loadYouTubeAPI();
      }
    }
  } catch (error) {
    console.error('비디오 로드 실패:', error);
    isLoading.value = false;
  }
};

// YouTube API 로드
const loadYouTubeAPI = () => {
  if (!window.YT) {
    const tag = document.createElement('script');
    tag.src = 'https://www.youtube.com/iframe_api';
    const firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
    
    window.onYouTubeIframeAPIReady = () => {
      if (selectedVideo.value) {
        initPlayer();
      }
    };
  }
};

// 자막 가져오기 함수 수정
const fetchTranscript = async () => {
  if (!player || !selectedVideo.value) return;

  try {
    // YouTube Data API를 사용하여 자동 자막 포함한 모든 자막 가져오기
    const videoId = selectedVideo.value.videoId;
    const response = await axios.get(
      `https://video.google.com/timedtext?lang=ko&v=${videoId}&type=list`
    );

    // XML 응답 파싱
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(response.data, "text/xml");
    const tracks = Array.from(xmlDoc.getElementsByTagName('track'));

    // 자동 생성 자막 포함하여 검색
    const autoTrack = tracks.find(track => 
      track.getAttribute('lang_code') === 'ko' || 
      track.getAttribute('lang_code') === 'en'
    );

    if (autoTrack) {
      const langCode = autoTrack.getAttribute('lang_code');
      const trackResponse = await axios.get(
        `https://video.google.com/timedtext?lang=${langCode}&v=${videoId}`
      );

      const transcriptXml = parser.parseFromString(trackResponse.data, "text/xml");
      const textElements = transcriptXml.getElementsByTagName('text');
      
      // 자막 텍스트 추출
      const transcript = Array.from(textElements).map(text => ({
        start: parseFloat(text.getAttribute('start')),
        duration: parseFloat(text.getAttribute('dur')),
        text: text.textContent
      }));

      console.log('자막 로드 성공:', transcript);
      return transcript;
    }

    console.log('자막을 찾을 수 없습니다.');
    return null;

  } catch (error) {
    console.error('자막 가져오기 실패:', error);
    return null;
  }
};

// 자막 표시를 위한 컴포넌트 추가
const showTranscript = (transcript) => {
  if (!transcript) return;

  // 현재 재생 시간에 해당하는 자막 찾기
  const currentTime = player.getCurrentTime();
  const currentCaption = transcript.find(caption => 
    currentTime >= caption.start && 
    currentTime < (caption.start + caption.duration)
  );

  if (currentCaption) {
    // 자막 표시 로직
    console.log('현재 자막:', currentCaption.text);
    // 여기에 자막 표시 UI 업데이트 코드 추가
  }
};

// initPlayer 함수 수정
const initPlayer = () => {
  if (!selectedVideo.value) return;
  
  try {
    player = new window.YT.Player(`video-player-${selectedVideo.value.vno}`, {
      events: {
        onReady: async (event) => {
          isLoading.value = false;
          console.log('Player ready');
          duration.value = event.target.getDuration();
          currentTime.value = event.target.getCurrentTime();
          event.target.setVolume(volume.value);
          
          // 자막 가져오기
          const transcript = await fetchTranscript();
          if (transcript) {
            // 주기적으로 자막 업데이트
            setInterval(() => showTranscript(transcript), 100);
          }
        },
        onStateChange: (event) => {
          isPlaying.value = event.data === window.YT.PlayerState.PLAYING;
          if (event.data === window.YT.PlayerState.PLAYING) {
            startProgressTracking();
          } else {
            stopProgressTracking();
          }
        }
      }
    });
  } catch (error) {
    console.error('플레이어 초기화 실패:', error);
    isLoading.value = false;
  }
};

// 모달 닫기 함수 수정
const closeVideo = () => {
  hasStarted.value = false;
  stopProgressTracking();
  if (player && player.stopVideo) {
    player.stopVideo();
  }
  selectedVideo.value = null;
  isLoading.value = true;
  
  // body에서 modal-open 클래스 제거
  document.body.classList.remove('modal-open');
  // CSS 변수 초기화
  document.documentElement.style.setProperty('--scrollbar-width', '0px');
};

onMounted(() => {
  fetchSavedVideos();
  window.addEventListener('keydown', handleKeyPress);
});

onUnmounted(() => {
  stopProgressTracking();
  window.removeEventListener('keydown', handleKeyPress);
});

const handleKeyPress = (e) => {
  if (selectedVideo.value && player) {
    if (e.key === 'Escape') {
      closeVideo();
    } else if (e.key === ' ') {
      // 스페이스바로 재생/일시정지
      e.preventDefault();
      togglePlay();
    } else if (e.key === 'ArrowLeft') {
      // 왼쪽 방향키: 5초 뒤로
      e.preventDefault();
      const newTime = Math.max(0, player.getCurrentTime() - 5);
      player.seekTo(newTime, true);
    } else if (e.key === 'ArrowRight') {
      // 오른쪽 방향키: 5초 앞으로
      e.preventDefault();
      const newTime = Math.min(player.getDuration(), player.getCurrentTime() + 5);
      player.seekTo(newTime, true);
    } else if (e.key === 'ArrowUp') {
      // 위쪽 방향키: 볼륨 증가
      e.preventDefault();
      const newVolume = Math.min(100, volume.value + 5);
      updateVolume({ target: { value: newVolume } });
    } else if (e.key === 'ArrowDown') {
      // 아래쪽 방향키: 볼륨 감소
      e.preventDefault();
      const newVolume = Math.max(0, volume.value - 5);
      updateVolume({ target: { value: newVolume } });
    }
  }
};

// selectedVideo 변경 감지
watch(selectedVideo, (newVal) => {
  if (!newVal) {
    stopProgressTracking();
  }
});

// 재생/일시정지 토글
const togglePlay = () => {
  if (player) {
    if (isPlaying.value) {
      player.pauseVideo();
    } else {
      player.playVideo();
    }
    isPlaying.value = !isPlaying.value;
  }
};

// 시간 포맷팅
const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60);
  const secs = Math.floor(seconds % 60);
  return `${mins}:${secs.toString().padStart(2, '0')}`;
};

// 륨 업데이트
const updateVolume = (event) => {
  const newVolume = event.target.value;
  volume.value = newVolume;
  if (player) {
    player.setVolume(newVolume);
    if (newVolume > 0) {
      player.unMute();
      isMuted.value = false;
    } else {
      player.mute();
      isMuted.value = true;
    }
  }

  // 볼륨 변경 알림 표시
  showVolumeNotification.value = true;
  if (volumeNotificationTimeout) {
    clearTimeout(volumeNotificationTimeout);
  }
  volumeNotificationTimeout = setTimeout(() => {
    showVolumeNotification.value = false;
  }, 1000);
};

// 음소거 토글
const toggleMute = () => {
  if (player) {
    if (isMuted.value) {
      player.unMute();
      player.setVolume(volume.value);
    } else {
      player.mute();
    }
    isMuted.value = !isMuted.value;
  }
};

// 비디오 탐색
const seekVideo = (event) => {
  if (!player) return;
  
  const progressBar = event.currentTarget;
  const clickPosition = (event.clientX - progressBar.getBoundingClientRect().left) / progressBar.offsetWidth;
  const newTime = clickPosition * duration.value;
  
  player.seekTo(newTime, true);
};

const startVideo = () => {
  hasStarted.value = true;
  if (player) {
    player.playVideo();
  }
};

// 전체화면 토글 함수
const toggleFullscreen = () => {
  const container = document.querySelector('.custom-player-container');
  if (!document.fullscreenElement) {
    container.requestFullscreen();
    isFullscreen.value = true;
  } else {
    document.exitFullscreen();
    isFullscreen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('fullscreenchange', () => {
    isFullscreen.value = !!document.fullscreenElement;
  });
});

// 비디오 내용 가져오기 함수
const fetchVideoContent = async (videoId) => {
  try {
    // 1. 비디오 상세 정보 가져오기
    const response = await axios.get(`https://www.googleapis.com/youtube/v3/videos`, {
      params: {
        part: 'snippet',
        id: videoId,
        key: YOUTUBE_API_KEY
      }
    });

    if (response.data.items && response.data.items.length > 0) {
      const videoData = response.data.items[0].snippet;
      
      // 2. 필요한 정보 추출
      const content = {
        title: videoData.title,
        description: videoData.description,
        tags: videoData.tags || [],
        publishedAt: videoData.publishedAt,
        channelTitle: videoData.channelTitle
      };

      console.log('비디오 내용:', content);
      return content;
    }
    
    return null;
  } catch (error) {
    console.error('비디오 내용 가져오기 실패:', error);
    return null;
  }
};
</script>

<style scoped>
.mypage-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.saved-videos-section {
  margin-top: 20px;
}

.video-tabs {
  margin: 20px 0;
  display: flex;
  gap: 10px;
}

.video-tabs button {
  padding: 8px 16px;
  border: none;
  border-radius: 20px;
  background: #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.video-tabs button.active {
  background: #DEB887;
  color: white;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.video-card {
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  background: white;
}

.thumbnail-wrapper {
  position: relative;
  cursor: pointer;
}

.thumbnail-wrapper img {
  width: 100%;
  aspect-ratio: 16/9;
  object-fit: cover;
}

.progress-bar {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: rgba(0,0,0,0.2);
}

.progress {
  height: 100%;
  background: #DEB887;
  transition: width 0.3s;
}

.progress.completed {
  background: #4CAF50;
}

.completion-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #4CAF50;
  color: white;
  padding: 5px;
  border-radius: 50%;
}

.video-info {
  padding: 15px;
}

.video-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.progress-text {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.video-actions {
  margin-top: 10px;
}

.remove-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  background: #ff4444;
  color: white;
  cursor: pointer;
  transition: background 0.3s;
}

.remove-btn:hover {
  background: #cc0000;
}

.thumbnail-placeholder {
  width: 100%;
  aspect-ratio: 16/9;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
}

.video-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  overflow: hidden;
}

.modal-content {
  max-height: 90vh;
  width: 90%;
  margin: auto;
  position: relative;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.custom-player-container {
  position: relative;
  background: #000;
  overflow: hidden;
  border-radius: 8px;
}

.player-wrapper {
  position: relative;
  padding-top: 56.25%;
  background: #000;
}

.player-wrapper iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.custom-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.player-wrapper:hover .custom-controls {
  opacity: 1;
}

.play-btn {
  background: none;
  border: none;
  color: #FFD700;
  font-size: 28px;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  outline: none;
  user-select: none;
}

.play-btn:hover {
  transform: scale(1.1);
  color: #FFF;
  background: rgba(255, 215, 0, 0.2);
  border-radius: 50%;
}

.progress-bar-container {
  flex: 1;
  padding: 10px 0;
}

.progress-bar {
  height: 4px;
  background: rgba(255,255,255,0.2);
  position: relative;
  border-radius: 2px;
  cursor: pointer;
  transition: height 0.2s ease;
}

.progress-bar:hover {
  height: 6px;
}

.progress-bar .progress {
  position: absolute;
  height: 100%;
  background: #FFD700;
  border-radius: 2px;
  position: relative;
  transition: background-color 0.3s ease;
}

.progress-handle {
  width: 12px;
  height: 12px;
  background: #FFD700;
  border: 2px solid #FFF;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.3s ease;
  opacity: 0;
}

.progress-bar:hover .progress-handle {
  opacity: 1;
}

.progress-handle:hover {
  transform: translate(-50%, -50%) scale(1.2);
  background: #FFED4A;
}

.time-display {
  color: #FFF;
  font-size: 13px;
  margin-top: 5px;
  font-family: 'Arial', sans-serif;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.5);
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  position: relative;
  transition: all 0.3s ease;
  height: 40px;
}

.volume-control:hover {
  background: rgba(255, 215, 0, 0.1);
  border-radius: 20px;
}

.volume-btn {
  background: none;
  border: none;
  color: #FFD700;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
  outline: none;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
}

.volume-btn:hover {
  color: #FFF;
  transform: scale(1.1);
  background: rgba(255, 215, 0, 0.3);
  animation: pulse 1s infinite;
}

.volume-slider {
  -webkit-appearance: none;
  width: 80px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.volume-slider:hover {
  height: 8px;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 12px;
  height: 12px;
  background: #FFD700;
  border: 2px solid #FFF;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
  margin-top: -4px;
}

.volume-slider::-webkit-slider-thumb:hover {
  background: #FFED4A;
  transform: scale(1.2);
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
}

.volume-slider::-webkit-slider-runnable-track {
  background: linear-gradient(
    to right,
    #FFD700 var(--volume-percentage),
    rgba(255, 215, 0, 0.2) var(--volume-percentage)
  );
  border-radius: 2px;
  height: 4px;
  transition: all 0.3s ease;
}

.volume-tooltip {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 215, 0, 0.9);
  color: #000;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.volume-control:hover .volume-tooltip {
  opacity: 1;
}

.modal-content {
  background: #1a1a1a;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
}

.modal-header {
  background: #222;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #333;
}

.modal-header h2 {
  color: #FFF;
  font-size: 18px;
  margin: 0;
  font-weight: 500;
}

.close-btn {
  background: none;
  border: none;
  color: #FFD700;
  font-size: 24px;
  cursor: pointer;
  padding: 5px;
  transition: color 0.2s ease;
}

.close-btn:hover {
  color: #FFF;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.8);
  display: flex;
  justify-content: center;
  align-items: center;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid rgba(255,255,255,0.1);
  border-top: 3px solid #FFD700;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 768px) {
  .custom-controls {
    padding: 15px;
  }
  
  .play-btn {
    font-size: 24px;
  }
  
  .volume-control {
    display: none;
  }
  
  .time-display {
    font-size: 12px;
  }
}

.custom-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #000;
  cursor: pointer;
  z-index: 2;
}

.thumbnail-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.thumbnail-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.3);
  display: flex;
  justify-content: center;
  align-items: center;
  transition: background 0.3s ease;
}

.play-button {
  width: 80px;
  height: 80px;
  background: rgba(255, 215, 0, 0.9); /* 골드 색상 */
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: background 0.3s ease;
}

.play-button:hover {
  background: rgba(255, 215, 0, 0.7); /* 어두운 골드 색상 */
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 215, 0, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(255, 215, 0, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 215, 0, 0);
  }
}

.volume-notification {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(255, 215, 0, 0.9);
  color: #000;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  z-index: 1000;
}

.volume-notification.show {
  opacity: 1;
  animation: fadeInOut 1s ease;
}

@keyframes fadeInOut {
  0% { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
  20% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  80% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
}

/* 컨트롤 레이아웃 */
.custom-controls {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 10;
}

.player-wrapper:hover .custom-controls {
  opacity: 1;
}

/* 오른쪽 컨트롤 스타일 */
.right-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto; /* 오른쪽 정렬 */
}

/* 전체화면 버튼 스타일 */
.fullscreen-btn {
  background: none;
  border: none;
  color: #FFD700;
  font-size: 24px;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  outline: none;
}

.fullscreen-btn:hover {
  transform: scale(1.1);
  color: #FFF;
  background: rgba(255, 215, 0, 0.2);
}

/* 전체화면 모드 스타일 */
.custom-player-container:fullscreen {
  width: 100vw;
  height: 100vh;
  background: black;
}

.custom-player-container:fullscreen .player-wrapper {
  height: 100%;
}

.custom-player-container:fullscreen iframe {
  width: 100%;
  height: 100%;
}

/* 전체화면 버튼 애니메이션 */
.fullscreen-btn i {
  transition: transform 0.3s ease;
}

.fullscreen-btn:hover i {
  transform: scale(1.1);
}

/* Firefox 전체화면 지원 */
.custom-player-container:-moz-full-screen {
  width: 100vw;
  height: 100vh;
  background: black;
}

/* Safari 전체화면 지원 */
.custom-player-container:-webkit-full-screen {
  width: 100vw;
  height: 100vh;
  background: black;
}

/* 전체화면 시 컨트롤 위치 조정 */
.custom-player-container:fullscreen .custom-controls {
  padding: 20px 30px;
}

/* 호버 시 툴팁 */
.fullscreen-btn {
  position: relative;
}

.fullscreen-btn::after {
  content: attr(title);
  position: absolute;
  top: -35px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.fullscreen-btn:hover::after {
  opacity: 1;
  visibility: visible;
}

/* 모달 오픈 시 body 스타일 */
:root {
  --scrollbar-width: 0px;
}

body.modal-open {
  overflow: hidden;
  padding-right: var(--scrollbar-width); /* 스크롤바 너비만큼 패딩 추가 */
}
</style> 