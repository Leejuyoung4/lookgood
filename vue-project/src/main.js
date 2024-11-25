import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// 현재 실행 중인 호스트의 IP 주소 가져오기
const currentIP = window.location.hostname;

// axios 기본 설정 (withCredentials 제거)
axios.defaults.baseURL = `http://${currentIP}:8080`
// axios.defaults.baseURL = `http://localhost:8080`
axios.defaults.withCredentials = false; 

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')