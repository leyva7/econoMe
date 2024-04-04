<template>
  <aside class="sidebar">
    <div class="logo-section">
      <img src="../assets/img/econome.png" alt="Logo" class="logo">
    </div>
    <div class="divider"></div>
    <nav class="nav">
      <a href="#" @click.prevent="navigateHome('/home-user')">
        <img src="../assets/icons/home.svg" alt="Home" class="nav-icon"> Home
      </a>
      <a href="#" @click.prevent="navigateHome('/home-user/spent')">
        <img src="../assets/icons/spent.svg" alt="Spent" class="nav-icon"> Gastos
      </a>
      <a href="#" @click.prevent="navigateHome('/home-user/income')">
        <img src="../assets/icons/income.svg" alt="Income" class="nav-icon"> Ingresos
      </a>
      <a href="#" @click.prevent="navigateHome('/home-user/evolution')">
        <img src="../assets/icons/evolution.svg" alt="Spent" class="nav-icon"> Evolución
      </a>
      <a href="#" @click.prevent="navigate('/home-user/operation')">
        <img src="../assets/icons/operations.svg" alt="Operation" class="nav-icon"> Operaciones
      </a>
    </nav>
    <div class="divider"></div>
    <div class="shared-accountings">
      <span>Contabilidades Compartidas</span>
      <button @click="emitOpenModal('addAccounting')">Añadir Contabilidad</button>
      <ul>
        <li v-for="(accounting, index) in sharedAccountings" :key="index" @click.prevent="navigateToAccounting(accounting)" class="shared-accountings-list">
          {{ accounting.name }}
        </li>
      </ul>
    </div>
  </aside>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import { useRouter } from 'vue-router';
import { useRoute } from 'vue-router';

export default defineComponent({
  name: 'AppSidebar',
  props: {
    sharedAccountings: Array,
  },
  emits: ['openModal'],
  setup(props, { emit }) {
    const router = useRouter();
    const route = useRoute(); // Obtiene la ruta actual
    const activePath = ref(route.path); // Almacena la ruta activa

    watch(route, (newRoute) => {
      activePath.value = newRoute.path; // Actualiza la ruta activa cuando cambie la ruta
    });

    const navigate = (path) => {
      router.push({
        path: path
      });
    };

    const navigateHome = (path) => {
      router.push({
        path: path,
        query: { id: localStorage.getItem('personalAccountingId') },
      });
    };

    const navigateToAccounting = (accounting) => {
      router.push({
        name: 'shared',
        params: { accountingName: accounting.name },
        query: { id: accounting.id },
      });
    };

    const emitOpenModal = (type) => {
      emit('openModal', type);
    };

    // Dentro de SidebarPage
    watch(() => props.sharedAccountings, (newVal) => {
      console.log("sharedAccountings ha cambiado:", newVal);
    }, { deep: true });

    return {
      activePath,
      navigate, navigateHome,
      navigateToAccounting,
      emitOpenModal,
    };
  },
});
</script>

<style>
.sidebar, .content {
  overflow-y: auto; /* Permite desplazamiento interno si es necesario */
}

.sidebar {
  width: 15%;
  background-color: #2C3E50;
  color: #FFFFFF;
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100vh;
  text-align: center;
  box-sizing: border-box;
  overflow-y: auto;
}

.divider{
  border-bottom: 1px solid #FFFFFF;
}

.divider, .logo-section, .nav, .shared-accountings {
  margin-bottom: 30px;
}

.logo-section, .nav a, .shared-accountings details summary, .shared-accountings details {
  display: flex;
  justify-content: center;
  align-items: center;
  text-decoration: none;
}

.logo {
  max-width: 150px;
  margin-right: 10px;
}

.nav-icon, .tooltip a {
  margin-right: 8px;
  text-decoration: none;
}

.nav a{
  transition: color 0.3s ease;
}

.nav a, .shared-accountings details summary {
  color: rgba(255, 255, 255, 0.78);
  padding: 5px 0;
  text-decoration: none;
  font-size: 20px;
}

.nav a:hover {
  color: #f0f0f0;
  filter: brightness(150%);
}

.shared-accountings details {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.shared-accountings ul {
  list-style-type: none; /* Quita los puntos */
  padding: 0;
  margin: 20px 0; /* Agrega un poco de margen arriba y abajo de la lista */
}

.shared-accountings a {
  color: #FFFFFF; /* Establece el color del texto de los enlaces a blanco */
  text-decoration: none; /* Remueve el subrayado de los enlaces */
}

.shared-accountings li {
  margin-bottom: 10px; /* Aumenta la separación */
  border: 2px solid #FFFFFF; /* Borde blanco alrededor de la lista */
}
.shared-accountings a:visited {
  color: #FFFFFF; /* Mantiene el color blanco incluso después de ser visitados */
}

</style>