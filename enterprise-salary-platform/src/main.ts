import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import router from './router' // 引入路由
import { createPinia } from 'pinia'

const app = createApp(App)

app.use(createPinia())
app.use(router) // 启用路由
app.use(ElementPlus, {
  locale: zhCn,
})

app.mount('#app')