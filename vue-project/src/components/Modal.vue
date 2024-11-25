<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      <!-- 헤더 -->
      <div class="modal-header">
        <span>이용약관</span>
        <button class="close-btn" @click="closeModal">X</button>
      </div>

      <!-- 본문 -->
      <div class="modal-body">
        <slot></slot> <!-- 내용을 여기에 삽입 -->
      </div>
    </div>
  </div>
</template>


<script setup>
import { defineProps, defineEmits } from 'vue';

// Props 정의
defineProps({
  isOpen: {
    type: Boolean,
    required: true,
  },
});

// Emits 정의
const emit = defineEmits(['close']);

const closeModal = () => {
  emit('close');
};
</script>

<style scoped>
/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 모달 컨텐츠 */
.modal-content {
  background: white;
  border-radius: 10px;
  max-width: 700px;
  height: 50vh; /* 고정 높이 */
  width: 90%;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
}

/* 모달 헤더 */
.modal-header {
  background: #ebd03b; /* 헤더 배경색 (파란색) */
  color: white; /* 텍스트 색상 */
  padding: 15px 20px;
  font-size: 18px;
  font-weight: bold;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 닫기 버튼 */
.close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  font-weight: bold;
  color: white;
  cursor: pointer;
  transition: transform 0.2s ease, color 0.2s ease;
}

.close-btn:hover {
  transform: scale(1.2);
  color: #ffcccb; /* 닫기 버튼 호버 시 색상 변경 */
}

/* 모달 본문 */
.modal-body {
  height: calc(100% - 50px); /* 헤더 높이를 제외한 나머지 영역 */
  overflow-y: auto;
  padding: 20px;
  line-height: 1.6;
  font-size: 16px;
  color: #333333;
}

/* 스크롤바 커스터마이징 */
.modal-body::-webkit-scrollbar {
  width: 8px;
}

.modal-body::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 4px;
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.4);
}

/* 모달 애니메이션 */
@keyframes slideDown {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-content {
  animation: slideDown 0.3s ease-out;
}

</style>

