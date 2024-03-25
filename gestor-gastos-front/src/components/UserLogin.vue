<template>
    <div class="login">
        <div class="form-container">
            <h1>Iniciar sesión</h1>
                <form @submit.prevent="loginAction">
                    <input type="text" id="username" name="username" placeholder="Nombre de usuario" v-model="username"/>
                    <input type="password" id="password" name="password" placeholder="Contraseña" v-model="password"/>
                    <button type="submit">Iniciar sesión</button>
                </form>
          <p class="register-invite">¿Aún no te has registrado?</p>
          <router-link to="/register">Registrate ahora</router-link>
        </div>
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
  .form-container {
    padding: 40px;
    background-color: rgba(255, 255, 255, 0.5); /* Fondo semi-transparente */
    backdrop-filter: blur(10px); /* Efecto de difuminado para el fondo */
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Sombra suave para profundidad */
    max-width: 400px;
    margin: auto;
    display: flex;
    flex-direction: column;
    align-items: center; /* Alinea los elementos internos al centro */
  }
  
  /* Actualiza los estilos de input para tener solo el borde inferior */
  form input {
    margin-bottom: 15px; /* Espacio entre campos */
    padding: 15px 10px; /* Aumenta el padding para más espacio alrededor del texto */
    border: none; /* Elimina todos los bordes */
    border-bottom: 2px solid var(--pickled-bluewood-500); /* Solo añade borde en la parte inferior */
    background-color: transparent; /* Fondo transparente */
    width: calc(100% - 20px); /* Ajusta el ancho para tener en cuenta el padding */
  }

  form input:focus{
    outline: none; /* Elimina el contorno al enfocar */
    border-bottom: 2px solid var(--pickled-bluewood-700); /* Cambia el color del borde inferior al enfocar */
  }
  
  /* Asegura que el botón se alinee correctamente y mantenga su estilo */
  form button {
    padding: 10px;
    margin-top: 20px; /* Espacio antes del botón */
    width: 100%; /* Ocupa todo el ancho disponible */
    background-color: var(--pickled-bluewood-700);
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  form button:hover {
    background-color: var(--pickled-bluewood-400);
  }
  
  .register-invite {
    margin-top: 20px; /* Aumenta el espacio después del formulario */
    font-size: 1rem;
    text-align: center;
  }
  
</style>
  
  