<template>
  <div class="container-fluid vh-100 d-flex align-items-center bg-custom-blue-900">
    <!-- Contenedor de fila centrada verticalmente -->
    <div class="row justify-content-center w-100">
      <!-- División centrada que ocupa toda la anchura -->
      <div class="text-center mb-5">
        <h1 class="logo-text text-white">econoMe</h1> <!-- Título principal -->
      </div>
      <!-- Columna para el formulario -->
      <div class="col-sm-12 col-md-6 col-lg-4">
        <!-- Formulario -->
        <form class="bg-white shadow p-4 rounded" novalidate ref="formRef">
          <h2 class="text-black mb-4 text-center">{{ isEditMode ? 'Editar datos usuario' : 'Datos usuario' }}</h2>
          <!-- Campo de nombre de usuario -->
          <div class="mb-3">
            <label for="username" class="form-label">Nombre de usuario</label>
            <input type="text" class="form-control" id="username" v-model.trim="user.username" :class="{ 'is-invalid': usernameError || (submitted && !user.username) }" required autocomplete="off" :disabled="!isEditMode">
          </div>
          <!-- Mensaje de error para nombre de usuario -->
          <div v-if="usernameError" class="invalid-feedback">
            {{ usernameError }}
          </div>
          <!-- Validación de campo requerido para nombre de usuario -->
          <div v-if="submitted && !user.username" class="invalid-feedback">
            El usuario es obligatorio.
          </div>
          <!-- Campo de nombre -->
          <div class="mb-3">
            <label for="name" class="form-label">Nombre</label>
            <input type="text" class="form-control" id="name" v-model.trim="user.name" :disabled="!isEditMode">
          </div>
          <!-- Campo de apellidos -->
          <div class="mb-3">
            <label for="surname" class="form-label">Apellidos</label>
            <input type="text" class="form-control" id="surname" v-model.trim="user.surname" :disabled="!isEditMode">
          </div>
          <!-- Campo de correo electrónico -->
          <div class="mb-3">
            <label for="mail" class="form-label">Correo Electrónico</label>
            <input type="email" class="form-control" id="mail" v-model.trim="user.mail" :disabled="!isEditMode">
          </div>
          <!-- Botones de acción (Editar/Guardar o Cancelar/Cerrar) -->
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
import { loadUserData, submitUserDataChanges } from "@/service/userService"; // Importa funciones de servicio
import { home } from "@/utils/global"; // Importa función home desde utilidades globales

export default {
  name: 'UserForm',
  setup() {
    const user = reactive({ // Objeto reactivamente definido para almacenar los datos del usuario
      username: '',
      name: '',
      surname: '',
      mail: '',
    });

    const isEditMode = ref(false); // Referencia reactiva para controlar el modo de edición

    const enableEdit = () => { // Función para alternar el modo de edición
      isEditMode.value = !isEditMode.value;
    };

    onMounted(async () => { // Hook para cargar los datos del usuario al montar el componente
      await loadUserData(user);
    });

    const submitted = ref(false); // Estado para verificar si el formulario ha sido enviado
    const formRef = ref(null); // Referencia al formulario
    const usernameError = ref(''); // Estado para manejar errores de validación de nombre de usuario

    const submitForm = async () => { // Función para enviar el formulario
      submitted.value = true; // Marca el formulario como enviado
      if (formRef.value && !formRef.value.checkValidity()) { // Verifica la validez del formulario
        formRef.value.classList.add('was-validated'); // Agrega clase para mostrar errores de validación en el HTML
        return; // Sale de la función si el formulario no es válido
      }
      await submitUserDataChanges(user, submitted, formRef, usernameError); // Llama a la función de servicio para enviar cambios de datos de usuario
      isEditMode.value = false; // Desactiva el modo de edición después de enviar
    };

    return { // Devuelve todos los datos y funciones necesarios para el template
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