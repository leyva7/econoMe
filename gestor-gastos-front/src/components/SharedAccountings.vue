<template>
  <div class="container mt-5 text-center">
    <div class="row mb-4">
      <div class="col-12 d-flex justify-content-between align-items-center">
        <h3 v-if="accountingSharedSelected.length > 0">
          {{ accountingSharedSelected[0].description }}
        </h3>
        <h3 v-else>No hay datos</h3>

        <div v-if="isUserCreator" class="my-3 button-group">
          <button @click="openUserManagementModal" class="btn btn-primary me-2">Gestionar usuarios</button>
          <button @click="deleteAccounting" class="btn btn-danger">Eliminar Contabilidad</button>
        </div>

        <UserManagementModal :isVisible="isUserManagementModalOpen" @update:isVisible="closeModal" />

      </div>
    </div>

    <div class="row g-4">
      <!-- Gráficos de Categorías y Últimas Operaciones -->
      <div class="col-12 col-lg-4 mb-2">
        <div class="card h-100">
          <h4 class="card-header">Gastos por Categorías</h4>
          <div class="card-body">
            <div class="chart-container">
              <canvas id="topCategoriesChart"></canvas>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-4 mb-2">
        <div class="card h-100">
          <h4 class="card-header">Ingresos por Categorías</h4>
          <div class="card-body">
            <div class="chart-container">
              <canvas id="topCategoriesIncomeChart"></canvas>
            </div>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-4 mb-2">
        <div class="card h-100">
          <h4 class="card-header">Últimas Operaciones</h4>
          <div class="card-body">
            <!-- Tabla de Últimas Operaciones -->
            <table class="table">
              <thead>
              <tr>
                <th>Categoría</th>
                <th>Cantidad</th>
                <th>Tipo</th>
                <th>Fecha</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="operation in latestOperations" :key="operation.id">
                <td>{{ operation.category }}</td>
                <td>{{ operation.quantity.toFixed(2) }}</td>
                <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
                <td>{{ operation.date }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <div class="row g-4">
      <!-- Detalles de Gastos, Detalles de Ingresos, Lista de Usuarios -->
      <div class="col-12 col-lg-4">
        <div class="card h-100">
          <h4 class="card-header">Detalle de Gastos</h4>
          <div class="card-body">
            <!-- Tabla de Detalles de Gastos -->
            <table class="table">
              <thead>
              <tr>
                <th>Categoría</th>
                <th>Total</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(spent, index) in processedSpents" :key="spent.category">
                <td>
                  <span class="category-color" :style="{ backgroundColor: categorySpentColors[index] }"></span>
                  {{ spent.category }}
                </td>
                <td>{{ spent.total.toFixed(2) }}</td>
              </tr>
              </tbody>
            </table>
            <table class="table">
              <thead>
              <tr>
                <th>Usuario</th>
                <th>Cantidad</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="spent in processedSpentsUser" :key="spent.id">
                <td>{{ spent.username }}</td>
                <td>{{ spent.total }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-4">
        <div class="card h-100">
          <h4 class="card-header">Detalle de Ingresos</h4>
          <div class="card-body">
            <!-- Tabla de Detalles de Ingresos -->
            <table class="table">
              <thead>
              <tr>
                <th>Categoría</th>
                <th>Total</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(income, index) in processedIncomes" :key="income.category">
                <td>
                  <span class="category-color" :style="{ backgroundColor: categoryIncomeColors[index] }"></span>
                  {{ income.category }}
                </td>
                <td>{{ income.total.toFixed(2) }}</td>
              </tr>
              </tbody>
            </table>
            <table class="table">
              <thead>
              <tr>
                <th>Usuario</th>
                <th>Cantidad</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="income in processedIncomesUser" :key="income.id">
                <td>{{ income.username }}</td>
                <td>{{ income.total }}</td>
              </tr>
              </tbody>
            </table>
          </div>
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
import { useAccountingStore } from '@/stores/accountingStore.js';
import {deleteUserAccounting as deleteAccountingApi} from "@/service/accountingService"
import {ref, onMounted, nextTick} from "vue";
import { useRouter } from 'vue-router';
import {Chart} from "chart.js";
import UserManagementModal from "@/components/UserManagementModal.vue";
export default {
  name: "SharedAccountings",
  components: {
    UserManagementModal,
  },
  setup() {
    const accountingStore = useAccountingStore();
    const {accountings, sharedAccountings, fetchAccountingsAsync, accountingId, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpents,
      spentsMonths, fetchSpentsMonthsAsync, processedIncomes, incomesMonths, fetchIncomeMonthsAsync, processedSpentsUser, processedIncomesUser, latestOperations, fetchOperationsAsync} = accountingStore;
    const router = useRouter();
    const isUserCreator = ref(false);
    const chart = ref(null);
    const chartIncomes = ref(null);
    const hasDataSpent = ref(false);
    const hasDataIncome = ref(false);
    const hasDataOperations = ref(false);

    const isUserManagementModalOpen = ref(false);

    onMounted(async () => {
      await fetchAccountingsAsync();
      calculateIsUserCreator();
      await fetchUsersAccountingAsync(accountingId.value);
      await fetchSpentsMonthsAsync(accountingId.value);
      await fetchIncomeMonthsAsync(accountingId.value);
      await fetchOperationsAsync(accountingId.value);
      hasDataSpent.value = spentsMonths.value.length > 0;
      hasDataIncome.value = incomesMonths.value.length > 0;
      hasDataOperations.value = latestOperations.value.length > 0;
      nextTick(() => {
        if (hasDataSpent.value) {
          initChart();
        }
        if (hasDataIncome.value){
          initIncomeChart();
        }
      });
    });

    const openUserManagementModal = () => {
      isUserManagementModalOpen.value = true;
    };

    const closeModal = () => {
      isUserManagementModalOpen.value = false;
    };

    const calculateIsUserCreator = () => {
      const sharedAccounting = sharedAccountings.value.find(accounting => accounting.id === Number(accountingId.value));
      if (sharedAccounting) {
        isUserCreator.value = sharedAccounting.userCreator === localStorage.getItem('username');
      } else {
        isUserCreator.value = false;
      }
    };

    const deleteAccounting = async () => {
      if (confirm("¿Estás seguro de que quieres eliminar esta contabilidad?")) {
        try {
          await deleteAccountingApi(accountingId.value, localStorage.getItem('username'));
          console.log("Eliminada la contabilidad");
          // Recargar las contabilidades para actualizar el sidebar
          await fetchAccountingsAsync();
          router.push({
            path: '/home-user', // Utiliza 'path' en lugar de 'name'
            query: { id: localStorage.getItem('personalAccountingId') }
          });
          window.location.reload();
        } catch (error) {
          console.error("Error al eliminar la contabilidad:", error);
        }
      }
    };

    const initChart = () => {
      const ctx = document.getElementById('topCategoriesChart');
      if (ctx && chart.value) {
        chart.value.destroy();
      }
      if (ctx) {
        chart.value = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: processedSpents.value.map(({ category }) => category),
            datasets: [{
              data: processedSpents.value.map(({ total }) => total),
              backgroundColor: ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be'],
            }]
          },
          options: {
            scales: {
              y: {
                display: false,
              },
              x: {
                display: false,
              }
            },
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false,
                position: 'top',
                align: 'center',
                padding: '100',
                labels: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en la leyenda
                  }
                }
              },
            }
          },
        });
      }
    };

    const initIncomeChart = () => {
      const ctx = document.getElementById('topCategoriesIncomeChart');
      if (ctx && chartIncomes.value) {
        chartIncomes.value.destroy(); // Destruye el gráfico anterior si existe
      }
      if (ctx) {
        chartIncomes.value = new Chart(ctx, {
          type: 'pie', // Cambia 'pie' por 'bar'
          data: {
            labels: processedIncomes.value.map(({ category }) => category), // Categorías
            datasets: [{
              label: 'Total por categoría',
              data: processedIncomes.value.map(({ total }) => total), // Totales
              backgroundColor: [
                '#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be'
              ]
            }]
          },
          options: {
            scales: {
              y: {
                display: false,
                beginAtZero: true,
                ticks: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en el eje Y
                  }
                }
              },
              x: {
                display:false,
                ticks: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en el eje X
                  }
                }
              }
            },
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false,
                position: 'top',
                align: 'center',
                padding: '100',
                labels: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en la leyenda
                  }
                }
              },
            }
          }
        });
      }
    };

    return {
      accountings,
      sharedAccountings,
      isUserCreator, deleteAccounting,
      usersAccounting,
      accountingSharedSelected,
      processedSpents,
      spentsMonths,
      processedIncomes,
      incomesMonths,
      hasDataSpent, hasDataIncome, hasDataOperations,
      processedSpentsUser,
      processedIncomesUser,
      latestOperations,
      isUserManagementModalOpen, openUserManagementModal, closeModal,
      categorySpentColors: ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be'],
      categoryIncomeColors: ['#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be']
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


