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
  </div>
</template>


<script>
import { ref } from 'vue';
import router from "@/router";
import { login } from '@/api/userAPI';
import { fetchAccountingPersonal} from "@/api/accountingAPI";
import {saveToastMessage} from "@/utils/toastService";

export default {
  name: 'UserLogin',
  setup() {
    const username = ref('');
    const password = ref('');

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
        saveToastMessage('success', 'Inicio de sesión exitoso');
        router.push({ name: 'home', query: { id: accountingData.data.id } });
      } catch (error) {
        console.error("Error en el inicio de sesión:", error);
        alert(error.response.data);
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
  .no-highlight:focus {
    outline: none !important;
    box-shadow: none !important;
  }

  form input {
    border: none;
    border-bottom: 2px solid var(--pickled-bluewood-500);
    background-color: transparent;
    width: calc(100% - 20px);
  }

  form input:focus {
    outline: none;
    border-bottom: 2px solid var(--pickled-bluewood-700);
  }

  .form-group {
    margin-bottom: 15px;
  }

  form button {
    width: 100%;
    background-color: var(--pickled-bluewood-700);
    color: white;
    border: none;
    border-radius: 5px;
  }

  form button:hover {
    background-color: var(--pickled-bluewood-600);
  }


</style>
  
  