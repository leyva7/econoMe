<template>
  <div class="container mt-5 text-center">
    <div class="row mb-4">
      <div class="col-12 d-flex justify-content-between align-items-center">
        <h3>{{ accountingSharedSelected.length > 0 ? accountingSharedSelected[0].description : 'No hay datos' }}</h3>

        <div v-if="isUserCreator" class="my-3 button-group">
          <button @click="openUserManagementModal" class="btn btn-primary me-2">Gestionar usuarios</button>
          <button @click="deleteAccounting" class="btn btn-danger">Eliminar Contabilidad</button>
        </div>

        <UserManagementModal :isVisible="isUserManagementModalOpen" @update:isVisible="closeModal" />

      </div>
    </div>

    <div class="row g-4">
      <IntervalSelector :isVisible="showElement" @update-selection="updateData" />
      <!-- Gráficos de Categorías y Últimas Operaciones -->
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

      <div class="col-12 col-lg-4 mb-2">
        <div class="card h-100">
          <h4 class="card-header">Últimas Operaciones</h4>
          <div v-if="hasData" class="card-body">
            <!-- Tabla de Últimas Operaciones -->
            <DataTable :pagination="paginations[0]" :columns="tableColumnsOperations" />
          </div>
          <NoDataMessage v-else/>
        </div>
      </div>

    </div>

    <div class="row g-4">
      <!-- Detalles de Gastos, Detalles de Ingresos-->
      <div class="col-12 col-lg-4" v-for="(table, index) in tables" :key="index">
        <div class="card h-100">
          <h4 class="card-header">{{ table.title }}</h4>
          <div v-if="table.hasData" class="card-body">
            <DataTable :pagination="paginations[index+1]" :columns="tableSpentUser" />
          </div>
          <NoDataMessage v-else />
        </div>
      </div>

      <div class="col-12 col-lg-4">
        <div class="card h-100">
          <h4 class="card-header">Usuarios</h4>
          <div class="card-body">
            <ul class="user-list list-group">
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
import { spentCategoryColors, incomeCategoryColors, hasData, hasDataIncome, hasDataSpent, commonOptions, pieOptions, tableColumnsOperations } from "@/utils/global";
import { useMultiplePagination } from "@/utils/usePagination";
import { useAccountingStore } from '@/stores/accountingStore.js';
import { deleteUserAccounting as deleteAccountingApi } from "@/api/accountingAPI";
import {ref, onMounted, nextTick, computed, watch} from "vue";
import { useRouter } from 'vue-router';
import UserManagementModal from "@/components/UserManagementModal.vue";
import IntervalSelector from "@/components/IntervalSelector.vue";
import DataTable from "@/components/DataTable.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import { processFilterSelection } from "@/utils/functions";
import { createChart } from "@/utils/chartService";
import {saveToastMessage} from "@/utils/toastService";

export default {
  name: "SharedAccountings",
  components: { UserManagementModal, IntervalSelector, DataTable, NoDataMessage },
  setup() {
    const accountingStore = useAccountingStore();
    const {
      accountings, sharedAccountings, loadAccountings, accountingId, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpents,
      fetchSpentsInterval, processedIncomes, fetchIncomeInterval, processedSpentsUser, processedIncomesUser, fetchOperationsAsync, operations,
      totalSpentMonth, totalIncomeMonth
    } = accountingStore;

    const paginationConfigs = [
      { data: operations, reduced: true },
      { data: processedSpentsUser, reduced: true },
      { data: processedIncomesUser, reduced: true }
    ];

    const paginations = useMultiplePagination(paginationConfigs);

    const router = useRouter();
    const isUserCreator = ref(false);

    const chartSpents = ref(null);
    const chartIncomes = ref(null);

    const isUserManagementModalOpen = ref(false);
    const showElement = ref(false);

    const charts = ref([
      { title: "Gastos por Categorías", id: "topCategoriesChart", hasData: computed(() => hasDataSpent.value) },
      { title: "Ingresos por Categorías", id: "topCategoriesIncomeChart", hasData: computed(() => hasDataIncome.value) }
    ]);

    const tables = ref([
      { title: "Detalle de Gastos", headers: ["Usuario", "Cantidad"], data: computed(() => processedSpentsUser.value), hasData: computed(() => hasDataSpent.value) },
      { title: "Detalle de Ingresos", headers: ["Usuario", "Cantidad"], data: computed(() => processedIncomesUser.value), hasData: computed(() => hasDataIncome.value) }
    ]);

    onMounted(async () => {
      await loadAccountings();
      calculateIsUserCreator();
      await fetchUsersAccountingAsync(accountingId.value);
      await updateData();
    });

    const updateData = async (selection) => {
      if (!selection || !selection.interval) {
        return;
      }

      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      const { filterType, startDate, endDate } = processFilterSelection(selection);

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);

      hasDataSpent.value = (totalSpentMonth.value > 0);
      hasDataIncome.value = (totalIncomeMonth.value > 0);
      hasData.value = totalSpentMonth.value > 0 || totalIncomeMonth.value > 0;

      await nextTick();
      initChart();
      initIncomeChart();
    };

    watch(isUserManagementModalOpen, async (newVal) => {
      if (newVal === false) {
        await fetchUsersAccountingAsync(accountingId.value);
      }
    });

    const openUserManagementModal = () => {
      isUserManagementModalOpen.value = true;
    };

    const closeModal = () => {
      isUserManagementModalOpen.value = false;
    };

    const calculateIsUserCreator = () => {
      const sharedAccounting = sharedAccountings.value.find(accounting => accounting.id === Number(accountingId.value));
      isUserCreator.value = sharedAccounting ? sharedAccounting.userCreator === localStorage.getItem('username') : false;
    };

    const deleteAccounting = async () => {
      if (confirm("¿Estás seguro de que quieres eliminar esta contabilidad?")) {
        try {
          await deleteAccountingApi(accountingId.value, localStorage.getItem('username'));
          console.log("Eliminada la contabilidad");
          router.push({
            path: '/home-user',
            query: { id: localStorage.getItem('personalAccountingId') }
          }).then(() => {
            // Añade un pequeño retraso para asegurar que la redirección se complete
            setTimeout(() => {
              location.reload();
            }, 100); // Puedes ajustar este tiempo si es necesario
          });
          saveToastMessage('success', 'Contabilidad eliminada con éxito');
          await loadAccountings();
        } catch (error) {
          console.error("Error al eliminar la contabilidad:", error);
        }
      }
    };

    const initChart = () => {
      showElement.value = false;
      const spentCtx = document.getElementById('topCategoriesChart');
      if (spentCtx) {
        if (chartSpents.value) chartSpents.value.destroy();
        const pieData = {
          labels: processedSpents.value.map(s => s.category),
          datasets: [{
            data: processedSpents.value.map(s => s.total),
            backgroundColor: spentCategoryColors,
          }]
        };
        chartSpents.value = createChart(spentCtx, 'pie', pieData, commonOptions, pieOptions);
      }
    };

    const initIncomeChart = () => {
      showElement.value = false;
      const incomeCtx = document.getElementById('topCategoriesIncomeChart');
      if (incomeCtx) {
        if (chartIncomes.value) chartIncomes.value.destroy();
        const pieData = {
          labels: processedIncomes.value.map(s => s.category),
          datasets: [{
            data: processedIncomes.value.map(s => s.total),
            backgroundColor: incomeCategoryColors,
          }]
        };
        chartIncomes.value = createChart(incomeCtx, 'pie', pieData, commonOptions, pieOptions);
      }
    };

    return {
      accountings, sharedAccountings, isUserCreator, deleteAccounting, usersAccounting, accountingSharedSelected, processedSpents, processedIncomes, hasDataSpent, hasDataIncome, hasData, processedSpentsUser, processedIncomesUser, isUserManagementModalOpen, openUserManagementModal, closeModal, incomeCategoryColors, spentCategoryColors, paginations, showElement, charts, tables, updateData, operations, tableColumnsOperations, tableSpentUser: [{ key: "username", label: "Usuario" }, { key: "total", label: "Cantidad" }]
    };
  }
}
</script>

<style scoped>

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


