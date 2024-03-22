<template>
  <div id="window">
    <!-- Sidebar con navegación y contabilidades compartidas -->
    <SidebarPage :sharedAccountings="sharedAccountings" @openModal="handleOpenModal"/>

      <!-- <div v-if="isModalOpen" class="modal-overlay">
        <div class="modal">
          <h2 v-if="modalContentType === 'addAccounting'">Añadir Contabilidad Compartida</h2>
          <h2 v-else-if="modalContentType === 'addOperations'">Nueva operación</h2>
          <form v-if="modalContentType === 'addAccounting'" @submit.prevent="submitSharedAccounting">
            <div class="form-group">
              <label for="accountName">Nombre de contabilidad</label>
              <input type="text" id="accountName" v-model="accountName">
            </div>
            <div class="form-group">
              <label for="description">Descripción</label>
              <textarea id="description" v-model="description"></textarea>
            </div>
            <div class="modal-actions">
              <button type="submit" class="btn-primary">Aceptar</button>
              <button type="button" @click="toggleModal(false)" class="btn-secondary">Cancelar</button>
            </div>
          </form>
          <form v-else-if="modalContentType === 'addOperations'" @submit.prevent="submitOperations">
            <div class="form-group">
              <label for="optionSelect">Tipo de gasto</label>
              <select id="optionSelect" v-model="selectedOption" @change="onSelectChange">
                <option v-for="option in options" :key="option" :value="option">
                  {{ option }}
                </option>
                <option value="custom">Otra (Especificar)</option>
              </select>
            </div>
            <div class="form-group" v-if="isCustomOptionSelected">
              <input
                  type="text"
                  placeholder="Escriba su opción"
                  id="customOption"
                  v-model="customOption"
              />
            </div>
            <div class="form-group">
              <label for="description">Descripción</label>
              <textarea id="description" v-model="operation.description"></textarea>
            </div>
            <div class="form-group">
              <label for="type">Tipo</label>
              <select id="type" v-model="operation.type">
                <option value="ingreso">Ingreso</option>
                <option value="gasto">Gasto</option>
              </select>
            </div>
            <div class="form-group">
              <label for="date">Fecha</label>
              <input type="date" id="date" v-model="operation.date">
            </div>
            <div class="form-group">
              <label for="amount">Cantidad</label>
              <input type="number" id="amount" v-model="operation.quantity" step="0.01">
            </div>
            <div class="modal-actions">
              <button type="submit" class="btn-primary">Aceptar</button>
              <button type="button" @click="toggleModal(false)" class="btn-secondary">Cancelar</button>
            </div>
          </form>
        </div>
      </div>-->
    <AddAccountingModal :isVisible="isModalOpen && modalContentType === 'addAccounting'" @update:isVisible="toggleModal" />
    <AddOperationModal :isVisible="isModalOpen && modalContentType === 'addOperations'" @update:isVisible="toggleModal" />

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
import SidebarPage from "@/components/AppSidebar.vue";
import AddAccountingModal from "@/components/AddAccountingModal.vue";
import AddOperationModal from "@/components/AddOperationModal.vue";

export default {
  name: 'UserHome',
  components:{
    SidebarPage,
    AddAccountingModal,
    AddOperationModal
  },
  setup() {
    const username = ref('');
    const showTooltip = ref(false);
    const isModalOpen = ref(false);
    const accountName = ref('');
    const description = ref('');
    const accountings = ref([]);
    const modalContentType = ref('');
    const userRole = ref('EDITOR'); // 'EDITOR' o 'VISUALIZER'
    //const selectedOption = ref('');
    //const customOption = ref('');
    const options = ref(['']); // Tus opciones predeterminadas
    const router = useRouter();
    /*const operation = ref({
      type: '',
      description: '',
      category: '',
      date: '',
      quantity: ''
    });*/

    /*const clearForm = () => {
      operation.value.type = '';
      operation.value.description = '';
      operation.value.category = '';
      operation.value.date = '';
      operation.value.quantity = '';
      customOption.value = ''; // Limpia también la opción personalizada si está seleccionada
      selectedOption.value = ''; // Limpia la opción seleccionada en el select
    };*/

    onMounted(() => {
      username.value = localStorage.getItem('username');
      //clearForm();
    });

    const fetchAccountings = async () => {
      try {
        const response = await axios.get('http://localhost:8081/api/accounting/accountingUser', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log("Datos recibidos:", response.data); // Verifica los datos recibidos
        accountings.value = response.data;
        console.log("Datos asignados a accountings:", accountings.value); // Verifica la asignación
      } catch (error) {
        console.error('Hubo un error al obtener las contabilidades:', error);
      }
    };

    onMounted(async () => {
      await fetchAccountings();
    });

    const fetchCategories = async (accountingId) => {
      try {
        console.log("aaaaaa" + accountingId);
        const response = await axios.get(`http://localhost:8081/api/accounting/${accountingId}/categories`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log(response);
        options.value = response.data; // Asigna la lista de opciones al ref 'options'
      } catch (error) {
        console.error('Hubo un error al obtener las categorías:', error);
      }
    };

    const navigate = (path) => {
      const personalAccounting = accountings.value.find(accounting => accounting.type === 'PERSONAL');

      if (personalAccounting && path.startsWith('/home-user')) {
        router.push({ path, query: { id: personalAccounting.id } });
      } else {
        // Para cualquier otra navegación, no modificamos el comportamiento original
        router.push(path);
      }
    };

    // Dentro de la función setup() en UserHome
    const handleOpenModal = (type) => {
      modalContentType.value = type; // Asume que 'modalContentType' es una ref reactive
      isModalOpen.value = true; // Asume que 'isModalOpen' es una ref reactive
    };

    /*const openAddAccountingModal = () => {
      modalContentType.value = 'addAccounting';
      isModalOpen.value = true;
      console.log("Modal Open:", isModalOpen.value, "Content Type:", modalContentType.value); // Debugging
    };*/

    const openOperationsModal = () => {
      modalContentType.value = 'addOperations';
      isModalOpen.value = true;
      console.log("Modal Open:", isModalOpen.value, "Content Type:", modalContentType.value); // Debugging
    };

    const toggleModal = (value) => {
      if (typeof value === 'boolean') {
        isModalOpen.value = value;
      } else {
        isModalOpen.value = !isModalOpen.value;
      }
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
        return 'Contabilidad personal';
      }
    });

    const sharedAccountings = computed(() => {
      return accountings.value.filter(accounting => accounting.type === 'SHARED');
    });


    /*const navigateToAccounting = (accounting) => {
      // Ejemplo de cómo podrías incluir el ID en la navegación sin mostrarlo en la URL directamente
      router.push({
        name: 'shared',
        params: { accountingName: accounting.name },
        query: { id: accounting.id } // Opcional, si quieres mantener el ID en la query de la URL
      });
    };*/

    /*const submitSharedAccounting = async () => {
      console.log("Submit form es llamado");
      if (!accountName.value || !description.value) {
        alert('Por favor, rellena todos los campos del formulario.');
        return;
      }

      if (accountName.value.trim().toLowerCase() === 'contabilidad personal') {
        alert('No se puede añadir una contabilidad compartida con el nombre "Contabilidad personal". Por favor, elige otro nombre.');
        return;
      }

      const accountingData = {
        name: accountName.value,
        description: description.value,
        userCreator: username.value,
        type: 'SHARED'
      };

      try {
        console.log(accountingData);
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
    };*/

    // Determina si la opción personalizada está seleccionada
    // const isCustomOptionSelected = computed(() => selectedOption.value === 'custom');

    // Maneja el cambio en el desplegable
    /*const onSelectChange = () => {
      if (!isCustomOptionSelected.value) {
        customOption.value = ''; // Limpia la opción personalizada si no está seleccionada
      }
    };*/

    const modifyUser = () => {
      navigate('modify-details');
    };

    const modifyPassword = () => {
      navigate('modify-password');
    };

    /*const updateUserRole = async () => {
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
    };*/

    const fetchUserRole = async (accountingId) => {
      try {
        const response = await axios.get(`http://localhost:8081/api/accounting/${accountingId}/rol`, {
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

    watch(() => [router.currentRoute.value.query.id], async ([id]) => {
      console.log(router.currentRoute.value.query.id);
      if (router.currentRoute.value.name === 'shared') {
        // Utiliza el ID directamente desde la consulta de la URL para buscar el rol del usuario
        await fetchUserRole(id);
      } else {
        // Lógica predeterminada si no estás en una contabilidad compartida o si falta el ID
        userRole.value = 'EDITOR';
      }
      await fetchCategories(id);
    }, { immediate: true, deep: true });


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

    /*const submitOperations = async () => {
      console.log(operation.value);
      console.log(customOption.value);
      // Verifica si todos los campos necesarios están presentes
      // Nota: Se agrega una verificación más explícita para 'category' dependiendo de si 'isCustomOptionSelected' es true
      let categoryValid = isCustomOptionSelected.value ? customOption.value.trim() !== '' : selectedOption.value !== '';
      if (!categoryValid || !operation.value.type || !operation.value.date || !operation.value.description || operation.value.quantity <= 0) {
        alert('Por favor, completa todos los campos.');
        return;
      }

      try {
        const accountingId = router.currentRoute.value.query.id;
        operation.value.accountingId = accountingId;
        operation.value.username = username.value;
        if (operation.value.type.toLowerCase() === 'ingreso') { // Corregido para usar '()'
          operation.value.type = 'INCOME';
        } else {
          operation.value.type = 'SPENT';
        }

        // Asigna la categoría basada en si la opción personalizada está seleccionada o no
        operation.value.category = isCustomOptionSelected.value ? customOption.value : selectedOption.value;

        console.log(operation.value);
        const response = await axios.post('http://localhost:8081/api/accounting/operation/register', operation.value, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log(response);
        alert('Operación registrada exitosamente.');
        await fetchCategories(accountingId);
        toggleModal(false);
      } catch (error) {
        console.error('Error al registrar operación:', error);
        alert('Ocurrió un error al registrar la operación. Por favor, inténtalo de nuevo.');
      }
    };*/

    return {
      isModalOpen,
      accountName,
      accountingName,
      description,
      toggleModal,
      //submitSharedAccounting,
      username,
      showTooltip,
      logout,
      modifyUser,
      modifyPassword,
      accountings,
      navigate,
      //updateUserRole,
      shouldShowAddButton,
      fetchUserRole,
      //submitOperations,
      //openAddAccountingModal,
      openOperationsModal,
      modalContentType,
      //isCustomOptionSelected,
      //onSelectChange,
      options,
      //selectedOption,
      //customOption,
      fetchCategories,
      //operation,
      //navigateToAccounting,
      sharedAccountings,
      handleOpenModal
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

.accounting-name{
  font-size: x-large;
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
  font-size: larger;
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

