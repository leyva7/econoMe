<template>
  <!-- Barra lateral con diseño flexible y estilos personalizados -->
  <nav class="sidebar d-flex flex-column bg-custom-blue-900 vh-100 overflow-auto p-4 text-center">
    <!-- Sección del logo, visible solo en pantallas anchas -->
    <div class="logo-section my-4 text-center">
      <img src="../assets/img/econome.png" alt="Logo" class="logo img-fluid" v-if="!isNarrowScreen">
    </div>
    <hr class="text-white">

    <!-- Navegación principal -->
    <nav class="nav flex-column align-items-center my-4 color-white-less">
      <!-- Enlaces a diferentes secciones del usuario -->
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

    <!-- Contabilidades compartidas -->
    <div class="shared-accountings mt-4 color-white-less">
      <span class="text-white">Contabilidades Compartidas</span>
      <!-- Botón para añadir una nueva contabilidad compartida -->
      <button @click="emitOpenModal('addAccounting')" class="btn btn-outline-light mt-2">Añadir Contabilidad</button>
      <!-- Lista de contabilidades compartidas, cada una es un enlace clickeable -->
      <ul class="list-unstyled mt-3">
        <li v-for="(accounting, index) in sharedAccountings" :key="index" @click.prevent="navigateToAccounting(accounting)" class="mb-2 cursor-pointer clickable-item">
          {{ accounting.name }}
        </li>
      </ul>
    </div>
  </nav>
</template>

<script>
import { defineComponent, ref, watch, watchEffect } from 'vue';
import { useRoute } from 'vue-router';
import { navigate, navigateHome, navigateToAccounting } from "@/utils/global";

export default defineComponent({
  name: 'AppSidebar',
  props: {
    sharedAccountings: Array,
  },
  emits: ['openModal'], // Define el evento emitido para abrir el modal

  setup(props, { emit }) {
    const route = useRoute(); // Obtiene la ruta actual desde Vue Router
    const activePath = ref(route.path); // Almacena la ruta activa actualmente

    const isNarrowScreen = ref(false); // Ref para verificar si la pantalla es estrecha

    // Observa cambios en la ruta para actualizar activePath
    watch(route, (newRoute) => {
      activePath.value = newRoute.path;
    });

    // Función para emitir evento y abrir el modal específico
    const emitOpenModal = (type) => {
      emit('openModal', type);
    };

    // Maneja cambios en el tamaño de la pantalla para ajustar isNarrowScreen
    window.addEventListener('resize', () => {
      isNarrowScreen.value = window.innerWidth < 1350; // Establece el umbral de pantalla estrecha
    });

    // Ejemplo de efecto de reacción al tamaño de la pantalla
    watchEffect(() => {
      isNarrowScreen.value = window.innerWidth < 1300; // Ajusta el valor de isNarrowScreen
    });

    // Devuelve variables y funciones para usar en el template
    return {
      activePath,
      navigate, // Función para navegar a una ruta específica
      navigateHome, // Función para navegar a la página de inicio del usuario
      navigateToAccounting, // Función para navegar a una contabilidad específica
      emitOpenModal, // Función para emitir el evento de abrir modal
      isNarrowScreen // Estado de pantalla estrecha
    };
  },
});
</script>

<style>
/* Estilos CSS específicos para este componente */

/* Estilos para el logo y los enlaces de navegación */
.logo-section, .nav a, .shared-accountings details summary, .shared-accountings details {
  display: flex;
  justify-content: center;
  align-items: center;
  text-decoration: none;
}

/* Estilos para el logo */
.logo {
  max-width: 150px;
  margin-right: 10px;
}

/* Estilos para los íconos de navegación */
.nav-icon, .tooltip a {
  margin-right: 8px;
  text-decoration: none;
}

/* Estilos para los enlaces de navegación */
.nav a, .shared-accountings details summary {
  color: rgba(255, 255, 255, 0.78); /* Color de texto para los enlaces */
  padding: 5px 0; /* Espaciado interior */
  text-decoration: none; /* Sin subrayado de texto */
  font-size: 20px; /* Tamaño de fuente */
}

/* Estilos para la sección de contabilidades compartidas */
.shared-accountings details {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

</style>
