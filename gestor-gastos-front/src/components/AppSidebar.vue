<template>
  <nav class="sidebar d-flex flex-column bg-custom-blue-900 vh-100 overflow-auto p-4 text-center">
    <div class="logo-section my-4 text-center">
      <img src="../assets/img/econome.png" alt="Logo" class="logo img-fluid" v-if="!isNarrowScreen">
    </div>
    <hr class="text-white">
    <nav class="nav flex-column align-items-center my-4 color-white-less">
      <a href="#" @click.prevent="navigateHome('/home-user')" class="color-white-less">
        <img src="../assets/icons/home.svg" alt="Home" class="nav-icon "> Home
      </a>
      <a href="#" @click.prevent="navigateHome('/home-user/spent')" class="color-white-less">
        <img src="../assets/icons/spent.svg" alt="Spent" class="nav-icon"> Gastos
      </a>
      <a href="#" @click.prevent="navigateHome('/home-user/income')" class="color-white-less">
        <img src="../assets/icons/income.svg" alt="Income" class="nav-icon"> Ingresos
      </a>
      <a href="#" @click.prevent="navigateHome('/home-user/evolution')" class="color-white-less">
        <img src="../assets/icons/evolution.svg" alt="Spent" class="nav-icon"> Evolución
      </a>
      <a href="#" @click.prevent="navigate('/home-user/operation')" class="color-white-less">
        <img src="../assets/icons/operations.svg" alt="Operation" class="nav-icon"> Operaciones
      </a>
    </nav>
    <hr class="text-white">
    <div class="shared-accountings mt-4 color-white-less">
      <span class="text-white">Contabilidades Compartidas</span>
      <button @click="emitOpenModal('addAccounting')" class="btn btn-outline-light mt-2">Añadir Contabilidad</button>
      <ul class="list-unstyled mt-3">
        <li v-for="(accounting, index) in sharedAccountings" :key="index" @click.prevent="navigateToAccounting(accounting)" class="mb-2">
          {{ accounting.name }}
        </li>
      </ul>
    </div>
  </nav>
</template>

<script>
import {defineComponent, ref, watch, watchEffect} from 'vue';
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

    const isNarrowScreen = ref(false);

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

    window.addEventListener('resize', () => {
      isNarrowScreen.value = window.innerWidth < 1350;
    });

    watchEffect(() => {
      // Actualiza isNarrowScreen basado en el ancho de la ventana
      isNarrowScreen.value = window.innerWidth < 1300; // Ejemplo de umbral
    });

    // Dentro de SidebarPage
    watch(() => props.sharedAccountings, (newVal) => {
      console.log("sharedAccountings ha cambiado:", newVal);
    }, { deep: true });

    return {
      activePath,
      navigate, navigateHome,
      navigateToAccounting,
      emitOpenModal,
      isNarrowScreen
    };
  },
});
</script>

<style>

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

.nav a, .shared-accountings details summary {
  color: rgba(255, 255, 255, 0.78);
  padding: 5px 0;
  text-decoration: none;
  font-size: 20px;
}

.shared-accountings details {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

</style>