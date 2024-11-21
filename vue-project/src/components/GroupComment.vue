<template>
  <div class="comments-section">
    <h2>댓글 목록</h2>
    <ul v-if="comments.length > 0">
      <li v-for="comment in comments" :key="comment.gBoardCommentNo" class="comment-item">
        <p>{{ comment.gBoardCommentContent }}</p> <!-- 댓글 내용 -->
        <small>
          <strong>작성자:</strong> {{ comment.userNo }} | <!-- 작성자 번호 -->
          <strong>등록일:</strong> {{ formatDate(comment.gBoardCommentRegDate) }} <!-- 등록일 -->
        </small>
        <!-- 수정 및 삭제 버튼 -->
        <button @click="startEdit(comment)">수정</button>
        <button @click="deleteComment(comment.gBoardCommentNo)">삭제</button>
      </li>
    </ul>
    <p v-else>댓글이 없습니다.</p>

    <!-- 수정 모드 -->
    <div v-if="editingComment">
      <textarea v-model="editingContent"></textarea>
      <button @click="saveEdit">저장</button>
      <button @click="cancelEdit">취소</button>
    </div>
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
      editingComment: null, // 수정 중인 댓글 객체
      editingContent: "", // 수정 중인 댓글 내용
    };
  },
  methods: {
    async fetchComments() {
      try {
        console.log("Fetching comments for gBoardNo:", this.gBoardNo);
        const response = await axios.get(
          `http://localhost:8080/api/group/comment/${this.gBoardNo}`
        );
        console.log("API Response:", response.data);
        this.comments = response.data;
      } catch (error) {
        console.error("Error fetching comments:", error);
        alert("댓글을 불러오는 중 문제가 발생했습니다.");
      }
    },
    formatDate(dateString) {
      const options = { year: "numeric", month: "long", day: "numeric" };
      return new Date(dateString).toLocaleDateString(undefined, options);
    },
    startEdit(comment) {
      this.editingComment = comment; // 수정 중인 댓글 저장
      this.editingContent = comment.gBoardCommentContent; // 기존 내용 로드
    },
    cancelEdit() {
      this.editingComment = null; // 수정 취소
      this.editingContent = "";
    },
    async saveEdit() {
  try {
    const response = await axios.put(
      `http://localhost:8080/api/group/comment/${this.editingComment.gBoardCommentNo}`,
      {
        gBoardCommentNo: this.editingComment.gBoardCommentNo, // 댓글 ID
        gBoardCommentContent: this.editingContent, // 수정된 내용
        gBoardNo: this.editingComment.gBoardNo, // 게시글 번호
        userNo: this.editingComment.userNo, // 작성자 번호
      }
    );
    alert(response.data);
    this.fetchComments();
  } catch (error) {
    console.error("댓글 수정 중 오류 발생:", error);
    alert("댓글 수정에 실패했습니다.");
  }
},
    async deleteComment(gBoardCommentNo) {
      const confirmed = confirm("정말로 댓글을 삭제하시겠습니까?");
      if (!confirmed) return;

      try {
        // 댓글 삭제 API 호출
        const response = await axios.delete(
          `http://localhost:8080/api/group/comment/${gBoardCommentNo}`
        );
        alert(response.data); // 성공 메시지
        this.fetchComments(); // 삭제 후 목록 다시 불러오기
      } catch (error) {
        console.error("댓글 삭제 중 오류 발생:", error);
        alert("댓글 삭제에 실패했습니다.");
      }
    },
  },
  mounted() {
    this.fetchComments(); // 컴포넌트가 마운트되면 댓글 가져오기
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
