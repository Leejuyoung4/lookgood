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
          <img :src="searchImage" alt="Search Icon" />
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
        <input v-model="newPostTitle" placeholder="제목" />
        <textarea v-model="newPostContent" placeholder="내용"></textarea>
        
        <!-- 파일 입력과 미리보기 -->
        <input 
          type="file" 
          @change="handleFileSelect" 
          accept="image/*"
        />
        
        <!-- 이미지 미리보기 -->
        <div v-if="imagePreview" class="image-preview">
          <img :src="imagePreview" alt="미리보기" style="max-width: 200px;" />
        </div>

        <button @click="submitPost">등록</button>
        <button @click="closeWriteModal">취소</button>
      </div>
    </div>


   <!-- 게시글 목록 -->
   <div class="list-container">
    <div class="list-grid">
      <RouterLink
        v-for="post in paginatedPosts" 
        :key="post.boardNo"
        :to="`/community/group/detail/${post.boardNo}`"
        class="list-link"
        @click="incrementViewCount(post.boardNo)"
      >
        <div class="list-item">
          <div class="item-header">
            <span class="tag" :class="{ ongoing: !post.boardIsResolved, completed: post.boardIsResolved }">
              {{ post.boardIsResolved ? '모집완료' : '모집중' }}
            </span>
            <div class="title">{{ truncateText(post.boardTitle, 30) }}</div>
          </div>
          <div class="description">{{ truncateText(post.boardContent, 100) }}</div>
          <div class="item-footer">
            <div class="author">{{ post.boardAuthor }}</div>
            <div class="info">
              <span class="info-item">
                <i class="fas fa-heart"></i>
                {{ post.boardLikeCount || 0 }}
              </span>
              <span class="info-item">
                <i class="fas fa-eye"></i>
                {{ post.boardViews || 0 }}
              </span>
              <span class="info-item">
                <i class="fas fa-comment"></i>
                {{ post.boardCommentsCount }}
              </span>
            </div>
          </div>
        </div>
      </RouterLink>
    </div>

    <!-- 페이지네이션 수정 -->
    <div class="pagination">
      <button 
        :disabled="currentPage === 1"
        @click="currentPage = Math.max(1, currentPage - 1)"
        class="page-button"
      >
        이전
      </button>
      
      <div class="page-numbers">
        <button
          v-for="pageNum in displayedPages"
          :key="pageNum"
          @click="currentPage = pageNum"
          class="page-number"
          :class="{ active: currentPage === pageNum }"
        >
          {{ pageNum }}
        </button>
      </div>

      <button 
        :disabled="currentPage === totalPages"
        @click="currentPage = Math.min(totalPages, currentPage + 1)"
        class="page-button"
      >
        다음
      </button>
    </div>
  </div>

  <!-- 로그인 모달만 Teleport로 추가 -->
  <Teleport to="body">
    <div v-if="showLoginModal" class="modal">
      <LoginViewModal 
        @close="closeLoginModal"
        @login-success="handleLoginSuccess"
      />
    </div>
  </Teleport>
</div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'; 
import axios from 'axios';
import router from '@/router';

import searchImage from '@/assets/img/search1.svg';
import penImg from '@/assets/img/pen.svg';
import LoginViewModal from '@/views/LoginViewModal.vue';

// 게시글 데이터
const posts = ref([]);

// 검색어 상태
const searchQuery = ref('');
// 현재 선택된 카테고리
const selectedCategory = ref('전체');
// 현재 정렬 기준
const sortBy = ref('latest');

// userInfo를 ref로 선언
const userInfo = ref(null);

// API 호출 함수
onMounted(async () => {
  try {
    const response = await axios.get('/api/group');
    
    // 각 게시글의 댓글 수를 가져오는 Promise 배열 생성
    const postsWithComments = await Promise.all(
      response.data.map(async (post) => {
        try {
          const commentsResponse = await axios.get(`/api/group/comment/${post.boardNo}`);
          return {
            ...post,
            boardCategory: post.boardIsResolved ? '모집완료' : '모집중',
            boardLikeCount: post.boardLikeCount || 0,
            boardViews: post.boardViews || 0,
            boardCommentsCount: commentsResponse.data.length || 0
          };
        } catch (error) {
          console.error(`게시글 ${post.boardNo}의 댓글 수 조회 실패:`, error);
          return post;
        }
      })
    );

    posts.value = postsWithComments;
  } catch (error) {
    console.error('API 호출 오류:', error);
    alert('게시글을 불러오는 중 문제가 발생했습니다.');
  }
});

// 필터링된 게시글
const filteredPosts = computed(() => {
  let filtered = posts.value;

  // 카테고리 필터링
  if (selectedCategory.value !== '전체') {
    filtered = filtered.filter(post => post.boardCategory === selectedCategory.value);
  }

  // 검색어 필터링
  if (searchQuery.value) {
    const sanitizedQuery = searchQuery.value.trim().toLowerCase();
    filtered = filtered.filter(post =>
      post.boardTitle.toLowerCase().includes(sanitizedQuery) || 
      post.boardContent.toLowerCase().includes(sanitizedQuery)
    );
  }

  // 정렬 기준 적용
  return [...filtered].sort((a, b) => {
    switch (sortBy.value) {
      case 'latest':
        return new Date(b.boardRegDate) - new Date(a.boardRegDate);
      case 'likes':
        return (b.boardLikeCount || 0) - (a.boardLikeCount || 0);
      case 'comments':
        return (b.boardCommentsCount || 0) - (a.boardCommentsCount || 0);
      default:
        return 0;
    }
  });
});

// 카테고리 필터링
const filterCategory = category => {
  selectedCategory.value = category;
};

// 게시글 색
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
const selectedFiles = ref([]); // 선택된 파일 배열
const imagePreview = ref(''); // 미리보기 URL

// 데이터 초기화 함수
const resetWriteModalData = () => {
  newPostTitle.value = ""; // 제목 초기화
  newPostContent.value = ""; // 내 초기화
  selectedFiles.value = []; // 파일 초기화
  imagePreview.value = ''; // 미리보기 초기화
};

// 글쓰기 모달 닫기 및 데이터 초기화
const closeWriteModal = () => {
  resetWriteModalData(); // 입력 데이터 초기화
  showWriteModal.value = false; // 모달 닫기
};

// 글쓰기 버튼 클릭 이벤트
const openWriteModal = () => {
  if (!isLoggedIn.value) {
    showLoginModal.value = true;
    return;
  }
  resetWriteModalData(); // 기존 력 데이터 초기화
  showWriteModal.value = true; // 모달 열기
};

// 파일 선택 이벤트 핸들러
const handleFileSelect = (event) => {
  const file = event.target.files[0];
  selectedFiles.value = event.target.files;
  
  if (file && file.type.includes('image')) {
    const reader = new FileReader();
    reader.onload = (e) => {
      imagePreview.value = e.target.result;
    };
    reader.readAsDataURL(file);
  } else {
    imagePreview.value = '';
  }
};



// 게시글 삭제
const deletePost = async (boardNo) => {
  if (!confirm('게시글을 삭제하시겠습니까?')) return;

  try {
    await axios.delete(`/api/group/${boardNo}`);
    alert('게시글이 삭제되었습니');
    await fetchPosts(); // 게시글 목록 새로고침
  } catch (error) {
    console.error('게시글 삭제 실패:', error);
    alert('게시글 삭제에 실패했습니다.');
  }
};

// 로그인 상태 확인
const isLoggedIn = computed(() => {
  return !!localStorage.getItem('userInfo');
});

// 로그인 모달 상태
const showLoginModal = ref(false);

// 로그인 모달 관련 함수들
const closeLoginModal = () => {
  showLoginModal.value = false;
};

const handleLoginSuccess = () => {
  showLoginModal.value = false;
  showWriteModal.value = true;
};

// 페이징 관련 로직 추가
const currentPage = ref(1);
const postsPerPage = 9; // 한 페이지당 게시글 수를 9개로 변경

// 페이징된 게시글 목록
const paginatedPosts = computed(() => {
  const startIndex = (currentPage.value - 1) * postsPerPage;
  return filteredPosts.value.slice(startIndex, startIndex + postsPerPage);
});

// 전체 페이지 수 계산
const totalPages = computed(() => {
  return Math.ceil(filteredPosts.value.length / postsPerPage);
});

// 텍스트 자르기 함수
const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + '...';
};

// 페이지 변경 시 유효성 검사 추가
watch(currentPage, (newPage) => {
  if (newPage > totalPages.value) {
    currentPage.value = totalPages.value;
  }
  if (newPage < 1) {
    currentPage.value = 1;
  }
});

// 필터링/정렬 시 페이지 초기화
watch([selectedCategory, sortBy, searchQuery], () => {
  currentPage.value = 1;
});

// 조회수 증가 함수
const incrementViewCount = async (boardNo) => {
  try {
    await axios.put(`/api/group/${boardNo}/view`);
    const post = posts.value.find(p => p.boardNo === boardNo);
    if (post) {
      post.boardViews = (post.boardViews || 0) + 1;
    }
  } catch (error) {
    console.error('조회수 증가 실패:', error);
  }
};

// 댓글 수 업데이트 함수
const updateCommentCount = async (boardNo) => {
  try {
    const response = await axios.get(`/api/group/comment/${boardNo}`);
    const commentCount = response.data.length;
    
    const postIndex = posts.value.findIndex(p => p.boardNo === boardNo);
    if (postIndex !== -1) {
      posts.value[postIndex] = {
        ...posts.value[postIndex],
        boardCommentsCount: commentCount
      };
    }
  } catch (error) {
    console.error('댓글 수 업데이트 실패:', error);
  }
};

// 주기적으로 댓글 수 업데이트 (선택사항)
const startCommentCountPolling = () => {
  setInterval(() => {
    posts.value.forEach(post => {
      updateCommentCount(post.boardNo);
    });
  }, 30000); // 30초마다 업데이트
};

onMounted(() => {
  startCommentCountPolling(); // 폴링 시작
});

// 좋아요 수 업데이트 함수
const updateLikeCounts = async () => {
  try {
    const response = await axios.get('/api/group');
    posts.value = response.data.map(post => ({
      ...post,
      boardCategory: post.boardIsResolved ? '모집완료' : '모집중',
      boardLikeCount: post.boardLikeCount || 0,
      boardViews: post.boardViews || 0
    }));
  } catch (error) {
    console.error('좋아요 수 업데이트 실패:', error);
  }
};

// 주기적 업데이트 설정
onMounted(() => {
  // 기존 코드...
  
  // 5초마다 좋아요 수 업데이트
  const interval = setInterval(updateLikeCounts, 5000);
  
  // 컴포넌트 언마운트 시 인터벌 정리
  onUnmounted(() => {
    clearInterval(interval);
  });
});

// 페이지네이션 관련 computed 속성 추가
const displayedPages = computed(() => {
  const pageGroup = Math.ceil(currentPage.value / 5);
  const start = (pageGroup - 1) * 5 + 1;
  const end = Math.min(pageGroup * 5, totalPages.value);
  
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});

// 컴포넌트 마운트 시 사용자 정보 로드
onMounted(() => {
  const storedUserInfo = localStorage.getItem('userInfo');
  console.log('저장된 사용자 정보:', storedUserInfo);
  
  if (storedUserInfo) {
    userInfo.value = JSON.parse(storedUserInfo);
    console.log('파싱된 사용자 정보:', userInfo.value);
  }
});

// 게시글 목록을 가져오는 함수
const fetchPosts = async () => {
  try {
    const response = await axios.get('/api/group');
    posts.value = response.data;
  } catch (error) {
    console.error('게시글 목록 조회 실패:', error);
  }
};

const submitPost = async () => {
  const storedUser = JSON.parse(localStorage.getItem('userInfo'));

  try {
    const formData = new FormData();
    formData.append('userNo', String(storedUser.userNo));
    formData.append('boardTitle', newPostTitle.value);
    formData.append('boardContent', newPostContent.value);
    
    if (selectedFiles.value.length > 0) {
      formData.append('boardFile', selectedFiles.value[0]);
    }

    const response = await axios.post('/api/group', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.data) {
      alert('게시글이 등록되었습니다.');
      closeWriteModal();
      await fetchPosts(); // 목록 새로고침
    }
  } catch (error) {
    console.error('에러:', error.response?.data || error.message);
    alert('게시글 등록에 실패했습니다.');
  }
};

// 컴포넌트 마운트 시 게시글 목록 가져오기
onMounted(() => {
  fetchPosts();
});

</script>

<style scoped>
/* 전체 컨���이너 */
.event-div {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 상단바 */
.top-bar {
  width: 100vw;
  position: relative;
  left: 50%;
  right: 50%;
  margin-left: -50vw;
  margin-right: -50vw;
  background: linear-gradient(to right, #ffd987, #fcdfa0);
  color: #ffffff;
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

/* 정렬탭1 */
.board-tab1 {
  width: 100%;
  margin: 40px 0 20px 0;
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #eee;
}

.board-tab1 .tab-item {
  position: relative;
  padding: 10px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #666;
  transition: color 0.3s ease, transform 0.2s ease;
}

.board-tab1 .tab-item:hover {
  color: #000; /* 호버 시  색상 */
  transform: translateY(-2px); /* 살짝 위로 이동 */
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

/* 검색바 */
.search-wrapper {
  width: 100%;
  max-width: 500px;
  margin: 20px auto;
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

/* 정렬탭2 */
.board-tab2 {
  width: 100%;
  margin: 20px 0;
  display: flex;
  gap: 20px;
}

.board-tab2 .tab-item2 {
  position: relative;
  padding: 8px 15px; /* 기존 패딩 유지 */
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  color: #aaa; /* 기본 색 */
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
  bottom: -5px; /* 버 아래쪽 여백 */
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

/* 반응형 디자인 (위치 유지, 크기만 조정) */
@media (max-width: 768px) {
  .board-tab1, .board-tab2 {
    width: 90%;
    margin: 20px auto 0;
  }

  .search-wrapper {
    width: 80%;
  }

  .write-wrapper {
    margin: 20px;
  }

  .list-container {
    padding: 20px 5%;
  }
}



/* 검색창 */
.search-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px auto;
  width: 500px;
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
  margin: 20px auto;
  width: 1200px;
  padding-right: 40px;
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
  z-index: 1000;
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
  width: 1200px;
  margin: 0 auto;
}

.list-grid {
  display: flex;
  flex-direction: column;
}

.list-item {
  display: flex;
  flex-direction: column;
  padding: 20px 56px;
  border-bottom: 1px solid #e0e0e0;
  border-top: 1px solid #e0e0e0;
  position: relative;
  transition: background-color 0.2s ease;
}

.list-item:hover {
  background-color: #f8f9fa;
}

/* 게시글 헤더 (태그 + 제목) */
.item-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

/* 태그 스타일 */
.tag {
  background: #ffd987;
  border-radius: 30px;
  padding: 6px 15px;
  color: #ffffff;
  font-size: 20px;
  font-weight: 600;
  min-width: 74px;
  text-align: center;
}

.tag.completed {
  background: #adb5bd;
}

/* 제목 */
.title {
  font-size: 24px;
  font-weight: 600;
  color: #000000;
}

/* 내용 */
.description {
  font-size: 24px;
  color: #000000;
  margin: 5px 0;
  padding-left: 0;
}

/* 하단 정보 */
.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

/* 작성자 */
.author {
  font-size: 24px;
  color: #878787;
}

/* 조회수, 좋아요, 댓글 정보 */
.info {
  display: flex;
  gap: 30px;
  align-items: center;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 24px;
  color: #000000;
}

.info-item i {
  font-size: 20px;
  color: #666;
}

/* 링크 일 제거 */
.list-link {
  text-decoration: none;
  color: inherit;
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.page-button {
  padding: 8px 16px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  font-size: 16px;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-number {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 16px;
  color: #666;
}

.page-number.active {
  background: #ffd987;
  color: white;
  border-radius: 50%;
}

/* 카테고리 태그 스타일 */
.category-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 12px;
}

.category-tag.yellow {
  background: #ffd987;
  color: #fff;
}

.category-tag.blue {
  background: #87CEEB;
  color: #fff;
}

.category-tag.green {
  background: #98FB98;
  color: #fff;
}

.category-tag.red {
  background: #FFB6C1;
  color: #fff;
}

/* 제목 스타일 */
.title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

/* 내용 스타일 */
.content {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

/* 하단 정보 영역 */
.post-info {
  display: flex;
  align-items: center;
  gap: 16px;
  color: #888;
  font-size: 13px;
}

.views, .comments {
  display: flex;
  align-items: center;
  gap: 4px;
}

.views i, .comments i {
  font-size: 16px;
}

/* 해결 여부 표시 */
.resolution-status {
  font-size: 13px;
  color: #888;
}

/* 게시글 목록 컨테이너 */
.list-container {
  display: flex;
  flex-direction: column;
  gap: 25px;
  padding: 20px 15%;
  background-color: #fff;
  margin: 0 auto;
}

/* 게시글 아이템 */
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

/* 아이템 헤더 (태그 + 제목) */
.item-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

/* 태그 스타일 */
.tag {
  display: inline-block;
  font-size: 12px;
  font-weight: bold;
  padding: 5px 10px;
  border-radius: 10px;
}

.tag.user { background-color: #ffe4b3; color: #ffa500; }
.tag.video { background-color: #cce5ff; color: #007bff; }
.tag.community { background-color: #d4edda; color: #28a745; }
.tag.etc { background-color: #f8d7da; color: #dc3545; }

/* 목 */
.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

/* 내용 */
.description {
  font-size: 14px;
  color: #777;
  margin: 10px 0;
}

/* 하단 정보 */
.item-footer {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #555;
  font-size: 14px;
}

/* 링크 스타일 제거 */
.list-link {
  text-decoration: none;
  color: inherit;
}

/* 다크모드 스타일 */
:root.dark-mode .event-div {
  background-color: #1a1a1a;
  color: #e0e0e0;
}

:root.dark-mode .top-bar {
  background: linear-gradient(to right, #c4a05e, #d4b36b);
  color: #1a1a1a;
}

:root.dark-mode .list-item {
  background: #2d2d2d;
  border: 1px solid #404040;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
}

:root.dark-mode .list-item:hover {
  background: #333333;
  transform: translateY(-5px);
  box-shadow: 0px 6px 12px rgba(0, 0, 0, 0.3);
}

:root.dark-mode .title {
  color: #e0e0e0;
}

:root.dark-mode .description {
  color: #b0b0b0;
}

:root.dark-mode .author {
  color: #909090;
}

:root.dark-mode .info-item {
  color: #909090;
}

:root.dark-mode .search-bar {
  background: #2d2d2d;
  border: 1px solid #404040;
}

:root.dark-mode .search-bar input {
  color: #e0e0e0;
}

:root.dark-mode .search-bar input::placeholder {
  color: #909090;
}

:root.dark-mode .search-bar:focus-within {
  background: #333333;
}

:root.dark-mode .board-tab1 .tab-item,
:root.dark-mode .board-tab2 .tab-item2 {
  color: #909090;
}

:root.dark-mode .board-tab1 .tab-item:hover,
:root.dark-mode .board-tab2 .tab-item2:hover {
  color: #e0e0e0;
}

:root.dark-mode .board-tab1 .tab-item.active,
:root.dark-mode .board-tab2 .tab-item2.active {
  color: #ffffff;
}

:root.dark-mode .modal-content {
  background: #2d2d2d;
  border: 1px solid #404040;
}

:root.dark-mode .modal-content input,
:root.dark-mode .modal-content textarea {
  background: #333333;
  border: 1px solid #404040;
  color: #e0e0e0;
}

:root.dark-mode .page-button,
:root.dark-mode .page-number {
  color: #909090;
}

:root.dark-mode .page-number.active {
  background: #c4a05e;
  color: #1a1a1a;
}

:root.dark-mode .tag {
  background: #c4a05e;
  color: #1a1a1a;
}

:root.dark-mode .tag.completed {
  background: #666666;
  color: #e0e0e0;
}

.image-preview {
  margin: 10px 0;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.image-preview img {
  max-width: 200px;
  height: auto;
  display: block;
  margin: 0 auto;
}

</style>

