<template>
  <AddOperationModal :isVisible="isModalOpen" :operationToEdit="operationToEdit" @update:isVisible="toggleModal" />
  <div class="operation-container">
    <p class="operation-title">Operaciones</p>
    <div class="operation-details">
      <div class="filters-container">
        <div class="filter">
          <!-- Selección de contabilidad -->
          <label for="accounting">Contabilidad:</label>
          <select v-model="selectedAccountingId" id="accounting">
            <option v-for="accounting in accountings" :key="accounting.id" :value="accounting.id">{{ accounting.name }}</option>
          </select>
        </div>

        <div class="filter">
          <!-- Selección del tipo de operación -->
          <label for="type">Operación:</label>
          <select v-model="selectedType">
            <option value="INCOME">Ingreso</option>
            <option value="SPENT">Gasto</option>
            <option value="BOTH">Ambas</option>
          </select>
        </div>

        <div class="filter">
          <!-- Selección de categoría basada en el tipo de operación -->
          <label for="category">Categoría:</label>
          <select v-model="selectedCategory">
            <option value="ALL">Todas</option>
            <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
          </select>
        </div>

        <div class="filter">
          <!-- Filtros adicionales -->
          <label for="quantity">Cantidad mínima:</label>
          <input type="number" v-model.number="quantityMin" placeholder="Cantidad Mínima" />
          <label for="quantity">Cantidad máxima:</label>
          <input type="number" v-model.number="quantityMax" placeholder="Cantidad Máxima" />
        </div>

        <div class="filter">
          <label for="date">Fecha inicio:</label>
          <input type="date" v-model="dateStart" placeholder="Desde" />
          <label for="date">Fecha fin:</label>
          <input type="date" v-model="dateEnd" placeholder="Hasta" />
        </div>

          <button @click="applyFilters">Aplicar Filtros</button>
      </div>

      <div class="operations-table-container">
        <table class="operations-table">
          <thead>
          <tr>
            <th>Usuario</th>
            <th>Contabilidad</th>
            <th>Operación</th>
            <th>Categoría</th>
            <th>Cantidad</th>
            <th>Fecha</th>
            <th>Acciones</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="operation in paginatedOperations" :key="operation.id">
            <td>{{ operation.username }}</td>
            <td>{{ accountings.find(accounting => accounting.id === operation.accountingId)?.name }}</td>
            <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
            <td>{{ operation.category }}</td>
            <td>{{ operation.quantity }}</td>
            <td>{{ operation.date }}</td>
            <td>
              <button v-if="accountingsRoles[operation.accountingId] === 'EDITOR'" @click="editOperation(operation)">Editar</button>
              <div v-else class="visualizer-img" >
                <img src="../assets/icons/eye.svg" alt="Visualizer">
              </div>
              <button v-if="accountingsRoles[operation.accountingId] === 'EDITOR'" @click="deleteOperation(operation.id)">Borrar</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
      <div class="pagination-controls">
        <button @click="prevPage" :disabled="currentPage <= 1">Anterior</button>
        <span>Página {{ currentPage }} de {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage >= totalPages">Siguiente</button>
      </div>
    </div>
  </div>
</template>

<script>
import AddOperationModal from "@/components/AddOperationModal.vue";


import { ref, watch, reactive, onMounted, computed, inject } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore.js';
import {
  fetchFilteredOperations,
  deleteOperation as deleteOperationApi
} from '@/service/operationService'

export default {
  name: 'OperationDetails',
  components:{
    AddOperationModal
  },
  setup() {
    const accountingStore = useAccountingStore();
    const { accountings, fetchAccountingsAsync, fetchAllAccountingsUserOperationsAsync, allAccountingUserOperations, fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, fetchCategoriesAsync, categories, fetchUserRoleAsync, userRole } = accountingStore;

    const selectedAccountingId = ref(null);
    const selectedType = ref('');
    const selectedCategory = ref('');
    const quantityMin = ref(0);
    const quantityMax = ref(1000);
    const dateStart = ref('');
    const dateEnd = ref('');

    const accountingsRoles = reactive({});
    const { isModalOpen, toggleModal } = inject('modalData');
    const operationToEdit = ref(null);

    const currentPage = ref(1);
    const operationsPerPage = 10;

    const filteredOperations = ref([]); // Para las operaciones filtradas

    onMounted(async () => {
      await fetchAccountingsAsync();
      await fetchAllAccountingsUserOperationsAsync();
      await fetchRolesForAccountings();
      filteredOperations.value = allAccountingUserOperations.value;
    });

    const fetchRolesForAccountings = async () => {
      for (const accounting of accountings.value) {
        await fetchUserRoleAsync(accounting.id);
        accountingsRoles[accounting.id] = userRole.value;
      }
    };

    watch([selectedAccountingId, selectedType], async ([newAccountId, newType]) => {
      if (!newAccountId) return;
      switch(newType) {
        case 'INCOME':
          await fetchCategoriesIncomeAsync(newAccountId);
          break;
        case 'SPENT':
          await fetchCategoriesSpentAsync(newAccountId);
          break;
        case 'BOTH':
          await fetchCategoriesAsync(newAccountId);
          break;
      }
    }, { immediate: true });

    const applyFilters = async () => {
      // Verifica que la contabilidad seleccionada, el tipo y la categoría no estén vacíos
      if (!selectedAccountingId.value || selectedType.value === '' || selectedCategory.value === '') {
        alert("Por favor, rellena todos los campos para filtrar.");
        return;
      }

      // Verifica que los campos de cantidad no sean vacíos y el rango sea válido
      if (quantityMin.value > quantityMax.value) {
        alert("El rango de cantidad no es válido.");
        return;
      }

      // Verifica que ambos campos de fecha tengan valor y el rango de fechas sea válido
      if ((!dateStart.value || !dateEnd.value) || (dateStart.value && dateEnd.value && dateStart.value > dateEnd.value)) {
        alert("Por favor, selecciona un rango de fechas válido.");
        return;
      }

      const filters = {
        accountingId: selectedAccountingId.value,
        type: selectedType.value.toUpperCase(), // Asegúrate de que el valor coincide con los tipos esperados en el backend
        category: selectedCategory.value,
        quantityMin: quantityMin.value,
        quantityMax: quantityMax.value,
        dateStart: dateStart.value,
        dateEnd: dateEnd.value
      };

      try {
        const response = await fetchFilteredOperations(filters);
        // Suponiendo que la respuesta del backend es la lista de operaciones filtradas
        filteredOperations.value = response.data;
      } catch (error) {
        console.error("Error aplicando filtros:", error);
      }
    };


    const totalPages = computed(() => Math.ceil(filteredOperations.value.length / operationsPerPage));

    const paginatedOperations = computed(() => {
      const start = (currentPage.value - 1) * operationsPerPage;
      const end = start + operationsPerPage;
      return filteredOperations.value.slice(start, end);
    });

    const nextPage = () => {
      if (currentPage.value < totalPages.value) currentPage.value++;
    };

    const prevPage = () => {
      if (currentPage.value > 1) currentPage.value--;
    };

    const editOperation = (operation) => {
      operationToEdit.value = operation;
      isModalOpen.value = true; // Asegúrate de que esta variable controle la visibilidad del modal.
    };
    const deleteOperation = async (id) => {
      try {
        const response = await deleteOperationApi(id);
        console.log("Eliminando operación", response);
        await fetchAllAccountingsUserOperationsAsync();
        location.reload();
      } catch (error) {
        console.error("Error aplicando filtros:", error);
      }
    };

    return {
      accountings,
      allAccountingUserOperations,
      selectedAccountingId,
      categories,
      selectedType,
      selectedCategory,
      quantityMin,
      quantityMax,
      dateStart,
      dateEnd,
      accountingsRoles,
      applyFilters,
      paginatedOperations, nextPage, prevPage, currentPage, totalPages,
      isModalOpen, toggleModal, operationToEdit,
      editOperation, deleteOperation
    };
  },
};
</script>

<style scoped>
.operation-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.operation-details{
  padding: 10px;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
}

.operation-title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.filters-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: center;
  margin-bottom: 20px;
}

.filter {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.filter label {
  margin-bottom: 5px;
  font-weight: bold;
}

.operations-table-container {
  overflow-x: auto; /* Permite el desplazamiento horizontal */
  max-width: 100%; /* Asegura que no se desborde del viewport */
}

.operations-table {
  margin: auto; /* Centra la tabla */
  border-collapse: collapse;
}

.operations-table th, .operations-table td {
  border: 1px solid #ccc;
  padding: 6px;
  text-align: left;
}

.operations-table th {
  background-color: #f2f2f2;
}

button {
  margin-right: 5px;
  cursor: pointer; /* Mejora la indicación de que es clickeable */
}

.pagination-controls {
  display: flex;
  gap: 10px;
  justify-content: center; /* Centra los controles de paginación */
  margin-top: 20px;
}

.visualizer-img {
  display: flex;
  justify-content: center; /* Centra horizontalmente */
  align-items: center; /* Centra verticalmente */
}

@media (max-width: 768px) { /* Ajusta según tus necesidades */
  .pagination-controls button {
    padding: 10px 20px; /* Aumenta el padding para hacer el botón más grande */
    font-size: 16px; /* Aumenta el tamaño de fuente para mejorar la legibilidad */
  }
}
</style>

