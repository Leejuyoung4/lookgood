<template>
  <Header />
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
      <button class="write-button">
        <img :src="penImg" class="button-icon"/>
        글쓰기
      </button>
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
        <span class="info-item"><i class="icon-eye"></i> 조회수: {{ post.iViews }}</span>
        <span class="info-item"><i class="icon-comment"></i> 댓글 수: {{ post.iCommentsCount }}</span>
        <span class="info-item"><i class="icon-resolved"></i> 해결 여부: {{ post.iIsResolved ? '해결됨' : '미해결' }}</span>
      </div>
    </div>
  </RouterLink>
</div>

      
  

    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'; 
import axios from 'axios';

import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
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
</script>


<style scoped>
/* 전체 레이아웃 */
.inquiry-div {
  background: #ffffff;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.top-bar {
  background: #ffd987;
  color: #ffffff;
  width: 100%;
  height: 80px;
  position: absolute;
  top: 60px;
  z-index: 10;
  overflow: hidden;
  font-size: 26px;
  font-weight: bold;
  padding-left: 200px;
  display: flex;
  align-items: center;
}

.board-tab1 {
  display: flex;
  width: 1000px;
  gap: 10px;
  margin: 140px auto 0;
  border-bottom: 1px solid #ccc;
  position: relative;
  z-index: 5;
}

.tab-item {
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 22px;
  position: relative;
  color: #333;
}

.tab-item::after {
  content: "";
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background-color: transparent;
  transition: background-color 0.3s ease, height 0.3s ease;
}

.tab-item.active::after {
  background-color: #000;
  height: 3px;
}

.tab-item:hover::after {
  background-color: #666;
}

.tab-item.active, .tab-item2.active {
  color: #000;
  font-weight: bold;
}
.board-tab2 {
  display: flex;
  justify-content: start;
  width: 1000px;
  margin: 50px auto 0;
  position: relative;
}
/* 정렬탭2 버튼 공통 스타일 */
.tab-item2 {
  padding: 10px 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  color: #aaaaaa;
  transition: color 0.3s ease, font-weight 0.3s ease;
}

/* 정렬탭2 버튼 활성화 스타일 */
.tab-item2.active {
  color: #000;
  font-weight: bold; /* 두껍게 표시 */
}

.tab-2:hover {
  color: #666;
}

/* 검색 바 */
.search-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px auto;
  width: 1000px; /* 전체 너비 */
  gap: 10px; /* 검색 바와 버튼 사이 간격 */
}

.search-bar {
  display: flex;
  align-items: center;
  background: rgb(255, 248, 232);
  border-radius: 20px;
  padding: 5px 15px;
  flex-grow: 1; /* 검색 바가 너비를 채우도록 설정 */
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); /* 그림자 효과 */
}

.search-bar input {
  flex-grow: 1;
  border: none;
  background: transparent;
  font-size: 18px;
  padding-left: 10px;
  outline: none;
}

.search-bar:focus-within {
  background-color: white;
}


.search-bar img {
  height: 22px;
}

/* 검색 버튼 */
.search-button {
  background: #ebd03b;
  color: #ffffff;
  border-radius: 10px;
  width: 70px; /* 버튼 크기 */
  height: 30px;
  font-size: 18px;
  border: none;
  transition: background-color 0.3s ease, transform 0.2s ease;
  cursor: pointer;
}

.search-button:hover {
  background-color: #d1b031;
}

.write-button {
  background-color: #ebd03b;
  color: #ffffff;
  border-radius: 10px; /* 버튼 테두리 둥글게 */
  height: 40px; /* 버튼 높이 */
  font-size: 18px; /* 글자 크기 */
  border: none; /* 테두리 제거 */
  display: flex; /* 아이콘과 텍스트 정렬을 위해 flex 사용 */
  align-items: center; /* 세로 정렬 */
  justify-content: center; /* 가로 정렬 */
  gap: 8px; /* 아이콘과 텍스트 간 간격 */
  transition: background-color 0.3s ease, transform 0.2s ease;
  cursor: pointer;
  position: absolute;
  right: 770px;
  top: 310px;
}
.write-button img {
  width: 30px;
}
.write-button:hover {
  background-color: #d1b031;
}

.list-container {
  display: flex;
  flex-direction: column;
  gap: 30px; /* 목록 간격 */
  padding: 120px 800px;
  background-color: #fff;
}

.list-item {
  border-bottom: 1px solid #e5e5e5;
  padding-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-footer {
  display: flex;
  justify-content: space-between; /* 좌우로 정렬 */
  align-items: center; /* 세로 중앙 정렬 */
}

/* 기본 태그 스타일 */
.tag {
  display: inline-block;
  font-size: 12px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 5px;
}

/* 회원 태그 스타일 */
.tag.user {
  background-color: #ffe4b3; /* 연한 주황색 */
  color: #ffa500; /* 주황색 글자 */
}

/* 영상 태그 스타일 */
.tag.video {
  background-color: #cce5ff; /* 연한 파란색 */
  color: #007bff; /* 파란색 글자 */
}

/* 모임 태그 스타일 */
.tag.community {
  background-color: #d4edda; /* 연한 초록색 */
  color: #28a745; /* 초록색 글자 */
}

/* 기타 태그 스타일 */
.tag.etc {
  background-color: #f8d7da; /* 연한 빨간색 */
  color: #dc3545; /* 빨간색 글자 */
}

/* 제목 스타일 */
.title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

/* 설명 */
.description {
  font-size: 14px;
  color: #555;
  margin-bottom: 10px;
}

/* 작성자 */
.writer {
  font-size: 12px;
  color: #999;
}

/* 정보 섹션 */
.info {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #666;
}

.info-item {
  display: flex;
  align-items: center;
}
</style>
