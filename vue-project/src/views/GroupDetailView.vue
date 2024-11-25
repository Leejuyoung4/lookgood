<template>
  <div class="detail-container">
    <!-- 게시글 헤더 -->
    <div class="post-header">
      <div class="status-wrapper">
        <div class="status-tag" :class="{ 'ongoing': !group.boardIsResolved, 'completed': group.boardIsResolved }">
          {{ group.boardIsResolved ? '모집완료' : '모집중' }}
        </div>
        
        <!-- 작성자일 때만 버튼 표시 -->
        <button 
          v-if="isCurrentUserAuthor"
          @click="toggleResolveStatus" 
          class="status-toggle-button"
        >
          {{ group.boardIsResolved ? '모집중으로 변경' : '모집완료로 변경' }}
        </button>
      </div>
      <h1 class="post-title">{{ group.boardTitle }}</h1>
      <div class="post-info">
        <span class="author">{{ group.boardAuthor }}</span>
        <span class="date">{{ formatDate(group.boardRegDate) }}</span>
        <span class="views">조회수 {{ group.boardViews }}</span>
      </div>
    </div>

    <!-- 게시글 본문 -->
    <div class="post-content">
      {{ group.boardContent }}
    </div>

    <!-- 이미지 갤러 -->
    <div v-if="group.boardFilesList?.length" class="image-gallery">
      <div v-for="(image, index) in group.boardFilesList" 
           :key="index" 
           class="image-container"
           @click="openImageModal(image)">
        <img :src="`http://localhost:8080/uploads/${image}`" :alt="`첨부 이미지 ${index + 1}`">
      </div>
    </div>

    <!-- 좋아요 & 공유 버튼 -->
    <div class="action-buttons">
      <button class="like-button" @click="toggleLike" :class="{ 'liked': isLiked }">
        <i class="fas fa-heart"></i>
        <span>{{ likeCount }}</span>
      </button>
      <button class="share-button" @click="sharePost">
        <i class="fas fa-share"></i>
        공유하기
      </button>
    </div>

    <!-- 댓글 섹션 -->
    <div class="comments-section">
      <h3>댓글 {{ comments.length }}개</h3>
      
      <!-- 댓글 작성 폼 -->
      <div class="comment-form">
        <textarea 
          v-model="newComment" 
          placeholder="댓글을 작성해주세요"
          :disabled="!isLoggedIn"
        ></textarea>
        <button @click="submitComment" :disabled="!isLoggedIn">
          댓글 작성
        </button>
      </div>

      <!-- 댓글 목록 -->
      <div class="comments-list">
        <div v-for="comment in comments" :key="comment.boardCommentNo" class="comment">
          <div class="comment-header">
            <span class="comment-author">{{ comment.userNickname }}</span>
            <span class="comment-date">{{ formatDate(comment.boardCommentRegDate) }}</span>
          </div>
          <div class="comment-content">{{ comment.boardCommentContent }}</div>
          <div class="comment-actions" v-if="isCommentAuthor(comment)">
            <button @click="deleteComment(comment.boardCommentNo)">삭제</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 이미지 모달 -->
    <div v-if="showImageModal" class="image-modal" @click="closeImageModal">
      <div class="modal-content">
        <img :src="`http://localhost:8080/uploads/${selectedImage}`" alt="확대된 이미지">
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const group = ref({});
const comments = ref([]);
const newComment = ref('');
const isLiked = ref(false);
const likeCount = ref(0);
const showImageModal = ref(false);
const selectedImage = ref(null);

// 로그인 상태 확인
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('userInfo');
});

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 게시글 데이터 로드
const loadPost = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/group/${route.params.boardNo}`);
    console.log('불러온 전체 게시글 데이터:', response.data);
    group.value = response.data;
    
    // 데이터 구조 확인
    console.log('게시글 작성자 정보:', {
      boardAuthor: group.value.boardAuthor,
      userNo: group.value.userNo,
      전체데이터: group.value
    });
  } catch (error) {
    console.error('게시글 로드 실패:', error);
  }
};

// 댓글 로드
const loadComments = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/group/comment/${route.params.boardNo}`);
    console.log('댓글 데이터:', response.data);
    comments.value = response.data;
  } catch (error) {
    console.error('댓글 로드 실패:', error);
  }
};

// 좋아요 상태 확인
const checkLikeStatus = async () => {
  if (!isLoggedIn.value) return;
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    const response = await axios.get(`http://localhost:8080/api/group/${route.params.boardNo}/like`, {
      params: {
        userNo: userInfo.userNo
      }
    });
    isLiked.value = response.data;
  } catch (error) {
    console.error('좋아요 상태 확인 실패:', error);
  }
};

// 좋아요 토글
const toggleLike = async () => {
  if (!isLoggedIn.value) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }

  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    const response = await axios.put(
      `http://localhost:8080/api/group/${route.params.boardNo}/like`, 
      null,  // PUT 요청의 body는 null
      {
        params: {
          userNo: userInfo.userNo
        }
      }
    );
    
    isLiked.value = response.data; // 서버에서 반환하는 boolean 값으로 설정
    // 좋아요 상태가 변경되었으므로 게시글 정보를 다시 불러옴
    await loadPost();
  } catch (error) {
    console.error('좋아요 처리 실패:', error);
    alert('좋아요 처리에 실패했습니다.');
  }
};

// 댓글 작성
const submitComment = async () => {
  if (!isLoggedIn.value) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }

  if (!newComment.value.trim()) {
    alert('댓글 내용을 입력해주세요.');
    return;
  }

  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    await axios.post(`http://localhost:8080/api/group/comment/${route.params.boardNo}`, {
      userNo: userInfo.userNo,
      boardCommentContent: newComment.value
    });

    newComment.value = '';
    await loadComments();
  } catch (error) {
    console.error('댓글 작성 실패:', error);
    alert('댓글 작성에 실패했습니다.');
  }
};

// 댓글 삭제
const deleteComment = async (commentNo) => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return;

  try {
    await axios.delete(`http://localhost:8080/api/group/comment/${commentNo}`);
    await loadComments();
  } catch (error) {
    console.error('댓글 삭제 실패:', error);
    alert('댓글 삭제에 실패했습니다.');
  }
};

// 댓글 수정 (필요한 경우)
const updateComment = async (commentNo, content) => {
  try {
    await axios.put(`http://localhost:8080/api/group/comment/${commentNo}`, {
      boardCommentContent: content
    });
    await loadComments();
  } catch (error) {
    console.error('댓글 수정 실패:', error);
    alert('댓글 수정에 실패했습니��.');
  }
};

// 댓글 좋아요
const likeComment = async (commentNo) => {
  if (!isLoggedIn.value) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }

  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    await axios.post(`http://localhost:8080/api/group/comment/${commentNo}/like`, userInfo.userNo);
    await loadComments();
  } catch (error) {
    console.error('댓글 좋아요 실패:', error);
    alert('좋아요 처리에 실패했습니다.');
  }
};

// 이미지 모달
const openImageModal = (image) => {
  selectedImage.value = image;
  showImageModal.value = true;
};

const closeImageModal = (e) => {
  if (e.target.classList.contains('image-modal')) {
    showImageModal.value = false;
  }
};

// 댓글 작성자 확인
const isCommentAuthor = (comment) => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    return userInfo && comment.userNo === userInfo.userNo;
  } catch (error) {
    console.error('사용자 정보 확인 실패:', error);
    return false;
  }
};

// 작성자 확인 computed 속성
const isCurrentUserAuthor = computed(() => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    // 닉네임으로 비교
    return userInfo && group.value && userInfo.userNickname === group.value.boardAuthor;
  } catch (error) {
    console.error('작성자 확인 중 오류:', error);
    return false;
  }
});

// 모집 상태 변경 함수
const toggleResolveStatus = async () => {
  const userInfo = JSON.parse(localStorage.getItem('userInfo'));
  
  // 닉네임으로 한번 더 검증
  if (!userInfo || userInfo.userNickname !== group.value.boardAuthor) {
    alert('게시글 작성자만 변경할 수 있습니다.');
    return;
  }

  try {
    const updatedGroup = {
      ...group.value,
      boardIsResolved: !group.value.boardIsResolved
    };

    const response = await axios.put(
      `http://localhost:8080/api/group/${route.params.boardNo}`,
      updatedGroup
    );
    
    if (response.status === 200) {
      group.value.boardIsResolved = !group.value.boardIsResolved;
      alert(group.value.boardIsResolved ? '모집완료로 변경되었습니다.' : '모집중으로 변경되었습니다.');
    }
  } catch (error) {
    console.error('상태 변경 실패:', error);
    alert('상태 변경에 실패했습니다.');
  }
};

// 컴포넌트 마운트 시 데이터 로드
onMounted(async () => {
  console.log('라우트 파라미터:', route.params);
  await loadPost();
  await loadComments();
});

</script>

<style scoped>
.detail-container {
  padding: 25px;
  background-color: #f9f9f9; /* 부드러운 배경색 */
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 */
  margin: 30px auto;
  max-width: 800px;
  border-radius: 12px;
  font-family: 'Noto Sans KR', sans-serif;
  line-height: 1.6;
  color: #333;
  position: relative; /* 내 요소 배치 조정 가능 */
}

.post-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.status-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.status-tag {
  font-size: 14px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 5px;
  color: white;
  margin-right: 10px;
}

.ongoing {
  background-color: #ffd987;
}

.completed {
  background-color: #ccc;
}

.post-title {
  font-size: 28px;
  font-weight: bold;
  color: #555;
  border-bottom: 3px solid #ffd987;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.post-info {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  color: #666;
}

.author,
.date,
.views {
  margin-right: 10px;
}

.post-content {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.image-container {
  position: relative;
  cursor: pointer;
}

.image-container img {
  max-width: 100%;
  border-radius: 8px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.image-container:hover img {
  transform: scale(1.05);
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.2);
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
}

.like-button,
.share-button {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  font-size: 14px;
  font-weight: bold;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.like-button {
  background-color: #ffd987;
  color: white;
}

.like-button:hover {
  background-color: #f8cd71;
  transform: scale(1.05);
}

.share-button {
  background-color: #ccc;
  color: white;
}

.share-button:hover {
  background-color: #aaa;
  transform: scale(1.05);
}

.comments-section {
  margin-top: 20px;
}

.comment-form {
  margin-top: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

.comment-form textarea {
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 5px;
  resize: none;
}

.comment-form button {
  padding: 8px 12px;
  background-color: #ffd987;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.comment-form button:hover {
  background-color: #f8cd71;
  transform: scale(1.05);
}

.comments-list {
  margin-top: 10px;
}

.comment {
  padding: 10px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 10px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.comment-author {
  font-weight: bold;
}

.comment-date {
  font-size: 12px;
  color: #666;
}

.comment-content {
  font-size: 14px;
  color: #666;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 5px;
}

.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-content {
  max-width: 80%;
  max-height: 80%;
  object-fit: contain;
}

.status-toggle-button {
  padding: 5px 10px;
  background-color: #fff;
  border: 2px solid #ffd987;
  color: #666;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.status-toggle-button:hover {
  background-color: #ffd987;
  color: white;
  transform: translateY(-2px);
}

</style>
