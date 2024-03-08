// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import FirstView from '@/views/First-view.vue';

const routes = [
  {
    path: '/',
    name: 'login',
    component: FirstView,
  },
  // Agrega más rutas según sea necesario
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
