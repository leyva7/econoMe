// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import FirstView from '@/views/First-view.vue';
import UserRegister from '@/views/UserRegister.vue';
import HomeUser from '@/views/HomeUser.vue';
import {createApp} from "vue";
import App from "@/App.vue";
import ModifyDetails from "@/views/ModifyDetails.vue";
import ModifyPassword from "@/views/ModifyPassword.vue";

const routes = [
  {
    path: '/home-user',
    name: 'home-user',
    component: HomeUser,
  },
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
  {
    path: '/modify-details',
    name: 'modify-details',
    component: ModifyDetails, // Define la ruta para la vista de registro
  },
  {
    path: '/modify-password',
    name: 'modify-password',
    component: ModifyPassword, // Define la ruta para la vista de registro
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

const app = createApp(App);
app.use(router);
app.mount('#app');
export default router;
