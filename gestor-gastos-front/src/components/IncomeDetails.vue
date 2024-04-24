<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4" v-if="hasDataIncome">Ingresos totales: {{ totalIncomeMonth.toFixed(2) }} €</h2>

    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <div v-if="hasDataIncome" class="row g-4">
      <!-- Gráfico por Categorías y Últimos Gastos en pantallas grandes; se apilan en pantallas más pequeñas -->
      <div class="col-12 col-lg-6">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Ingresos por categorías</h3>
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
            <tr v-for="(income, index) in processedIncomes" :key="income.category">
              <td>
                <span class="category-color" :style="{ backgroundColor: incomeCategoryColors[index] }"></span>
                {{ income.category }}
              </td>
              <td>{{ income.total.toFixed(2) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div class="col-12 col-lg-6">
        <!-- Evolución de los Gastos -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Evolución de ingresos por meses</h3>
          <div class="chart-container">
            <canvas id="lineChart"></canvas>
          </div>
        </div>
      </div>
      <div v-if="hasDataIncome" class="mt-3 col-12">
        <div class="bg-white rounded shadow p-3">
          <h3 class="text-center mb-3">Últimas operaciones</h3>
          <!-- Tabla de Últimas Operaciones -->
          <DataTable :data="paginations[0].paginatedData.value" :columns="tableColumnsOperations"
                     :currentPage="paginations[0].currentPage.value"
                     :totalPages="paginations[0].totalPages.value"
                     @prev-page="paginations[0].prevPage"
                     @next-page="paginations[0].nextPage" />
        </div>
      </div>
    </div>
    <NoDataMessage v-else/>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import {onMounted, ref, nextTick} from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import {commonOptions, incomeCategoryColors, pieOptions, hasDataIncome, tableColumnsOperations} from "@/utils/global";
import {useMultiplePagination} from "@/utils/usePagination";
import {processFilterSelection} from "@/utils/functions";
import IntervalSelector from "@/components/IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import {createChart} from "@/utils/chartService";
import DataTable from "@/components/DataTable.vue";

Chart.register(...registerables);

export default {
  name: "IncomeDetails",
  components:{ IntervalSelector, NoDataMessage, DataTable },
  setup() {
    const { accountingId, processedIncomes, fetchIncomeAsync,fetchIncomeInterval, incomesFiltered, latestIncomes, monthlyIncomeData, totalIncomeMonth, processDailyIncomeData} = useAccountingStore();
    const paginationConfigs = [
      { data: incomesFiltered, reduced: false },
    ];
    const paginations = useMultiplePagination(paginationConfigs);
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

      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);

      hasDataIncome.value = totalIncomeMonth.value > 0;

      await nextTick();
      initChart();
      initLineChart();
    };

    const initChart = () => {
      showElement.value = false;
      const pieCtx = document.getElementById('topCategoriesChart');
      if (pieCtx) {
        if (pieCtx && chart.value) {
          chart.value.destroy();
        }
        const pieData = {
          labels: processedIncomes.value.map(s => s.category),
          datasets: [{
            data: processedIncomes.value.map(s => s.total),
            backgroundColor: incomeCategoryColors,
          }]
        };
        chart.value = createChart(pieCtx, 'pie', pieData, commonOptions, pieOptions);
      }
    };

    const initLineChart = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('lineChart');
      if (lineCtx) {
        if(linesChart.value) linesChart.value.destroy();
        const lineData = {
          labels: processDailyIncomeData.value.map(item => item.date),
          datasets: [{
            label: 'Ingresos totales',
            data: processDailyIncomeData.value.map(item => item.total),
            fill: false,
            borderColor: '#297243',
            tension: 0.1
          }]
        }
        linesChart.value = createChart(lineCtx, 'line', lineData, commonOptions)
      }
    };

    return {
      processDailyIncomeData, totalIncomeMonth, incomesFiltered, fetchIncomeAsync, fetchIncomeInterval, processedIncomes, hasDataIncome, latestIncomes, monthlyIncomeData, incomeCategoryColors,
      paginations, showElement, updateData, tableColumnsOperations
    };
  },
};
</script>

<style scoped>

</style>
