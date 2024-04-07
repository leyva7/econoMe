<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Evolución</h2>

    <!-- Gráfico de Evolución y Ahorros -->
    <div v-if="hasData" class="row">
      <div class="col-12 col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow h-100">
          <h3 class="text-center mb-3">Evolución últimos 6 meses</h3>
          <div class="chart-container" style="width: 100%; height: auto;">
            <canvas id="evolution"></canvas>
          </div>
        </div>
      </div>

      <div class="col-12 col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Ahorro últimos 6 meses</h3>
            <table class="table">
              <thead>
              <tr>
                <th>Mes</th>
                <th>Ahorro</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="saving in monthlySavingsData" :key="saving.month">
                <td>{{ saving.month }}</td>
                <td>{{ saving.totalSavings.toFixed(2) }} €</td>
              </tr>
              </tbody>
            </table>
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

Chart.register(...registerables);
export default {
  name: "EvolutionDetails",
  setup() {
    const { accountingId, fetchSpentsAsync, fetchIncomeAsync, monthlySpentData, monthlyIncomeData, monthlySavingsData} = useAccountingStore();
    const evolution = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

    onMounted(async () => {
      await fetchSpentsAsync(accountingId.value);
      await fetchIncomeAsync(accountingId.value);
      // Revisa después de obtener los datos si hay gastos
      hasData.value = monthlySpentData.value.length > 0 && monthlyIncomeData.value.length > 0;
      nextTick(() => {
        if (hasData.value) {
          initLineChart();
        }
      });
    });

    const initLineChart = () => {
      const ctx = document.getElementById('evolution');
      if (ctx) {
        if(evolution.value) evolution.value.destroy(); // Destruye el chart anterior si existe
        evolution.value = new Chart(ctx, {
          type: 'line',
          data: {
            labels: monthlyIncomeData.value.map(data => `${data.month}`),
            datasets: [
              {
                label: 'Ingresos por mes',
                data: monthlyIncomeData.value.map(data => data.total),
                fill: false,
                borderColor: '#297243',
                tension: 0.1,
                backgroundColor: 'rgba(41, 114, 67, 0.5)' // Color semi transparente para ingresos
              },
              {
                label: 'Gastos por mes',
                data: monthlySpentData.value.map(data => data.total),
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
      hasData, fetchSpentsAsync, fetchIncomeAsync, monthlySpentData, monthlyIncomeData, accountingId, monthlySavingsData
    };
  },
};

</script>

<style scoped>

.table th, .table td {
  border: 1px solid #ccc;
  padding: 2px;
  text-align: center;
}

.table th {
  background-color: var(--pickled-bluewood-300);
  color: #000000;
}

</style>