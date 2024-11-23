<template>
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
        <input
          type="text"
          v-model="searchQuery"
          placeholder="검색어를 입력하세요"
        />
        <button class="search-button" @click="searchPosts">검색</button>

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
      <button class="write-button" @click="openWriteModal">
        <img :src="penImg" class="button-icon"/>
        글쓰기
      </button>
    </div>

    <!-- 글쓰기 모달 -->
    <div v-if="showWriteModal" class="modal">
  <div class="modal-content">
    <h3>글쓰기</h3>
    <form @submit.prevent="submitPost" enctype="multipart/form-data">
      <label>
        제목:
        <input type="text" v-model="newPostTitle" placeholder="제목을 입력하세요" required />
      </label>
      <label>
        내용:
        <textarea v-model="newPostContent" placeholder="내용을 입력하세요" required></textarea>
      </label>
      <label>
        첨부 파일 (여러 개 선택 가능):
        <input type="file" multiple @change="handleFileUpload" />
      </label>

      <!-- 이미지 미리보기 -->
      <div v-if="filePreviews.length > 0" class="image-previews">
        <div v-for="(preview, index) in filePreviews" :key="index" class="image-preview">
          <img :src="preview" alt="미리보기" />
        </div>
      </div>

      <div class="form-actions">
        <button type="submit">등록</button>
        <button type="button" @click="closeWriteModal">취소</button>
      </div>
    </form>
  </div>
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
          ><i class="icon-heart"></i> ❤️ {{ post.gBoardLikeCount }}</span>
        <span class="info-item"
          ><i class="icon-eye"></i> 👀 {{ post.gBoardViews }}</span>
        <span class="info-item"
          ><i class="icon-comment"></i> 🗨️ {{ post.gBoardCommentsCount }}</span>
        </div>
      </div>
      </RouterLink> 
    </div>
    
  

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'; 
import axios from 'axios';
import router from '@/router';

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
      const sanitizedQuery = searchQuery.value.trim();
      filtered = filtered.filter(post =>
        post.gBoardTitle.includes(sanitizedQuery) || post.gBoardContent.includes(sanitizedQuery)
      );
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
  const sanitizedKeyword = searchQuery.value.trim();
  if (!sanitizedKeyword) {
    alert("검색어를 입력해주세요.");
    return;
  }
  
  console.log('검색 버튼 클릭:', sanitizedKeyword);
  // `searchQuery`를 업데이트하면 `filteredPosts`가 자동으로 재계산됨
  searchQuery.value = sanitizedKeyword;
};

// 게시글 정렬
const sortPosts = criteria => {
  sortBy.value = criteria;
};

// 글쓰기
const showWriteModal = ref(false); // 글쓰기 모달 표시 상태
const newPostTitle = ref(''); // 새 글 제목
const newPostContent = ref(''); // 새 글 내용

// 데이터 초기화 함수
const resetWriteModalData = () => {
  newPostTitle.value = ""; // 제목 초기화
  newPostContent.value = ""; // 내용 초기화
  selectedFiles.value = []; // 파일 초기화
  filePreviews.value = []; // 미리보기 초기화
};

// 글쓰기 모달 닫기 및 데이터 초기화
const closeWriteModal = () => {
  resetWriteModalData(); // 입력 데이터 초기화
  showWriteModal.value = false; // 모달 닫기
};

// 글쓰기 버튼 클릭 이벤트
const openWriteModal = () => {
  resetWriteModalData(); // 기존 입력 데이터 초기화
  showWriteModal.value = true; // 모달 열기
};

// 여러 파일을 저장할 상태와 미리보기 URL 배열
const selectedFiles = ref([]); // 선택된 파일 배열
const filePreviews = ref([]); // 파일 미리보기 URL 배열

// 파일 선택 이벤트 핸들러
const handleFileUpload = (event) => {
  const files = Array.from(event.target.files); // 선택된 파일 배열로 변환
  selectedFiles.value = files; // 선택된 파일 저장

  // 미리보기 생성 (이미지 파일만 처리)
  filePreviews.value = files
    .filter((file) => file.type.startsWith("image/"))
    .map((file) => URL.createObjectURL(file));
};


// 서버로 글 등록 요청
const submitPost = async () => {
  try {
    const formData = new FormData();
    formData.append("gBoardTitle", newPostTitle.value);
    formData.append("gBoardContent", newPostContent.value);

    // 첨부된 파일 추가
    if (selectedFiles.value.length > 0) {
      selectedFiles.value.forEach((file) => {
        formData.append("gBoardFiles", file);
      });
    }

    // 서버에 데이터 전송
    const response = await axios.post("http://localhost:8080/api/group", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    alert("게시글이 성공적으로 등록되었습니다!");

    // 작성 후 페이지 새로고침
    const createdPostId = response.data.gBoardNo; // 서버에서 생성된 게시글 ID 반환
    router.push(`/community/group/detail/${createdPostId}`);
    window.location.reload(); // 페이지 새로고침
  } catch (error) {
    console.error("글 등록 중 오류 발생:", error);
    alert("게시글 등록에 실패했습니다.");
  }
};

</script>



<style scoped>
/* 전체 레이아웃 */
.event-div {
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
  justify-content: start; /* 기존 위치 유지 */
  width: 1000px;
  margin: 140px auto 0;
  border-bottom: 1px solid #ccc;
  position: relative;
  z-index: 5;
}

.board-tab1 .tab-item {
  position: relative;
  padding: 10px 20px; /* 기존 패딩 유지 */
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #666; /* 기본 색상 */
  transition: color 0.3s ease, transform 0.2s ease; /* 부드러운 전환 효과 */
}

.board-tab1 .tab-item:hover {
  color: #000; /* 호버 시 강조 색상 */
  transform: translateY(-2px); /* 살짝 위로 이동 */
}

.board-tab1 .tab-item {
  position: relative; /* 하단 선 위치 조정을 위해 필요 */
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #333;
  }
  
.board-tab1 .tab-item.active {
  color: #000; /* 활성화된 상태의 글자 색상 */
  font-weight: bold;
}

.board-tab1 .tab-item.active::after {
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
  justify-content: start; /* 기존 위치 유지 */
  width: 1000px;
  margin: 50px auto 0;
  position: relative;
}

.board-tab2 .tab-item2 {
  position: relative;
  padding: 8px 15px; /* 기존 패딩 유지 */
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #aaa; /* 기본 색상 */
  transition: color 0.3s ease, transform 0.2s ease; /* 부드러운 전환 효과 */
}

.board-tab2 .tab-item2:hover {
  color: #555; /* 호버 색상 */
  transform: translateY(-2px); /* 살짝 위로 이동 */
}

.board-tab2 .tab-item2.active {
  color: #000; /* 활성화된 상태의 글자 색상 */
  font-weight: bold;
}

.board-tab2 .tab-item2.active::after {
  content: '';
  position: absolute;
  bottom: -5px; /* 버튼 아래쪽 여백 */
  left: 10%; /* 선 양쪽 여백 */
  right: 10%;
  height: 3px;
  background-color: #ffd987; /* 강조 색상 */
  border-radius: 2px;
  transition: width 0.3s ease, background-color 0.3s ease; /* 부드러운 전환 효과 */
}

/* 공통 효과: 기본 애니메이션 */
.tab-item:hover::after,
.tab-item2:hover::after {
  background-color: #ccc; /* 호버 시 강조 */
}

/* 반응형 디자인 (위치는 유지, 크기만 조정) */
@media (max-width: 768px) {
  .board-tab1, .board-tab2 {
    width: 90%; /* 너비만 살짝 줄임 */
    margin: 140px auto 0;
  }

  .tab-item, .tab-item2 {
    font-size: 16px; /* 작은 화면에서 폰트 크기 조정 */
  }
}



/* 검색창 */
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

.write-wrapper {
  display: flex;
  justify-content: flex-end;
  margin: 20px 580px;
}

.write-button {
  background-color: #ffd987;
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
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.write-button img {
  width: 24px;
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
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  width: 400px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.modal-content input,
.modal-content textarea {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.modal-content .form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-content button {
  padding: 10px 20px;
  background-color: #ffd987;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.modal-content button:hover {
  background-color: #f8cd71;
}

/* 이미지 미리보기 스타일 */
.image-previews {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.image-preview img {
  max-width: 100px;
  max-height: 100px;
  border-radius: 5px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}


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
  transform: translateY(-5px);
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.15);
}

.tag {
  display: inline-block;
  font-size: 12px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 10px;
}

.tag.ongoing {
  background-color: #ffd987;
  color: white;
}

.tag.completed {
  background-color: #d3d3d3;
  color: #555;
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

.info {
  display: flex;
  gap: 20px;
  color: #555;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

</style>