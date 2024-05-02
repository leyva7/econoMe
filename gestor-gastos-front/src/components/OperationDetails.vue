<template>
  <AddOperationModal :isVisible="isModalOpen" :operationToEdit="operationToEdit" @update:isVisible="toggleModal" />
  <OperationInfoModal :isVisible="isNewModalOpen" :operationToShow="operationToShow" @update:isVisible="toggleModal" />

  <div class="container mt-5">
    <div class="text-center mb-4">
      <p class="fs-4">Operaciones</p>
    </div>

    <!-- Filtros -->
    <div class="mb-4">
      <div class="row g-3 justify-content-left">
        <!-- Selección de contabilidad -->
        <div class="col-md-auto">
          <div class="form-floating">
            <select v-model="selectedAccountingId" id="accounting" class="form-select">
              <option v-for="accounting in accountings" :key="accounting.id" :value="accounting.id">{{ accounting.name }}</option>
            </select>
            <label for="accounting">Contabilidad:</label>
          </div>
        </div>
        <!-- Selección del tipo de operación -->
        <div class="col-md-auto">
          <div class="form-floating">
            <select v-model="selectedType" class="form-select">
              <option value="INCOME">Ingreso</option>
              <option value="SPENT">Gasto</option>
              <option value="BOTH">Ambas</option>
            </select>
            <label for="type">Operación:</label>
          </div>
        </div>
        <!-- Selección de categoría -->
        <div class="col-md-auto">
          <div class="form-floating">
            <select v-model="selectedCategory" class="form-select">
              <option value="ALL">Todas</option>
              <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
            </select>
            <label for="category">Categoría:</label>
          </div>
        </div>
        <div class="col-md-auto">
          <div class="d-flex align-items-center gap-2">
            <div class="form-floating">
              <input type="number" class="form-control" id="quantityMin" v-model.number="quantityMin" placeholder="Cantidad Mínima">
              <label for="quantityMin">Cantidad mínima:</label>
            </div>
            <div class="form-floating">
              <input type="number" class="form-control" id="quantityMax" v-model.number="quantityMax" placeholder="Cantidad Máxima">
              <label for="quantityMax">Cantidad máxima:</label>
            </div>
          </div>
        </div>
        <div class="col-md-auto">
          <div class="d-flex align-items-center gap-2">
            <div class="form-floating">
              <input type="date" class="form-control" id="dateStart" v-model="dateStart" placeholder="Desde">
              <label for="dateStart">Fecha inicio:</label>
            </div>
            <div class="form-floating">
              <input type="date" class="form-control" id="dateEnd" v-model="dateEnd" placeholder="Hasta">
              <label for="dateEnd">Fecha fin:</label>
            </div>
          </div>
        </div>
        <div class="d-flex justify-content-end mt-3">
          <button @click="applyFilters" class="btn btn-primary">Aplicar Filtros</button>
        </div>
      </div>
    </div>

    <!-- Tabla de operaciones y paginación -->
    <div class="table-responsive mb-4">
      <table class="table table-hover">
        <thead>
        <tr>
          <th>Usuario</th>
          <th>Contabilidad</th>
          <th>Operación</th>
          <th>Categoría</th>
          <th>Cantidad</th>
          <th>Fecha</th>
          <th class="text-center">Acciones</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="operation in paginations[0].paginatedData.value" :key="operation.id">
          <td>{{ operation.username }}</td>
          <td>{{ accountings.find(accounting => accounting.id === operation.accountingId)?.name }}</td>
          <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
          <td>{{ operation.category }}</td>
          <td>{{ operation.quantity }}</td>
          <td>{{ operation.date }}</td>
          <td>
            <div class="d-flex justify-content-around">
              <button class="btn btn-primary btn-sm" @click="showOperation(operation)">Ver</button>
              <button class="btn btn-primary btn-sm" v-if="accountingsRoles[operation.accountingId] === 'EDITOR'" @click="editOperation(operation)">Editar</button>
              <button class="btn btn-danger btn-sm" v-if="accountingsRoles[operation.accountingId] === 'EDITOR'" @click="deleteOperation(operation.id)">Eliminar</button>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </div>

    <div class="d-flex justify-content-center mb-4">
      <div v-if="paginations[0].totalPages.value > 1" class="pagination-container d-flex justify-content-center mb-4">
        <button @click=paginations[0].prevPage() class="btn btn-secondary me-2" :disabled="paginations[0].currentPage.value <= 1">Anterior</button>
        <span class="me-2">Página {{ paginations[0].currentPage }} de {{ paginations[0].totalPages.value }}</span>
        <button @click=paginations[0].nextPage() class="btn btn-secondary" :disabled="paginations[0].currentPage >= paginations[0].totalPages.value">Siguiente</button>
      </div>
    </div>
  </div>
</template>

<script>
import AddOperationModal from "@/components/AddOperationModal.vue";
import OperationInfoModal from "@/components/OperationInfoModal.vue";

import { ref, watch, reactive, onMounted } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore.js';
import { fetchFilteredOperations, deleteOperation as deleteOperationApi } from '@/api/operationAPI'
import { isModalOpen, isNewModalOpen, toggleModal, operationToEdit, operationToShow, editOperation, showOperation} from "@/utils/modal";
import { useMultiplePagination } from "@/utils/usePagination";

export default {
  name: 'OperationDetails',
  components:{
    AddOperationModal, OperationInfoModal
  },
  setup() {
    const accountingStore = useAccountingStore();
    const { accountings, loadAccountings, fetchAllAccountingsUserOperationsAsync, allAccountingUserOperations, fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, fetchCategoriesAsync, categories, fetchUserRoleAsync, userRole } = accountingStore;

    const selectedAccountingId = ref(null);
    const selectedType = ref('');
    const selectedCategory = ref('');
    const quantityMin = ref(0);
    const quantityMax = ref(1000);
    const dateStart = ref('');
    const dateEnd = ref('');

    const accountingsRoles = reactive({});

    const filteredOperations = ref([]);

    const paginationConfigs = [
      { data: filteredOperations, reduced: false },
    ];

    const paginations = useMultiplePagination(paginationConfigs);

    onMounted(async () => {
      await loadAccountings();
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
      paginations,
      isModalOpen, isNewModalOpen, toggleModal, operationToEdit, operationToShow,
      showOperation, editOperation, deleteOperation
    };
  },
};
</script>

<style scoped>

button {
  margin-right: 5px;
  cursor: pointer; /* Mejora la indicación de que es clickeable */
}

@media (max-width: 768px) { /* Ajusta según tus necesidades */
  .pagination-controls button {
    padding: 10px 20px; /* Aumenta el padding para hacer el botón más grande */
    font-size: 16px; /* Aumenta el tamaño de fuente para mejorar la legibilidad */
  }
}
</style>

