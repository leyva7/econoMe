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
          <DataTable :data="paginations[0].paginatedData.value" :columns="tableColumnEvolution"
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
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import IntervalSelector from "@/components/IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import {processFilterSelection} from "@/utils/functions";
import {commonOptions, hasData} from "@/utils/global";
import {createChart} from "@/utils/chartService";
import {useMultiplePagination} from "@/utils/usePagination";
import DataTable from "@/components/DataTable.vue";

Chart.register(...registerables);
export default {
  name: "EvolutionDetails",
  components: {IntervalSelector, NoDataMessage, DataTable},
  setup() {
    const { accountingId, fetchSpentsInterval, fetchIncomeInterval, totalIncomeMonth, totalSpentMonth, combinedDataProcessed, fetchOperationsAsync, processDailySpentData, savingsData} = useAccountingStore();
    const paginationConfigs = [
      { data: savingsData, reduced: false },
    ];
    const paginations = useMultiplePagination(paginationConfigs);
    const evolution = ref(null);
    const savings = ref(null);

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
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);

      hasData.value = totalSpentMonth.value > 0 || totalIncomeMonth.value > 0;

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
      hasData, fetchSpentsInterval, fetchIncomeInterval, processDailySpentData, accountingId, showElement, updateData, combinedDataProcessed, savingsData,
      paginations, tableColumnEvolution : [
        { key: "date", label: "Fecha" },
        { key: "savings", label: "Ahorro" }
      ]
    };
  },
};

</script>

<style scoped>

</style>