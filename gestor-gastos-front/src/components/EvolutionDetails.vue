<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Evolución</h2>

    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Gráfico de Evolución y Ahorros -->
    <div v-if="hasData" class="row">
      <div class="col-12 mb-3">
        <div class="p-3 bg-white rounded shadow h-100">
          <h3 class="text-center mb-3">Evolución gastos e ingresos</h3>
          <div class="chart-container">
            <canvas id="evolution"></canvas>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Ahorro</h3>
          <div class="chart-container">
            <canvas id="savings"></canvas>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow">
          <div class="table-container table-responsive">
            <table class="table">
              <thead>
              <tr>
                <th>Fecha</th>
                <th>Ahorro</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="data in paginatedOperations" :key="data.date">
                <td>{{ data.date }}</td>
                <td>{{ data.savings.toFixed(2) }} €</td>
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

    <!-- Mensaje si no hay datos -->
    </div>
    <p v-else class="text-center">No hay datos disponibles</p>
  </div>
</template>

<script>

import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import IntervalSelector from "@/components/IntervalSelector.vue";
import {processFilterSelection} from "@/utils/functions";
import {commonOptions} from "@/utils/global";
import {createChart} from "@/utils/chartService";
import {usePagination} from "@/utils/usePagination";

Chart.register(...registerables);
export default {
  name: "EvolutionDetails",
  components: {IntervalSelector},
  setup() {
    const { accountingId, fetchSpentsInterval, fetchIncomeMonthsAsync, totalIncomeMonth, totalSpentMonth, combinedDataProcessed, fetchOperationsAsync, processDailySpentData, savingsData} = useAccountingStore();
    const { currentPage, totalPages, paginatedOperations, nextPage, prevPage } = usePagination(savingsData);
    const evolution = ref(null);
    const savings = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

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
      await fetchIncomeMonthsAsync(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);

      hasData.value = (totalSpentMonth.value > 0 || totalIncomeMonth.value > 0);

      await nextTick();
      initLineChart();
      initLineSavings();
    };

    const initLineChart = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('evolution');
      if (lineCtx && combinedDataProcessed.value) {
        if(evolution.value) evolution.value.destroy();
        const lineData = {
          labels: combinedDataProcessed.value.map(item => item.date),
          datasets: [
            {
              label: 'Ingresos',
              data: combinedDataProcessed.value.map(item => item.income),
              fill: false,
              borderColor: '#297243',
              tension: 0.1,
              backgroundColor: 'rgba(41, 114, 67, 0.5)' // Color semi transparente para ingresos
            },
            {
              label: 'Gastos',
              data: combinedDataProcessed.value.map(item => item.spent),
              fill: false,
              borderColor: '#c45850',
              tension: 0.1,
              backgroundColor: 'rgba(196, 88, 80, 0.5)' // Color semi transparente para gastos
            }
          ]
        };
        evolution.value = createChart(lineCtx, 'line', lineData, commonOptions);
      }
    };

    const initLineSavings = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('savings');
      if (lineCtx && savingsData.value) {
        if(savings.value) savings.value.destroy();
        const lineData = {
          labels: savingsData.value.map(item => item.date),
          datasets: [
            {
              label: 'Ahorro',
              data: savingsData.value.map(item => item.savings),
              fill: false,
              borderColor: '#35526f',
              tension: 0.1,
              backgroundColor: '#35526f' // Color semi transparente para ingresos
            }
          ]
        };
        savings.value = createChart(lineCtx, 'bar', lineData, commonOptions);
      }
    };


    return {
      hasData, fetchSpentsInterval, fetchIncomeMonthsAsync, processDailySpentData, accountingId, showElement, updateData, combinedDataProcessed, savingsData,
      currentPage, totalPages, paginatedOperations, nextPage, prevPage
    };
  },
};

</script>

<style scoped>

</style>