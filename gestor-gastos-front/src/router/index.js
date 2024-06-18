// Importación de las funciones createRouter y createWebHistory desde vue-router
import { createRouter, createWebHistory } from 'vue-router';

// Importación de los componentes de las vistas y componentes necesarios
import FirstView from '@/views/First-view.vue';
import UserRegister from '@/views/UserRegister.vue';
import HomeUser from '@/views/HomeUser.vue';
import ModifyDetails from "@/views/ModifyDetails.vue";
import ModifyPassword from "@/views/ModifyPassword.vue";
import HomeDetails from "@/components/HomeDetails.vue";
import SpentDetails from "@/components/SpentDetails.vue";
import IncomeDetails from "@/components/IncomeDetails.vue";
import EvolutionDetails from "@/components/EvolutionDetails.vue";
import SharedAccountings from "@/components/SharedAccountings.vue";
import OperationDetails from "@/components/OperationDetails.vue";

// Definición de las rutas de la aplicación
const routes = [
  {
    path: '/', // Ruta raíz, que lleva a la vista de inicio de sesión (FirstView)
    name: 'login',
    component: FirstView,
  },
  {
    path: '/register', // Ruta para el registro de usuario
    name: 'register',
    component: UserRegister,
  },
  {
    path: '/home-user', // Ruta base para la sección de usuario logueado (HomeUser)
    component: HomeUser,
    children: [ // Rutas hijas de /home-user para diferentes secciones
      {
        path: '', // Ruta vacía, muestra los detalles del hogar por defecto (HomeDetails)
        name: 'home',
        component: HomeDetails,
      },
      {
        path: 'spent', // Ruta para detalles de gastos (SpentDetails)
        name: 'spent',
        component: SpentDetails,
      },
      {
        path: 'income', // Ruta para detalles de ingresos (IncomeDetails)
        name: 'income',
        component: IncomeDetails,
      },
      {
        path: 'evolution', // Ruta para detalles de evolución (EvolutionDetails)
        name: 'evolution',
        component: EvolutionDetails,
      },
      {
        path: 'operation', // Ruta para detalles de operaciones (OperationDetails)
        name: 'operation',
        component: OperationDetails,
      },
      {
        path: 'shared/:accountingName', // Ruta dinámica para mostrar contabilidades compartidas (SharedAccountings)
        name: 'shared',
        component: SharedAccountings,
      },
    ],
  },
  {
    path: '/modify-details', // Ruta para modificar detalles de usuario (ModifyDetails)
    name: 'modify-details',
    component: ModifyDetails,
  },
  {
    path: '/modify-password', // Ruta para modificar contraseña (ModifyPassword)
    name: 'modify-password',
    component: ModifyPassword,
  },
];

// Creación del enrutador utilizando createRouter y createWebHistory
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL), // Historial de navegación basado en el historial web
  routes, // Rutas definidas anteriormente
});

// Exportación del enrutador para ser utilizado en la aplicación
export default router;
