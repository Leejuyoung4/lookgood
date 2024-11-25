<template>
  <div class="comments-section">
    <h2>댓글 목록</h2>
    <ul v-if="comments.length > 0">
      <li v-for="comment in comments" :key="comment.iCommentNo" class="comment-item">
        <!-- 수정 모드일 때 -->
        <div v-if="editingComment === comment.iCommentNo">
          <textarea v-model="editingContent"></textarea>
          <button @click="saveEdit">저장</button>
          <button @click="cancelEdit">취소</button>
        </div>
        <!-- 수정 모드가 아닐 때 -->
        <div v-else>
          <p>{{ comment.iCommentContent }}</p>
          <small>
            <strong>작성자:</strong> {{ comment.userNo }} | <!-- 작성자 번호 -->
            <strong>등록일:</strong> {{ formatDate(comment.iCommentRegDate) }} <!-- 등록일 -->
          </small>
          <button @click="startEdit(comment)">수정</button>
          <button @click="deleteComment(comment.iCommentNo)">삭제</button>
        </div>
      </li>
    </ul>
    <p v-else>댓글이 없습니다.</p>
    <!-- 댓글 작성 -->
    <div>
      <textarea v-model="newComment" placeholder="댓글을 입력하세요"></textarea>
      <button @click="addComment">작성</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

// Props 선언
defineProps({
  inquiryId: {
    type: Number,
    required: true,
  },
});

// 데이터 상태
const comments = ref([]); // 댓글 목록
const newComment = ref(""); // 새로 작성할 댓글
const editingComment = ref(null); // 수정 중인 댓글 ID
const editingContent = ref(""); // 수정 중인 댓글 내용

// 댓글 목록 가져오기
const fetchComments = async () => {
  try {
    console.log(inquiryId);

    const response = await axios.get(
      `http://localhost:8080/api/inquiry/comments/${inquiryId}`
    );
    comments.value = response.data || [];
  } catch (error) {
    console.error("댓글 목록 가져오기 중 오류 발생:", error);
    alert("댓글을 불러오는 중 문제가 발생했습니다.");
    comments.value = [];
  }
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  const options = { year: "numeric", month: "long", day: "numeric" };
  return new Date(dateString).toLocaleDateString(undefined, options);
};

// 댓글 작성
const addComment = async () => {
  if (!newComment.value.trim()) {
    alert("댓글 내용을 입력하세요.");
    return;
  }
  try {
    console.log("userNo 확인:", userNo); // userNo 값 확인

    await axios.post(`http://localhost:8080/api/inquiry/comments/${inquiryId}`, {
      iCommentContent: newComment.value,
      userNo: 1, // 올바른 userNo 값 전달 필요
    });

    alert("댓글이 성공적으로 작성되었습니다.");
    newComment.value = "";
    fetchComments(); // 댓글 목록 갱신
  } catch (error) {
    console.error("댓글 작성 중 오류 발생:", error);
    alert("댓글 작성에 실패했습니다.");
  }
};


// 댓글 수정 시작
const startEdit = (comment) => {
  editingComment.value = comment.iCommentNo; // 현재 수정 중인 댓글 ID 설정
  editingContent.value = comment.iCommentContent; // 기존 댓글 내용 로드
};

// 댓글 수정 취소
const cancelEdit = () => {
  editingComment.value = null;
  editingContent.value = "";
};

// 댓글 수정 저장
const saveEdit = async () => {
  if (!editingContent.value.trim()) {
    alert("댓글 내용을 입력하세요.");
    return;
  }
  try {
    await axios.put(
      `http://localhost:8080/api/inquiry/comments/${editingComment.value}`,
      {
        iCommentContent: editingContent.value,
      }
    );
    alert("댓글이 성공적으로 수정되었습니다.");
    editingComment.value = null;
    editingContent.value = "";
    fetchComments(); // 댓글 목록 갱신
  } catch (error) {
    console.error("댓글 수정 중 오류 발생:", error);
    alert("댓글 수정에 실패했습니다.");
  }
};

// 댓글 삭제
const deleteComment = async (iCommentNo) => {
  const confirmed = confirm("정말로 댓글을 삭제하시겠습니까?");
  if (!confirmed) return;

  try {
    await axios.delete(
      `http://localhost:8080/api/inquiry/comments/${iCommentNo}`
    );
    alert("댓글이 삭제되었습니다.");
    fetchComments(); // 댓글 목록 갱신
  } catch (error) {
    console.error("댓글 삭제 중 오류 발생:", error);
    alert("댓글 삭제에 실패했습니다.");
  }
};

// 컴포넌트 마운트 시 댓글 목록 가져오기
onMounted(() => {
  fetchComments();
});
</script>


<style scoped>
.comments-section {
  margin: 20px;
}
.comment-item {
  margin-bottom: 15px;
}
</style>
