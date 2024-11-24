<template>
  <div class="mypage-container">
    <!-- 저장한 영상 섹션 -->
    <section class="saved-videos-section">
      <div class="section-header">
        <h2>저장한 영상</h2>
        <div class="playlist-controls">
          <select v-model="selectedPlaylist">
            <option value="">모든 영상</option>
            <option v-for="name in playlistNames" :key="name" :value="name">
              {{ name }}
            </option>
          </select>
          <button @click="showPlaylistModal = true" class="create-playlist-btn">
            <i class="bi bi-plus"></i> 새 플레이리스트
          </button>
        </div>
      </div>

      <!-- 영상 목록 -->
      <div class="video-grid">
        <div v-for="video in filteredVideos" :key="video.vNo" class="video-card">
          <!-- 기존 비디오 카드 내용 -->
          <div class="playlist-actions">
            <select 
              v-model="video.playlistName"
              @change="updateVideoPlaylist(video)"
            >
              <option value="">플레이리스트 선택</option>
              <option v-for="name in playlistNames" :key="name" :value="name">
                {{ name }}
              </option>
            </select>
          </div>
        </div>
      </div>
    </section>

    <!-- 플레이리스트 생성 모달 -->
    <div v-if="showPlaylistModal" class="modal-overlay">
      <div class="modal-content">
        <h3>새 플레이리스트</h3>
        <input 
          v-model="newPlaylistName" 
          placeholder="플레이리스트 이름"
          @keyup.enter="createPlaylist"
        >
        <div class="modal-actions">
          <button @click="showPlaylistModal = false">취소</button>
          <button @click="createPlaylist">만들기</button>
        </div>
      </div>
    </div>

    <div class="playlists-section">
      <h2>내 플레이리스트</h2>
      
      <div class="playlist-list">
        <div v-for="playlist in playlists" :key="playlist" class="playlist-item">
          <h3>{{ playlist }}</h3>
          <div class="video-grid">
            <div v-for="video in playlistVideos[playlist]" 
                 :key="video.vNo" 
                 class="video-card"
                 @click="goToVideo(video.vNo)">
              <img :src="getThumbUrl(video.videoId)" :alt="video.vTitle">
              <h4>{{ video.vTitle }}</h4>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'

const savedVideos = ref([])
const selectedPlaylist = ref('')
const showPlaylistModal = ref(false)
const newPlaylistName = ref('')

// 고유한 플레이리스트 이름 목록
const playlistNames = computed(() => {
  const names = new Set(savedVideos.value
    .map(v => v.playlistName)
    .filter(name => name)) // null/undefined 제거
  return Array.from(names)
})

// 선택된 플레이리스트에 따른 필터링
const filteredVideos = computed(() => {
  if (!selectedPlaylist.value) return savedVideos.value
  return savedVideos.value.filter(v => v.playlistName === selectedPlaylist.value)
})

// 비디오의 플레이리스트 업데이트
const updateVideoPlaylist = async (video) => {
  try {
    await axios.put(`/api/videos/${video.vNo}`, {
      playlistName: video.playlistName
    })
  } catch (error) {
    console.error('플레이리스트 업데이트 실패:', error)
  }
}

// 새 플레이리스트 생성 (실제로는 이름만 추가)
const createPlaylist = () => {
  if (playlistNames.value.includes(newPlaylistName.value)) {
    alert('이미 존재하는 플레이리스트 이름입니다.')
    return
  }
  showPlaylistModal.value = false
  newPlaylistName.value = ''
}
</script> 