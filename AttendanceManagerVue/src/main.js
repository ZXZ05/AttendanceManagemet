import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import './styles/app.css'
import { installInitializeMixin } from '@/../initialize'
import { installElementPlus } from '@/plugins/element-plus'

const app = createApp(App)
installInitializeMixin(app)

installElementPlus(app)
app.use(router)

app.mount('#app')
