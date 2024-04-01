<template>
  <div class="header-container">
    <h3 v-if="accountingSharedSelected.length > 0" class="accounting-description">{{ accountingSharedSelected[0].description }}</h3>
    <h3 v-else>No hay datos</h3>
    <!-- Opciones de gestión para el creador flotantes -->
    <div class="floating-management-buttons">
      <UserManagementModal :isVisible="isModalOpen" @close="isModalOpen = false"/>
      <div v-if="isUserCreator" class="management-buttons">
        <button @click="openUserManagement">Gestionar Usuarios</button>
        <button @click="deleteAccounting">Eliminar Contabilidad</button>
      </div>
    </div>
  </div>
  <div class="shared-accounting-details">
    <div v-if="hasDataSpent" class="spents-container">
      <p class="title-shared">Gastos</p>
      <canvas id="topCategoriesChart"></canvas>
      <!-- Tabla de processedSpentsUser debajo del gráfico -->
      <div class="table-container">
        <p class="title-shared">Detalle de Gastos</p>
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
    <h3 v-else>No hay datos</h3>
    <div v-if="hasDataIncome" class="income-container">
      <p class="title-shared">Ingresos</p>
      <canvas id="topCategoriesIncomeChart"></canvas>
      <!-- Tabla de processedIncomesUser debajo del gráfico -->
      <div class="table-container">
        <p class="title-shared">Detalle de Ingresos</p>
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
    <h3 v-else>No hay datos</h3>
    <div class="operations-users-container">
      <div v-if="hasDataOperations" class="operations-container">
        <p class="title-shared">Últimas operaciones</p>
        <table class="table">
          <thead>
          <tr>
            <th>Categoría</th>
            <th>Cantidad</th>
            <th>Tipo de operación</th>
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
      <h3 v-else>No hay datos</h3>
      <!-- Container para la lista de usuarios movido aquí -->
      <section class="user-list-container">
        <h3>Usuarios Actuales</h3>
        <ul class="user-list">
          <li v-for="(user, index) in usersAccounting" :key="index">
            {{ user.username}} {{user.role}}
          </li>
        </ul>
      </section>
    </div>
  </div>
</template>

<script>
import { useAccountingStore } from '@/stores/accountingStore.js';
import {deleteUserAccounting as deleteAccountingApi} from "@/service/accountingService"
import {ref, onMounted, nextTick} from "vue";
import UserManagementModal from './UserManagementModal.vue';
import { useRouter } from 'vue-router';
import {Chart} from "chart.js";
export default {
  name: "SharedAccountings",
  components: {
    UserManagementModal
  },
  setup() {
    const accountingStore = useAccountingStore();
    const {accountings, sharedAccountings, fetchAccountingsAsync, accountingId, fetchUsersAccountingAsync, usersAccounting, accountingSharedSelected, processedSpents,
      spentsMonths, fetchSpentsMonthsAsync, processedIncomes, incomesMonths, fetchIncomeMonthsAsync, processedSpentsUser, processedIncomesUser, latestOperations, fetchOperationsAsync} = accountingStore;
    const router = useRouter();
    const isUserCreator = ref(false);
    const isModalOpen = ref(false); // Añadido para controlar la visibilidad del modal
    const chart = ref(null);
    const chartIncomes = ref(null);
    const hasDataSpent = ref(false);
    const hasDataIncome = ref(false);
    const hasDataOperations = ref(false);

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

    const calculateIsUserCreator = () => {
      const sharedAccounting = sharedAccountings.value.find(accounting => accounting.id === Number(accountingId.value));
      if (sharedAccounting) {
        isUserCreator.value = sharedAccounting.userCreator === localStorage.getItem('username');
      } else {
        isUserCreator.value = false;
      }
    };

    const openUserManagement = () => {
      isModalOpen.value = true; // Abre el modal
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
            plugins: {
              legend: {
                display: false,
                position: 'left',
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
      console.log(ctx);
      if (ctx && chartIncomes.value) {
        chartIncomes.value.destroy(); // Destruye el gráfico anterior si existe
      }
      if (ctx) {
        chartIncomes.value = new Chart(ctx, {
          type: 'bar', // Cambia 'pie' por 'bar'
          data: {
            labels: processedIncomes.value.map(({ category }) => category), // Categorías
            datasets: [{
              label: 'Total por categoría',
              data: processedIncomes.value.map(({ total }) => total), // Totales
              backgroundColor: [
                '#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be'
              ],
              borderColor: [
                '#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be'
              ],
              borderWidth: 1
            }]
          },
          options: {
            scales: {
              y: {
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
            plugins: {
              legend: {
                display: false // Puedes cambiarlo a true si deseas mostrar la leyenda
              }
            }
          }
        });
      }
    };

    return {
      accountings,
      sharedAccountings,
      isUserCreator, openUserManagement, deleteAccounting, isModalOpen,
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
      categorySpentColors: ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be'],
      categoryIncomeColors: ['#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be']
    };
  }
}
</script>

<style scoped>

.header-container {
  height: 5%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  gap: 20px;
}

.accounting-description {
  font-size: 1.5rem;
  text-align: left;
  margin: 0;
  font-weight: bold;
  color: #2C3E50;
  flex-grow: 1;
  width: 70%;
}

.shared-accounting-details {
  display: flex;
  flex-direction: row;
  align-items: flex-start; /* Ajuste para alinear el contenido al inicio */
  padding: 20px;
  gap: 20px;
  overflow: hidden; /* Para evitar desbordamiento */
  height: calc(95% - 80px);
}

.spents-container, .income-container, .operations-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 10px;
  background-color: #ffffff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  height: calc(100% - 16px);
  overflow-y: auto; /* Permite desplazamiento vertical si necesario */
}

.operations-users-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: calc(100% - 16px);
}

.title-shared {
  font-size: x-large;
}

.table {
  width: 100%;
  margin-top: 15px;
}

.table th, .table td {
  border: 1px solid #ccc;
  padding: 2px;
  text-align: center;
}

.table th {
  background-color: var(--pickled-bluewood-300);
  color: #000000;
}

.category-color {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 4px;
  vertical-align: middle;
}

.management-buttons {
  display: flex;
  gap: 10px;
}

.management-buttons button {
  width: 150px;
  height: 40px;
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f2f2f2;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: background-color 0.3s;
}

.management-buttons button:hover {
  background-color: #e9e9e9;
}

.management-buttons button:active {
  background-color: #ddd;
}

.floating-management-buttons {
  display: flex;
  flex-direction: row;
  justify-content: flex-end; /* Alinea los botones a la derecha */
  position: relative; /* Cambio para que no sea absoluto y ocupe espacio en el flujo normal */
  width: 100%; /* Ocupa todo el ancho disponible */
  padding-right: 20px; /* Espaciado para alinear con el contenido principal */
  gap: 10px; /* Espaciado entre botones */
}

.floating-management-buttons button {
  width: 150px;
  height: 40px;
  padding: 5px 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f2f2f2;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  transition: background-color 0.3s;
}

.user-list-container {
  max-width: 100%; /* Ajuste para ocupar el espacio disponible */
  overflow-y: auto;
  overflow-x: hidden;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f2f2f2;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.user-list {
  width: 100%;
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-list li {
  width: 100%;
  padding: 10px;
  margin: 5px 0;
  background-color: #ecf0f1;
  border-radius: 5px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  text-align: center;
}

#topCategoriesChart, #topCategoriesIncomeChart {
  width: auto;
  height: 45%; /* Ajuste para que los gráficos ocupen una altura proporcional */
  margin: auto;
}
</style>


