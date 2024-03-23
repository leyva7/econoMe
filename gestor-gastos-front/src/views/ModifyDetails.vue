<template>
  <div class="main-container">
    <div class="form-container">
      <form @submit.prevent="submitForm" class="register-form">
        <div class="form-group">
          <label for="username">Nombre de usuario</label>
          <input type="text" id="username" v-model.trim="user.username" required placeholder="Tu username">
        </div>

        <div class="form-group">
          <label for="name">Nombre</label>
          <input type="text" id="name" v-model.trim="user.name" required placeholder="Tu nombre">
        </div>

        <div class="form-group">
          <label for="surname">Apellidos</label>
          <input type="text" id="surname" v-model.trim="user.surname" required placeholder="Tus apellidos">
        </div>

        <div class="form-group">
          <label for="mail">Correo Electrónico</label>
          <input type="email" id="mail" v-model.trim="user.mail" required placeholder="Tu correo electrónico">
        </div>

        <div class="form-group">
          <button type="submit">Cambiar datos</button>
        </div>
        <div class="form-group">
          <button type="button" @click="home">Volver atrás</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'UserForm',
  setup() {
    const router = useRouter();
    const user = reactive({
      username: '',
      name: '',
      surname: '',
      mail: '',
    });

    const home = () => {
      const personalAccountingId = localStorage.getItem('personalAccountingId');
      router.push({
        name: 'home',
        query: { id: personalAccountingId }
      });
    };

    const loadUserData = () => {
      axios.get('http://localhost:8081/api/users/details', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('userToken')}`,
        },
      }).then((response) => {
        user.username = response.data.username;
        user.name = response.data.name;
        user.surname = response.data.surname;
        user.mail = response.data.mail;
      }).catch(error => {
        if (error.response && error.response.status === 401) {
          // Si el código de estado de la respuesta es 401, trata el error como una sesión expirada
          console.error("Sesión expirada. Por favor, inicia sesión de nuevo.", error);
          alert("Sesión expirada. Por favor, inicia sesión de nuevo.");
          router.push({ name: 'login' });
        } else {
          console.error("Error al cargar los datos del usuario", error);
        }
      });
    };

    onMounted(loadUserData);

    const submitForm = async () => {
      try {
        await axios.put('http://localhost:8081/api/users/modifyDetails', user, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        alert('Usuario modificado exitosamente.');
        localStorage.setItem('username', user.username);
        router.push({ name: 'login' }).then(() => {
          window.location.reload();
        });
      } catch (error) {
        console.error('Error al modificar datos del usuario:', error);
        alert('Ocurrió un error al modificar los datos del usuario. Por favor, inténtalo de nuevo.');
      }
    };

    return {
      user,
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
