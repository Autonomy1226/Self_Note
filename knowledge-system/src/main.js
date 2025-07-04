import { createApp } from 'vue'
import './style.css'
import './assets/obsidian-theme.css'
import App from './App.vue'
import router from './router/index.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

createApp(App).use(router).use(ElementPlus).mount('#app')
