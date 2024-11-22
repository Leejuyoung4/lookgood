<template>
  <div class="inquiry-div">

    <!-- 상단바 -->
    <div class="top-bar">
      커뮤니티
      <br/>
      불편한 점이 있으신가요?
    </div>

    <!-- 정렬탭1 -->
    <div class="board-tab1">
      <button v-for="tab in tabs" :key="tab.id" class="tab-item"
      :class="{ active: activeTab === tab.id }"
      @click="setActiveTab(tab.id)"
      >
        {{ tab.name }}
      </button>
    </div>
    
    <!-- 검색 바 -->
    <div class="search-wrapper">
      <div class="search-bar">
        <router-link to="/search">
          <img :src="searchImage" alt="Search Icon" />
        </router-link>
        <input type="text" placeholder="검색어를 입력하세요" />
        <button class="search-button" @click="searchPost">검색</button>
      </div>
    </div>
    
    <!-- 정렬탭2 -->
  <div class="board-tab2">
    <button 
      class="tab-item2" 
      :class="{ active: sortBy === 'latest' }" 
      @click="sortPosts('latest')">
      최신순
    </button>
    <button 
      class="tab-item2" 
      :class="{ active: sortBy === 'comments' }" 
      @click="sortPosts('comments')">
      댓글많은순
    </button>
  </div>

    <!-- 글쓰기 버튼 -->
    <div class="write-wrapper">
      <!-- 글쓰기 버튼 -->
      <button class="write-button" @click="openWriteModal">
        <img :src="penImg" class="button-icon" />
        글쓰기
      </button>
    </div>

<!-- 글쓰기 모달 -->
<div v-if="showWriteModal" class="modal">
  <div class="modal-content">
    <h3>글쓰기</h3>
    <form @submit.prevent="submitPost">
      <input type="text" v-model="newPostTitle" placeholder="제목" required />
      <textarea v-model="newPostContent" placeholder="내용" required></textarea>
      <button type="submit">등록</button>
      <button type="button" @click="showWriteModal = false">닫기</button>
    </form>
  </div>
</div>


   <!-- 게시글 목록 -->
   <div class="list-container">
  <RouterLink 
    v-for="(post, index) in filteredPosts" 
    :key="index" 
    :to="`/community/inquiries/detail/${post.iNo}`">
    <div class="list-item">
      <div class="item-header">
        <!-- 태그 -->
        <span
          class="tag"
          :class="{ user: post.iCategoryName === '회원', 
                    video: post.iCategoryName === '영상', 
                    community: post.iCategoryName === '모임', 
                    etc: post.iCategoryName === '기타' 
          }">
          {{ post.iCategoryName }}
        </span>
        <!-- 제목 -->
        <div class="title">{{ post.iTitle }}</div>
      </div>
      <!-- 설명 -->
      <div class="description">{{ post.iContents }}</div>
      <!-- 작성자 및 기타 정보 -->
      <div class="item-footer">
        <span class="info-item"><i class="icon-eye"></i> 👀 {{ post.iViews }}</span>
        <span class="info-item"><i class="icon-comment"></i> 🗨️ {{ post.iCommentsCount }}</span>
        <span class="info-item"><i class="icon-resolved"></i> 해결 여부: {{ post.iIsResolved ? '해결됨' : '미해결' }}</span>
      </div>
    </div>
  </RouterLink>
</div>

      
  

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'; 
import axios from 'axios';

import searchImage from '@/assets/img/search1.svg';
import penImg from '@/assets/img/pen.svg';

// 게시글 데이터
const posts = ref([]);
// 현재 활성화된 탭 (기본값은 '전체')
const activeTab = ref('all');
// 검색어 상태
const searchQuery = ref('');
// 현재 정렬 기준
const sortBy = ref('latest');

// API 호출 함수
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/inquiry');
    posts.value = response.data; // 데이터를 성공적으로 저장
  } catch (error) {
    console.error('API 호출 오류:', error);
    alert('게시글을 불러오는 중 문제가 발생했습니다. 다시 시도해주세요.');
  }
});



const filteredPosts = computed(() => {
  let filtered = posts.value;

  // 탭 필터링
  if (activeTab.value !== 'all') {
    filtered = filtered.filter(post => post.iCategoryName === activeTab.value);
  }

  // 검색어 필터링
  if (searchQuery.value) {
    filtered = filtered.filter(post => post.iTitle.includes(searchQuery.value));
  }

  // 정렬 기준
  if (sortBy.value === 'latest') {
    return filtered.sort((a, b) => new Date(b.iRegDate) - new Date(a.iRegDate)); // 최신순
  } else if (sortBy.value === 'comments') {
    return filtered.sort((a, b) => b.iCommentsCount - a.iCommentsCount); // 댓글 많은 순
  }

  return filtered;
});


// 게시글 검색
const searchPosts = () => {
  // 이미 computed에서 searchQuery를 활용하므로 별도 동작 불필요
};

// 게시글 정렬
const sortPosts = criteria => {
  sortBy.value = criteria;
};

// 탭 데이터 설정
const tabs = ref([
  { name: "전체", id: "all" },
  { name: "회원", id: "회원" },
  { name: "영상", id: "영상" },
  { name: "모임", id: "모임" },
  { name: "기타", id: "기타" }
]);

// 탭 클릭 시 활성화된 탭 업데이트
const setActiveTab = (id) => {
  activeTab.value = id; // 현재 활성화된 탭을 업데이트
};

// 글쓰기
const showWriteModal = ref(false); // 글쓰기 모달 표시 상태
const newPostTitle = ref(''); // 새 글 제목
const newPostContent = ref(''); // 새 글 내용

// 글쓰기 버튼 클릭 이벤트
const openWriteModal = () => {
  showWriteModal.value = true; // 모달 표시 상태를 true로 변경
};

const submitPost = async () => {
    try {
        const newPost = {
            iTitle: newPostTitle.value,
            iContents: newPostContent.value,
            iFile: null, // 필요하면 파일 추가
            iViews: 0,
            iIsResolved: false,
            iCommentsCount: 0,
            iCategoryName: "기타", // 카테고리 설정
        };

        const response = await axios.post("http://localhost:8080/api/inquiry", newPost);

        // 등록 성공 시 목록에 추가
        posts.value.unshift(response.data); // 목록 맨 위에 추가
        alert("글이 성공적으로 등록되었습니다.");

        

        // 입력값 초기화
        newPostTitle.value = "";
        newPostContent.value = "";
        showWriteModal.value = false;
    } catch (error) {
        console.error("글 등록 중 오류 발생:", error);
        alert("글 등록에 실패했습니다.");
    }
};



</script>


<style scoped>
/* 전체 레이아웃 */
.inquiry-div {
  background: linear-gradient(to bottom, #f9f9f9, #ffffff);
  padding: 20px 0;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
  font-family: 'Noto Sans KR', sans-serif;
}

.top-bar {
  background: linear-gradient(to right, #ffd987, #fcdfa0);
  color: #ffffff;
  width: 100%;
  height: 100px;
  text-align: center;
  font-size: 26px;
  font-weight: bold;
  padding: 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

/* 정렬탭1 스타일 */
.board-tab1 {
  display: flex;
  justify-content: start; /* 위치는 기존과 동일하게 유지 */
  width: 1000px;
  margin: 140px auto 0;
  border-bottom: 1px solid #ddd; /* 부드러운 하단 구분선 */
  position: relative;
  z-index: 5;
}

.tab-item {
  padding: 10px 25px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #666; /* 기본 색상 */
  position: relative;
  transition: color 0.3s ease, transform 0.2s ease;
}

.tab-item:hover {
  color: #000; /* 호버 시 글자 색 강조 */
  transform: translateY(-2px); /* 살짝 위로 올라가는 효과 */
}

.tab-item.active {
  color: #000;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px; /* 버튼 아래쪽 여백 */
  left: 10%; /* 선 양쪽 여백 */
  right: 10%;
  height: 3px;
  background-color: #ffd987; /* 강조 색상 */
  border-radius: 3px;
  transition: width 0.3s ease, background-color 0.3s ease; /* 부드러운 전환 효과 */
}

/* 정렬탭2 스타일 */
.board-tab2 {
  display: flex;
  justify-content: start; /* 위치 유지 */
  width: 1000px;
  margin: 50px auto 0;
  position: relative;
}

.tab-item2 {
  padding: 10px 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #aaa; /* 기본 색상 */
  position: relative;
  transition: color 0.3s ease, transform 0.2s ease;
}

.tab-item2:hover {
  color: #555;
  transform: translateY(-2px);
}

.tab-item2.active {
  color: #000;
  font-weight: bold;
}

.tab-item2.active::after {
  content: '';
  position: absolute;
  bottom: -5px;
  left: 10%;
  right: 10%;
  height: 3px;
  background-color: #ffd987; /* 활성화된 정렬탭 색상 */
  border-radius: 2px;
}

/* 검색 바 */
.search-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px auto;
  width: 40%;
  gap: 15px;
}

.search-bar {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 25px;
  padding: 10px 20px;
  flex-grow: 1;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.search-bar input {
  flex-grow: 1;
  border: none;
  background: transparent;
  font-size: 16px;
  padding: 10px;
  outline: none;
}

.search-bar:focus-within {
  background-color: #f9f9f9;
}

.search-bar img {
  height: 22px;
}

.search-button {
  background: #ffd987;
  color: white;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  cursor: pointer;
  transition: transform 0.2s ease, background-color 0.3s ease;
}

.search-button:hover {
  background-color: #f8cd71;
  transform: translateY(-2px);
}

/* 글쓰기 버튼 */
.write-wrapper {
  display: flex;
  justify-content: flex-end;
  margin: 20px 580px;
}

.write-button {
  background: #ffd987;
  color: #fff;
  border-radius: 25px;
  padding: 10px 20px;
  font-size: 16px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: transform 0.2s ease, background-color 0.3s ease;
}

.write-button img {
  width: 20px;
}

.write-button:hover {
  background-color: #f8cd71;
  transform: translateY(-2px);
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* 배경 흐림 효과 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 모달이 다른 요소 위에 오도록 설정 */
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}


/* 게시글 목록 */
.list-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
  padding: 20px 15%;
  background-color: #fff;
}

.list-item {
  background: #f9f9f9;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.3s ease;
}

.list-item:hover {
  transform: translateY(-5px); /* 카드 위로 살짝 이동 */
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.15);
}

.item-footer {
  display: flex;
  /* justify-content: space-between; */
  align-items: center;
  margin-top: 10px;
  gap: 20px;
}

.tag {
  display: inline-block;
  font-size: 12px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 10px;
}

.tag.user {
  background-color: #ffe4b3; /* 회원 태그 */
  color: #ffa500;
}

.tag.video {
  background-color: #cce5ff; /* 영상 태그 */
  color: #007bff;
}

.tag.community {
  background-color: #d4edda; /* 모임 태그 */
  color: #28a745;
}

.tag.etc {
  background-color: #f8d7da; /* 기타 태그 */
  color: #dc3545;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.description {
  font-size: 14px;
  color: #777;
  margin-bottom: 15px;
}

.author {
  font-size: 12px;
  color: #aaa;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #555;
}

</style>
