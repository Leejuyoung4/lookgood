<template>
  <div class="info-div">
    <div class="suggestion">Suggestion</div>
    <div class="suggestion-info">이 대회! 알고 계셨나요?</div>

    <div class="more-info">
      <RouterLink to="/competition" style="text-decoration: none;">
        <span class="more-info-yellow">대회 </span>
        <span class="more-info-black">더보기</span>
      </RouterLink>
    </div>

    <div class="images-container">
      <img class="event1" :src="randomImages[0]" @click="goToMain" style="cursor: pointer;"/>
      <img class="event2" :src="randomImages[1]" @click="goToMain" style="cursor: pointer;"/>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import event1 from '@/assets/img/event1.jpg';
import event2 from "@/assets/img/event2.jpg";
import event3 from "@/assets/img/event3.jpg";
import event4 from "@/assets/img/event4.jpg";
import event5 from "@/assets/img/event5.jpg";

// 이미지 리스트
const images = [event1, event2, event3, event4, event5];
const randomImages = ref([]);

// 랜덤으로 이미지 두 개 선택
const selectRandomImages = () => {
  const shuffled = images.sort(() => 0.5 - Math.random());
  randomImages.value = shuffled.slice(0, 2);
};

onMounted(() => {
  selectRandomImages();
});
</script>

<style scoped>
.info-div {
  background: #fae4b5;
  box-sizing: border-box;
  height: 1080px;
  position: relative;
  overflow: hidden;
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
  color: #000000;
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
  color: #000000;
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
  
}

</style>