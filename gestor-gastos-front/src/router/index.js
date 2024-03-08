// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import FirstView from '@/views/First-view.vue';
import UserRegister from '@/views/UserRegister.vue';

const routes = [
  {
    path: '/',
    name: 'login',
    component: FirstView,
  },
  {
    path: '/register',
    name: 'register',
    component: UserRegister, // Define la ruta para la vista de registro
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
