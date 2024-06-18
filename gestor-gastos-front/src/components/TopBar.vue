<template>
  <div class="navbar bg-light shadow-sm mb-2 mt-2 custom-rounded">
    <div class="container-fluid">
      <!-- Nombre de la contabilidad como título de la barra de navegación -->
      <span class="navbar-brand mb-0 h1">{{ accountingName }}</span>

      <!-- Dropdown para opciones de usuario -->
      <div class="dropdown">
        <a class="nav-link dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
          <!-- Icono de usuario y nombre de usuario -->
          <img src="../assets/icons/user.svg" alt="User Icon" class="rounded-circle" style="width: 40px; height: 40px;">
          {{ username }}
        </a>

        <!-- Menú desplegable -->
        <ul class="dropdown-menu ml-4" aria-labelledby="dropdownMenuLink">
          <!-- Opciones del menú -->
          <li><a class="dropdown-item" href="#" @click="navigate('/modify-details')">Datos usuario</a></li>
          <li><a class="dropdown-item" href="#" @click="navigate('/modify-password')">Modificar contraseña</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item" href="#" @click="logout">Cerrar sesión</a></li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
// Importa el store y las funciones de utilidad
import { useAccountingStore } from '@/stores/accountingStore';
import { logout, navigate } from "@/utils/global";
import { onMounted } from "vue";

// Desestructura las variables y funciones necesarias del store
const { username, accountingName, loadAccountings } = useAccountingStore();

// Ejecuta la carga de las contabilidades al montar el componente
onMounted(async () => {
  await loadAccountings();
});

</script>

<style scoped>
.navbar {
  border-radius: 0.5rem;
}

.dropdown-menu {
  right: 0;
  left: auto;
}
</style>
