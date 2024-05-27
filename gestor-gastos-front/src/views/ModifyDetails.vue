<template>
  <div class="container-fluid vh-100 d-flex align-items-center bg-custom-blue-900">
    <div class="row justify-content-center w-100">
      <div class="text-center mb-5">
        <h1 class="logo-text text-white">econoMe</h1>
      </div>
      <div class="col-sm-12 col-md-6 col-lg-4">
        <form class="bg-white shadow p-4 rounded" novalidate ref="formRef">
          <h2 class="text-black mb-4 text-center">{{ isEditMode ? 'Editar datos usuario' : 'Datos usuario' }}</h2>
          <div class="mb-3">
            <label for="username" class="form-label">Nombre de usuario</label>
            <input type="text" class="form-control" id="username" v-model.trim="user.username" :class="{ 'is-invalid': usernameError || (submitted && !user.username) }" required autocomplete="off" :disabled="!isEditMode">
          </div>
          <div v-if="usernameError" class="invalid-feedback">
            {{ usernameError }}
          </div>
          <div v-if="submitted && !user.username" class="invalid-feedback">
            El usuario es obligatorio.
          </div>
          <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <input type="text" class="form-control" id="name" v-model.trim="user.name" :disabled="!isEditMode">
          </div>
          <div class="mb-3">
            <label for="surname" class="form-label">Apellidos</label>
            <input type="text" class="form-control" id="surname" v-model.trim="user.surname" :disabled="!isEditMode">
          </div>
          <div class="mb-3">
            <label for="mail" class="form-label">Correo Electrónico</label>
            <input type="email" class="form-control" id="mail" v-model.trim="user.mail" :disabled="!isEditMode">
          </div>
          <div class="text-end">
            <button type="button" class="btn btn-secondary me-2" @click="enableEdit">{{ isEditMode ? 'Cancelar' : 'Editar' }}</button>
            <button type="button" class="btn btn-primary text-white" @click="isEditMode ? submitForm() : home()">{{ isEditMode ? 'Guardar' : 'Cerrar' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, ref, onMounted } from 'vue';
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

    const isEditMode = ref(false);

    const enableEdit = () => {
      isEditMode.value = !isEditMode.value;
    };

    onMounted(async () => {
      await loadUserData(user);
    });

    const submitted = ref(false);
    const formRef = ref(null);
    const usernameError = ref('');

    const submitForm = async () => {
      submitted.value = true;
      if (formRef.value && !formRef.value.checkValidity()) {

        formRef.value.classList.add('was-validated');
        return;
      }
      await submitUserDataChanges(user, submitted, formRef, usernameError);
      isEditMode.value = false;
    };

    return {
      user,
      isEditMode,
      enableEdit,
      submitForm,
      home,
      formRef, usernameError, submitted
    };
  },
};
</script>
