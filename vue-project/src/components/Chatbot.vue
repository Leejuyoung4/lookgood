<template>
  <div class="chatbot-container">
    <div class="chat-messages" ref="messageContainer">
      <div v-for="(message, index) in messages" 
           :key="index" 
           :class="['message', message.type]">
        {{ message.text }}
      </div>
    </div>
    <div class="chat-input">
      <input 
        v-model="userInput" 
        @keyup.enter="sendMessage"
        placeholder="질문을 입력하세요..."
        type="text"
      >
      <button @click="sendMessage">
        <i class="bi bi-send-fill"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';

// props로 event 데이터 받기
const props = defineProps({
  event: {
    type: Object,
    required: true,
    default: () => ({})
  }
});

const messages = ref([
  { 
    type: 'bot', 
    text: '안녕하세요! 이벤트 도우미입니다. 궁금하신 내용을 물어보세요! 😊' 
  }
]);

const userInput = ref('');
const messageContainer = ref(null);

const scrollToBottom = async () => {
  await nextTick();
  if (messageContainer.value) {
    messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
  }
};

const getBotResponse = (input) => {
  console.log('이벤트 데이터:', props.event); // 디버깅용
  
  const lowerInput = input.toLowerCase();
  
  // 이벤트 소개
  if (lowerInput.includes('소개') || lowerInput.includes('이벤트') || lowerInput.includes('뭐하는')) {
    return props.event.description || '죄송합니다. 이벤트 정보를 불러올 수 없습니다.';
  }
  
  // 시간 관련
  if (lowerInput.includes('시간') || lowerInput.includes('언제')) {
    return `운영 시간 안내입니다!\n\n⏰ 입장: ${props.event.checkInTime}\n⏰ 퇴장: ${props.event.checkOutTime}`;
  }
  
  // 장소/위치
  if (lowerInput.includes('장소') || lowerInput.includes('위치') || lowerInput.includes('어디')) {
    return `행사 장소 안내입니다!\n\n📍 주소: ${props.event.address}\n\n* 상단 지도에서 자세한 위치를 확인하실 수 있습니다.`;
  }
  
  // 참가비
  if (lowerInput.includes('참가비') || lowerInput.includes('비용') || lowerInput.includes('금액')) {
    return `참가비 안내입니다!\n\n💰 참가비: ${props.event.entryFee?.toLocaleString()}원`;
  }
  
  // 주차
  if (lowerInput.includes('주차')) {
    return `주차 안내입니다!\n\n🚗 주차 ${props.event.parkingAvailable ? '가능' : '불가능'}합니다.`;
  }
  
  // 시설
  if (lowerInput.includes('시설') || lowerInput.includes('편의')) {
    return `시설 안내입니다!\n\n🏢 제공 시설:\n${props.event.facilities}`;
  }

  // 문의
  if (lowerInput.includes('문의') || lowerInput.includes('연락') || lowerInput.includes('전화')) {
    return `문의처 안내입니다!\n\n📞 문의 전화: ${props.event.inquiryNumber}\n🌐 웹사이트: ${props.event.websiteUrl}`;
  }

  // 기본 응답
  return `안내 가능한 정보입니다! 😊\n\n🔍 이벤트 소개\n⏰ 운영 시간\n📍 장소/위치\n💰 참가비\n🚗 주차\n🏢 시설\n🌐 문의\n\n궁금하신 내용을 선택해서 물어보세요!`;
};

const sendMessage = () => {
  const trimmedInput = userInput.value.trim();
  if (!trimmedInput) return;
  
  // 사용자 메시지 추가
  messages.value.push({ type: 'user', text: trimmedInput });
  
  // 봇 응답 생성 및 추가
  const botResponse = getBotResponse(trimmedInput);
  messages.value.push({ type: 'bot', text: botResponse });
  
  // 스크롤 맨 아래로 이동
  scrollToBottom();
  
  // 사용자 입력 초기화
  userInput.value = '';
};

onMounted(() => {
  scrollToBottom();
});
</script>

<style scoped>
.chatbot-container {
  display: flex;
  flex-direction: column;
  height: 500px;
  width: 380px;
  background-color: #FFF5E6;
  overflow: hidden;
  position: relative;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  height: calc(100% - 60px);
  min-height: 0;
}

.message {
  max-width: 85%;
  padding: 12px 16px;
  border-radius: 15px;
  margin: 3px 0;
  font-size: 15px;
  line-height: 1.5;
  word-break: break-word;
}

.message.bot {
  align-self: flex-start;
  background-color: #FFE4B5;
  color: #8B4513;
  border-bottom-left-radius: 5px;
}

.message.user {
  align-self: flex-end;
  background-color: #DEB887;
  color: white;
  border-bottom-right-radius: 5px;
}

.chat-input {
  height: 70px;
  min-height: 70px;
  max-height: 70px;
  padding: 15px;
  background-color: #FFE4B5;
  display: flex;
  gap: 10px;
  align-items: center;
  border-top: 1px solid rgba(222, 184, 135, 0.3);
}

.chat-input input {
  flex: 1;
  height: 45px;
  padding: 0 20px;
  border: 2px solid #DEB887;
  border-radius: 25px;
  outline: none;
  font-size: 15px;
}

.chat-input button {
  width: 45px;
  height: 45px;
  min-width: 45px;
  min-height: 45px;
  border-radius: 50%;
  border: none;
  background-color: #DEB887;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.chat-input button i {
  font-size: 18px;
}

@media (max-width: 768px) {
  .chatbot-container {
    width: 340px;
    height: 450px;
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>