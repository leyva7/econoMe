<template>
  <div class="container-fluid py-5 bg-custom-blue-900 d-flex align-items-center" style="min-height: 100vh;">
    <!-- Contenedor principal con altura mínima de 100vh y estilos de fondo y alineación -->
    <div class="row justify-content-center w-100">
      <!-- Fila centrada horizontalmente -->
      <div class="text-center mb-5">
        <h1 class="logo-text text-white">econoMe</h1>
        <!-- Título principal del sitio -->
      </div>
      <div class="col-sm-12 col-md-6 col-lg-4">
        <!-- Columna que ocupa todo el ancho en dispositivos medianos y grandes -->
        <form @submit.prevent="submitForm($event)" class="bg-white shadow p-4 rounded needs-validation" novalidate ref="formRef">
          <!-- Formulario de registro de usuario -->
          <h2 class="text-black mb-4 text-center">Registro de usuario</h2>
          <!-- Título del formulario -->
          <div class="mb-3">
            <label for="username" class="form-label">Nombre de usuario</label>
            <!-- Campo de nombre de usuario -->
            <input type="text" class="form-control" id="username" v-model.trim="user.username" :class="{ 'is-invalid': usernameError || (submitted && !user.username) }" required autocomplete="off">
            <!-- Validación de nombre de usuario -->
            <div v-if="usernameError" class="invalid-feedback">
              {{ usernameError }}
            </div>
            <div v-if="submitted && !user.username" class="invalid-feedback">
              El usuario es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <!-- Campo de nombre -->
            <input type="text" class="form-control" id="name" v-model.trim="user.name" :class="{ 'is-invalid': (submitted && !user.name) }" required autocomplete="off">
            <!-- Validación de nombre -->
            <div v-if="submitted && !user.name" class="invalid-feedback">
              El nombre es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="surname" class="form-label">Apellidos</label>
            <!-- Campo de apellidos -->
            <input type="text" class="form-control" id="surname" v-model.trim="user.surname" :class="{ 'is-invalid': (submitted && !user.surname) }" required autocomplete="off">
            <!-- Validación de apellidos -->
            <div v-if="submitted && !user.surname" class="invalid-feedback">
              El apellido es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="mail" class="form-label">Correo Electrónico</label>
            <!-- Campo de correo electrónico -->
            <input type="email" class="form-control" id="mail" v-model.trim="user.mail" :class="{ 'is-invalid': (submitted && !user.mail) }" required autocomplete="off">
            <!-- Validación de correo electrónico -->
            <div v-if="submitted && !user.mail" class="invalid-feedback">
              El email es obligatorio.
            </div>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">Contraseña</label>
            <!-- Campo de contraseña -->
            <input type="password" class="form-control" id="password" v-model.trim="user.password" :class="{ 'is-invalid': (submitted && !user.password) }" required autocomplete="off">
            <!-- Validación de contraseña -->
            <div v-if="submitted && !user.password" class="invalid-feedback">
              La contraseña es obligatoria.
            </div>
          </div>
          <div class="d-flex justify-content-between">
            <!-- Botones alineados a la derecha -->
            <button type="button" class="btn btn-secondary" @click="navigate('/')">Volver atrás</button>
            <!-- Botón para navegar hacia atrás -->
            <button type="submit" class="btn btn-primary">Registrar</button>
            <!-- Botón para enviar el formulario de registro -->
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { submitRegisterUser } from "@/service/userService"; // Importa la función para registrar usuario desde el servicio
import { navigate } from "@/utils/global"; // Importa la función navigate desde utilidades globales

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

    const submitted = ref(false); // Estado de si el formulario ha sido enviado
    const formRef = ref(null); // Referencia al formulario
    const usernameError = ref(''); // Error específico de nombre de usuario

    const submitForm = async () => {
      submitted.value = true; // Marca el formulario como enviado
      if (formRef.value && !formRef.value.checkValidity()) {
        formRef.value.classList.add('was-validated'); // Agrega clases de validación de Bootstrap si el formulario no es válido
        return;
      }

      await submitRegisterUser(user.value, submitted, formRef, usernameError); // Llama a la función para registrar usuario con los datos del formulario
    };

    return { // Devuelve los datos y funciones necesarias para el template
      user,
      submitForm,
      navigate,
      formRef,
      submitted,
      usernameError
    };
  }
};
</script>