<template>
  <div>
    <!-- 게시글 상세 정보 -->
    <div class="inquiry-detail">
      <h1>{{ inquiry.iTitle }}</h1>
      <p>{{ inquiry.iContents }}</p>
      <ul>
        <li><strong>작성자:</strong> {{ inquiry.iAuthor }}</li>
        <li><strong>등록일:</strong> {{ formatDate(inquiry.iRegDate) }}</li>
        <li><strong>조회수:</strong> {{ inquiry.iViews }}</li>
      </ul>
      <div v-if="inquiry.images && inquiry.images.length > 0" class="image-gallery">
        <h3>첨부 이미지</h3>
        <div class="image-wrapper">
          <img
            v-for="(image, index) in inquiry.images"
            :key="index"
            :src="image"
            alt="첨부 이미지"
            class="uploaded-image"
          />
        </div>
      </div>
    </div>

    <!-- 삭제 및 수정 버튼 -->
    <div class="actions">
      <button @click="editPost">수정</button>
      <button @click="deletePost">삭제</button>
    </div>

    <!-- 댓글 섹션 -->
    <div class="comments-section">
      <h2>댓글</h2>
      <ul v-if="comments.length > 0">
        <li
          v-for="comment in comments"
          :key="comment.iCommentNo"
          class="comment-item"
        >
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
              <strong>등록일:</strong> {{ formatDate(comment.iCommentRegDate) }}
              <!-- 등록일 -->
            </small>
            <button @click="startEdit(comment)">수정</button>
            <button @click="deleteComment(comment.iCommentNo)">삭제</button>
          </div>
        </li>
      </ul>
      <p v-else>댓글이 없습니다.</p>
    </div>

    <!-- 댓글 작성 -->
    <div class="comment-form">
      <textarea
        v-model="newCommentContent"
        placeholder="댓글을 입력하세요"
      ></textarea>
      <button @click="submitComment">댓글 작성</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const inquiry = ref({});
const comments = ref([]); // 댓글 목록
const newCommentContent = ref(""); // 새로운 댓글 내용
const editingComment = ref(null); // 수정 중인 댓글 ID
const editingContent = ref(""); // 수정 중인 댓글 내용

// 게시글 상세 데이터 및 조회수 증가
onMounted(async () => {
  const iNo = route.params.iNo;
  try {
    // 조회수 증가
    await axios.put(`http://localhost:8080/api/inquiry/${iNo}/view`);

    // 게시글 데이터 가져오기
    const response = await axios.get(`http://localhost:8080/api/inquiry/${iNo}`);
    inquiry.value = response.data;

    // 이미지 경로 생성
    inquiry.value.images = inquiry.value.iFiles
      ?.split(";")
      .filter((file) => file.trim() !== "")
      .map((file) => {
        const fileName = file.split("\\").pop();
        return `http://localhost:8080/uploads/${fileName}`;
      });

    // 댓글 목록 가져오기
    fetchComments();
  } catch (error) {
    console.error("상세 조회 API 오류:", error);
    alert("게시글을 불러오는 중 문제가 발생했습니다.");
  }
});

// 날짜 포맷팅
const formatDate = (dateString) => {
  const options = { year: "numeric", month: "long", day: "numeric" };
  return new Date(dateString).toLocaleDateString(undefined, options);
};

// 댓글 목록 가져오기
const fetchComments = async () => {
  try {
    const response = await axios.get(
      `http://localhost:8080/api/inquiry/comments/${route.params.iNo}` // 수정된 경로
    );
    comments.value = response.data || [];
  } catch (error) {
    console.error("댓글 가져오기 오류:", error);
  }
};

// 댓글 작성
const submitComment = async () => {
  if (!newCommentContent.value.trim()) {
    alert("댓글 내용을 입력해주세요.");
    return;
  }

  try {
    await axios.post(`http://localhost:8080/api/inquiry/comments/${route.params.iNo}`, {
  iCommentContent: newCommentContent.value,
});

    alert("댓글이 작성되었습니다.");
    newCommentContent.value = "";
    fetchComments(); // 댓글 목록 갱신
  } catch (error) {
    console.error("댓글 작성 중 오류:", error);
    alert("댓글 작성에 실패했습니다.");
  }
};

// 댓글 수정
const startEdit = (comment) => {
  editingComment.value = comment.iCommentNo; // 수정 중인 댓글 ID 설정
  editingContent.value = comment.iCommentContent; // 댓글 내용 로드
};

const cancelEdit = () => {
  editingComment.value = null; // 수정 모드 해제
  editingContent.value = ""; // 수정 내용 초기화
};

const saveEdit = async () => {
  try {
    await axios.put(
      `http://localhost:8080/api/inquiry/comments/${editingComment.value}`,
      { iCommentContent: editingContent.value }
    );
    alert("댓글이 성공적으로 수정되었습니다.");
    editingComment.value = null; // 수정 모드 해제
    editingContent.value = ""; // 수정 내용 초기화
    fetchComments(); // 댓글 목록 갱신
  } catch (error) {
    console.error("댓글 수정 중 오류:", error);
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
    console.error("댓글 삭제 중 오류:", error);
    alert("댓글 삭제에 실패했습니다.");
  }
};

// 수정 페이지로 이동
const editPost = () => {
  router.push(`/community/inquiries/edit/${inquiry.value.iNo}`);
};

// 삭제 기능
const deletePost = async () => {
  const confirmed = confirm("정말로 삭제하시겠습니까?");
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8080/api/inquiry/${inquiry.value.iNo}`);
    alert("게시글이 삭제되었습니다.");
    router.push("/community/inquiry"); // 목록 페이지로 이동
  } catch (error) {
    console.error("삭제 중 오류 발생:", error);
    alert("삭제 중 문제가 발생했습니다. 다시 시도해주세요.");
  }
};
</script>


<style scoped>
.inquiry-detail {
  background: #f9f9f9; /* 밝은 배경색 */
  padding: 30px;
  margin: 30px auto;
  max-width: 900px;
  border-radius: 15px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); /* 부드러운 그림자 */
  font-family: 'Noto Sans KR', sans-serif;
  line-height: 1.8;
  color: #333; /* 기본 텍스트 색상 */
}

.inquiry-detail h1 {
  font-size: 30px;
  font-weight: bold;
  color: #555;
  border-bottom: 2px solid #ffd987; /* 강조 색상 */
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.inquiry-detail p {
  font-size: 24px;
  margin: 15px 0;
  color: #555;
}

.inquiry-detail ul {
  list-style: none;
  padding: 0;
  margin-top: 10px;
}

.inquiry-detail ul li {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.image-gallery {
  margin-top: 30px;
}

.image-gallery h3 {
  font-size: 20px;
  font-weight: bold;
  color: #444;
  margin-bottom: 15px;
}

.image-wrapper {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr)); /* 반응형 그리드 */
  gap: 20px;
}

.image-wrapper img {
  width: 100%;
  height: auto;
  border-radius: 10px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.image-wrapper img:hover {
  transform: scale(1.05);
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.2); /* 강조 효과 */
}

.actions {
  display: flex;
  justify-content: flex-end; /* 오른쪽 정렬 */
  gap: 15px;
  margin-top: 20px;
}

.actions button {
  padding: 10px 20px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.actions button:first-child {
  background-color: #ffd987; /* 수정 버튼 색상 */
  color: white;
}

.actions button:first-child:hover {
  background-color: #f8cd71;
  transform: scale(1.05);
}

.actions button:last-child {
  background-color: #ccc; /* 삭제 버튼 색상 */
  color: white;
}

.actions button:last-child:hover {
  background-color: #aaa;
  transform: scale(1.05);
}

</style>
