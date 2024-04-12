<template>
  <div class="container-fluid py-5 bg-custom-blue-900 d-flex align-items-center" style="min-height: 100vh;">
    <div class="row justify-content-center w-100">
      <div class="col-sm-12 col-md-6 col-lg-4">
        <form @submit.prevent="submitForm($event)" class="bg-white shadow p-4 rounded needs-validation" novalidate ref="formRef">
          <h2 class="text-black mb-4 text-center">Registro de usuario</h2>
          <div class="mb-3">
            <label for="username" class="form-label">Nombre de usuario</label>
            <input type="text" class="form-control" id="username" v-model.trim="user.username" :class="{ 'is-invalid': usernameError || (submitted && !user.username) }" required autocomplete="off">
            <div v-if="usernameError" class="invalid-feedback">
              {{ usernameError }}
            </div>
            <div v-if="submitted && !user.username" class="invalid-feedback">
              El usuario es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <input type="text" class="form-control" id="name" v-model.trim="user.name" :class="{ 'is-invalid': (submitted && !user.name) }" required autocomplete="off">
            <div v-if="submitted && !user.name" class="invalid-feedback">
              El nombre es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="surname" class="form-label">Apellidos</label>
            <input type="text" class="form-control" id="surname" v-model.trim="user.surname" :class="{ 'is-invalid': (submitted && !user.surname) }" required autocomplete="off">
            <div v-if="submitted && !user.surname" class="invalid-feedback">
              El apellido es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="mail" class="form-label">Correo Electrónico</label>
            <input type="email" class="form-control" id="mail" v-model.trim="user.mail" :class="{ 'is-invalid': (submitted && !user.mail) }" required autocomplete="off">
            <div v-if="submitted && !user.mail" class="invalid-feedback">
              El email es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Contraseña</label>
            <input type="password" class="form-control" id="password" v-model.trim="user.password" :class="{ 'is-invalid': (submitted && !user.password) }" required autocomplete="off">
            <div v-if="submitted && !user.password" class="invalid-feedback">
              La contraseña es obligatoria.
            </div>
          </div>
          <div class="d-flex justify-content-between">
            <button type="button" class="btn btn-secondary" @click="home">Volver atrás</button>
            <button type="submit" class="btn btn-primary">Registrar</button>
          </div>
        </form>
      </div>
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

    const submitted = ref(false);
    const formRef = ref(null);
    const usernameError = ref('');

    const home = () => {
      router.push({ name: 'login' });
    };

    const submitForm = async () => {
      submitted.value = true;
      if (!formRef.value) return;

      formRef.value.classList.add('was-validated');

      if(formRef.value.checkValidity()){
        try {
          await registerUser({ user: user.value, accounting: {
              name: 'Contabilidad personal',
              description: `Contabilidad de uso personal del usuario ${user.value.username}`,
              type: 'PERSONAL'
            }});

          alert('Usuario y contabilidad registrados exitosamente.');
          router.push('/');
        } catch (error) {
          if (error.response && error.response.data.error.includes('nombre de usuario')) {
            usernameError.value = 'Este nombre de usuario ya está registrado.';
            const usernameInput = formRef.value.querySelector('#username');
            usernameInput.classList.remove('is-valid');
            usernameInput.classList.add('is-invalid');
          }
        }
      }
    };

  return { user, submitForm, home, formRef, submitted, usernameError };

}
};
</script>


<style scoped>
</style>
