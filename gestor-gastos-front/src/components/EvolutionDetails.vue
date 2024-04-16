<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Evolución</h2>

    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Gráfico de Evolución y Ahorros -->
    <div v-if="hasData" class="row">
      <div class="col-12 col-lg-6 mb-3">
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
          {{monthlySavingsData}}
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
import { useAccountingStore } from '../stores/accountingStore';
import IntervalSelector from "@/components/IntervalSelector.vue";
import {processFilterSelection} from "@/utils/functions";

Chart.register(...registerables);
export default {
  name: "EvolutionDetails",
  components: {IntervalSelector},
  setup() {
    const { accountingId, fetchSpentsInterval, fetchIncomeMonthsAsync, processDailySpentData, processDailyIncomeData, monthlySavingsData, totalIncomeMonth, totalSpentMonth} = useAccountingStore();
    const evolution = ref(null);
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

      console.log(monthlySavingsData);

      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      const { filterType, startDate, endDate } = processFilterSelection(selection);

      console.log(`Fetching with accountingId: ${accountingId.value}, filterType: ${filterType}, startDate: ${startDate}, endDate: ${endDate}`);

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeMonthsAsync(accountingId.value, filterType, startDate, endDate);

      hasData.value = (totalSpentMonth.value > 0 || totalIncomeMonth.value > 0);

      await nextTick();
      initLineChart();
    };

    const initLineChart = () => {
      const ctx = document.getElementById('evolution');
      if (ctx) {
        if(evolution.value) evolution.value.destroy(); // Destruye el chart anterior si existe
        evolution.value = new Chart(ctx, {
          type: 'line',
          data: {
            labels: processDailySpentData.value.map(item => item.date),
            datasets: [
              {
                label: 'Ingresos',
                data: processDailyIncomeData.value.map(item => item.total),
                fill: false,
                borderColor: '#297243',
                tension: 0.1,
                backgroundColor: 'rgba(41, 114, 67, 0.5)' // Color semi transparente para ingresos
              },
              {
                label: 'Gastos',
                data: processDailySpentData.value.map(item => item.total),
                fill: false,
                borderColor: '#c45850',
                tension: 0.1,
                backgroundColor: 'rgba(196, 88, 80, 0.5)' // Color semi transparente para gastos
              }
            ]
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
                display: true,
                labels: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en la leyenda
                  }
                }
              }
            }
          }
        });
      }
    };


    return {
      hasData, fetchSpentsInterval, fetchIncomeMonthsAsync, processDailyIncomeData, processDailySpentData, accountingId, monthlySavingsData, showElement, updateData
    };
  },
};

</script>

<style scoped>

</style>