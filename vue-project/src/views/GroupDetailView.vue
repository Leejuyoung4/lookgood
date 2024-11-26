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
        <span class="author">작성자: {{ userIds.get(group.userNo) || `사용자${group.userNo}` }}</span>
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
      <button 
        class="like-button" 
        :class="{ 'liked': isLiked }" 
        @click="toggleLike"
      >
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
            <div class="comment-author-info">
              <span class="comment-author">{{ userIds.get(comment.userNo) || `사용자${comment.userNo}` }}</span>
              <span v-if="comment.userNo === group.userNo" class="author-badge">글쓴이</span>
            </div>
            <span class="comment-date">{{ formatDate(comment.boardCommentRegDate) }}</span>
          </div>
          
          <div v-if="editingCommentId === comment.boardCommentNo" class="comment-edit-form">
            <textarea 
              v-model="editingCommentText" 
              class="edit-textarea"
            ></textarea>
            <div class="edit-buttons">
              <button @click="saveCommentEdit(comment.boardCommentNo)" class="save-button">저장</button>
              <button @click="cancelCommentEdit" class="cancel-button">취소</button>
            </div>
          </div>
          <div v-else class="comment-content">{{ comment.boardCommentContent }}</div>
          
          <div class="comment-actions" v-if="isCommentAuthor(comment)">
            <button @click="startCommentEdit(comment)" class="edit-button">수정</button>
            <button @click="deleteComment(comment.boardCommentNo)" class="delete-button">삭제</button>
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
import { ref, onMounted, computed, onUnmounted } from 'vue';
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

// 사용자 ID를 저장할 상태 추가
const userIds = ref(new Map());

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

// 사용자 ID를 가져오는 함수
const getUserId = async (userNo) => {
  // 이미 캐시된 ID가 있다면 반환
  if (userIds.value.has(userNo)) {
    return userIds.value.get(userNo);
  }

  try {
    const response = await axios.get(`http://localhost:8080/api/user/id/${userNo}`);
    if (response.data.success) {
      const userId = response.data.userId;
      userIds.value.set(userNo, userId);
      return userId;
    } else {
      return `사용자${userNo}`;
    }
  } catch (error) {
    console.error('사용자 ID 조회 실패:', error);
    return `사용자${userNo}`;
  }
};

// 게시글 로드
const loadPost = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/group/${route.params.boardNo}`);
    group.value = response.data;
    
    // 게시글 작성자 ID 조회
    if (group.value.userNo) {
      await getUserId(group.value.userNo);
    }
  } catch (error) {
    console.error('게시글 로드 실패:', error);
  }
};

// 댓글 로드
const loadComments = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/group/comment/${route.params.boardNo}`);
    comments.value = response.data;
    
    // 각 댓글 작성자의 ID 조회
    for (const comment of comments.value) {
      if (comment.userNo) {
        await getUserId(comment.userNo);
      }
    }
  } catch (error) {
    console.error('댓글 로드 실패:', error);
  }
};

// 좋아요 수 로드 함수 수정
const loadLikeCount = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/group/${route.params.boardNo}`);
    likeCount.value = response.data.boardLikeCount || 0;
  } catch (error) {
    console.error('좋아요 수 로드 실패:', error);
    likeCount.value = 0;
  }
};

// 좋아요 토글 함수 수정
const toggleLike = async () => {
  if (!isLoggedIn.value) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }

  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    
    await axios.put(
      `http://localhost:8080/api/group/${route.params.boardNo}/like`, 
      null,
      {
        params: { userNo: userInfo.userNo }
      }
    );
    
    // 좋아요 상태 토글
    isLiked.value = !isLiked.value;
    
    // 좋아요 수 업데이트
    if (isLiked.value) {
      likeCount.value++;
    } else {
      likeCount.value--;
    }
    
    // 애니메이션 효과
    const likeButton = document.querySelector('.like-button');
    likeButton.classList.add('animate');
    setTimeout(() => likeButton.classList.remove('animate'), 300);
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

// 글 삭제
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
    alert('댓글 수정에 실패했습니.');
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
    return userInfo && group.value && userInfo.userNo === group.value.userNo;
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

// 좋아요 상태 확인 함수 추가
const checkLikeStatus = async () => {
  if (!isLoggedIn.value) return;
  
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo'));
    const response = await axios.get(
      `http://localhost:8080/api/group/${route.params.boardNo}/like`,
      {
        params: { userNo: userInfo.userNo }
      }
    );
    isLiked.value = response.data;
  } catch (error) {
    console.error('좋아요 상태 확인 실패:', error);
    isLiked.value = false;
  }
};

// 댓글 수정 관련 상태 추가
const editingCommentId = ref(null);
const editingCommentText = ref('');

// 댓글 수정 시작
const startCommentEdit = (comment) => {
  editingCommentId.value = comment.boardCommentNo;
  editingCommentText.value = comment.boardCommentContent;
};

// 댓글 수정 취소
const cancelCommentEdit = () => {
  editingCommentId.value = null;
  editingCommentText.value = '';
};

// 댓글 수정 저장
const saveCommentEdit = async (commentNo) => {
  if (!editingCommentText.value.trim()) {
    alert('댓글 내용을 입력해주세요.');
    return;
  }

  try {
    await axios.put(`http://localhost:8080/api/group/comment/${commentNo}`, {
      boardCommentContent: editingCommentText.value
    });
    
    // 수정 모드 종료
    editingCommentId.value = null;
    editingCommentText.value = '';
    
    // 댓글 목록 새로고침
    await loadComments();
  } catch (error) {
    console.error('댓글 수정 실패:', error);
    alert('댓글 수정에 실패했습니다.');
  }
};

// onMounted 수정
onMounted(async () => {
  console.log('라우트 파라미터:', route.params);
  await loadPost();
  await loadComments();
  await loadLikeCount();  // 좋아요 수 로드
  await checkLikeStatus(); // 좋아요 상태 인
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
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  background-color: #e0e0e0; /* 기본 상태는 회색 */
  color: #666;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.like-button.liked {
  background-color: #ffd987; /* 좋아요 누른 상태는 노란색 */
  color: white;
}

.like-button i {
  font-size: 18px;
  transition: all 0.3s ease;
  color: #666; /* 기본 상태 아이콘 색상 */
}

.like-button.liked i {
  color: white; /* 좋아요 누른 상태의 아이콘 색상 */
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

.like-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  background-color: #e0e0e0; /* 기본 상태는 회색 */
  color: #666;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.like-button.liked {
  background-color: #ffd987; /* 좋아요 누른 상태는 노란색 */
  color: white;
}

.like-button i {
  font-size: 18px;
  transition: all 0.3s ease;
  color: #666; /* 기본 상태 아이콘 색상 */
}

.like-button.liked i {
  color: white; /* 좋아요 누른 상태의 아이콘 색상 */
}

.like-button:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.like-button:hover i {
  transform: scale(1.2);
}

.like-button.animate {
  animation: likeAnimation 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.like-button.liked i {
  color: white;
}

@keyframes likeAnimation {
  0% { transform: scale(1); }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); }
}

/* 좋아요 숫자 애니메이션 */
.like-button span {
  min-width: 20px;
  text-align: center;
  transition: all 0.3s ease;
}

.like-button.animate span {
  animation: countAnimation 0.3s ease;
}

@keyframes countAnimation {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.comment-author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-badge {
  background-color: #ffd987;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.comment-edit-form {
  margin: 10px 0;
}

.edit-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  min-height: 60px;
  margin-bottom: 8px;
}

.edit-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.save-button,
.cancel-button,
.edit-button,
.delete-button {
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s ease;
}

.save-button {
  background-color: #ffd987;
  color: white;
}

.cancel-button {
  background-color: #e0e0e0;
  color: #666;
}

.edit-button {
  background-color: #e0e0e0;
  color: #666;
}

.delete-button {
  background-color: #ff6b6b;
  color: white;
}

.save-button:hover,
.cancel-button:hover,
.edit-button:hover,
.delete-button:hover {
  transform: translateY(-1px);
  opacity: 0.9;
}

/* 게시글 목록 컨테이너 */
.list-container {
  width: 100%;
  max-width: 1000px; /* 컨테이너 최대 너비 설정 */
  margin: 0 auto;
  padding: 0 20px;
}

/* 그리드를 리스트 형태로 변경 */
.list-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 10px;
}

/* 게시글 아이템 스타일 */
.list-item {
  background: #ffffff;
  border-radius: 8px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  height: auto;
  border: 1px solid #eee;
  transition: all 0.2s ease;
  height: auto;
}

.list-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

/* 게시글 헤더 */
.item-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 태그 스타일 */
.tag {
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  min-width: 65px;
  text-align: center;
}

.tag.ongoing {
  background-color: #ffd987;
  color: white;
}

.tag.completed {
  background-color: #e9ecef;
  color: #666;
}

/* 제목 스타일 */
.title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  flex-grow: 1;
}

/* 내용 스타일 */
.description {
  margin: 8px 0;
  color: #666;
  font-size: 14px;
  margin: 8px 0;
  line-height: 1.5;
}

/* 푸터 스타일 */
.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}

.author {
  color: #888;
  font-size: 13px;
}

.info {
  display: flex;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 13px;
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 0;
  padding: 10px 0;
}

</style>