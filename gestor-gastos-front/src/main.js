import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // Importa Vue Router

import './assets/styles/global.css';

// Crea una instancia de la aplicación Vue
const app = createApp(App);

// Usa Vue Router
app.use(router);

// Monta la aplicación
app.mount('#app');
