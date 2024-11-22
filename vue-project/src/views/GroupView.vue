<template>
  <Header />
  <div class="event-div">

    <!-- 상단바 -->
    <div class="top-bar">
      커뮤니티
      <br/>
      우리 모두 다 함께 도전해보아요!
    </div>

  <!-- 정렬탭1 -->
  <div class="board-tab1">
    <button
      class="tab-item"
      :class="{ active: selectedCategory === '전체' }"
      @click="filterCategory('전체')"
    >
      전체
    </button>
    <button
      class="tab-item"
      :class="{ active: selectedCategory === '모집중' }"
      @click="filterCategory('모집중')"
    >
      모집중
    </button>
    <button
      class="tab-item"
      :class="{ active: selectedCategory === '모집완료' }"
      @click="filterCategory('모집완료')"
    >
      모집완료
    </button>
  </div>
    
    <!-- 검색 바 -->
    <div class="search-wrapper">
      <div class="search-bar">
        <router-link to="/search">
          <img :src="searchImage" alt="Search Icon" />
        </router-link>
        <input type="text" placeholder="검색어를 입력하세요" />
        <button class="search-button">검색</button>
      </div>
    </div>
    
    <!-- 정렬탭2 -->
  <div class="board-tab2">
    <button
      class="tab-item2"
      :class="{ active: sortBy === 'latest' }"
      @click="sortPosts('latest')"
    >
      최신순
    </button>
    <button
      class="tab-item2"
      :class="{ active: sortBy === 'likes' }"
      @click="sortPosts('likes')"
    >
      좋아요순
    </button>
    <button
      class="tab-item2"
      :class="{ active: sortBy === 'comments' }"
      @click="sortPosts('comments')"
    >
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
      :to="`/community/group/detail/${post.gBoardNo}`">
      <div class="list-item" >
        <div class="item-header">
        <!-- 태그 (상태에 따라 색상 변경) -->
        <span
        class="tag"
        :class="{ ongoing: post.gBoardCategory === '모집중', 
        completed: post.gBoardCategory === '모집완료'
        }">
        {{ post.gBoardCategory }}
        </span>
        <!-- 제목 -->
        <div class="title">{{ post.gBoardTitle }}</div>
      </div>
      <!-- 설명 -->
      <div class="description">{{ post.gBoardContent }}</div>
      <!-- 작성자 -->
      <div class="author">{{ post.gBoardAuthor }}</div>
      <!-- 정보 -->
      <div class="info">
        <span class="info-item"
          ><i class="icon-heart"></i> {{ post.gBoardLikeCount }}</span>
        <span class="info-item"
          ><i class="icon-eye"></i> {{ post.gBoardViews }}</span>
        <span class="info-item"
          ><i class="icon-comment"></i> {{ post.gBoardCommentsCount }}</span>
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

// 검색어 상태
const searchQuery = ref('');
// 현재 선택된 카테고리
const selectedCategory = ref('전체');
// 현재 정렬 기준
const sortBy = ref('latest');

// API 호출 함수
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/group');
    // 데이터 변환 (gBoardIsResolved 값을 기반으로 gBoardCategory 생성)
    posts.value = response.data.map(post => ({
      ...post,
      gBoardCategory: post.gBoardIsResolved ? '모집완료' : '모집중' // 상태 변환
    }));
  } catch (error) {
    console.error('API 호출 오류:', error);
    alert('게시글을 불러오는 중 문제가 발생했습니다. 다시 시도해주세요.');
  }
});

// 필터링된 게시글
const filteredPosts = computed(() => {
  let filtered = posts.value;

  // 카테고리 필터링
  if (selectedCategory.value !== '전체') {
    filtered = filtered.filter(post => post.gBoardCategory === selectedCategory.value);
  }

  // 검색어 필터링
  if (searchQuery.value) {
    filtered = filtered.filter(post => post.gBoardTitle.includes(searchQuery.value));
  }

  // 정렬 기준 적용
  if (sortBy.value === 'latest') {
    return filtered.sort((a, b) => new Date(b.gBoardRegDate) - new Date(a.gBoardRegDate)); // 최신순
  } else if (sortBy.value === 'likes') {
    return filtered.sort((a, b) => b.gBoardLikeCount - a.gBoardLikeCount); // 좋아요순
  } else if (sortBy.value === 'comments') {
    return filtered.sort((a, b) => b.gBoardCommentsCount - a.gBoardCommentsCount); // 댓글 많은 순
  }
  return filtered;
});

// 카테고리 필터링
const filterCategory = category => {
  selectedCategory.value = category;
};

// 게시글 검색
const searchPosts = () => {
  // 이미 computed에서 searchQuery를 활용하므로 별도 동작 불필요
};

// 게시글 정렬
const sortPosts = criteria => {
  sortBy.value = criteria;
};
</script>


<style scoped>
/* 전체 레이아웃 */
.event-div {
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

.board-tab1, .board-tab2 {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}

.tab-item, .tab-item2 {
  background: none;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 16px;
  transition: color 0.3s;
}

.tab-item.active, .tab-item2.active {
  color: #000;
  font-weight: bold;
}

/* 정렬탭1 스타일 */
/* 정렬탭1 스타일 */
.board-tab1 .tab-item {
  position: relative; /* 하단 선 위치 조정을 위해 필요 */
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #333;
}

.board-tab1 .tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -5px; /* 버튼 아래쪽으로 여백 */
  left: 0;
  right: 0;
  height: 3px; /* 두꺼운 선 */
  background-color: #000; /* 선 색상 */
  border-radius: 2px; /* 선 끝 모서리 둥글게 */
}

/* 정렬탭2 스타일 */
.board-tab2 .tab-item2 {
  padding: 10px 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #aaaaaa; /* 기본 색상 */
  font-weight: normal; /* 기본 글꼴 두께 */
  transition: color 0.3s ease, font-weight 0.3s ease;
}

.board-tab2 .tab-item2.active {
  color: #000; /* 활성화된 상태의 색상 */
  font-weight: bold; /* 활성화된 상태의 글꼴 두께 */
}


.board-tab1 {
  display: flex;
  justify-content: start;
  width: 1000px;
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
  display: block;
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

.board-tab2 {
  display: flex;
  justify-content: start;
  width: 1000px;
  margin: 50px auto 0;
  position: relative;
}
.tab-item2 {
  padding: 10px 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 20px;
  color: #aaaaaa;
  transition: color 0.3s ease;
}

.tab-item2.active {
  color: #000;
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
  /* bottom: 50px; */
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
}

/* 기본 태그 스타일 */
.tag {
  display: inline-block;
  font-size: 12px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 5px;
}

/* 모집중 태그 스타일 */
.tag.ongoing {
  background-color: #ffe4b3; /* 연한 주황색 */
  color: #ffa500; /* 주황색 글자 */
}

/* 모집완료 태그 스타일 */
.tag.completed {
  background-color: #d3d3d3; /* 연한 회색 */
  color: #808080; /* 회색 글자 */
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
.author {
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