<template>
  <div class="container-fluid py-5 bg-custom-blue-900 d-flex align-items-center" style="min-height: 100vh;">
    <div class="row justify-content-center w-100">
      <div class="col-sm-12 col-md-6 col-lg-4"> <!-- Ajusta esta línea para cambiar el ancho -->
        <form @submit.prevent="submitForm" class="bg-white shadow p-4 rounded">
          <h2 class="text-black mb-4 text-center">Modificar Datos</h2>
          <div class="mb-3">
            <label for="username" class="form-label">Nombre de usuario</label>
            <input type="text" class="form-control" id="username" v-model.trim="user.username" required placeholder="Tu username">
          </div>
          <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <input type="text" class="form-control" id="name" v-model.trim="user.name" required placeholder="Tu nombre">
          </div>
          <div class="mb-3">
            <label for="surname" class="form-label">Apellidos</label>
            <input type="text" class="form-control" id="surname" v-model.trim="user.surname" required placeholder="Tus apellidos">
          </div>
          <div class="mb-3">
            <label for="mail" class="form-label">Correo Electrónico</label>
            <input type="email" class="form-control" id="mail" v-model.trim="user.mail" required placeholder="Tu correo electrónico">
          </div>
          <div class="text-end">
            <button type="submit" class="btn btn-primary me-2">Cambiar datos</button>
            <button type="button" @click="home" class="btn btn-secondary">Volver atrás</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import {home} from "@/utils/global";

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

</style>
