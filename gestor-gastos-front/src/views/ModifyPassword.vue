<template>
  <div class="main-container">
    <div class="form-container">
      <form @submit.prevent="submitForm" class="register-form">
        <div class="form-group">
          <label for="currentPassword">Contraseña actual</label>
          <input type="password" id="currentPassword" v-model.trim="userPassword.currentPassword">
        </div>

        <div class="form-group">
          <label for="newPassword">Contraseña nueva</label>
          <input type="password" id="newPassword" v-model.trim="userPassword.newPassword">
        </div>

        <div class="form-group">
          <button type="submit">Cambiar contraseña</button>
        </div>
        <div class="form-group">
          <button type="button" @click="home">Volver atrás</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { reactive } from 'vue';
import axios from 'axios';
import router from "@/router";

export default {
  name: 'UserPassword',
  setup() {
    const userPassword = reactive({
      currentPassword: '',
      newPassword: '',
    });

    const home = () => {
      router.push({ name: 'home-user' });
    };

    const submitForm = async () => {
      try {
        // Asegúrate de cambiar la URL al endpoint correcto para cambiar la contraseña
        await axios.put('http://localhost:8081/api/users/modifyPassword', userPassword, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        alert('Contraseña modificada exitosamente.');
        router.push({ name: 'login' });
      } catch (error) {
        console.error('Error al modificar la contraseña:', error);
        alert('Ocurrió un error al modificar la contraseña. Por favor, inténtalo de nuevo.');
      }
    };

    return {
      userPassword,
      submitForm,
      home,
    };
  },
};
</script>



<style scoped>
.main-container{
  background-color: var(--blue-chill-300);
}

.form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
}

.register-form {
  padding: 20px;
  border-radius: 15px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-width: 400px;
  width: 100%;
  background-color: white; /* Fondo sólido para el formulario para mejorar la legibilidad */
}

.form-group {
  display: flex;
  flex-direction: column;
}

input[type="text"],
input[type="mail"],
input[type="password"] {
  padding: 10px 0;
  border: none;
  border-bottom: 2px solid #ccc;
  background-color: transparent;
  margin-bottom: 15px;
}

input[type="text"]:focus,
input[type="mail"]:focus,
input[type="password"]:focus {
  outline: none;
  border-bottom-color: #007bff;
}

button {
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  align-self: center; /* Asegura que el botón esté centrado en el formulario */
}

button:hover {
  background-color: #0056b3;
}
</style>
