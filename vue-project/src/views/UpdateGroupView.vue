<template>
  <div>
    <h1>게시글 수정</h1>
    <form @submit.prevent="updatePost">
      <input v-model="group.gBoardTitle" placeholder="제목" required />
      <textarea v-model="group.gBoardContent" placeholder="내용" required></textarea>
      <button type="submit">수정</button>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const group = ref({
  gBoardTitle: '',
  gBoardContent: '', 
});

onMounted(async () => {
  const { gBoardNo } = route.params;
  const response = await axios.get(`http://localhost:8080/api/group/${gBoardNo}`);
  group.value = response.data;
});

const updatePost = async () => {
  try {
    console.log("전송 데이터:", group.value);
    await axios.put(`http://localhost:8080/api/group/${group.value.gBoardNo}`, 
    group.value);
    alert("수정이 완료되었습니다.");
    router.push(`/community/group/detail/${group.value.gBoardNo}`);
  } catch (error) {
    console.error("수정 중 오류 발생:", error);
    alert("수정 중 문제가 발생했습니다.");
  }
};

</script>
