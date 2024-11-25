<template>
  <div>
    <h1>문의 수정</h1>
    <form @submit.prevent="updatePost">
      <!-- 제목 입력 -->
      <input v-model="inquiry.iTitle" placeholder="제목" required />
      <!-- 내용 입력 -->
      <textarea v-model="inquiry.iContents" placeholder="내용" required></textarea>
      <!-- 이미지 첨부 -->
      <input type="file" multiple @change="handleFileChange" />
      <!-- 해결 상태 -->
      <label>
        <input type="checkbox" v-model="inquiry.iIsResolved" />
        해결됨
      </label>

      <!-- 수정 버튼 -->
      <button type="submit">수정</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

// Vue Router에서 파라미터와 라우터 객체 가져오기
const route = useRoute();
const router = useRouter();

// 수정할 문의 데이터를 저장할 객체
const inquiry = ref({
  iTitle: '',
  iContents: '',
});

const files = ref([]); // 업로드된 파일들 저장

const handleFileChange = (event) => {
  files.value = Array.from(event.target.files); // 선택된 파일 저장
};


// 컴포넌트 로드 시 데이터 가져오기
onMounted(async () => {
  const { iNo } = route.params; // 라우터에서 게시글 번호 가져오기
  try {
    const response = await axios.get(`http://localhost:8080/api/inquiry/${iNo}`);
    inquiry.value = response.data; // 데이터 저장
  } catch (error) {
    console.error('수정 페이지 데이터 불러오기 오류:', error);
    alert('문의 데이터를 불러오는 중 문제가 발생했습니다.');
  }
});

// 수정 API 호출 함수
const updatePost = async () => {
  try {
    const formData = new FormData();

    // Inquiry 객체를 JSON 문자열로 변환하여 추가
    formData.append(
      'inquiry',
      JSON.stringify({
        iNo: inquiry.value.iNo,
        iTitle: inquiry.value.iTitle,
        iContents: inquiry.value.iContents,
        iIsResolved: inquiry.value.iIsResolved,
      })
    );

    // 파일 추가
    files.value.forEach((file) => {
      formData.append('files', file);
    });

    console.log([...formData.entries()]); // 전송 데이터 확인

    // API 요청
    await axios.put(`http://localhost:8080/api/inquiry/${inquiry.value.iNo}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });

    alert('수정이 완료되었습니다.');
    router.push(`/community/inquiries/detail/${inquiry.value.iNo}`); // 상세 페이지로 이동
  } catch (error) {
    console.error('수정 중 오류 발생:', error);
    alert('수정 중 문제가 발생했습니다.');
  }
};


</script>

<style scoped>
/* 스타일 정의 */
h1 {
  font-size: 24px;
  margin-bottom: 20px;
}

form {
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-width: 600px;
  margin: 0 auto;
}

input,
textarea {
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  padding: 10px 15px;
  font-size: 16px;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
