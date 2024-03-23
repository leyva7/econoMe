<template>
    <div class="login">
        <div class="form-container">
            <h1>Iniciar sesión</h1>
                <form @submit.prevent="login">
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
import axios from 'axios';
import { useRouter } from 'vue-router';

export default {
  name: 'UserLogin',
  setup() {
    const username = ref('');
    const password = ref('');
    const router = useRouter();

    const login = () => {
      const userCredentials = {
        username: username.value,
        password: password.value,
      };

      axios.post('http://localhost:8081/api/auth/login', userCredentials)
          .then(response => {
            console.log("Respuesta del inicio de sesión:", response.data);
            localStorage.setItem('userToken', response.data.token);
            localStorage.setItem('username', response.data.username);
            fetchPersonalAccountingId();
            alert("Inicio de sesión exitoso");
            router.push({ name: 'home' });
          })
          .catch(error => {
            if (error.response && error.response.data) {
              console.error("Error en el inicio de sesión:", error.response.data);
            } else {
              console.error("Error en el inicio de sesión:", error);
            }
            alert("Error al iniciar sesión");
          });
    };

    const navigate = () => {
      console.log("entrando");
      router.push({ name: 'register' });
    };

    const fetchPersonalAccountingId = () => {
      axios.get('http://localhost:8081/api/accounting/accountingPersonal', {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('userToken')}`
        }
      })
          .then(response => {
            console.log("ID de contabilidad personal:", response.data.id);
            localStorage.setItem('personalAccountingId', response.data.id);
            router.push({ name: 'home', query: { id: response.data.id } });
          })
          .catch(error => {
            console.error("Error al obtener el ID de contabilidad personal:", error);
            alert("Error al obtener detalles de contabilidad personal");
          });
    };

    return {
      username,
      password,
      login,
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
    border-bottom: 2px solid var(--blue-chill-500); /* Solo añade borde en la parte inferior */
    background-color: transparent; /* Fondo transparente */
    width: calc(100% - 20px); /* Ajusta el ancho para tener en cuenta el padding */
  }

  form input:focus{
    outline: none; /* Elimina el contorno al enfocar */
    border-bottom: 2px solid var(--blue-chill-700); /* Cambia el color del borde inferior al enfocar */
  }
  
  /* Asegura que el botón se alinee correctamente y mantenga su estilo */
  form button {
    padding: 10px;
    margin-top: 20px; /* Espacio antes del botón */
    width: 100%; /* Ocupa todo el ancho disponible */
    background-color: var(--blue-chill-700);
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }
  
  form button:hover {
    background-color: var(--blue-chill-400);
  }
  
  .register-invite {
    margin-top: 20px; /* Aumenta el espacio después del formulario */
    font-size: 1rem;
    text-align: center;
  }
  
</style>
  
  