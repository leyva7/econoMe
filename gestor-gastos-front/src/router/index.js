// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import FirstView from '@/views/First-view.vue';
import UserRegister from '@/views/UserRegister.vue';
import HomeUser from '@/views/HomeUser.vue';
import {createApp} from "vue";
import App from "@/App.vue";
import ModifyDetails from "@/views/ModifyDetails.vue";
import ModifyPassword from "@/views/ModifyPassword.vue";
import HomeDetails from "@/components/HomeDetails.vue";
import SpentDetails from "@/components/SpentDetails.vue";
import IncomeDetails from "@/components/IncomeDetails.vue";
import EvolutionDetails from "@/components/EvolutionDetails.vue";
import SharedAccountings from "@/components/SharedAccountings.vue";
import OperationDetails from "@/components/OperationDetails.vue";

const routes = [
  {
    path: '/',
    name: 'login',
    component: FirstView,
  },
  {
    path: '/register',
    name: 'register',
    component: UserRegister,
  },
  {
    path: '/home-user',
    component: HomeUser,
    children: [
      {
        path: '', // Ruta vacía para el componente predeterminado
        name: 'home',
        component: HomeDetails,
      },
      {
        path: 'spent',
        name: 'spent',
        component: SpentDetails,
      },
      {
        path: 'income',
        name: 'income',
        component: IncomeDetails,
      },
      {
        path: 'evolution',
        name: 'evolution',
        component: EvolutionDetails,
      },
      {
        path: 'operation',
        name: 'operation',
        component: OperationDetails,
      },
      {
        path: 'shared/:accountingName',
        name: 'shared',
        component: SharedAccountings,
      },
    ],
  },
  {
    path: '/modify-details',
    name: 'modify-details',
    component: ModifyDetails,
  },
  {
    path: '/modify-password',
    name: 'modify-password',
    component: ModifyPassword,
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
