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
  const iNo = route.params.iNo;
  try {
    await axios.put(`http://localhost:8080/api/inquiry/${iNo}/view`);
    const response = await axios.get(`http://localhost:8080/api/inquiry/${iNo}`);
    inquiry.value = response.data;

    // 이미지 경로 생성
    inquiry.value.images = inquiry.value.iFiles
  ?.split(';') // ';'로 구분된 파일 경로를 나눔
  .filter((file) => file.trim() !== '') // 빈 문자열 제거
  .map((file) => {
    const fileName = file.split('\\').pop(); // 경로에서 파일명만 추출
    return `http://localhost:8080/uploads/${fileName}`; // 서버 URL로 변환
  });



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
