<template>
  <div class="mypage-container">
    <!-- 학습 통계 섹션 -->
    <section class="stats-section">
      <div class="stat-card">
        <i class="bi bi-clock-history"></i>
        <div class="stat-info">
          <h3>총 학습시간</h3>
          <p>{{ formatTotalTime }}</p>
        </div>
      </div>
      <div class="stat-card">
        <i class="bi bi-play-circle"></i>
        <div class="stat-info">
          <h3>학습 중인 영상</h3>
          <p>{{ inProgressCount }}개</p>
        </div>
      </div>
      <div class="stat-card">
        <i class="bi bi-check-circle"></i>
        <div class="stat-info">
          <h3>완료한 영상</h3>
          <p>{{ completedCount }}개</p>
        </div>
      </div>
      <div class="stat-card">
        <i class="bi bi-graph-up"></i>
        <div class="stat-info">
          <h3>평균 진도율</h3>
          <p>{{ averageProgress }}%</p>
        </div>
      </div>
    </section>

    <!-- 저장된 영상 섹션 -->
    <section class="saved-videos-section">
      <h2>저장된 영상</h2>
      
      <!-- ��� 메뉴 -->
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
          :class="{ 'completed': video.progressRate >= 100 || video.isCompleted }"
        >
          <div class="thumbnail-wrapper">
            <button 
              class="delete-btn"
              @click.stop="confirmDelete(video.vno, video.vTitle)"
              title="저장 취소"
            >
              <i class="bi bi-x-lg"></i>
            </button>

            <img 
              :src="`https://img.youtube.com/vi/${video.videoId}/mqdefault.jpg`"
              :alt="video.vTitle"
              @click="goToVideo(video.vno)"
              @error="handleImageError($event, video)"
            >
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
            <div class="badges" v-if="video.progressRate >= 100">
              <span class="badge" v-if="isFirstComplete(video)">
                <i class="bi bi-1-circle-fill"></i> 첫 완강!
              </span>
              <span class="badge" v-if="isQuickLearner(video)">
                <i class="bi bi-lightning-fill"></i> 빠른 학습
              </span>
              <span class="badge" v-if="isPerfectWatch(video)">
                <i class="bi bi-star-fill"></i> 완벽 시청
              </span>
            </div>
            <p class="progress-text">
              진도율: {{ video.progressRate || 0 }}%
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- 비디오 모달 -->
    <div v-if="selectedVideo" class="video-modal" @click.self="closeVideo">
      <div class="modal-content">
        <button class="close-btn" @click="closeVideo">
          <i class="bi bi-x-lg"></i>
        </button>

        <div class="custom-player-container">
          <div class="player-wrapper">
            <!-- 메모 입력 영역 -->
            <div class="modal-memo" @dblclick.self="handleMemoDoubleClick">
              <!-- 메모 입력 폼 -->
              <div 
                v-if="showMemoInput" 
                class="memo-input-container"
                :style="{ left: memoPosition.x + 'px', top: memoPosition.y + 'px' }"
              >
                <input 
                  ref="memoInput"
                  v-model="newMemoText"
                  @keyup.enter="saveMemoWithAnimation"
                  @blur="handleBlur"
                  placeholder="메모를 입력하세요"
                  class="memo-input"
                />
              </div>
            </div>
            <iframe
              :id="`video-player-${selectedVideo.vno}`"
              :src="`https://www.youtube.com/embed/${selectedVideo.videoId}?enablejsapi=1&controls=0&modestbranding=1&rel=0&showinfo=0&iv_load_policy=3&disablekb=1&playsinline=1&annotations=0`"
              frameborder="0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowfullscreen
              class="youtube-player"
            ></iframe>
            
            <!-- 커스텀 컨트롤 화면 오버레이 -->
            <div 
              v-if="!hasStarted" 
              class="custom-overlay" 
              @click="handleOverlayClick"
            >
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
            <div class="custom-controls" :class="{ 'show-controls': hasStarted || isHovering }">
              <button class="play-btn" @click="togglePlay">
                <i :class="isPlaying ? 'bi bi-pause-fill' : 'bi bi-play-fill'"></i>
              </button>
              
              <div 
                class="progress-bar-container" 
                @click="handleSeek"
                @mousedown="startDragging"
                @mousemove="handleDragging"
                @mouseup="stopDragging"
                @mouseleave="stopDragging"
              >
                <div class="progress-bar">
                  <div class="progress-background"></div>
                  <div 
                    class="progress-filled" 
                    :style="{ width: `${currentProgress}%` }"
                  ></div>
                  <div 
                    class="progress-handle"
                    :style="{ left: `${currentProgress}%` }"
                  ></div>
                </div>
                <div class="time-display">
                  {{ formatTime(currentTime) }} / {{ formatTime(duration) }}
                </div>
              </div>
              
              <div class="right-controls">
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
                  >
                </div>
                <button class="fullscreen-btn" @click="toggleFullscreen">
                  <i class="bi" :class="isFullscreen ? 'bi-fullscreen-exit' : 'bi-fullscreen'"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 메모 사이드바 추가 -->
        <div class="memo-sidebar">
          <h3>메모 목록</h3>
          <div class="memo-list">
            <div v-for="memo in savedMemos" :key="memo.id" class="memo-item">
              <div class="memo-timestamp" @click="seekToTimestamp(memo.timestamp)">
                {{ formatTime(memo.timestamp) }}
              </div>
              <div class="memo-content">{{ memo.content }}</div>
              <button class="memo-delete-btn" @click="deleteMemo(memo.id)">×</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 볼륨 변경 알림 -->
    <div class="volume-notification" :class="{ show: showVolumeNotification }">
      볼륨: {{ volume }}%
    </div>

    <div class="badges">
      <h3>획득한 뱃지</h3>
      <div class="badge-container">
        <span v-for="(badge, index) in badges" :key="index" class="badge">{{ badge }}</span>
      </div>
    </div>

    <!-- 기존 스타일에 추가 -->
    <Transition name="toast">
      <div v-if="showToast" class="toast-message">
        {{ toastMessage }}
      </div>
    </Transition>

    <!-- 삭제 확인 모달 수정 -->
    <Transition name="modal">
      <div v-if="showDeleteModal" class="delete-modal-overlay" @click.self="cancelDelete">
        <div class="delete-modal">
          <div class="delete-modal-content">
            <div class="modal-icon">
              <i class="bi bi-question-circle"></i>
            </div>
            <h3>저장 취소</h3>
            <p>{{ deleteTarget.title }}</p>
            <p class="sub-text">저장 목록에서 삭제할까요?</p>
            <div class="delete-modal-buttons">
              <button class="cancel-btn" @click="cancelDelete">
                <i class="bi bi-x"></i> 취소
              </button>
              <button class="confirm-btn" @click="confirmDeleteAction">
                <i class="bi bi-trash"></i> 삭제
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
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
const isHovering = ref(false);

// API 키를 직접 사용하는 대신 경변수에서 가져
const API_KEY = import.meta.env.VITE_YOUTUBE_API_KEY;

// 필링된 디오 목록
const filteredVideos = computed(() => {
  switch (activeTab.value) {
    case 'completed':
      return savedVideos.value.filter(video => video.progressRate >= 100 || video.isCompleted);
    case 'inProgress':
      return savedVideos.value.filter(video => video.progressRate < 100 && !video.isCompleted);
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
    
    if (response.data.success && response.data.videos) {
      // 각 비디오의 상세 정보와 재생시간을 함께 가져오기
      const videosWithDetails = await Promise.all(
        response.data.videos.map(async (savedVideo) => {
          try {
            const videoResponse = await axios.get(`/api/videos/${savedVideo.vno}`);
            
            // YouTube API로 비디오 상세 정보 가져오기
            const youtubeResponse = await axios.get(
              `https://www.googleapis.com/youtube/v3/videos`,
              {
                params: {
                  part: 'contentDetails',
                  id: videoResponse.data.videoId,
                  key: API_KEY
                }
              }
            );

            // YouTube duration string을 분으로 변환
            let duration = 0;
            if (youtubeResponse.data.items && youtubeResponse.data.items[0]) {
              duration = convertYouTubeDuration(youtubeResponse.data.items[0].contentDetails.duration);
            }

            return {
              ...savedVideo,
              ...videoResponse.data,
              duration: duration, // 영상 길이(분)
              image: videoResponse.data.videoId 
                ? `https://img.youtube.com/vi/${videoResponse.data.videoId}/mqdefault.jpg`
                : null,
              progressRate: savedVideo.progressRate || 0,
              isCompleted: savedVideo.isCompleted || false
            };
          } catch (error) {
            console.error(`비디 ${savedVideo.vno} 상 정�� 가져오기 실패:`, error);
            return savedVideo;
          }
        })
      );

      savedVideos.value = videosWithDetails;
    }
  } catch (error) {
    console.error('저장된 영상 로딩 실패:', error);
  } finally {
    isLoading.value = false;
  }
};

// YouTube duration string을 분로 변환하는 함수
const convertYouTubeDuration = (duration) => {
  const match = duration.match(/PT(\d+H)?(\d+M)?(\d+S)?/);
  
  const hours = (match[1] ? parseInt(match[1].slice(0, -1)) : 0);
  const minutes = (match[2] ? parseInt(match[2].slice(0, -1)) : 0);
  const seconds = (match[3] ? parseInt(match[3].slice(0, -1)) : 0);
  
  return hours * 60 + minutes + seconds / 60;
};

// 비디오 장 취소
const unsaveVideo = async (videoNo) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    const response = await axios.delete(`/api/saved-videos/${videoNo}`, {
      params: { userNo: userInfo.userNo }
    });
    
    if (response.data.success) {
      // localStorage에서 해당 비디오의 재생 시간 정보 삭제
      const video = savedVideos.value.find(v => v.vno === videoNo);
      if (video && video.videoId) {
        const savedTimes = JSON.parse(localStorage.getItem('videoTimes') || '{}');
        delete savedTimes[video.videoId];
        localStorage.setItem('videoTimes', JSON.stringify(savedTimes));
      }
      
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
  progressInterval = setInterval(() => {
    updateProgress();
    // 현재 재생 위치 저장 (매 1초마다)
    if (player && selectedVideo.value) {
      const currentTime = player.getCurrentTime();
      saveVideoTime(selectedVideo.value.videoId, currentTime);
    }
  }, 1000); // 1초마다 업데이트
};

// 진도율 추 중지
const stopProgressTracking = () => {
  if (progressInterval) {
    clearInterval(progressInterval);
    updateProgress(); // 마지 상태 저장
  }
};

// 디디오 재생 위치 저장 함수 수정
const saveVideoTime = (videoId, currentTime) => {
  const savedTimes = JSON.parse(localStorage.getItem('videoTimes') || '{}');
  savedTimes[videoId] = currentTime;
  localStorage.setItem('videoTimes', JSON.stringify(savedTimes));
};

// 저장된 재생 위치 가져오기 함수
const getSavedTime = (videoId) => {
  const savedTimes = JSON.parse(localStorage.getItem('videoTimes') || '{}');
  return savedTimes[videoId] || 0;
};

// 재생바 관련 함수들 수정
const handleSeek = (event) => {
  if (!player || !duration.value) return;
  
  const progressBar = event.currentTarget;
  const rect = progressBar.getBoundingClientRect();
  const clickPosition = (event.clientX - rect.left) / rect.width;
  const newTime = duration.value * clickPosition;
  
  player.seekTo(newTime, true);
  currentProgress.value = clickPosition * 100;
  currentTime.value = newTime;
};

// 진행률 업데이트 함수 수정
const updateProgress = async () => {
  if (!player || !selectedVideo.value) return;
  
  try {
    const videoDuration = player.getDuration();
    const videoCurrentTime = player.getCurrentTime();
    
    duration.value = videoDuration;
    currentTime.value = videoCurrentTime;
    
    const progress = (videoCurrentTime / videoDuration) * 100;
    currentProgress.value = Math.min(100, progress);
    
    const roundedProgress = Math.floor(progress);
    const savedVideo = savedVideos.value.find(v => v.vno === selectedVideo.value.vno);
    
    // 현재 진행률이 저장된 진도율보다 높을 때만 업데이트
    if (savedVideo && roundedProgress > savedVideo.progressRate) {
      await updateVideoProgress(selectedVideo.value.vno, roundedProgress);
      
      const videoIndex = savedVideos.value.findIndex(v => v.vno === selectedVideo.value.vno);
      if (videoIndex !== -1) {
        savedVideos.value[videoIndex].progressRate = roundedProgress;
        savedVideos.value = [...savedVideos.value];
      }
    }
  } catch (error) {
    console.error('진행률 업데이트 실패:', error);
  }
};

// 진도율 업데이트 API 호출 함수
const updateVideoProgress = async (vno, progressRate) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    if (!userInfo) return;

    const response = await axios.put(`/api/saved-videos/${vno}/progress`, null, {
      params: {
        userNo: userInfo.userNo,
        progressRate: progressRate
      }
    });
    
    if (response.data.success) {
      console.log(`진도율 ${progressRate}% 업데이트 성공`);
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

// 비오 선택 및 모달 열기 함수 수정
const goToVideo = async (vno) => {
  try {
    const video = savedVideos.value.find(v => v.vno === vno);
    if (video && video.videoId) {
      // 새로 추가된 영상인지 확인
      if (video.progressRate === 0) {
        // localStorage에 이전 시청 기록 삭제
        const savedTimes = JSON.parse(localStorage.getItem('videoTimes') || '{}');
        delete savedTimes[video.videoId];
        localStorage.setItem('videoTimes', JSON.stringify(savedTimes));
        
        // 진행률 초기화
        await resetVideoProgress(vno);
      }

      selectedVideo.value = video;
      
      // 디오 내용 가져오기
      const content = await fetchVideoContent(video.videoId);
      if (content) {
        // 내용을 상태에 저장
        videoContent.value = content;
      }
      
      currentProgress.value = video.progressRate || 0;
      isLoading.value = true;
      
      // body에 modal-open 클래스 추가
      document.body.classList.add('modal-open');
      // 스크롤바 너비 산하여 CSS 수로 설정
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
  return new Promise((resolve) => {
    if (!window.YT) {
      const tag = document.createElement('script');
      tag.src = 'https://www.youtube.com/iframe_api';
      const firstScriptTag = document.getElementsByTagName('script')[0];
      firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
      
      window.onYouTubeIframeAPIReady = () => {
        if (selectedVideo.value) {
          initPlayer();
        }
        resolve();
      };
    } else {
      resolve();
    }
  });
};

// player 상태 추가
const isPlayerReady = ref(false);

// initPlayer 함수 수정
const initPlayer = () => {
  if (!selectedVideo.value) return;
  
  try {
    player = new window.YT.Player(`video-player-${selectedVideo.value.vno}`, {
      events: {
        onReady: (event) => {
          player = event.target;  // player 객체 올바르게 할당
          isLoading.value = false;
          isPlayerReady.value = true;  // player 준비 완료
          duration.value = player.getDuration();
          
          // 저장된 재생 시간이 있다면 해당 위치로 이동
          const savedTime = getSavedTime(selectedVideo.value.videoId);
          if (savedTime > 0) {
            player.seekTo(savedTime, true);
          }
        },
        onStateChange: (event) => {
          switch(event.data) {
            case window.YT.PlayerState.PLAYING:
              isPlaying.value = true;
              hasStarted.value = true;
              startProgressTracking();
              break;
            case window.YT.PlayerState.PAUSED:
              isPlaying.value = false;
              stopProgressTracking();
              break;
            case window.YT.PlayerState.ENDED:
              isPlaying.value = false;
              stopProgressTracking();
              // 영상이 완전히 끝났을 때의 처리
              handleVideoEnd();
              break;
          }
        }
      }
    });
  } catch (error) {
    console.error('플레이어 초기화 실패:', error);
    isLoading.value = false;
  }
};

// 영상 종료 처리 함수 추가
const handleVideoEnd = async () => {
  try {
    const savedVideo = savedVideos.value.find(v => v.vno === selectedVideo.value.vno);
    if (savedVideo && !savedVideo.isCompleted) {
      // 진도율 100%로 업데이트
      await updateVideoProgress(selectedVideo.value.vno, 100);
      
      const videoIndex = savedVideos.value.findIndex(v => v.vno === selectedVideo.value.vno);
      if (videoIndex !== -1) {
        savedVideos.value[videoIndex].progressRate = 100;
        savedVideos.value[videoIndex].isCompleted = true;
        checkForBadges(savedVideos.value[videoIndex]);
      }
      
      // 재생 시간 초기화
      saveVideoTime(selectedVideo.value.videoId, 0);
      
      // 영상 종료 후 모달 닫기 및 메시지 표시
      setTimeout(() => {
        closeVideo();
        displayToast('축하합니다! 영상을 완료하셨습니다. 🎉');
      }, 1000);  // 1초 후 닫기
    }
  } catch (error) {
    console.error('영상 종료 처리 실패:', error);
  }
};

// 모달 닫기 함수 수정
const closeVideo = () => {
  if (player && selectedVideo.value) {
    // 현재 재생 위치 저장
    const currentTime = player.getCurrentTime();
    saveVideoTime(selectedVideo.value.videoId, currentTime);
    
    player.stopVideo();
  }
  
  hasStarted.value = false;
  stopProgressTracking();
  selectedVideo.value = null;
  isLoading.value = true;
  
  document.body.classList.remove('modal-open');
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
      // 왼쪽 방키: 5초 뒤로
      e.preventDefault();
      const newTime = Math.max(0, player.getCurrentTime() - 5);
      player.seekTo(newTime, true);
    } else if (e.key === 'ArrowRight') {
      // 오른쪽 방향키: 5초 앞으
      e.preventDefault();
      const newTime = Math.min(player.getDuration(), player.getCurrentTime() + 5);
      player.seekTo(newTime, true);
    } else if (e.key === 'ArrowUp') {
      // 위쪽 방향키: 볼륨 증가
      e.preventDefault();
      const newVolume = Math.min(100, volume.value + 5);
      updateVolume({ target: { value: newVolume } });
    } else if (e.key === 'ArrowDown') {
      // 아래쪽 방향키: 볼륨 소
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
  if (!player || !isPlayerReady.value) {
    console.log('플레이어가 아직 준비되지 않았습니다.');
    return;
  }
  
  try {
    if (isPlaying.value) {
      player.pauseVideo();
    } else {
      player.playVideo();
    }
  } catch (error) {
    console.error('재생/일시정지 전환 실패:', error);
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
  
  // 볼 슬라더의 배경 업데이트
  const volumeSlider = event.target;
  volumeSlider.style.setProperty('--volume-percentage', `${newVolume}%`);
  
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

  // 볼륨 변경 알 표시
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
  if (!videoId) return null;
  
  try {
    const response = await axios.get('https://www.googleapis.com/youtube/v3/videos', {
      params: {
        part: 'snippet',
        id: videoId,
        key: API_KEY
      }
    });

    if (response.data.items && response.data.items.length > 0) {
      return response.data.items[0].snippet;
    }
    return null;
  } catch (error) {
    console.error('비디오 내용 가져오기 실패:', error);
    return null;
  }
};

// 레벨 계산
const userLevel = computed(() => {
  const level = Math.floor(completedCount.value / 5) + 1; // 5개 완료할 때다 레벨업
  return level;
});

// 다음 레벨까지 남은 영상 수
const videosUntilNextLevel = computed(() => {
  return 5 - (completedCount.value % 5);
});

// 계를 위한 computed 속성들
const formatTotalTime = computed(() => {
  const totalMinutes = savedVideos.value.reduce((total, video) => {
    const duration = video.duration || 0;
    const progress = video.progressRate || 0;
    return total + (duration * progress / 100);
  }, 0);
  
  const hours = Math.floor(totalMinutes / 60);
  const minutes = Math.floor(totalMinutes % 60);
  
  if (hours > 0) {
    return `${hours}시간 ${minutes}분`;
  }
  return `${minutes}분`;
});

const completedCount = computed(() => {
  return savedVideos.value.filter(video => 
    video.progressRate >= 100 || video.isCompleted
  ).length;
});

const inProgressCount = computed(() => {
  return savedVideos.value.filter(video => 
    video.progressRate > 0 && video.progressRate < 100 && !video.isCompleted
  ).length;
});

const averageProgress = computed(() => {
  if (savedVideos.value.length === 0) return 0;
  const total = savedVideos.value.reduce((sum, video) => 
    sum + (video.progressRate || 0), 0
  );
  return Math.round(total / savedVideos.value.length);
});

// 재생바 관련 상태
const progressBarRef = ref(null);

// 재생바 클릭 처리
const handleProgressBarClick = (event) => {
  if (!player || !progressBarRef.value) return;
  
  const rect = progressBarRef.value.getBoundingClientRect();
  const clickPosition = (event.clientX - rect.left) / rect.width;
  
  // 이미 완료된 영상이 아닐 경우에만 진행 업데이트
  const savedVideo = savedVideos.value.find(v => v.vno === selectedVideo.value.vno);
  if (!savedVideo || (!savedVideo.isCompleted && savedVideo.progressRate < 100)) {
    const newTime = player.getDuration() * clickPosition;
    player.seekTo(newTime, true);
    currentProgress.value = Math.min(100, clickPosition * 100);
  }
};

// 첫 완강 뱃지
const isFirstComplete = (video) => {
  const completedVideos = savedVideos.value.filter(v => v.progressRate >= 100);
  return completedVideos.length === 1 && completedVideos[0].vno === video.vno;
};

// 빠른 학습 뱃지
const isQuickLearner = (video) => {
  const watchTime = video.watchTime || 0;
  const videoDuration = video.duration || 0;
  return watchTime > 0 && watchTime <= videoDuration * 1.2;
};

// 벽 시청 뱃지
const isPerfectWatch = (video) => {
  return video.progressRate >= 95; // 95% 이상 시청한 경우
};

const badges = ref([]); // 뱃지 상태 관리

// 뱃지 추가 함수
const addBadge = (badge) => {
  if (!badges.value.includes(badge)) {
    badges.value.push(badge);
    console.log(`뱃지 획득: ${badge}`);
  }
};

// 비디오 완료 시 뱃지 부여
const checkForBadges = (video) => {
  if (video.progressRate >= 100) {
    if (completedCount.value === 1) {
      addBadge("첫 완강!");
    }
    if (completedCount.value === 10) {
      addBadge("10개 완료!");
    }
    // 추가적인 뱃지 조건을 여기에 추가할 수 있습니.
  }
};

const handleImageError = (event, video) => {
  // 이미지 로드 실패시 기본 이미���로 대체
  event.target.src = 'path/to/fallback-image.jpg'; // 기본 이미지 경로를 지정하세요
  console.error(`Failed to load thumbnail for video ${video.vno}`);
};

// 컴포넌트 상단에 ref 추가
const showToast = ref(false);
const toastMessage = ref('');

// 토스트 메시지 표시 함수
const displayToast = (message) => {
  toastMessage.value = message;
  showToast.value = true;
  setTimeout(() => {
    showToast.value = false;
  }, 3000);
};

// 디오 시청 시작 시점 초기화 함수 추가
const resetVideoProgress = async (videoNo) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    await axios.put(`/api/saved-videos/${videoNo}/progress`, null, {
      params: {
        userNo: userInfo.userNo,
        progressRate: 0
      }
    });

    const videoIndex = savedVideos.value.findIndex(v => v.vno === videoNo);
    if (videoIndex !== -1) {
      savedVideos.value[videoIndex].progressRate = 0;
      savedVideos.value[videoIndex].isCompleted = false;
    }
  } catch (error) {
    console.error('행률 초기화 실패:', error);
  }
}

// 메모 관련 상
const showMemoInput = ref(false);
const memoPosition = ref({ x: 0, y: 0 });
const newMemoText = ref('');
const savedMemos = ref([]); // 저장된 메모 목록
const editingMemoId = ref(null);
const editingMemoText = ref('');
const memoInput = ref(null);

// 더블클릭 이벤트 핸들러
const handleMemoDoubleClick = (event) => {
  const rect = event.target.getBoundingClientRect();
  memoPosition.value = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top
  };
  showMemoInput.value = true;
  
  nextTick(() => {
    memoInput.value?.focus();
  });
};

// 메모 저장
const saveMemoWithAnimation = () => {
  if (!newMemoText.value.trim()) return;
  
  const memo = {
    id: Date.now(),
    content: newMemoText.value,
    timestamp: Math.floor(player.getCurrentTime()),
    createdAt: new Date()
  };
  
  savedMemos.value.push(memo);
  newMemoText.value = '';
  showMemoInput.value = false;
};

// 메모 관리 함수들
const seekToTimestamp = (timestamp) => {
  if (player) {
    player.seekTo(timestamp, true);
  }
};

const deleteMemo = (memoId) => {
  savedMemos.value = savedMemos.value.filter(m => m.id !== memoId);
};

const handleBlur = () => {
  if (newMemoText.value.trim()) {
    saveMemoWithAnimation();
  }
  showMemoInput.value = false;
};

// startVideo 함수 수정
const startVideo = () => {
  if (!player || !isPlayerReady.value) {
    console.log('플레이어가 아직 준비되지 않았습니다.');
    return;
  }
  
  try {
    player.playVideo();
    hasStarted.value = true;
    isPlaying.value = true;
  } catch (error) {
    console.error('비디오 재생 시작 실패:', error);
  }
};

// 커스텀 오버레이 클릭 핸들러 수정
const handleOverlayClick = () => {
  if (isPlayerReady.value) {
    startVideo();
  } else {
    console.log('플레이어가 준비되길 기다리는 중...');
  }
};

// 상태 추가
const showDeleteModal = ref(false);
const deleteTarget = ref({ vno: null, title: '' });

// 삭제 확인 함수 수정
const confirmDelete = (videoNo, videoTitle) => {
  deleteTarget.value = { vno: videoNo, title: videoTitle };
  showDeleteModal.value = true;
};

// 삭제 취소
const cancelDelete = () => {
  showDeleteModal.value = false;
  deleteTarget.value = { vno: null, title: '' };
};

// 삭제 실행
const confirmDeleteAction = async () => {
  if (deleteTarget.value.vno) {
    await unsaveVideo(deleteTarget.value.vno);
    showDeleteModal.value = false;
    deleteTarget.value = { vno: null, title: '' };
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
  width: 100%;
  padding-top: 56.25%; /* 16:9 비율 */
  overflow: hidden;
}

.thumbnail-wrapper img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.thumbnail-wrapper:hover img {
  transform: scale(1.05);
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
  display: flex;
  flex-direction: row;
  width: 90%;
  max-width: 1400px;
  background: #1a1a1a;
  border-radius: 12px;
  overflow: hidden;
}



.custom-player-container {
  flex: 1;
  min-width: 0;
}

.memo-sidebar {
  width: 300px;
  background: #242424;
  padding: 15px;
  border-left: 1px solid #333;
  overflow-y: auto;
  max-height: 100%;
}

.modal-memo {
  position: absolute;
  top: 60px;
  left: 0;
  width: 100%;
  height: calc(100% - 140px);
  z-index: 3;
  pointer-events: auto;
}

.player-wrapper {
  position: relative;
  padding-top: 56.25%;
  width: 100%;
}

.player-wrapper iframe {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
}

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
  z-index: 4;
  pointer-events: auto;
}

.custom-controls.show-controls {
  opacity: 1;
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
  margin: 0 15px;
  position: relative;
  padding: 10px 0;
}

.progress-bar {
  height: 6px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  cursor: pointer;
  position: relative;
  overflow: visible;
  transition: all 0.2s ease;
}

.progress-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}

.progress-filled {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: linear-gradient(90deg, #FFD700, #FFA500);
  border-radius: 10px;
  transition: width 0.1s linear;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.progress-handle {
  position: absolute;
  top: 50%;
  width: 12px;
  height: 12px;
  background: #FFF;
  border: 2px solid #FFD700;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
  transition: all 0.2s ease;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.progress-bar:hover {
  height: 8px;
}

.progress-bar:hover .progress-handle {
  opacity: 1;
  width: 16px;
  height: 16px;
  border-color: #FFA500;
}

.progress-bar:hover .progress-filled {
  background: linear-gradient(90deg, #FFD700, #FFDB58);
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
}

.time-display {
  position: absolute;
  bottom: -18px;
  left: 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  font-family: 'Arial', sans-serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

/* 호버 효과 */
.progress-bar:hover .progress-filled {
  background: linear-gradient(90deg, #FFD700, #FFDB58);
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
}

.progress-bar:hover .progress-handle {
  border-color: #FFA500;
  background: #FFF;
  transform: translate(-50%, -50%) scale(1.1);
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
  height: 32px
}

.volume-btn:hover {
  color: #FFF;
  transform: scale(1.1);
  background: rgba(255, 215, 0, 0.3);
  animation: pulse 1s infinite;
}

.volume-slider {

  width: 80px;
  height: 4px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 2px;
  outline: none;
  transition: all 0.3s ease;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.volume-control:hover .volume-slider {
  opacity: 1;
}

/* Webkit (Chrome, Safari, Edge) 브라우저용 스타일 */
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

.volume-slider::-webkit-slider-runnable-track {
  background: linear-gradient(
    to right,
    #FFD700 var(--volume-percentage, 100%),
    rgba(255, 255, 255, 0.2) var(--volume-percentage, 100%)
  );
  border-radius: 2px;
  height: 4px;
}

/* Firefox 브라우저용 스타일 */
.volume-slider::-moz-range-thumb {
  width: 12px;
  height: 12px;
  background: #FFD700;
  border: 2px solid #FFF;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.volume-slider::-moz-range-progress {
  background-color: #FFD700;
  height: 4px;
  border-radius: 2px;
}

.volume-slider::-moz-range-track {
  background-color: rgba(255, 255, 255, 0.2);
  height: 4px;
  border-radius: 2px;
}

/* 호버 효과 */
.volume-slider::-webkit-slider-thumb:hover {
  transform: scale(1.2);
  background: #FFED4A;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
}

.volume-slider::-moz-range-thumb:hover {
  transform: scale(1.2);
  background: #FFED4A;
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.7);
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

.modal-header {
  padding: 15px 20px;
  background: #222;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h2 {
  color: #fff;
  font-size: 1.2rem;
  margin: 0;
  max-width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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
  .modal-content {
    width: 95%;
  }
  
  .modal-header h2 {
    font-size: 1rem;
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
  z-index: 3;
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
  margin-left: auto; /* 오쪽 정렬 */
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

/* 전체화면 버튼 애니메션 */
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

/* 전체화면 시 컨트롤 위치 조 */
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

/* 모달 오픈 시 body 타일 */
:root {
  --scrollbar-width: 0px;
}

body.modal-open {
  overflow: hidden;
  padding-right: var(--scrollbar-width); /* 스크롤바 너비만큼 패딩 추가 */
}

/* YouTube 브랜딩 숨기기를 위한 스타일 추가 */
.youtube-player {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

/* iframe 위에 투명한 오버레이 추가 */
.player-wrapper::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  pointer-events: none;
}

/* 트롤바의 z-index 증가 */
.custom-controls {
  z-index: 2;
}

/* YouTube 워터마크와 타이틀 숨기기 */
.ytp-chrome-top,
.ytp-watermark,
.ytp-title-channel,
.ytp-title,
.ytp-share-button,
.ytp-watch-later-button {
  display: none !important;
}

.video-card.completed {
  border: 2px solid #4CAF50;
  box-shadow: 0 0 10px rgba(76, 175, 80, 0.3);
}

.video-card.completed .progress-bar .progress {
  background-color: #4CAF50;
}

.video-card.completed .progress-text {
  color: #4CAF50;
  font-weight: bold;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #FFD700;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.no-data {
  text-align: center;
  padding: 2rem;
  color: #666;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-card i {
  font-size: 2rem;
  color: #DEB887;
}

.stat-info h3 {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.stat-info p {
  margin: 5px 0 0 0;
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

.youtube-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
  background: transparent;
}

:deep(.ytp-pause-overlay),
:deep(.ytp-chrome-top),
:deep(.ytp-chrome-bottom),
:deep(.ytp-watermark),
:deep(.ytp-show-cards-title),
:deep(.ytp-cards-button),
:deep(.ytp-cards-teaser),
:deep(.ytp-ce-element),
:deep(.ytp-endscreen-content),
:deep(.ytp-endscreen-previous),
:deep(.ytp-endscreen-next) {
  display: none !important;
}

.badges {
  display: flex;
  gap: 8px;
  margin: 8px 0;
  flex-wrap: wrap;
}

.badge {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  color: white;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  animation: badgeAppear 0.3s ease-out;
}

.badge i {
  font-size: 14px;
}

@keyframes badgeAppear {
  from {
    transform: scale(0.8);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 뱃지 호버 효과 */
.badge:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
  background: linear-gradient(135deg, #FFE44D, #FFB347);
}

.badge-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

/* 기존 스타일에 추가 */
.toast-message {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 15px 30px;
  border-radius: 25px;
  font-size: 16px;
  z-index: 9999;
  animation: toastFadeIn 0.5s, toastFadeOut 0.5s 2.5s;
}

@keyframes toastFadeIn {
  from {
    opacity: 0;
    transform: translate(-50%, 20px);
  }
  to {
    opacity: 1;
    transform: translate(-50%, 0);
  }
}

@keyframes toastFadeOut {
  from {
    opacity: 1;
    transform: translate(-50%, 0);
  }
  to {
    opacity: 0;
    transform: translate(-50%, 20px);
  }
}

/* 메모 입력 필드 스타일 */
.memo-input-container {
  position: absolute;
  z-index: 5;
  transform: translate(-50%, -50%);
}

.memo-input {
  padding: 8px 12px;
  border: 2px solid #FFD93D;
  border-radius: 20px;
  background: white;
  font-size: 14px;
  min-width: 150px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.memo-input:focus {
  outline: none;
  border-color: #FFB84C;
  box-shadow: 0 2px 12px rgba(255, 184, 76, 0.2);
}

/* 저장된 메모 스타일 */
.memo-item {
  position: absolute;
  background: rgba(255, 217, 61, 0.9);
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 13px;
  color: #4F4F4F;
  max-width: 200px;
  word-break: break-word;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  animation: memoAppear 0.3s ease-out;
  transition: transform 0.5s ease-out;
}

@keyframes memoAppear {
  from {
    opacity: 0;
    transform: scale(0.8);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.memo-slide {
  animation: slideRight 0.5s ease-out forwards;
}

@keyframes slideRight {
  from {
    transform: translateX(0);
  }
  to {
    transform: translateX(100px);
  }
}

/* 다크모드 스타일 */
:root.dark-mode .memo-input {
  background: #2a2a2a;
  color: #fff;
  border-color: #FFB84C;
}

:root.dark-mode .memo-item {
  background: rgba(255, 184, 76, 0.8);
  color: #1a1a1a;
}

.modal-content {
  display: flex;
  width: 90%;
  max-width: 1400px;
}

.memo-sidebar {
  width: 300px;
  background: #242424;
  padding: 15px;
  border-left: 1px solid #333;
}

.memo-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.memo-item {
  background: #2a2a2a;
  border-radius: 8px;
  padding: 12px;
  position: relative;
}

.memo-timestamp {
  color: #FFD700;
  font-size: 0.9em;
  cursor: pointer;
  margin-bottom: 5px;
}

.memo-content {
  color: #fff;
  word-break: break-word;
}

.memo-delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: none;
  border: none;
  color: #ff4444;
  cursor: pointer;
  font-size: 18px;
  padding: 4px;
}

.memo-delete-btn:hover {
  color: #ff6666;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 5;
}

.close-btn i {
  color: white;
  font-size: 20px;
}

.close-btn:hover {
  background: rgba(255, 0, 0, 0.7);
  transform: scale(1.1);
}

.thumbnail-wrapper {
  position: relative;
}

.delete-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  background: rgba(0, 0, 0, 0.7);
  border: none;
  border-radius: 50%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 2;
  opacity: 1;  /* 항상 보이도록 변경 */
}

.delete-btn i {
  font-size: 16px;
}

.delete-btn:hover {
  background: rgba(255, 0, 0, 0.8);
  transform: scale(1.1);
}

/* 모달 트랜지션 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

/* 삭제 ��인 모달 스타일 */
.delete-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.delete-modal {
  background: white;
  border-radius: 20px;
  padding: 30px;
  width: 90%;
  max-width: 360px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  animation: modalPop 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.delete-modal-content {
  text-align: center;
}

.modal-icon {
  width: 70px;
  height: 70px;
  background: #FFE4E1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.modal-icon i {
  font-size: 32px;
  color: #FF6B6B;
}

.delete-modal h3 {
  color: #333;
  margin-bottom: 12px;
  font-size: 1.4rem;
  font-weight: 600;
}

.delete-modal p {
  color: #666;
  margin-bottom: 8px;
  font-size: 1rem;
  word-break: break-word;
}

.sub-text {
  font-size: 0.9rem;
  color: #888;
  margin-bottom: 24px;
}

.delete-modal-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.delete-modal button {
  padding: 12px 24px;
  border-radius: 50px;
  border: none;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.cancel-btn:hover {
  background: #e5e5e5;
  transform: translateY(-2px);
}

.confirm-btn {
  background: #FF6B6B;
  color: white;
}

.confirm-btn:hover {
  background: #FF5252;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

/* 모달 애니메이션 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

@keyframes modalPop {
  0% {
    opacity: 0;
    transform: scale(0.95) translateY(10px);
  }
  100% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style> 