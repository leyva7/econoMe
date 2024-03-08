<template>
  <div class="main-container">
    <div class="form-container">
      <form @submit.prevent="submitForm" class="register-form">
        <div class="form-group">
          <label for="name">Nombre</label>
          <input type="text" id="name" v-model.trim="user.name" required>
        </div>

        <div class="form-group">
          <label for="surname">Apellidos</label>
          <input type="text" id="surname" v-model.trim="user.surname" required>
        </div>

        <div class="form-group">
          <label for="email">Correo Electrónico</label>
          <input type="email" id="email" v-model.trim="user.email" required>
        </div>

        <div class="form-group">
          <label for="password">Contraseña</label>
          <input type="password" id="password" v-model.trim="user.password" required>
        </div>

        <div class="form-group">
          <button type="submit">Registrar</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';

export default {
  name: 'UserRegister',
  setup() {
    const user = ref({
      name: '',
      surname: '',
      email: '',
      password: ''
    });

    const submitForm = () => {
      if (!user.value.name || !user.value.surname || !user.value.email || !user.value.password) {
        alert('Por favor, completa todos los campos.');
        return;
      }
      console.log('Formulario enviado:', user.value);
      // Aquí iría la lógica para procesar los datos del formulario
    };

    return { user, submitForm };
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
input[type="email"],
input[type="password"] {
  padding: 10px 0;
  border: none;
  border-bottom: 2px solid #ccc;
  background-color: transparent;
  margin-bottom: 15px;
}

input[type="text"]:focus,
input[type="email"]:focus,
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
