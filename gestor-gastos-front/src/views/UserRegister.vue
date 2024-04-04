<template>
  <div class="main-container">
    <div class="form-container">
      <form @submit.prevent="submitForm" class="register-form">
        <div class="form-group">
          <label for="username">Nombre de usuario</label>
          <input type="text" id="username" v-model.trim="user.username" required autocomplete="off">
        </div>

        <div class="form-group">
          <label for="name">Nombre</label>
          <input type="text" id="name" v-model.trim="user.name" required autocomplete="off">
        </div>

        <div class="form-group">
          <label for="surname">Apellidos</label>
          <input type="text" id="surname" v-model.trim="user.surname" required autocomplete="off">
        </div>

        <div class="form-group">
          <label for="mail">Correo Electrónico</label>
          <input type="email" id="mail" v-model.trim="user.mail" required autocomplete="off">
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <input type="password" id="password" v-model.trim="user.password" required autocomplete="off">
        </div>

        <div class="form-group">
          <button type="submit">Registrar</button>
        </div>
        <div class="form-group">
          <button type="button" @click="home">Volver atrás</button>
        </div>

      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { registerUser } from '@/service/userService';
import router from "@/router";

export default {
  name: 'UserRegister',
  setup() {
    const user = ref({
      username: '',
      name: '',
      surname: '',
      mail: '',
      password: ''
    });

    const home = () => {
      router.push({ name: 'login' });
    };

    const submitForm = async () => {
      if (!user.value.username || !user.value.name || !user.value.surname || !user.value.mail || !user.value.password) {
        alert('Por favor, completa todos los campos.');
        return;
      }

      try {
        await registerUser({ user: user.value, accounting: {
            name: 'Contabilidad personal',
            description: `Contabilidad de uso personal del usuario ${user.value.username}`,
            type: 'PERSONAL'
          }});

        alert('Usuario y contabilidad registrados exitosamente.');
        router.push('/');
      } catch (error) {
        console.error('Error al registrar usuario y contabilidad:', error.response.data.error);
        alert('Error al registrar usuario y contabilidad: ' + error.response.data.error);
      }
    };

  return { user, submitForm, home };

}
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
