import { defineStore } from 'pinia'

// 테마 스토어 정의
export const useThemeStore = defineStore('theme', {
  // 상태(state) 정의
  state: () => ({
    isDarkMode: false  // 다크모드 상태를 저장할 변수
  }),
  
  // 액션(actions) 정의
  actions: {
    // 다크모드 토글 함수
    toggleDarkMode() {
      this.isDarkMode = !this.isDarkMode  // 상태 변경
      localStorage.setItem('darkMode', this.isDarkMode)  // 로컬 스토리지에 저장
      document.documentElement.classList.toggle('dark-mode')  // HTML에 클래스 토글
    },
    
    // 테마 초기화 함수
    initializeTheme() {
      // 로컬 스토리지에서 저장된 테마 설정 가져오기
      const savedMode = localStorage.getItem('darkMode')
      
      if (savedMode !== null) {
        // 저장된 설정이 있으면 그 설정 사용
        this.isDarkMode = savedMode === 'true'
      } else {
        // 저장된 설정이 없으면 시스템 설정 사용
        this.isDarkMode = window.matchMedia('(prefers-color-scheme: dark)').matches
      }
      
      // 초기 테마 적용
      if (this.isDarkMode) {
        document.documentElement.classList.add('dark-mode')
      }
    }
  }
})