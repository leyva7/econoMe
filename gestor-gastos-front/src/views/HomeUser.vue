<template>
  <div id="window">
    <!-- Sidebar con navegación y contabilidades compartidas -->
    <aside class="sidebar">
      <div class="logo-section">
        <img src="../assets/img/econome.png" alt="Logo" class="logo">
      </div>
      <div class="divider"></div>
      <nav class="nav">
        <a href="#" @click="navigate('/home-user')"><img src="../assets/icons/home.svg" alt="Home" class="nav-icon"> Home</a>
        <a href="#" @click="navigate('/home-user/spent')"><img src="../assets/icons/spent.svg" alt="Gastos" class="nav-icon"> Gastos</a>
        <a href="#" @click="navigate('/home-user/income')"><img src="../assets/icons/income.svg" alt="Ingresos" class="nav-icon"> Ingresos</a>
        <a href="#" @click="navigate('/home-user/evolution')"><img src="../assets/icons/evolution.svg" alt="Evolucion" class="nav-icon"> Evolución</a>
      </nav>
      <div class="divider"></div>
      <div class="shared-accountings">
      <span>Contabilidades Compartidas</span>
        <button @click="openAddAccountingModal">Añadir Contabilidad</button>
      <ul>
        <a href="#" v-for="(accounting, index) in accountings" :key="index" @click="navigate('/home-user/shared' + accounting.name)"><li>{{ accounting.name }}</li></a>
      </ul>
    </div>
      <div v-if="isModalOpen" class="modal-overlay">
        <div class="modal">
          <h2 v-if="modalContentType === 'addAccounting'">Añadir Contabilidad Compartida</h2>
          <h2 v-else-if="modalContentType === 'addOperations'">Nuevo Cuestionario</h2>
          <!-- Formulario para añadir contabilidad -->
          <form v-if="modalContentType === 'addAccounting'" @submit.prevent="submitForm">
            <div class="form-group">
              <label for="accountName">Nombre de contabilidad</label>
              <input type="text" id="accountName" v-model="accountName">
            </div>
            <div class="form-group">
              <label for="description">Descripción</label>
              <textarea id="description" v-model="description"></textarea>
            </div>
          </form>
          <form v-else-if="modalContentType === 'addOperations'" @submit.prevent="submitOperations">
            <div class="form-group">
              <label for="accountName">Tipo de gasto</label>
              <input type="text" id="accountName" v-model="accountName">
            </div>
            <div class="form-group">
              <label for="description">Descripción</label>
              <textarea id="description" v-model="description"></textarea>
            </div>
          </form>
          <div class="modal-actions">
            <button type="submit" class="btn-primary">Aceptar</button>
            <button type="button" @click="toggleModal(false)" class="btn-secondary">Cancelar</button>
          </div>
        </div>
      </div>
    </aside>

    <!-- Área de contenido principal -->
    <main class="content">
      <!-- Barra superior con acciones del usuario -->
      <div class="top-bar">
        <div class="left-content">
          <span class="accounting-name">{{ accountingName }}</span>
        </div>
        <div class="user-actions-container">
          <div class="user-actions">
            <span class="username">{{username}}</span>
            <img src="../assets/icons/user.svg" alt="User Icon" class="user-icon">
          </div>
          <div class="tooltip">
            <a href="#" @click="navigate('/modify-details')">Modificar usuario</a>
            <a href="#" @click="navigate('/modify-password')">Modificar contraseña</a>
            <a href="#" @click="navigate('/')">Cerrar sesión</a>
          </div>
        </div>
      </div>
      <!-- Contenido dinámico aquí -->
      <div class="dynamic-content">
        <router-view></router-view>
        <button v-if="shouldShowAddButton" @click="openOperationsModal" class="add-floating-button">
          <span class="plus-icon">+</span>
        </button>
      </div>
    </main>
  </div>
</template>


<script>
import { ref, onMounted, watch } from 'vue';
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'UserHome',
  setup() {
    const username = ref('');
    const showTooltip = ref(false);
    const isModalOpen = ref(false);
    const accountName = ref('');
    const description = ref('');
    const accountings = ref([]);
    const modalContentType = ref('');
    const userRole = ref('EDITOR'); // 'EDITOR' o 'VISUALIZER'
    const router = useRouter();

    onMounted(() => {
      username.value = localStorage.getItem('username');
    });

    onMounted(async () => {
      await fetchAccountings();
    });

    const navigate = (path) => {
      router.push(path);
    };

    const openAddAccountingModal = () => {
      modalContentType.value = 'addAccounting';
      isModalOpen.value = true;
      console.log("Modal Open:", isModalOpen.value, "Content Type:", modalContentType.value); // Debugging
    };

    const openOperationsModal = () => {
      modalContentType.value = 'addOperations';
      isModalOpen.value = true;
      console.log("Modal Open:", isModalOpen.value, "Content Type:", modalContentType.value); // Debugging
    };

    const toggleModal = (open) => {
      isModalOpen.value = open;
    };

    const logout = () => {
      localStorage.clear(); // Limpia todo el almacenamiento local
      navigate('login');
    };

    const accountingName = computed(() => {
      if (router.currentRoute.value.name == 'shared') {
        return router.currentRoute.value.params.accountingName;
      }
      else{
        return 'Contabilidad Personal';
      }
    });

    const submitForm = async () => {
      if (!accountName.value || !description.value) {
        alert('Por favor, rellena todos los campos del formulario.');
        return;
      }

      const accountingData = {
        name: accountName.value,
        description: description.value,
        userCreator: username.value,
        type: 'SHARED'
      };

      try {
        await axios.post('http://localhost:8081/api/accounting/register', accountingData, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log('Contabilidad añadida con éxito:', accountingData);
        alert("Contabilidad añadida con éxito");
        toggleModal(false); // Cierra el modal después de enviar

        // Vuelve a cargar las contabilidades compartidas para mostrar la última añadida
        await fetchAccountings();

      } catch (error) {
        console.error('Error al añadir contabilidad:', error);
        alert('Ocurrió un error al añadir la contabilidad. Por favor, inténtalo de nuevo.');
      }
    };

    const submitOperations = async () => {
      // Lógica para manejar el envío del cuestionario
    };

    const fetchAccountings = async () => {
      try {
        const response = await axios.get('http://localhost:8081/api/accounting/accountingUserShared', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log("Datos recibidos:", response.data); // Verifica los datos recibidos
        accountings.value = response.data;
        console.log("Datos asignados a accountings:", accountings.value); // Verifica la asignación
      } catch (error) {
        console.error('Hubo un error al obtener las contabilidades compartidas:', error);
      }
    };

    const modifyUser = () => {
      navigate('modify-details');
    };

    const modifyPassword = () => {
      navigate('modify-password');
    };

    const updateUserRole = async () => {
      try {
        const response = await axios.get('url-para-obtener-detalle-contabilidad', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        userRole.value = response.data.role;
      } catch (error) {
        console.error('Error al obtener el rol del usuario:', error);
      }
    };

    const fetchUserRole = async (accountingName) => {
      try {
        const response = await axios.get(`http://localhost:8081/api/accounting/${accountingName}/rol`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log(response);
        userRole.value = response.data.role;
        console.log(userRole);
      } catch (error) {
        console.error('Error al obtener el rol del usuario:', error);
        userRole.value = null;
      }
    };

    watch(() => router.currentRoute.value, async (newRoute) => {
      if (newRoute.name === 'shared') {
        // Extrae el nombre de la contabilidad compartida de los parámetros de la ruta
        const accountingName = newRoute.params.accountingName;
        // Realiza la llamada a la API para obtener el rol del usuario en esta contabilidad
        await fetchUserRole(accountingName);
      } else {
        userRole.value = 'EDITOR';
      }
    }, { immediate: true });

    const shouldShowAddButton = computed(() => {
      const routeNamesToShowButton = ['home', 'income', 'spent', 'evolution'];
      if (routeNamesToShowButton.includes(router.currentRoute.value.name)) {
        return true;
      }
      if (router.currentRoute.value.name === 'shared' && userRole.value === 'EDITOR') {
        return true;
      }
      return false;
    });

    return {
      isModalOpen,
      accountName,
      accountingName,
      description,
      toggleModal,
      submitForm,
      username,
      showTooltip,
      logout,
      modifyUser,
      modifyPassword,
      accountings,
      navigate,
      updateUserRole,
      shouldShowAddButton,
      fetchUserRole,
      submitOperations,
      openAddAccountingModal,
      openOperationsModal,
      modalContentType
    };
  },
};
</script>


<style scoped>
#window {
  display: flex;
  max-height: 100vh;
  overflow: hidden;
}

.sidebar, .content {
  overflow-y: auto; /* Permite desplazamiento interno si es necesario */
}

.sidebar {
  width: 15%;
  background-color: #2C3E50;
  color: #FFFFFF;
  padding: 20px;
  display: flex;
  flex-direction: column;
  height: 100vh;
  text-align: center;
  box-sizing: border-box;
  overflow-y: auto;
}

.divider{
  border-bottom: 1px solid #FFFFFF;
}

.divider, .logo-section, .nav, .shared-accountings {
  margin-bottom: 30px;
}

.logo-section, .nav a, .shared-accountings details summary, .shared-accountings details {
  display: flex;
  justify-content: center;
  align-items: center;
}

.logo {
  max-width: 150px;
  margin-right: 10px;
}

.nav-icon, .tooltip a {
  margin-right: 8px;
}

.nav a, .shared-accountings details summary {
  color: #FFFFFF;
  padding: 5px 0;
  text-decoration: none;
}

.shared-accountings details {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.shared-accountings ul {
  list-style-type: none; /* Quita los puntos */
  padding: 0;
}

.shared-accountings li {
  margin-bottom: 10px; /* Aumenta la separación */
}

.add-icon {
  cursor: pointer;
  margin-left: 10px;
}

.modal {
  background-color: #2C3E50;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.6);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
}

.form-group input,
.form-group textarea {
  width: calc(100% - 20px); /* Ajustar para padding */
  padding: 10px;
  margin-top: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.content {
  flex-grow: 1;
  overflow-y: auto; /* Permite desplazamiento vertical dentro del content si es necesario */
  padding: 20px;
  display: flex;
  flex-direction: column;
  background-color: #2C3E50;
}

.dynamic-content {
  margin-top: 20px;
  margin-bottom: 30px;
  overflow-y: auto; /* Permite desplazamiento vertical dentro del dynamic-content si es necesario */
  flex-grow: 1; /* Asegura que este contenedor llene el espacio disponible en .content */
  background-color: #ECF0F1;
  border-radius: 20px;
}


.content, .dynamic-content {
  flex-grow: 1;
  overflow: auto;
  height: 100vh;
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between; /* Ajusta los elementos a los extremos */
  padding: 40px;
  background-color: rgb(236, 240, 241);
  border-radius: 25px;
  color: #2C3E50;
  font-size: 16px;
  font-weight: bold;
}

.left-content {
  /* Estilos para el contenido izquierdo (nombre de la contabilidad) */
  display: flex;
  align-items: center;
}

.user-actions-container {
  /* Ajustes para alinear los elementos de usuario a la derecha */
  display: flex;
  align-items: center;
}

.user-actions-container, .user-actions {
  position: relative;
  display: inline-block;
}

.user-actions{
  display: flex;
  align-items: center;
}

.username {
  margin-right: 10px;
  color: #2C3E50;
  font-size: 16px;
  font-weight: bold;
}

.user-icon {
  width: 50px;
  height: auto;
  margin-right: 15px;
}

.tooltip {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  background-color: #FFFFFF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
  padding: 10px;
  border-radius: 5px;
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.5s, visibility 0.5s;
}

.user-actions-container:hover .tooltip, .user-actions:hover .tooltip {
  opacity: 1;
  visibility: visible;
}

.tooltip a {
  display: block;
  color: #2C3E50;
  text-align: center;
  text-decoration: none;
  margin: 5px 0;
}

.user-actions-container:hover .tooltip, .user-actions:hover .tooltip {
  display: block;
}

.add-floating-button {
  position: fixed;
  right: 25px;
  bottom: 20px;
  z-index: 100;
  padding: 0.8em; /* Ajustado para ser un poco más pequeño y proporcional */
  font-size: 16px; /* Tamaño de fuente general del botón reducido */
  color: #ffffff;
  background-color: #2C3E50;
  border: none;
  border-radius: 50%; /* Círculo perfecto */
  width: 60px; /* Ancho fijo para mantener el círculo perfecto */
  height: 60px; /* Altura fija para mantener el círculo perfecto */
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease 0s;
  cursor: pointer;
  outline: none;
  display: flex; /* Para centrar el ícono "+" */
  justify-content: center; /* Centrado horizontal */
  align-items: center; /* Centrado vertical */
}

.plus-icon {
  font-size: 60px; /* Tamaño específico para el símbolo "+" */
  line-height: 1; /* Asegura que el símbolo "+" se centre verticalmente */
}

.add-floating-button:hover {
  background-color: #2C3E50;
  box-shadow: 0px 15px 20px #2C3E50;
  color: #fff;
  transform: translateY(-7px);
}

.add-floating-button:active {
  transform: translateY(-1px);
}

</style>

