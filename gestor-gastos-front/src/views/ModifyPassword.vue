<template>
  <div class="user-password-container">
    <div class="container-fluid py-5 bg-custom-blue-900 d-flex align-items-center" style="min-height: 100vh;">
      <div class="row justify-content-center w-100">
        <div class="col-sm-12 col-md-6 col-lg-4">
          <form @submit.prevent="submitForm" class="bg-white shadow p-4 rounded">
            <h2 class="text-black mb-4 text-center">Cambiar Contraseña</h2>
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
  </div>
</template>

<script>
import { reactive } from 'vue';
import { submitPasswordChange } from "@/service/userService";
import { home } from "@/utils/global";

export default {
  name: 'UserPassword',
  setup() {
    const userPassword = reactive({
      currentPassword: '',
      newPassword: '',
    });

    const submitForm = async () => {
      await submitPasswordChange(userPassword);
    };

    return {
      userPassword,
      submitForm,
      home
    };
  },
};
</script>
