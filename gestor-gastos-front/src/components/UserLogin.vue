<template>
  <div class="w-50">
    <form @submit.prevent="loginAction" class="bg-white p-4 rounded shadow">
      <h1 class="h3 mb-3 font-weight-normal text-center">Iniciar sesión</h1>
      <div class="form-group mb-3">
        <input type="text" id="username" name="username" class="form-control no-highlight" placeholder="Nombre de usuario" v-model="username" required autofocus autocomplete="off">
      </div>
      <div class="form-group mb-3">
        <input type="password" id="password" name="password" class="form-control no-highlight" placeholder="Contraseña" v-model="password" required>
      </div>
      <button class="btn btn-lg btn-primary btn-block" type="submit">Iniciar sesión</button>
    </form>
    <p class="mt-4 bg" >¿Aún no te has registrado? <router-link to="/register" class="register-link">Registrate ahora</router-link></p>
  </div>
</template>


<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '@/service/userService';
import { fetchAccountingPersonal} from "@/service/accountingService";

export default {
  name: 'UserLogin',
  setup() {
    const username = ref('');
    const password = ref('');
    const router = useRouter();

    const loginAction = async () => {
      try {
        const userCredentials = {
          username: username.value,
          password: password.value,
        };

        const data = await login(userCredentials);
        localStorage.setItem('userToken', data.token);
        localStorage.setItem('username', data.username);
        const accountingData = await fetchAccountingPersonal();
        localStorage.setItem('personalAccountingId', accountingData.data.id);
        alert("Inicio de sesión exitoso");
        router.push({ name: 'home', query: { id: accountingData.data.id } });
      } catch (error) {
        console.error("Error en el inicio de sesión:", error);
        alert("Error al iniciar sesión");
      }
    };

    const navigate = () => {
      router.push({ name: 'register' });
    };

    return {
      username,
      password,
      loginAction,
      navigate
    };
  }
};
</script>
  
  
  <style scoped>
  .no-highlight:focus {
    outline: none !important;
    box-shadow: none !important;
  }

  form input {
    border: none; /* Elimina todos los bordes */
    border-bottom: 2px solid var(--pickled-bluewood-500); /* Solo añade borde en la parte inferior */
    background-color: transparent; /* Fondo transparente */
    width: calc(100% - 20px); /* Ajusta el ancho para tener en cuenta el padding */
  }

  form input:focus {
    outline: none; /* Elimina el contorno al enfocar */
    border-bottom: 2px solid var(--pickled-bluewood-700); /* Cambia el color del borde inferior al enfocar */
  }

  .form-group {
    margin-bottom: 15px; /* Espacio entre campos */
  }

  form button {
    width: 100%; /* Ocupa todo el ancho disponible */
    background-color: var(--pickled-bluewood-700);
    color: white;
    border: none;
    border-radius: 5px;
  }

  form button:hover {
    background-color: var(--pickled-bluewood-400);
  }

  .register-link {
    color: var(--pickled-bluewood-700); /* Cambia el color del texto a rojo */
    font-weight: bold; /* Añade negrita al texto */
    text-decoration: none;
  }

  .register-link:hover {
    text-decoration: underline; /* Subraya el texto al pasar el cursor sobre él */
  }

</style>
  
  