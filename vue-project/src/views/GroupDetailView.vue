<template>
  <div>
    <Header />
    <div class="group-detail">
      <h1>{{ group.gBoardTitle }}</h1>
      <p>{{ group.gBoardContent }}</p>
      <ul>
        <li><strong>작성자:</strong> {{ group.gBoardAuthor }}</li>
        <li><strong>등록일:</strong> {{ group.gBoardRegDate }}</li>
        <li><strong>조회수:</strong> {{ group.gBoardViews }}</li>
        <li><strong>좋아요:</strong> {{ group.gBoardLikeCount }}</li>
        <li><strong>댓글 수:</strong> {{ group.gBoardCommentsCount }}</li>
        <li><strong>상태:</strong> {{ group.gBoardIsResolved ? '모집완료' : '모집중' }}</li>
      </ul>
    </div>

    <div class="actions">
      <button @click="editPost">수정</button>
      <button @click="deletePost">삭제</button>
    </div>
    
    <GroupComment v-if="group.gBoardNo" :gBoardNo="group.gBoardNo" />
    <Footer />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import GroupComment from '@/components/GroupComment.vue';

const route = useRoute();
const router = useRouter();
const group = ref({}); // 게시글 데이터

const editPost = () => {
  router.push(`/community/group/edit/${group.value.gBoardNo}`);
};

const deletePost = async () => {
  const confirmed = confirm('정말로 삭제하시겠습니까?');
  if (!confirmed) return;

  try {
    await axios.delete(`http://localhost:8080/api/group/${group.value.gBoardNo}`);
    alert('게시글이 삭제되었습니다.');
    router.push('/community/group'); // 목록 페이지로 이동
  } catch (error) {
    console.error('삭제 중 오류 발생:', error);
    alert('삭제 중 문제가 발생했습니다. 다시 시도해주세요.');
  }
};

onMounted(async () => {
  const gBoardNo = route.params.gBoardNo;
  console.log("Route gBoardNo:", gBoardNo);
  try {
    const response = await axios.get(`http://localhost:8080/api/group/${gBoardNo}`);
    group.value = response.data;
    console.log("Loaded group data:", group.value);
  } catch (error) {
    console.error("Error loading group data:", error);
  }
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
</style>
