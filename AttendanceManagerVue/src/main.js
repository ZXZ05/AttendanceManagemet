import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { installInitializeMixin } from '@/../initialize'
import axios from './axios/axios.js'

const app = createApp(App)
installInitializeMixin(app)

app.config.globalProperties.axios = axios
app.config.globalProperties.$axios = axios

app.use(ElementPlus)
app.use(router)

app.mount('#app')
