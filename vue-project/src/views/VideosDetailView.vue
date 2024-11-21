<template>
  <div class="video-detail">
    <!-- 메인 비디오 섹션 -->
    <div class="main-content">
        <div class="main-video">
        <iframe
          class="video-player"
          :src="`https://www.youtube.com/embed/${videoId}`"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
      </div>

      <!-- 비디오 정보 섹션 -->
      <div class="video-info">
        <h1 class="title">{{ video.vtitle }}</h1>
        <div class="stats">
          <span class="views">조회수 {{ video.vviews }}회</span>
          <span class="date">{{ formatDate(video.vuploadDate) }}</span>
        </div>

        <!-- 액션 버튼 -->
        <div class="action-buttons">
          <button class="action-btn like" @click="handleLike">
            <i class="fas fa-thumbs-up"></i>
            <span>{{ video.likes || 0 }}</span>
          </button>
          <button class="action-btn save">
            <i class="fas fa-bookmark"></i>
            <span>저장</span>
          </button>
          <button class="action-btn share">
            <i class="fas fa-share"></i>
            <span>공유</span>
          </button>
        </div>
      </div>

      <!-- 강사 정보 -->
      <div class="instructor-section">
        <div class="instructor-info">
          <div class="instructor-details">
            <h3>{{ video.vinstructor }}</h3>
            <p class="instructor-intro">{{ video.vinstructorIntro }}</p>
          </div>
        </div>
      </div>

      <!-- 비디오 설명 -->
      <div class="video-description">
        <h3>강의 설명</h3>
        <p>{{ video.vdescription }}</p>
      </div>
    </div>

    <!-- 추천 비디오 사이드바 -->
    <div class="recommended-videos">
      <h2>다른 영상 추천</h2>
      <div v-for="rec in recommendedVideos" :key="rec.id" class="recommended-video">
        <img :src="rec.thumbnail" alt="thumbnail">
        <div class="rec-video-info">
          <h3>{{ rec.title }}</h3>
          <p>{{ rec.uploader }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

// route 정의 추가
const route = useRoute();


// 데이터 초기화
const video = ref({});
const comments = ref([]);
const recommendedVideos = ref([]);
const isAuthor = ref(false);

// 날짜 포맷팅 함수
const formatDate = (date) => {
  if (!date) return '';
  
  // date가 문자열인 경우 Date 객체로 변환
  const dateObj = typeof date === 'string' ? new Date(date) : date;
  
  return new Intl.DateTimeFormat('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).format(dateObj);
};

// 비디오 정보 가져오기
const fetchVideoDetails = async () => {
  try {
    // 임시로 하드코딩된 데이터 반환
    video.value = {
      vtitle: "스트림을 활용한 파이썬 웹앱 제작하기",
      vviews: "1,000,000",
      vuploadDate: new Date(),
      likes: 500000,
      vinstructor: "이경석",
      vinstructorIntro: "안녕하세요. 저는 싸피 4반의 원빈입니다~~",
      vdescription: "영상소개 내용 쓰면 될듯\n이 영상은 스트림을 활용한 파이썬 웹앱 제작에 관한 동영상입니다.\n가나다라마바사아자차카타파하"
    };
    // API 호출이 필요할 때는 아래 코드 사용
    // const response = await axios.get(`/api/videos/${route.params.id}`);
    // video.value = response.data;
  } catch (error) {
    console.error('비디오 로딩 실패:', error);
  }
};

// 좋아요/싫어요 처리
const handleLike = async () => {
  try {
    // 임시로 좋아요 수만 증가
    video.value.likes = (video.value.likes || 0) + 1;
    // API 호출이 필요할 때는 아래 코드 사용
    // await axios.post(`/api/videos/${video.value.id}/like`);
  } catch (error) {
    console.error('좋아요 처리 실패:', error);
  }
};

// 나머지 메서드들...

onMounted(() => {
  fetchVideoDetails();
});
</script>

<style scoped>
.video-detail {
  max-width: 1850px;
  margin: 0 auto;
  padding: 20px;
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
  background: #f9f9f9;
}

/* 메인 비디오 영역 */
.main-video {
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 12px;
  overflow: hidden;
  background: #000;
  margin-bottom: 20px;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 비디오 정보 영역 */
.video-info {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: #0f0f0f;
  margin-bottom: 12px;
}

.stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e5e5;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 18px;
  border: none;
  background: #f2f2f2;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-btn:hover {
  background: #e5e5e5;
}

/* 강사 정보 */
.instructor-section {
  padding: 16px 0;
  border-bottom: 1px solid #e5e5e5;
}

.instructor-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.instructor-details h3 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

/* 비디오 설명 */
.video-description {
  padding: 16px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #0f0f0f;
}

/* 추천 비디오 사이드바 */
.recommended-videos {
  position: sticky;
  top: 24px;
}

.recommended-videos h2 {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 12px;
}

.recommended-video {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  cursor: pointer;
}

.recommended-video img {
  width: 168px;
  height: 94px;
  border-radius: 8px;
  object-fit: cover;
}

.rec-video-info {
  flex: 1;
}

.rec-video-info h3 {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.rec-video-info p {
  font-size: 12px;
  color: #606060;
}

/* 반응형 디자인 */
@media (max-width: 1200px) {
  .video-detail {
    grid-template-columns: 1fr;
  }
  
  .recommended-videos {
    position: static;
  }
}

@media (max-width: 768px) {
  .video-detail {
    padding: 0;
  }
  
  .stats {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: space-between;
  }
  
  .recommended-video img {
    width: 120px;
    height: 67px;
  }
}
</style>