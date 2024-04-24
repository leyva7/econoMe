<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4 " v-if=hasDataSpents>{{'Gasto total: ' + totalSpentMonth.toFixed(2) + ' €'}}</h2>

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
import { spentCategoryColors, hasDataSpents, commonOptions, pieOptions, tableColumnsOperations } from "@/utils/global";
import {createChart} from "@/utils/chartService";
import {processFilterSelection} from "@/utils/functions";
import {useMultiplePagination} from "@/utils/usePagination";
import IntervalSelector from "./IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import DataTable from "@/components/DataTable.vue";


Chart.register(...registerables);

export default {
  name: "SpentDetails",
  components:{ IntervalSelector, NoDataMessage, DataTable },
  setup() {
    const { accountingId, processedSpents, fetchSpentsInterval, spentsFiltered, totalSpentMonth, processDailySpentData} = useAccountingStore();
    const paginationConfigs = [
      { data: spentsFiltered, reduced: false },
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

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);

      hasDataSpents.value = totalSpentMonth.value > 0;

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
      spentsFiltered, accountingId, processedSpents, hasDataSpents, totalSpentMonth, spentCategoryColors,
      paginations, showElement, updateData, processDailySpentData, tableColumnsOperations
    };
  },
};
</script>

<style scoped>

</style>
