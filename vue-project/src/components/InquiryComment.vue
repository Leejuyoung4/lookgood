<template>
  <div class="inquiry-comments">
    <h3>댓글</h3>

    <!-- 댓글 목록 -->
    <div v-if="comments.length > 0" class="comment-list">
      <div v-for="comment in comments" :key="comment.iCommentNo" class="comment-item">
        <p>{{ comment.iCommentContent }}</p>
        <small>작성자: {{ comment.userNo }} | {{ comment.iCommentRegDate }}</small>
        <!-- 수정 및 삭제 버튼: 작성자일 경우에만 표시 -->
        <div v-if="isLoggedIn && comment.userNo === loggedInUserId">
          <button @click="startEditing(comment)">수정</button>
          <button @click="deleteComment(comment.iCommentNo)">삭제</button>
        </div>
      </div>
    </div>
    <div v-else>
      <p>댓글이 없습니다. 첫 번째 댓글을 작성해보세요!</p>
    </div>

    <!-- 댓글 작성 -->
    <div v-if="isLoggedIn" class="comment-form">
      <textarea v-model="newComment" placeholder="댓글을 입력하세요"></textarea>
      <button @click="addComment">작성</button>
    </div>
    <div v-else>
      <p>댓글 작성은 로그인 후 이용 가능합니다.</p>
    </div>

    <!-- 댓글 수정 -->
    <div v-if="isEditing" class="comment-edit-form">
      <textarea v-model="editingCommentContent" placeholder="댓글 내용을 수정하세요"></textarea>
      <button @click="updateComment">수정</button>
      <button @click="cancelEditing">취소</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

// Props 정의
defineProps({
  inquiryId: {
    type: Number,
    required: true,
  },
  loggedInUserId: {
    type: Number,
    required: false,
  },
});

// Reactive 상태 관리
const comments = ref([]); // 댓글 목록
const newComment = ref(''); // 새로운 댓글 내용
const isEditing = ref(false); // 수정 모드 여부
const editingCommentId = ref(null); // 수정 중인 댓글 ID
const editingCommentContent = ref(''); // 수정 중인 댓글 내용

// 로그인 여부
const isLoggedIn = computed(() => !!loggedInUserId);

// 댓글 목록 조회
const fetchComments = async () => {
  try {
    const response = await axios.get(`/api/${inquiryId}`);
    comments.value = response.data;
  } catch (error) {
    console.error('댓글 조회 실패:', error);
  }
};

// 댓글 작성
const addComment = async () => {
  if (!newComment.value.trim()) {
    alert('댓글 내용을 입력하세요.');
    return;
  }

  try {
    const response = await axios.post(`/api/${inquiryId}/comments`, {
      iCommentContent: newComment.value,
      userNo: loggedInUserId,
    });
    alert(response.data);
    newComment.value = '';
    fetchComments(); // 새로고침
  } catch (error) {
    console.error('댓글 작성 실패:', error);
  }
};

// 댓글 삭제
const deleteComment = async (commentId) => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return;

  try {
    const response = await axios.delete(`/api/comments/${commentId}`);
    alert(response.data);
    fetchComments(); // 새로고침
  } catch (error) {
    console.error('댓글 삭제 실패:', error);
  }
};

// 댓글 수정 시작
const startEditing = (comment) => {
  isEditing.value = true;
  editingCommentId.value = comment.iCommentNo;
  editingCommentContent.value = comment.iCommentContent;
};

// 댓글 수정 취소
const cancelEditing = () => {
  isEditing.value = false;
  editingCommentId.value = null;
  editingCommentContent.value = '';
};

// 댓글 수정 완료
const updateComment = async () => {
  if (!editingCommentContent.value.trim()) {
    alert('댓글 내용을 입력하세요.');
    return;
  }

  try {
    const response = await axios.put(`/api/comments/${editingCommentId.value}`, {
      iCommentContent: editingCommentContent.value,
    });
    alert(response.data);
    cancelEditing();
    fetchComments(); // 새로고침
  } catch (error) {
    console.error('댓글 수정 실패:', error);
  }
};

// 컴포넌트가 마운트되었을 때 댓글 목록을 가져옵니다.
onMounted(() => {
  fetchComments();
});
</script>

<style scoped>
.inquiry-comments {
  margin-top: 20px;
}

.comment-list {
  margin-bottom: 20px;
}

.comment-item {
  border-bottom: 1px solid #ccc;
  padding: 10px 0;
}

.comment-form textarea,
.comment-edit-form textarea {
  width: 100%;
  height: 80px;
  margin-bottom: 10px;
}

.button {
  margin-right: 10px;
  padding: 5px 10px;
  border: none;
  background-color: #007bff;
  color: white;
  cursor: pointer;
}

.button:hover {
  background-color: #0056b3;
}
</style>
