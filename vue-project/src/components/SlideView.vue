<template>
	<div class="wrapper">
	  <Carousel ref="carousel" :autoplay="5000" :wrap-around="true">
		<Slide v-for="slide in slides" :key="slide">
		  <div class="carousel__item">
			<img class="slideImg" :src="slide" style="cursor: pointer;" />
		  </div>
		</Slide>
  
		<template #addons>
		  <!-- 커스텀 내비게이션 버튼을 페이지네이션 양옆에 배치 -->
		  <div class="custom-navigation">
			<button class="controller-button" @click="prevSlide">‹</button>
			<Pagination />
			<button class="controller-button" @click="nextSlide">›</button>
		  </div>
		</template>
	  </Carousel>
	</div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { Carousel, Pagination, Slide } from 'vue3-carousel';
  import home_1 from "@/assets/img/exer1.avif";
  import home_2 from "@/assets/img/exer2.jfif";
  import home_3 from "@/assets/img/exer3.jfif";
  
  import "vue3-carousel/dist/carousel.css";
  
  const slides = ref([home_1, home_2, home_3]);
  
  // Carousel 인스턴스 참조 변수
  const carousel = ref(null);
  
  // 슬라이드 이동 함수
  function prevSlide() {
	if (carousel.value) {
	  carousel.value.prev();
	}
  }
  
  function nextSlide() {
	if (carousel.value) {
	  carousel.value.next();
	}
  }
  </script>
  
  <style>
  .wrapper {
	margin-top: 7vh;
	margin-bottom: 20vh;
	position: relative;
  }
  
  .carousel__item {
	width: 100%;
	height: 500px;
	color: var(--vc-clr-white);
	font-size: 20px;
	border-radius: 8px;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
  }
  
  .slideImg {
	width: 100%;
	height: 100%;
	object-fit: contain;
	border-radius: 8px;
  }
  
  /* 커스텀 내비게이션 및 페이지네이션 스타일 */
  .custom-navigation {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 15px;
	background-color: #75757505;
	color: white;
	border-radius: 20px;
	padding: 10px 20px;
	position: absolute;
	bottom: 20px;
	left: 50%;
	transform: translateX(-50%);
  }
  
  .controller-button {
	background: none;
	border: none;
	color: white;
	font-size: 18px;
	cursor: pointer;
	display: flex;
	align-items: center;
	justify-content: center;
  }
  
  .controller-button:hover {
	color: #ccc;
  }
  </style>
  