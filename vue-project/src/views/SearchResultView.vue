<template>
  <div class="search-result-view">
    <!-- 헤더 -->
    <header class="header">
        검색 결과
    </header>

    <!-- 결과 컨테이너 -->
    <main class="results-container">
      <!-- <h1>검색 결과</h1> -->

      <!-- 결과가 없을 때 -->
      <div v-if="searchResults.length === 0" class="no-results">
        <p>결과가 없습니다.</p>
      </div>

      <!-- 결과 목록 -->
      <ul v-else class="results-list">
        <li v-for="result in searchResults" :key="result.id" class="result-item">
          <!-- <h2 class="result-source">{{ result.source }}</h2> -->
          <p class="result-content">{{ result.content }}</p>
        </li>
      </ul>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const searchQuery = ref(route.query.query || ''); // URL에서 검색어 가져오기
const searchResults = ref([]); // 검색 결과 저장
const isSearching = ref(false); // 로딩 상태 저장

// 검색 로직
const fetchSearchResults = async () => {
  if (searchQuery.value.trim()) {
    isSearching.value = true; // 로딩 상태 활성화
    try {
      const response = await axios.get(`/api/search`, {
        params: { query: searchQuery.value },
      });
      searchResults.value = response.data; // 검색 결과 저장
    } catch (error) {
      console.error('검색 로직 실행 중 오류 발생:', error);
      searchResults.value = []; // 오류 발생 시 빈 결과
    } finally {
      isSearching.value = false; // 로딩 상태 해제
    }
  } else {
    searchResults.value = []; // 빈 검색어 처리
  }
};

// 최초 검색 실행
onMounted(() => {
  fetchSearchResults();
});

// URL 쿼리 변경 감지
watch(
  () => route.query.query, // 감시 대상: URL의 query
  (newQuery) => {
    searchQuery.value = newQuery || ''; // 검색어 업데이트
    fetchSearchResults(); // 새로운 검색 실행
  }
);
</script>

<style scoped>
/* 전체 컨테이너 */
.search-result-view {
  max-width: 1200px;
  margin: 200px auto;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 헤더 스타일 */
.header {
  font-size: 38px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}

/* 검색 결과 컨테이너 */
.results-container {
  padding: 20px;
  background: #fff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
}

/* 결과가 없을 때 스타일 */
.no-results {
  text-align: center;
  color: #6c757d;
  font-size: 16px;
}

/* 결과 목록 */
.results-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.result-item {
  padding: 15px;
  border-bottom: 1px solid #dee2e6;
  display: flex;
  flex-direction: column;
}

.result-item:last-child {
  border-bottom: none;
}

/* 결과 제목 */
.result-source {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #495057;
}

/* 결과 내용 */
.result-content {
  font-size: 22px;
  color: #6c757d;
  line-height: 1.6;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .search-result-view {
    padding: 15px;
  }

  .header {
    font-size: 24px;
  }

  .results-container {
    padding: 15px;
  }

  .result-item {
    padding: 10px;
  }

  .result-source {
    font-size: 16px;
  }

  .result-content {
    font-size: 12px;
  }
}
</style>
