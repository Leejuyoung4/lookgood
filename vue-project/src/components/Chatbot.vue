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
  console.log('입력된 질문:', input); // 디버깅용
  console.log('이벤트 데이터:', props.event); // 디버깅용
  
  const lowerInput = input.toLowerCase();
  
  // 이벤트 소개
  if (lowerInput.includes('소개') || lowerInput.includes('이벤트') || lowerInput.includes('뭐하는')) {
    return `${props.event.title || '이벤트'} 소개입니다!\n\n${props.event.description || '상세 내용 준비중입니다.'}`;
  }
  
  // 날짜/시간
  if (lowerInput.includes('날짜') || lowerInput.includes('시간') || lowerInput.includes('언제')) {
    const date = props.event.eventDate ? new Date(props.event.eventDate).toLocaleDateString() : '날짜 미정';
    const time = props.event.eventTime || '시간 미정';
    return `행사 일정 안내입니다!\n\n📅 날짜: ${date}\n⏰ 시간: ${time}`;
  }
  
  // 장소/위치
  if (lowerInput.includes('장소') || lowerInput.includes('위치') || lowerInput.includes('어디')) {
    return `행사 장소 안내입니다!\n\n📍 ${props.event.address || '장소 미정'}\n\n* 상단 지도에서 자세한 위치를 확인하실 수 있습니다.`;
  }
  
  // 참가비
  if (lowerInput.includes('참가비') || lowerInput.includes('비용') || lowerInput.includes('금액')) {
    const fee = props.event.entryFee ? `${props.event.entryFee.toLocaleString()}원` : '무료';
    return `참가비 안내입니다!\n\n💰 참가비: ${fee}\n\n* 현장 카드결제 가능합니다.`;
  }
  
  // 참가 인원
  if (lowerInput.includes('인원') || lowerInput.includes('신청')) {
    const max = props.event.maxParticipants || '인원제한 없음';
    const current = props.event.currentParticipants || 0;
    return `참가 인원 안내입니다!\n\n👥 최대 인원: ${max}\n현재 신청: ${current}명`;
  }
  
  // 준비물
  if (lowerInput.includes('준비물') || lowerInput.includes('챙길')) {
    return props.event.preparations 
      ? `준비물 안내입니다!\n\n${props.event.preparations}`
      : '특별한 준비물은 없습니다. 편하게 오시면 됩니다! 😊';
  }

  // 기본 응답 (키워드를 찾지 못했을 때)
  return `안내 가능한 정보입니다! 😊\n\n🔍 이벤트 소개\n📅 날짜/시간\n📍 장소/위치\n💰 참가비\n👥 참가 인원\n🎒 준비물\n\n궁금하신 내용을 선택해서 물어보세요!`;
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