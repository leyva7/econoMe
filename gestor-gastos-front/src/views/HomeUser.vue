<template>
  <div id="window">
    <!-- Sidebar con navegación y contabilidades compartidas -->
    <aside class="sidebar">
      <div class="logo-section">
        <img src="../assets/img/econome.png" alt="Logo" class="logo">
      </div>
      <div class="divider"></div>
      <nav class="nav">
        <a href="#" @click="navigate('home')"><img src="../assets/icons/home.svg" class="nav-icon"> Home</a>
        <a href="#" @click="navigate('gastos')"><img src="../assets/icons/spent.svg" class="nav-icon"> Gastos</a>
        <a href="#" @click="navigate('ingresos')"><img src="../assets/icons/income.svg" class="nav-icon"> Ingresos</a>
        <a href="#" @click="navigate('evolucion')"><img src="../assets/icons/evolution.svg" class="nav-icon"> Evolución</a>
      </nav>
      <div class="divider"></div>
      <div class="shared-accountings">
      <span>Contabilidades Compartidas</span>
      <img src="../assets/icons/add.svg" alt="Añadir" @click="toggleModal(true)" class="add-icon">
      <ul>
        <li v-for="(accounting, index) in accountings" :key="index">{{ accounting.name }}</li>
      </ul>
    </div>
      <div v-if="isModalOpen" class="modal-overlay">
        <div class="modal">
          <h2>Añadir Contabilidad Compartida</h2>
          <form @submit.prevent="submitForm">
            <div class="form-group">
              <label for="accountName">Nombre de contabilidad</label>
              <input type="text" id="accountName" v-model="accountName">
            </div>
            <div class="form-group">
              <label for="description">Descripción</label>
              <textarea id="description" v-model="description"></textarea>
            </div>
            <div class="modal-actions">
              <button type="submit" class="btn-primary">Añadir</button>
              <button type="button" @click="toggleModal(false)" class="btn-secondary">Cancelar</button>
            </div>
          </form>
        </div>
      </div>
    </aside>

    <!-- Área de contenido principal -->
    <main class="content">
      <!-- Barra superior con acciones del usuario -->
      <div class="top-bar">
        <div class="user-actions-container">
          <div class="user-actions">
            <span class="username">{{username}}</span>
            <img src="../assets/icons/user.svg" alt="User Icon" class="user-icon">
          </div>
          <div class="tooltip">
            <a href="#" @click="modifyUser">Modificar usuario</a>
            <a href="#" @click="modifyPassword">Modificar contraseña</a>
            <a href="#" @click="logout">Cerrar sesión</a>
          </div>
        </div>
      </div>
      <!-- Contenido dinámico aquí -->
      <div class="dynamic-content">
        Aquí va el contenido dinámico de las distintas secciones.
      </div>
    </main>
  </div>
</template>


<script>
import { ref, onMounted } from 'vue';
import router from "@/router";
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

    onMounted(() => {
      username.value = localStorage.getItem('username');
    });

    onMounted(async () => {
      await fetchAccountings();
    });

    const navigate = (routeName) => {
      router.push({ name: routeName });
    };

    const toggleModal = (open) => {
      isModalOpen.value = open;
    };

    const logout = () => {
      localStorage.clear(); // Limpia todo el almacenamiento local
      navigate('login');
    };

    const submitForm = () => {
      // Lógica para manejar la adición de una nueva contabilidad compartida
      console.log('Añadiendo contabilidad:', accountName.value, description.value);
      toggleModal(false); // Cierra el modal después de enviar
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

    return {
      isModalOpen,
      accountName,
      description,
      toggleModal,
      submitForm,
      username,
      showTooltip,
      logout,
      modifyUser,
      modifyPassword,
      accountings
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

.modal-content {
  background-color: #2C3E50;
  padding: 20px;
  border-radius: 10px;
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
  overflow-y: auto;
  padding: 20px;
  box-sizing: border-box;
  height: calc(100vh - 80px);
}

.dynamic-content {
  overflow: hidden;
}


.content, .dynamic-content {
  flex-grow: 1;
  overflow: auto;
  height: 100vh;
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: flex-end;
  padding: 40px;
  background-color: #ECF0F1;
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


/*@media (max-width: 768px) {

  #window {
    flex-direction: column;
  }

  .sidebar, .content {
    width: 100%;
    height: auto;
  }

  .sidebar {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-around;
    padding: 10px 0;
  }

  .logo-section {
    flex: 1;
    justify-content: flex-start;
    margin-bottom: 0;
    padding-left: 20px;
  }

  .nav {
    flex: 2;
    justify-content: center;
  }

  .shared-accountings {
    flex: 1;
    padding-right: 20px;
    text-align: right;
  }

  .content {
    order: -1;
  }

  .top-bar {
    justify-content: space-between;
    flex-direction: row-reverse;
    padding: 10px 20px;
  }

  .user-actions .tooltip {
    top: 100%;
    right: auto;
    left: 50%;
    transform: translateX(-50%);
  }


  .nav a, .shared-accountings details summary {
    padding: 5px 10px;
  }

  .dynamic-content {
    padding: 20px;
  }

  .tooltip a {
    display: block;
  }

  .nav-icon {
    width: 24px;
  }
}
*/

</style>

