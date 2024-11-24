<template>
  <div>
    <div class="inquiry-detail">
      <h1>{{ inquiry.iTitle }}</h1>
      <p>{{ inquiry.iContents }}</p>
      <ul>
        <li><strong>작성자:</strong> {{ inquiry.iAuthor }}</li>
        <li><strong>등록일:</strong> {{ inquiry.iRegDate }}</li>
        <li><strong>조회수:</strong> {{ inquiry.iViews }}</li>
      </ul>
      <div v-if="inquiry.images && inquiry.images.length > 0" class="image-gallery">
  <h3>첨부 이미지</h3>
  <div
    class="image-wrapper"
    v-for="(image, index) in inquiry.images"
    :key="index"
  >
    <img
      :src="`http://localhost:8080/uploads/${image}`" 
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const inquiry = ref({});

// 상세 게시글 데이터 가져오기
onMounted(async () => {
  const iNo = route.params.iNo; // 라우터에서 iNo 파라미터 가져오기
  try {
    await axios.put(`http://localhost:8080/api/inquiry/${iNo}/view`);
    const response = await axios.get(`http://localhost:8080/api/inquiry/${iNo}`);
    inquiry.value = response.data;

    // 이미지 배열 추가
    if (!Array.isArray(inquiry.value.images)) {
      inquiry.value.images = inquiry.value.iFiles?.split(',') || []; // 파일 필드에서 이미지 배열 생성
    }
  } catch (error) {
    console.error('상세 조회 API 오류:', error);
    alert('게시글을 불러오는 중 문제가 발생했습니다.');
  }
});

// 수정 페이지로 이동
const editPost = () => {
  router.push(`/community/inquiries/edit/${inquiry.value.iNo}`);
};

// 삭제 기능
const deletePost = async () => {
  const confirmed = confirm('정말로 삭제하시겠습니까?');
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8080/api/inquiry/${inquiry.value.iNo}`);
    alert('게시글이 삭제되었습니다.');
    router.push('/community/inquiry'); // 목록 페이지로 이동
  } catch (error) {
    console.error('삭제 중 오류 발생:', error);
    alert('삭제 중 문제가 발생했습니다. 다시 시도해주세요.');
  }
};
</script>


<style scoped>
.inquiry-detail {
  padding: 20px;
  background-color: #fff;
  margin: 20px auto;
  max-width: 800px;
  border-radius: 8px;
}
.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

/* 이미지 */
.image-gallery {
  margin-top: 20px;
}

.image-wrapper {
  margin-bottom: 15px;
}

.image-wrapper img {
  max-width: 100%;
  height: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
}

</style>
