<template>
  <div class="user-password-container">
    <!-- Contenedor principal con altura mínima de 100vh y estilos de fondo y alineación -->
    <div class="container-fluid py-5 bg-custom-blue-900 d-flex align-items-center" style="min-height: 100vh;">
      <!-- Contenedor de fila centrada verticalmente -->
      <div class="row justify-content-center w-100">
        <!-- División centrada que ocupa toda la anchura en dispositivos medianos y grandes -->
        <div class="text-center mb-5">
          <h1 class="logo-text text-white">econoMe</h1> <!-- Título principal del sitio -->
        </div>
        <!-- Columna para el formulario -->
        <div class="col-sm-12 col-md-6 col-lg-4">
          <!-- Formulario para cambiar contraseña -->
          <form @submit.prevent="submitForm" class="bg-white shadow p-4 rounded">
            <h2 class="text-black mb-4 text-center">Cambiar Contraseña</h2> <!-- Título del formulario -->
            <!-- Campo de contraseña actual -->
            <div class="mb-3">
              <label for="currentPassword" class="form-label">Contraseña actual</label>
              <input type="password" class="form-control" id="currentPassword" v-model.trim="userPassword.currentPassword" required>
            </div>
            <!-- Campo de contraseña nueva -->
            <div class="mb-3">
              <label for="newPassword" class="form-label">Contraseña nueva</label>
              <input type="password" class="form-control" id="newPassword" v-model.trim="userPassword.newPassword" required>
            </div>
            <!-- Botones alineados a la derecha -->
            <div class="text-end">
              <button type="submit" class="btn btn-primary me-2">Cambiar contraseña</button> <!-- Botón para enviar el formulario -->
              <button type="button" @click="home" class="btn btn-secondary">Volver atrás</button> <!-- Botón para volver a la página anterior -->
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive } from 'vue';
import { submitPasswordChange } from "@/service/userService"; // Importa la función para cambiar la contraseña desde el servicio
import { home } from "@/utils/global"; // Importa la función home desde utilidades globales

export default {
  name: 'UserPassword',
  setup() {
    const userPassword = reactive({ // Objeto reactivamente definido para almacenar las contraseñas
      currentPassword: '', // Contraseña actual
      newPassword: '', // Nueva contraseña
    });

    const submitForm = async () => { // Función para enviar el formulario
      await submitPasswordChange(userPassword); // Llama a la función para cambiar la contraseña con las contraseñas actuales y nuevas
    };

    return { // Devuelve los datos y funciones necesarias para el template
      userPassword,
      submitForm,
      home
    };
  },
};
</script>