<template>
  <div class="container-fluid vh-100"> <!-- Contenedor fluido que ocupa toda la altura de la ventana -->
    <div class="row h-100"> <!-- Fila que ocupa toda la altura de su contenedor -->
      <!-- Columna izquierda -->
      <div class="col-md-6 d-flex align-items-center justify-content-center bg-custom-blue-500 text-black">
        <AppDescription /> <!-- Componente AppDescription centrado verticalmente -->
      </div>
      <!-- Columna derecha -->
      <div class="col-md-6 d-flex align-items-center justify-content-center bg-custom-blue-300">
        <UserLogin /> <!-- Componente UserLogin centrado verticalmente -->
      </div>
    </div>
  </div>
</template>

<script>
import AppDescription from '../components/AppDescription.vue'; // Importa el componente AppDescription
import UserLogin from '../components/UserLogin.vue'; // Importa el componente UserLogin

export default {
  name: 'App', // Nombre del componente
  components: {
    AppDescription, // Registra el componente AppDescription
    UserLogin, // Registra el componente UserLogin
  },
};
</script>
