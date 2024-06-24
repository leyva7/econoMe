<template>
  <div class="w-50">
    <!-- Formulario de inicio de sesión -->
    <form @submit.prevent="loginAction" class="bg-white p-4 rounded shadow">
      <h1 class="h3 mb-3 font-weight-normal text-center">Iniciar sesión</h1>

      <!-- Campo de nombre de usuario -->
      <div class="form-group mb-3">
        <input type="text" id="username" name="username" class="form-control no-highlight" placeholder="Nombre de usuario" v-model="username" required autofocus autocomplete="off">
      </div>

      <!-- Campo de contraseña -->
      <div class="form-group mb-3">
        <input type="password" id="password" name="password" class="form-control no-highlight" placeholder="Contraseña" v-model="password" required>
      </div>

      <!-- Botón de inicio de sesión -->
      <button class="btn btn-lg btn-primary btn-block" type="submit">Iniciar sesión</button>
    </form>
  </div>
</template>

<script>
import { ref } from 'vue';
import router from "@/router"; // Importa el enrutador para la navegación
import { login } from '@/api/userAPI'; // Importa la función de API para iniciar sesión
import { fetchAccountingPersonal } from "@/api/accountingAPI"; // Importa la función de API para obtener datos contables
import { saveToastMessage } from "@/utils/toastService"; // Importa el servicio de mensajes tipo toast

export default {
  name: 'UserLogin',
  setup() {
    const username = ref(''); // Referencia reactiva para el nombre de usuario
    const password = ref(''); // Referencia reactiva para la contraseña

    // Función para manejar el evento de inicio de sesión
    const loginAction = async () => {
      try {
        const userCredentials = {
          username: username.value,
          password: password.value,
        };

        // Llama a la función de API para iniciar sesión
        const data = await login(userCredentials);

        // Almacena el token de usuario y el nombre de usuario en el almacenamiento local
        localStorage.setItem('userToken', data.token);
        localStorage.setItem('username', data.username);

        // Obtiene datos contables personales del usuario autenticado
        const accountingData = await fetchAccountingPersonal();
        localStorage.setItem('personalAccountingId', accountingData.data.id);

        // Muestra un mensaje de éxito y redirige a la página principal de la aplicación
        saveToastMessage('success', 'Inicio de sesión exitoso');
        router.push({ name: 'home', query: { id: accountingData.data.id } });
      } catch (error) {
        saveToastMessage('error', 'Nombre de usuario o contraseña incorrecta') // Muestra el mensaje de error en caso de fallo de autenticación
        location.reload();
      }
    };

    return {
      username,
      password,
      loginAction
    };
  }
};
</script>

<style scoped>
/* Estilos específicos del componente van aquí */

/* Elimina el resaltado y sombra del campo cuando está enfocado */
.no-highlight:focus {
  outline: none !important;
  box-shadow: none !important;
}

/* Estilos para los campos de entrada */
form input {
  border: none;
  border-bottom: 2px solid var(--pickled-bluewood-500);
  background-color: transparent;
  width: calc(100% - 20px);
}

/* Estilos cuando el campo de entrada está enfocado */
form input:focus {
  outline: none;
  border-bottom: 2px solid var(--pickled-bluewood-700);
}

/* Estilos para el grupo de formularios */
.form-group {
  margin-bottom: 15px;
}

/* Estilos para el botón de enviar */
form button {
  width: 100%;
  background-color: var(--pickled-bluewood-700);
  color: white;
  border: none;
  border-radius: 5px;
}

/* Estilos cuando el botón de enviar está en hover */
form button:hover {
  background-color: var(--pickled-bluewood-600);
}

</style>
