<template>
  <div class="comments-section">
    <h2>댓글 목록</h2>
    <ul v-if="comments.length > 0">
      <li v-for="comment in comments" :key="comment.gBoardCommentNo" class="comment-item">
        <!-- 수정 모드일 때 -->
        <div v-if="editingComment === comment.gBoardCommentNo">
          <textarea v-model="editingContent"></textarea>
          <button @click="saveEdit">저장</button>
          <button @click="cancelEdit">취소</button>
        </div>
        <!-- 수정 모드가 아닐 때 -->
        <div v-else>
          <p>{{ comment.gBoardCommentContent }}</p>
          <small>
            <strong>작성자:</strong> {{ comment.userNo }} | <!-- 작성자 번호 -->
            <strong>등록일:</strong> {{ formatDate(comment.gBoardCommentRegDate) }} <!-- 등록일 -->
          </small>
          <button @click="startEdit(comment)">수정</button>
          <button @click="deleteComment(comment.gBoardCommentNo)">삭제</button>
        </div>
      </li>
    </ul>
    <p v-else>댓글이 없습니다.</p>
  </div>
</template>


<script>
import axios from "axios";

export default {
  name: "GroupComment",
  props: {
    gBoardNo: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      comments: [], // 댓글 데이터
      editingComment: null, // 현재 수정 중인 댓글의 ID
      editingContent: "", // 수정 중인 댓글 내용
    };
  },
  methods: {
    async fetchComments() {
  try {
    const response = await axios.get(
      `http://localhost:8080/api/group/comment/${this.gBoardNo}`
    );
    // 데이터가 null이거나 undefined일 경우 빈 배열로 설정
    this.comments = response.data || [];
  } catch (error) {
    console.error("Error fetching comments:", error);
    alert("댓글을 불러오는 중 문제가 발생했습니다.");
    // 오류가 발생한 경우에도 기본값을 빈 배열로 설정
    this.comments = [];
  }
},
    formatDate(dateString) {
      const options = { year: "numeric", month: "long", day: "numeric" };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
    startEdit(comment) {
      this.editingComment = comment.gBoardCommentNo; // 현재 수정 중인 댓글 ID 설정
      this.editingContent = comment.gBoardCommentContent; // 기존 댓글 내용 로드
    },
    cancelEdit() {
      this.editingComment = null; // 수정 모드 해제
      this.editingContent = ""; // 수정 내용 초기화
    },
    async saveEdit() {
      try {
        await axios.put(
          `http://localhost:8080/api/group/comment/${this.editingComment}`,
          {
            gBoardCommentContent: this.editingContent, // 수정된 댓글 내용
          }
        );
        alert("댓글이 성공적으로 수정되었습니다.");
        this.editingComment = null; // 수정 모드 해제
        this.editingContent = ""; // 수정 내용 초기화
        this.fetchComments(); // 댓글 목록 새로고침
      } catch (error) {
        console.error("댓글 수정 중 오류 발생:", error);
        alert("댓글 수정에 실패했습니다.");
      }
    },
    async deleteComment(gBoardCommentNo) {
      const confirmed = confirm("정말로 댓글을 삭제하시겠습니까?");
      if (!confirmed) return;

      try {
        const response = await axios.delete(
          `http://localhost:8080/api/group/comment/${gBoardCommentNo}`
        );
        alert(response.data); // 성공 메시지
        this.fetchComments(); // 삭제 후 목록 새로고침
      } catch (error) {
        console.error("댓글 삭제 중 오류 발생:", error);
        alert("댓글 삭제에 실패했습니다.");
      }
    },
  },
  mounted() {
    this.fetchComments(); // 컴포넌트 마운트 시 댓글 가져오기
  },
};
</script>


<style scoped>
.comments-section {
  margin: 20px;
}
.comment-item {
  margin-bottom: 15px;
}
</style>
