import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/app.css'
import { installInitializeMixin } from '@/../initialize'

const app = createApp(App)
installInitializeMixin(app)

app.use(ElementPlus)
app.use(router)

app.mount('#app')
