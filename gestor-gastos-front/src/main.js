import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ToastPlugin from 'vue-toast-notification';
import 'vue-toast-notification/dist/theme-bootstrap.css';

import './assets/styles/global.css';

const app = createApp(App);

// Usa Vue Router
app.use(router);
app.use(ToastPlugin);

// Monta la aplicación
app.mount('#app');
