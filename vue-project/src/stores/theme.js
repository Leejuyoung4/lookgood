import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const isDarkMode = ref(localStorage.getItem('darkMode') === 'true')

  function initializeTheme() {
    const savedTheme = localStorage.getItem('darkMode')
    if (savedTheme) {
      isDarkMode.value = savedTheme === 'true'
    }
    
    if (isDarkMode.value) {
      document.documentElement.classList.add('dark-mode')
    } else {
      document.documentElement.classList.remove('dark-mode')
    }
  }

  watch(isDarkMode, (newValue) => {
    localStorage.setItem('darkMode', newValue)
    if (newValue) {
      document.documentElement.classList.add('dark-mode')
    } else {
      document.documentElement.classList.remove('dark-mode')
    }
  })

  function toggleDarkMode() {
    isDarkMode.value = !isDarkMode.value
  }

  return { 
    isDarkMode, 
    toggleDarkMode,
    initializeTheme
  }
})