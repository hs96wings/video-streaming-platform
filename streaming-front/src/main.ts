import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import vuetify from './plugins/vuetify';
import router from '@/router/index';

// axios 전역 설정은 이제 src/api/axios.ts에서 모두 처리됨

const app = createApp(App);
const pinia = createPinia();
app.use(pinia);
app.use(router);
app.use(vuetify);

app.mount('#app');
