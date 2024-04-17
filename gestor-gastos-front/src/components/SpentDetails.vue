<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">{{ hasDataSpents ? 'Gasto total: ' + totalSpentMonth.toFixed(2) + ' €' : 'No hay datos disponibles' }}</h2>

    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <div v-if="hasDataSpents" class="row g-4">
      <!-- Gráfico por Categorías y Últimos Gastos en pantallas grandes; se apilan en pantallas más pequeñas -->
      <div class="col-12 col-lg-6">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Gastos por categorías</h3>
          <div class="chart-container">
            <canvas id="topCategoriesChart"></canvas>
          </div>
          <!-- Tabla de categorías justo debajo del gráfico -->
          <table class="table mt-3">
            <thead>
            <tr>
              <th>Categoría</th>
              <th>Total</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="(spent, index) in processedSpents" :key="spent.category">
              <td>
                <span class="category-color" :style="{ backgroundColor: spentCategoryColors[index] }"></span>
                {{ spent.category }}
              </td>
              <td>{{ spent.total.toFixed(2) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="col-12 col-lg-6">
        <!-- Evolución de los Gastos -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Evolución de gastos</h3>
          <div class="chart-container">
            <canvas id="lineChart"></canvas>
          </div>
        </div>
      </div>
      <div v-if="hasDataSpents" class="mt-3 col-12">
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
    <!-- Mensaje si no hay datos -->
    <p v-else class="text-center">No hay datos disponibles</p>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import {onMounted, ref, nextTick} from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import { spentCategoryColors, hasDataSpents, commonOptions, pieOptions } from "@/utils/global";
import {createChart} from "@/utils/chartService";
import {processFilterSelection} from "@/utils/functions";
import {usePagination} from "@/utils/usePagination";
import IntervalSelector from "./IntervalSelector.vue";


Chart.register(...registerables);

export default {
  name: "SpentDetails",
  components:{ IntervalSelector },
  setup() {
    const { accountingId, processedSpents, fetchSpentsInterval, spentsFiltered, totalSpentMonth, latestSpents, dailySpentData, processDailySpentData} = useAccountingStore();
    const { currentPage, totalPages, paginatedOperations, nextPage, prevPage } = usePagination(spentsFiltered);
    const chart = ref(null);
    const linesChart = ref(null);

    const showElement = ref(false);

    onMounted(async () => {
      updateData();
    });

    const updateData = async (selection) => {

      if (!selection || !selection.interval) {
        return;
      }

      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      const { filterType, startDate, endDate } = processFilterSelection(selection);

      console.log(`Fetching with accountingId: ${accountingId.value}, filterType: ${filterType}, startDate: ${startDate}, endDate: ${endDate}`);

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);

      hasDataSpents.value = (totalSpentMonth.value > 0);

      await nextTick();
      initChart();
      initLineChart();
    };

    const initChart = () => {
      showElement.value = false;
      const pieCtx = document.getElementById('topCategoriesChart');
      const pieData = {
        labels: processedSpents.value.map(s => s.category),
        datasets: [{
          data: processedSpents.value.map(s => s.total),
          backgroundColor: spentCategoryColors,
        }]
      };
      if (pieCtx) {
        if (pieCtx && chart.value) {
          chart.value.destroy();
        }
        chart.value = createChart(pieCtx, 'pie', pieData, commonOptions, pieOptions);
      }
    };

    const initLineChart = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('lineChart');
      const lineData = {
        labels: processDailySpentData.value.map(item => item.date),
        datasets: [{
          label: 'Gasto total',
          data: processDailySpentData.value.map(item => item.total),
          borderColor: '#a01414',
          tension: 0.1
        }]
      };
      if (lineCtx) {
        if(linesChart.value) linesChart.value.destroy();
        linesChart.value = createChart(lineCtx, 'line', lineData, commonOptions)
      }
    };

    return {
      spentsFiltered, accountingId, processedSpents, hasDataSpents, totalSpentMonth, latestSpents, dailySpentData, spentCategoryColors,
      paginatedOperations, nextPage, prevPage, showElement, totalPages, currentPage, updateData, processDailySpentData
    };
  },
};
</script>

<style scoped>

</style>
