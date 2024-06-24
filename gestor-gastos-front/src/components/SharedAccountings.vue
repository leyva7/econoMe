<template>
  <div class="container mt-5 text-center">
    <div class="row mb-4">
      <div class="col-12 d-flex justify-content-between align-items-center">
        <!-- Título dinámico basado en la descripción del primer elemento seleccionado -->
        <h3>{{ accountingSharedSelected.length > 0 ? accountingSharedSelected[0].description : 'No hay datos' }}</h3>

        <!-- Botones de gestión visibles solo si el usuario es el creador -->
        <div v-if="isUserCreator" class="my-3 button-group">
          <button @click="openUserManagementModal" class="btn btn-primary me-2">Gestionar usuarios</button>
          <button @click="deleteAccounting" class="btn btn-danger">Eliminar Contabilidad</button>
        </div>

        <!-- Componente de modal para gestionar usuarios -->
        <UserManagementModal :isVisible="isUserManagementModalOpen" @update:isVisible="closeModal" />
      </div>
    </div>

    <div class="row g-4">
      <!-- Selector de intervalo para actualizar los datos -->
      <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

      <!-- Renderizado dinámico de gráficos -->
      <template v-for="(chart, index) in charts" :key="index">
        <div class="col-12 col-lg-4 mb-2">
          <div class="card h-100">
            <h4 class="card-header">{{ chart.title }}</h4>
            <div v-if="chart.hasData" class="card-body">
              <div class="chart-container">
                <canvas :id="chart.id"></canvas>
              </div>
            </div>
            <NoDataMessage v-else/>
          </div>
        </div>
      </template>

      <!-- Últimas operaciones -->
      <div class="col-12 col-lg-4 mb-2">
        <div class="card h-100">
          <h4 class="card-header">Últimas Operaciones</h4>
          <div v-if="hasData" class="card-body">
            <DataTable :pagination="paginations[0]" :columns="tableColumnsOperations" />
          </div>
          <NoDataMessage v-else/>
        </div>
      </div>
    </div>

    <div class="row g-4">
      <!-- Detalles de Gastos e Ingresos -->
      <div class="col-12 col-lg-4" v-for="(table, index) in tables" :key="index">
        <div class="card h-100">
          <h4 class="card-header">{{ table.title }}</h4>
          <div v-if="table.hasData" class="card-body">
            <DataTable :pagination="paginations[index+1]" :columns="tableSpentUser" />
          </div>
          <NoDataMessage v-else />
        </div>
      </div>

      <!-- Lista de usuarios -->
      <div class="col-12 col-lg-4">
        <div class="card h-100">
          <h4 class="card-header">Usuarios</h4>
          <div class="card-body">
            <ul class="user-list list-group">
              <!-- Renderizado dinámico de usuarios y roles -->
              <li v-for="(user, index) in usersAccounting" :key="index" class="list-group-item d-flex justify-content-between align-items-center">
                {{ user.username }}
                <span class="badge bg-primary rounded-pill">{{ user.role === 'EDITOR' ? 'EDITOR' : 'VISUALIZADOR' }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// Importación de utilidades, componentes y servicios necesarios
import { spentCategoryColors, incomeCategoryColors, hasData, hasDataIncome, hasDataSpent, commonOptions, pieOptions, tableColumnsOperations } from "@/utils/global";
import { useMultiplePagination } from "@/utils/usePagination";
import { useAccountingStore } from '@/stores/accountingStore.js';
import { deleteUserAccounting as deleteAccountingApi } from "@/api/accountingAPI";
import { ref, onMounted, nextTick, computed, watch } from "vue";
import { useRouter } from 'vue-router';
import UserManagementModal from "@/components/UserManagementModal.vue";
import IntervalSelector from "@/components/IntervalSelector.vue";
import DataTable from "@/components/DataTable.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import { processFilterSelection } from "@/utils/functions";
import { createChart } from "@/utils/chartService";
import { saveToastMessage } from "@/utils/toastService";

export default {
  name: "SharedAccountings",
  components: { UserManagementModal, IntervalSelector, DataTable, NoDataMessage },
  setup() {
    // Uso del store de contabilidad
    const accountingStore = useAccountingStore();
    const {
      accountings, sharedAccountings, loadAccountings, accountingId, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpents,
      fetchSpentsInterval, processedIncomes, fetchIncomeInterval, processedSpentsUser, processedIncomesUser, fetchOperationsAsync, operations,
      totalSpentMonth, totalIncomeMonth
    } = accountingStore;

    // Configuraciones de paginación para diferentes conjuntos de datos
    const paginationConfigs = [
      { data: operations, reduced: true },
      { data: processedSpentsUser, reduced: true },
      { data: processedIncomesUser, reduced: true }
    ];

    const paginations = useMultiplePagination(paginationConfigs); // Lógica de paginación personalizada

    const router = useRouter(); // Router para navegación dentro de la aplicación
    const isUserCreator = ref(false); // Variable reactiva para controlar si el usuario es el creador de la contabilidad

    const chartSpents = ref(null); // Referencia para el gráfico de gastos
    const chartIncomes = ref(null); // Referencia para el gráfico de ingresos

    const isUserManagementModalOpen = ref(false); // Control de visibilidad del modal de gestión de usuarios
    const showElement = ref(false); // Variable para mostrar elementos dinámicamente después de cierto evento

    // Configuración dinámica de gráficos y tablas
    const charts = ref([
      { title: "Gastos por Categorías", id: "topCategoriesChart", hasData: computed(() => hasDataSpent.value) },
      { title: "Ingresos por Categorías", id: "topCategoriesIncomeChart", hasData: computed(() => hasDataIncome.value) }
    ]);

    const tables = ref([
      { title: "Detalle de Gastos", headers: ["Usuario", "Cantidad"], data: computed(() => processedSpentsUser.value), hasData: computed(() => hasDataSpent.value) },
      { title: "Detalle de Ingresos", headers: ["Usuario", "Cantidad"], data: computed(() => processedIncomesUser.value), hasData: computed(() => hasDataIncome.value) }
    ]);

    // Función ejecutada al montar el componente
    onMounted(async () => {
      await loadAccountings(); // Carga las contabilidades
      calculateIsUserCreator(); // Calcula si el usuario es el creador de la contabilidad
      await fetchUsersAccountingAsync(accountingId.value); // Obtiene los usuarios asociados a la contabilidad
      await updateData(); // Actualiza los datos iniciales
    });

    // Función para actualizar los datos basados en la selección de intervalo
    const updateData = async (selection) => {
      if (!selection || !selection.interval) {
        return;
      }

      setTimeout(() => {
        showElement.value = true; // Muestra elementos después de cierto tiempo
      }, 1000);

      const { filterType, startDate, endDate } = processFilterSelection(selection); // Procesa la selección de filtro

      // Obtiene y actualiza datos de gastos, ingresos y operaciones
      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);

      // Actualiza las variables de estado de disponibilidad de datos
      hasDataSpent.value = (totalSpentMonth.value > 0);
      hasDataIncome.value = (totalIncomeMonth.value > 0);
      hasData.value = totalSpentMonth.value > 0 || totalIncomeMonth.value > 0;

      await nextTick(); // Espera a la próxima actualización de ciclo para ejecutar acciones
      initChart(); // Inicializa el gráfico de gastos
      initIncomeChart(); // Inicializa el gráfico de ingresos
    };

    // Observador para actualizar los usuarios de contabilidad al cerrar el modal de gestión de usuarios
    watch(isUserManagementModalOpen, async (newVal) => {
      if (newVal === false) {
        await fetchUsersAccountingAsync(accountingId.value); // Vuelve a obtener los usuarios de contabilidad
      }
    });

    // Función para abrir el modal de gestión de usuarios
    const openUserManagementModal = () => {
      isUserManagementModalOpen.value = true;
    };

    // Función para cerrar el modal de gestión de usuarios
    const closeModal = () => {
      isUserManagementModalOpen.value = false;
    };

    // Calcula si el usuario actual es el creador de la contabilidad
    const calculateIsUserCreator = () => {
      const sharedAccounting = sharedAccountings.value.find(accounting => accounting.id === Number(accountingId.value));
      isUserCreator.value = sharedAccounting ? sharedAccounting.userCreator === localStorage.getItem('username') : false;
      console.log(sharedAccounting);
      console.log(sharedAccounting.userCreator);
      console.log(localStorage.getItem('username'));
    };

    // Función para eliminar la contabilidad
    const deleteAccounting = async () => {
      if (confirm("¿Estás seguro de que quieres eliminar esta contabilidad?")) {
        try {
          await deleteAccountingApi(accountingId.value, localStorage.getItem('username')); // Elimina la contabilidad usando la API
          saveToastMessage('success', 'Contabilidad eliminada'); // Muestra un mensaje de éxito
          router.push({
            path: '/home-user',
            query: { id: localStorage.getItem('personalAccountingId') }
          }).then(() => {
            // Añade un pequeño retraso para asegurar que la redirección se complete
            setTimeout(() => {
              location.reload();
            }, 100); // Puedes ajustar este tiempo si es necesario
          });
        } catch (error) {
          saveToastMessage('error', 'Ocurrió un error al eliminar la contabilidad'); // Muestra un mensaje de éxito
        }
      }
    };

    // Inicializa el gráfico de gastos
    const initChart = () => {
      showElement.value = false;
      const spentCtx = document.getElementById('topCategoriesChart'); // Obtiene el contexto del gráfico de gastos
      if (spentCtx) {
        if (chartSpents.value) chartSpents.value.destroy(); // Destruye cualquier gráfico anterior
        const pieData = {
          labels: processedSpents.value.map(s => s.category), // Etiquetas para los datos del gráfico
          datasets: [{
            data: processedSpents.value.map(s => s.total), // Datos del gráfico
            backgroundColor: spentCategoryColors, // Colores de fondo de los datos
          }]
        };
        chartSpents.value = createChart(spentCtx, 'pie', pieData, commonOptions, pieOptions); // Crea el gráfico de tipo 'pie'
      }
    };

    // Inicializa el gráfico de ingresos
    const initIncomeChart = () => {
      showElement.value = false;
      const incomeCtx = document.getElementById('topCategoriesIncomeChart'); // Obtiene el contexto del gráfico de ingresos
      if (incomeCtx) {
        if (chartIncomes.value) chartIncomes.value.destroy(); // Destruye cualquier gráfico anterior
        const pieData = {
          labels: processedIncomes.value.map(s => s.category), // Etiquetas para los datos del gráfico
          datasets: [{
            data: processedIncomes.value.map(s => s.total), // Datos del gráfico
            backgroundColor: incomeCategoryColors, // Colores de fondo de los datos
          }]
        };
        chartIncomes.value = createChart(incomeCtx, 'pie', pieData, commonOptions, pieOptions); // Crea el gráfico de tipo 'pie'
      }
    };

    // Retorna propiedades y funciones para su uso en el componente
    return {
      accountings, sharedAccountings, isUserCreator, deleteAccounting, usersAccounting, accountingSharedSelected, processedSpents, processedIncomes,
      hasDataSpent, hasDataIncome, hasData, processedSpentsUser, processedIncomesUser, isUserManagementModalOpen, openUserManagementModal, closeModal,
      incomeCategoryColors, spentCategoryColors, paginations, showElement, charts, tables, updateData, operations, tableColumnsOperations,
      tableSpentUser: [{ key: "username", label: "Usuario" }, { key: "total", label: "Cantidad" }]
    };
  }
}
</script>

<style scoped>
/* Estilos para la lista de usuarios */
.user-list {
  margin-bottom: 0; /* Elimina el margen inferior si es necesario */
}

.list-group-item {
  background-color: #f8f9fa; /* Color de fondo ligero para cada elemento */
  border-color: #dee2e6; /* Color del borde */
}

.list-group-item:nth-child(odd) {
  background-color: #e9ecef; /* Alternar colores de fondo para mejor legibilidad */
}
</style>

