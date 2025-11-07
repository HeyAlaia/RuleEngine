import { createApp } from 'vue'
import App from './App.vue'
import 'virtual:uno.css'
import ElementPlus from "element-plus";
import { createPinia } from "pinia";

const store = createPinia();
const app = createApp(App)
// 注册插件
app.use(ElementPlus)
app.use(store);
app.mount('#app')
