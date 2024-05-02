<template>
  <div class="container-fluid vh-100 d-flex align-items-center bg-custom-blue-900">
    <div class="row justify-content-center w-100">
      <div class="col-sm-12 col-md-6 col-lg-4">
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
import { loadUserData, submitUserDataChanges } from "@/service/userService";
import { home } from "@/utils/global";

export default {
  name: 'UserForm',
  setup() {
    const user = reactive({
      username: '',
      name: '',
      surname: '',
      mail: '',
    });

    onMounted(() => {
      loadUserData(user);
    });

    const submitForm = async () => {
      await submitUserDataChanges(user);
    };

    return {
      user,
      submitForm,
      home
    };
  },
};
</script>
