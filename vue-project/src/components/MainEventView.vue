<template>
  <div class="info-div">
    <div class="suggestion">Suggestion</div>
    <div class="suggestion-info">이 대회! 알고 계셨나요?</div>

    <div class="more-info">
      <RouterLink to="/event" style="text-decoration: none;">
        <span class="more-info-yellow">대회 </span>
        <span class="more-info-black">더보기</span>
      </RouterLink>
    </div>

    <div class="images-container">
      <RouterLink :to="`/event/detail/${selectedEvents[0]?.id}`" v-if="selectedEvents[0]">
        <img class="event1" :src="selectedEvents[0]?.image" style="cursor: pointer;"/>
      </RouterLink>
      <RouterLink :to="`/event/detail/${selectedEvents[1]?.id}`" v-if="selectedEvents[1]">
        <img class="event2" :src="selectedEvents[1]?.image" style="cursor: pointer;"/>
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { RouterLink } from 'vue-router';

// 이미지와 이벤트 ID 매핑
const eventLinks = [
  { 
    image: new URL('@/assets/img/badminton.png', import.meta.url).href, 
    id: 7 
  },
  { 
    image: new URL('@/assets/img/running.png', import.meta.url).href, 
    id: 8
  },
  { 
    image: new URL('@/assets/img/running1.png', import.meta.url).href, 
    id: 43 
  },
  { 
    image: new URL('@/assets/img/ocean.png', import.meta.url).href, 
    id: 45  // ocean 이미지의 ID를 45로 변경
  },
  { 
    image: new URL('@/assets/img/walking.png', import.meta.url).href, 
    id: 33 
  }
];

const selectedEvents = ref([]);

const selectRandomImages = () => {
  const shuffled = [...eventLinks].sort(() => 0.5 - Math.random());
  selectedEvents.value = shuffled.slice(0, 2);
};

onMounted(() => {
  selectRandomImages();
});
</script>

<style scoped>
.info-div {
  background: var(--bg-color);
  box-sizing: border-box;
  height: 1080px;
  position: relative;
  overflow: hidden;
  transition: background-color 0.3s ease;
}

.suggestion {
  color: #ff9c2b;
  font-size: 34px;
  font-weight: 800;
  position: absolute;
  left: calc(50% - 80px);
  top: 110px;
}
.suggestion-info {
  color: var(--text-color);
  text-align: center;
  font-size: 32px;
  font-weight: 400;
  position: absolute;
  left: calc(50% - 306px);
  top: 190px;
  width: 612px;
}

.more-info {
  text-align: center;
  font-family: "-", sans-serif;
  font-size: 34px;
  font-weight: 400;
  position: absolute;
  left: calc(50% - 82px);
  top: 950px;
}
.more-info-yellow {
  color: #ff9c2b;
  font-family: "Inter-ExtraBold", sans-serif;
  font-size: 34px;
  font-weight: 600;
}
.more-info-black {
  color: var(--text-color);
}

.images-container {
  position: absolute;
  top: 330px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10vw;
}

.event1,
.event2 {
  width: 400px;
  height: 500px;
  object-fit: cover;
  border-radius: 8px;
  transition: all 0.3s ease;
  filter: var(--image-filter, none);
}

.specific-image {
  opacity: 1 !important;
  visibility: visible !important;
  display: block !important;
}

:root.dark-mode .event1,
:root.dark-mode .event2 {
  --image-filter: brightness(0.8);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
}

</style>