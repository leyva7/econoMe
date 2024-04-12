<template>
  <div class="container-fluid py-5 bg-custom-blue-900 d-flex align-items-center" style="min-height: 100vh;">
    <div class="row justify-content-center w-100">
      <div class="col-sm-12 col-md-6 col-lg-4">
        <form @submit.prevent="submitForm" class="bg-white shadow p-4 rounded">
          <h2 class="text-white mb-4 text-center">Cambiar Contraseña</h2>

          <div class="mb-3">
            <label for="currentPassword" class="form-label">Contraseña actual</label>
            <input type="password" class="form-control" id="currentPassword" v-model.trim="userPassword.currentPassword" required>
          </div>

          <div class="mb-3">
            <label for="newPassword" class="form-label">Contraseña nueva</label>
            <input type="password" class="form-control" id="newPassword" v-model.trim="userPassword.newPassword" required>
          </div>

          <div class="text-end">
            <button type="submit" class="btn btn-primary me-2">Cambiar contraseña</button>
            <button type="button" @click="home" class="btn btn-secondary">Volver atrás</button>
          </div>
        </form>
      </div>
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
      const personalAccountingId = localStorage.getItem('personalAccountingId');
      router.push({
        name: 'home',
        query: { id: personalAccountingId }
      });
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

</style>
