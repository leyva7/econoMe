<template>
  <div class="navbar bg-light shadow-sm mb-2 mt-2 custom-rounded">
    <div class="container-fluid">
      <span class="navbar-brand mb-0 h1">{{ accountingName }}</span>
      <div class="dropdown">
        <a class="nav-link dropdown-toggle" href="#" role="button" id="dropdownMenuLink" data-bs-toggle="dropdown" aria-expanded="false">
          <img src="../assets/icons/user.svg" alt="User Icon" class="rounded-circle" style="width: 40px; height: 40px;">
          {{ username }}
        </a>

        <ul class="dropdown-menu ml-4" aria-labelledby="dropdownMenuLink">
          <li><a class="dropdown-item" href="#" @click="navigate('/modify-details')">Modificar usuario</a></li>
          <li><a class="dropdown-item" href="#" @click="navigate('/modify-password')">Modificar contraseña</a></li>
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item" href="#" @click="logout">Cerrar sesión</a></li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router';
import {useAccountingStore} from '@/stores/accountingStore';

const router = useRouter();
const {username, accountingName, navigate} = useAccountingStore();

const logout = () => {
  localStorage.clear();
  router.push('/');
};
</script>

<style scoped>
.navbar {
  border-radius: 0.5rem;
}

.dropdown-menu {
  right: 0;
  left: auto;
}

/* Estilos adicionales si es necesario */
</style>
