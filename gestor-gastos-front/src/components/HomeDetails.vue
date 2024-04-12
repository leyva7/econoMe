<template>
  <div class="container mt-5">
    <h2 v-if="hasData" class="text-center mb-4">Resumen</h2>
    <h2 v-else class="text-center mb-4">No hay datos disponibles</h2>

    <div class="filters-container pb-4">
      <select v-if="showElement" v-model="selectedInterval" @change="updateData" class="interval-select">
        <option value="last7days">Últimos 7 días</option>
        <option value="last30days">Últimos 30 días</option>
        <option value="custom">Personalizado</option>
      </select>
      <div v-if="selectedInterval === 'custom'" class="custom-interval-container me-2">
        <input type="date" v-model="customStart" class="date-input" />
        <input type="date" v-model="customEnd" class="date-input" />
        <button @click="updateData" class="update-button">Actualizar</button>
      </div>
    </div>


    <!-- Contenedor para Gastos VS Ingresos y Detalles -->
    <div v-if="hasData" class="row">
      <div class="col-lg-6 mb-3">
        <!-- Gastos VS Ingresos -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Gastos VS Ingresos</h3>
          <div class="chart-container" v-show="hasData">
            <canvas id="comparativeChart"></canvas>
          </div>
        </div>
      </div>

      <div class="col-lg-6 mb-3">
        <!-- Detalles de Ingresos y Gastos -->
        <div class="row">
          <div class="col-12 mb-3">
            <div class="bg-white rounded shadow p-3">
              <h4 class="text-center">Gastos Totales: {{ totalSpentMonth.toFixed(2) }} €</h4>
              <div class="row g-3"> <!-- g-3 proporciona un espaciado/gap entre las columnas -->
                <div class="col-md-6">
                  <div class="card text-center">
                    <div class="card-body">
                      <h5 class="card-title">
                        {{ Object.keys(categoriesDifferences.spentDifferences)[0] }}
                      </h5>
                      <div class="card-text">
                        <div class="arrow-up arrow-up-spent"></div><h5>+{{categoriesDifferences.spentDifferences[Object.keys(categoriesDifferences.spentDifferences)[0]]}}</h5>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="card text-center">
                    <div class="card-body">
                      <h5 class="card-title">
                        {{ Object.keys(categoriesDifferences.spentDifferences)[1] }}
                      </h5>
                      <div class="card-text font-weight-bold">
                        <div class="arrow-down arrow-down-spent"></div><h5>{{categoriesDifferences.spentDifferences[Object.keys(categoriesDifferences.spentDifferences)[1]]}}</h5>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12">
            <div class="bg-white rounded shadow p-3">
              <h4 class="text-center">Ingresos Totales: {{ totalIncomeMonth.toFixed(2) }} €</h4>
              <div class="row g-3">
              <div class="col-md-6">
                <div class="card text-center">
                  <div class="card-body">
                    <h5 class="card-title">
                      {{ Object.keys(categoriesDifferences.incomeDifferences)[0] }}
                    </h5>
                    <div class="card-text">
                      <div class="arrow-up arrow-up-income"></div><h5>+{{categoriesDifferences.incomeDifferences[Object.keys(categoriesDifferences.incomeDifferences)[0]]}}</h5>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card text-center">
                  <div class="card-body">
                    <h5 class="card-title">
                      {{ Object.keys(categoriesDifferences.incomeDifferences)[1] }}
                    </h5>
                    <div class="card-text font-weight-bold">
                      <div class="arrow-down arrow-down-income"></div><h5>{{categoriesDifferences.incomeDifferences[Object.keys(categoriesDifferences.incomeDifferences)[1]]}}</h5>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Últimas operaciones -->
    <div v-if="hasData" class="mt-3">
      <div class="bg-white rounded shadow p-3">
        <h3 class="text-center mb-3">Últimas operaciones</h3>
        <!-- Tabla de Últimas Operaciones -->
        <div class="table-container table-responsive">
          <table class="table">
            <thead>
            <tr>
              <th>Tipo de operación</th>
              <th>Categoría</th>
              <th>Cantidad</th>
              <th>Fecha</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="operation in paginatedOperations" :key="operation.id">
              <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
              <td>{{ operation.category }}</td>
              <td>{{ operation.quantity.toFixed(2) }}</td>
              <td>{{ operation.date }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination-container d-flex justify-content-center mb-4">
          <button @click="prevPage" class="btn btn-secondary me-2" :disabled="currentPage <= 1">Anterior</button>
          <span class="me-2">Página {{ currentPage }} de {{ totalPages }}</span>
          <button @click="nextPage" class="btn btn-secondary" :disabled="currentPage >= totalPages">Siguiente</button>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>

import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick, computed, inject} from 'vue';
import { useAccountingStore } from '../stores/accountingStore';

Chart.register(...registerables);
export default {
  name: "HomeDetails",
  setup() {
    const { accountingId, fetchSpentsInterval, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, operations, fetchOperationsAsync, formatAsYYYYMMDD } = useAccountingStore();
    const chart = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

    const showElement = ref(false);

    const selectedInterval = ref('last7days'); // Valor predeterminado
    const customStart = ref('');
    const customEnd = ref('');

    const currentPage = ref(1);
    const operationsPerPage = 7;

    const accountingIID = inject('accountingId');

    onMounted(async () => {
      updateData();
      hasData.value = totalSpentMonth.value > 0;
    });

    const updateData = async () => {
      console.log(accountingIID);
      setTimeout(() => {
        showElement.value = true; // Mostramos el componente después del retraso
      }, 1000);
      let filterType;
      let startDate = formatAsYYYYMMDD(customStart.value);
      let endDate = formatAsYYYYMMDD(customEnd.value);

      if (selectedInterval.value === 'custom') {
        if (!customStart.value || !customEnd.value) {
          return;
        }
        startDate = formatAsYYYYMMDD(customStart.value);
        endDate = formatAsYYYYMMDD(customEnd.value);
        filterType = 'custom';
      } else {
        filterType = selectedInterval.value;
      }

      console.log(`Fetching with accountingId: ${accountingId.value}, filterType: ${filterType}, startDate: ${startDate}, endDate: ${endDate}`);

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeMonthsAsync(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);
      await categoryDifferencesAsync(accountingId.value, filterType, startDate, endDate);

      console.log("Category differences:", categoriesDifferences.value);

      hasData.value = (totalSpentMonth.value > 0 || Object.keys(categoriesDifferences.spentDifferences).length > 0 || Object.keys(categoriesDifferences.incomeDifferences).length > 0);

      await nextTick();
      initChart();
    };

    const totalPages = computed(() => Math.ceil(operations.value.length / operationsPerPage));

    const paginatedOperations = computed(() => {
      const start = (currentPage.value - 1) * operationsPerPage;
      const end = start + operationsPerPage;
      return operations.value.slice(start, end);
    });

    const nextPage = () => {
      if (currentPage.value < totalPages.value) currentPage.value++;
    };

    const prevPage = () => {
      if (currentPage.value > 1) currentPage.value--;
    };

    const initChart = () => {
      showElement.value = false;
      const ctx = document.getElementById('comparativeChart');
      if (ctx && ctx.getContext) {
        if (chart.value) {
          chart.value.destroy();
        }
        const spentTotal = totalSpentMonth.value;
        const incomeTotal = totalIncomeMonth.value;

        chart.value = new Chart(ctx, {
          type: 'pie',
          data: {
            labels: ['Ingresos', 'Gastos'],
            datasets: [{
              data: [incomeTotal, spentTotal],
              backgroundColor: ['#297243', '#a01414'],
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'top',
                labels: {
                  color: 'black',
                  font: {
                    size: 16
                  }
                }
              },
            }
          },
        });
      }
    };


    return {
      accountingId, hasData, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, fetchOperationsAsync, operations,
      selectedInterval,
      customStart,
      customEnd,
      updateData,
      showElement,
      paginatedOperations, totalPages, currentPage, nextPage, prevPage
    };
  },
};

</script>

<style scoped>

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50px; /* Ajusta según necesites */
  margin-top: 20px; /* Asegura un margen consistente desde la tabla */
}

.table-container {
  min-height: 250px; /* Ajusta a la altura mínima que prefieras para la tabla */
}

</style>