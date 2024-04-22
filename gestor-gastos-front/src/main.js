import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

import './assets/styles/global.css';

const app = createApp(App);

// Usa Vue Router
app.use(router);

// Monta la aplicación
app.mount('#app');
