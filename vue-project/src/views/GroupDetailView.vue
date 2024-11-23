<template>
  <div>
    <!-- 수정 모드가 아닐 때 -->
    <div v-if="!editMode" class="group-detail">
      <h1>{{ group.gBoardTitle }}</h1>
      <p>{{ group.gBoardContent }}</p>
      <ul>
        <li><strong>작성자:</strong> {{ group.gBoardAuthor }}</li>
        <li><strong>등록일:</strong> {{ formatDate(group.gBoardRegDate) }}</li>
        <li><strong>조회수:</strong> {{ group.gBoardViews }}</li>
        <li><strong>좋아요:</strong> ❤️ {{ group.gBoardLikeCount }}</li>
        <li><strong>댓글 수:</strong> {{ group.gBoardCommentsCount }}</li>
        <li><strong>상태:</strong> {{ group.gBoardIsResolved ? '모집완료' : '모집중' }}</li>
        <!-- 첨부 파일 -->
        <li v-if="group.gBoardFile">
          <strong>첨부 파일:</strong> 
          <a :href="'/uploads/' + group.gBoardFile" target="_blank">{{ group.gBoardFile }}</a>
        </li>
      </ul>
    </div>

    <!-- 수정 모드일 때 -->
    <div v-else class="edit-form">
      <form @submit.prevent="submitEdit" enctype="multipart/form-data">
        <label>
          제목:
          <input type="text" v-model="editData.gBoardTitle" required />
        </label>
        <label>
          내용:
          <textarea v-model="editData.gBoardContent" required></textarea>
        </label>
        <label>
          상태:
          <select v-model="editData.gBoardIsResolved">
            <option :value="false">모집중</option>
            <option :value="true">모집완료</option>
          </select>
        </label>
        <!-- 파일 첨부 -->
        <label>
          첨부 파일:
          <input type="file" @change="handleFileUpload" />
        </label>
        <div class="form-actions">
          <button type="submit">수정 완료</button>
          <button type="button" @click="cancelEdit">취소</button>
        </div>
      </form>
    </div>

    <!-- 수정 및 삭제 버튼 -->
    <div v-if="!editMode" class="actions">
      <button @click="toggleEdit">수정</button>
      <button @click="deletePost">삭제</button>
      <button @click="toggleLike">
        <span v-if="isLiked">❤️</span>
        <span v-else>🤍</span>
        좋아요 {{ group.gBoardLikeCount }}
      </button>

    </div>

    <div class="comment-form">
    <textarea
      v-model="newCommentContent"
      placeholder="댓글을 입력하세요"
      class="comment-input"
    ></textarea>
    <button @click="submitComment" class="submit-button">댓글 작성</button>
  </div>



    <!-- 댓글 컴포넌트 -->
    <GroupComment v-if="group.gBoardNo" :gBoardNo="group.gBoardNo" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import GroupComment from '@/components/GroupComment.vue';

const route = useRoute();
const router = useRouter();

const group = ref({}); // 게시글 데이터
const editData = ref({}); // 수정 데이터
const editMode = ref(false); // 수정 모드 상태
const selectedFile = ref(null); // 파일 업로드 상태

// 파일 선택 이벤트 핸들러
const handleFileUpload = (event) => {
  selectedFile.value = event.target.files[0];
};

// 수정 모드 토글
const toggleEdit = () => {
  editMode.value = true;
  editData.value = { ...group.value }; // 기존 데이터를 복사해 수정용 데이터로 설정
};

// 수정 취소
const cancelEdit = () => {
  editMode.value = false;
  selectedFile.value = null;
};

// 수정 제출
const submitEdit = async () => {
  try {
    const formData = new FormData();
    formData.append("gBoardTitle", editData.value.gBoardTitle);
    formData.append("gBoardContent", editData.value.gBoardContent);
    formData.append("gBoardIsResolved", editData.value.gBoardIsResolved);

    if (selectedFile.value) {
      formData.append("gBoardFile", selectedFile.value);
    }

    await axios.put(`http://localhost:8080/api/group/${group.value.gBoardNo}`, {
  gBoardTitle: editData.value.gBoardTitle,
  gBoardContent: editData.value.gBoardContent,
  gBoardIsResolved: editData.value.gBoardIsResolved,
});


    alert("게시글이 성공적으로 수정되었습니다.");
    group.value = { ...editData.value }; // 수정 후 데이터 반영
    if (selectedFile.value) {
      group.value.gBoardFile = selectedFile.value.name;
    }
    editMode.value = false;
  } catch (error) {
    console.error("게시글 수정 중 오류 발생:", error);
    alert("수정에 실패했습니다. 다시 시도해주세요.");
  }
};


// 삭제 요청
const deletePost = async () => {
  const confirmed = confirm('정말로 삭제하시겠습니까?');
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8080/api/group/${group.value.gBoardNo}`);
    alert('게시글이 삭제되었습니다.');
    router.push('/community/group');
  } catch (error) {
    console.error('삭제 중 오류 발생:', error);
    alert('삭제 중 문제가 발생했습니다. 다시 시도해주세요.');
  }
};

const isLiked = ref(false); // 좋아요 상태
const toggleLike = async () => {
  try {
    const response = await axios.put(`http://localhost:8080/api/group/${group.value.gBoardNo}/like`, null, {
      params: { userNo: 1 }, // 로그인된 사용자 ID
    });

    isLiked.value = response.data; // 좋아요 상태 갱신
    group.value.gBoardLikeCount += isLiked.value ? 1 : -1; // 좋아요 수 업데이트
  } catch (error) {
    console.error("좋아요 토글 중 오류 발생:", error);
    alert("좋아요 상태를 변경하지 못했습니다. 다시 시도해주세요.");
  }
};

// 초기 데이터 로드
onMounted(async () => {
  const gBoardNo = route.params.gBoardNo;
  const userNo = 1; // 현재 로그인된 사용자 번호 (로그인 로직에 따라 동적으로 변경 필요)
  
  try {
    // 게시글 데이터 로드
    const response = await axios.get(`http://localhost:8080/api/group/${gBoardNo}`);
    group.value = response.data;

    // 조회수 증가 요청
    await axios.put(`http://localhost:8080/api/group/${gBoardNo}/view`);

    // 좋아요 여부 확인
    const likeResponse = await axios.get(`http://localhost:8080/api/group/${gBoardNo}/like`, {
      params: { userNo },
    });
    isLiked.value = likeResponse.data; // 좋아요 여부 상태 반영
  } catch (error) {
    console.error('게시글 데이터 로드 중 오류:', error);
  }
});



// 날짜 포맷팅
const formatDate = (dateString) => {
  const options = { year: 'numeric', month: 'long', day: 'numeric' };
  return new Date(dateString).toLocaleDateString(undefined, options);
};

const newCommentContent = ref("");
const comments = ref([]);

// 댓글 작성
const submitComment = async () => {
  if (!newCommentContent.value.trim()) {
    alert("댓글 내용을 입력해주세요.");
    return;
  }

  try {
    // 댓글 작성 요청
    const response = await axios.post(
      `http://localhost:8080/api/group/comment/${route.params.gBoardNo}`,
      {
        gBoardCommentContent: newCommentContent.value,
        userNo: 1, // 로그인된 사용자 번호
      }
    );

    alert("댓글이 작성되었습니다.");
    newCommentContent.value = ""; // 입력 내용 초기화

    // 작성된 댓글 즉시 목록에 추가 (응답 데이터 활용)
    comments.value.push(response.data);

    // 또는 전체 댓글 목록 새로고침
    await fetchComments();
  } catch (error) {
    console.error("댓글 작성 중 오류 발생:", error);
    alert("댓글 작성에 실패했습니다.");
  }
};


// 댓글 목록 가져오기
const fetchComments = async () => {
  try {
    const response = await axios.get(`http://localhost:8080/api/group/comment/${route.params.gBoardNo}`);
    comments.value = response.data; // 댓글 목록 업데이트
  } catch (error) {
    console.error("댓글 목록을 가져오는 중 오류 발생:", error);
    alert("댓글 목록을 가져오는 데 실패했습니다.");
  }
};

// 초기 데이터 로드
onMounted(() => {
  fetchComments();
});







</script>

<style scoped>
.group-detail {
  padding: 20px;
  background-color: #fff;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  max-width: 800px;
  border-radius: 8px;
}
.group-detail h1 {
  font-size: 24px;
  margin-bottom: 10px;
}
.group-detail ul {
  list-style: none;
  padding: 0;
}
.group-detail li {
  margin-bottom: 8px;
}

.edit-form {
  padding: 20px;
  background-color: #fff;
  box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
  margin: 20px auto;
  max-width: 800px;
  border-radius: 8px;
}

.edit-form label {
  display: block;
  margin-bottom: 10px;
  font-size: 14px;
  color: #333;
}

.edit-form input,
.edit-form textarea,
.edit-form select {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.edit-form .form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.edit-form button {
  padding: 10px 20px;
  background-color: #ffd987;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.edit-form button:hover {
  background-color: #f8cd71;
}
</style>
