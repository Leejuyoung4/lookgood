<template>
	<div class="wrapper">
	  <Carousel ref="carousel" :autoplay="5000" :wrap-around="true">
		<Slide v-for="(slide, index) in slides" :key="slide" @click="goToPage(index)">
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
  import { useRouter } from 'vue-router';
  import { Carousel, Pagination, Slide } from 'vue3-carousel';
  import "vue3-carousel/dist/carousel.css";
  
  const router = useRouter();
  
  // 이미지와 이동할 경로를 매핑 (URL 사용)
  const slideLinks = [
	{ 
	  image: new URL('@/assets/img/kang.png', import.meta.url).href,
	  path: '/videos/12' 
	},
	{ 
	  image: new URL('@/assets/img/pang.png', import.meta.url).href,
	  path: '/videos/13' 
	},
	{ 
	  image: new URL('@/assets/img/lia.png', import.meta.url).href,
	  path: '/videos/8' 
	}
  ];
  
  const slides = ref(slideLinks.map(slide => slide.image));
  
  // 페이지 이동 함수
  const goToPage = (index) => {
	router.push(slideLinks[index].path);
  };
  
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
	margin-top: 5vh;
	margin-bottom: 10vh;
	position: relative;
	max-width: 1200px;
	margin-left: auto;
	margin-right: auto;
  }
  
  .carousel__item {
	width: 100%;
	height: 450px;
	color: var(--vc-clr-white);
	font-size: 20px;
	border-radius: 8px;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
  }
  
  .slideImg {
	width: 90%;
	height: 90%;
	object-fit: contain;
	border-radius: 8px;
  }
  
  /* 커스텀 내비게이션 및 페이지네이션 스타일 */
  .custom-navigation {
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 20px;
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
	font-size: 24px;
	cursor: pointer;
	display: flex;
	align-items: center;
	justify-content: center;
  }
  
  .controller-button:hover {
	color: #ccc;
  }
  </style>
  