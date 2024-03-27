<template>
  <p v-if="hasData" class="title">Evolución</p>
  <p v-else class="title">No hay datos disponibles</p>
  <div class="evolution-details">
    <div v-if="hasData" class="recuadro-tendencia">
      <p class ="title-evolution">Evolución últimos 6 meses</p>
      <canvas id="evolution"></canvas>
    </div>
    <div v-if="hasData" class="recuadro-secundario">
      <p class="title-evolution">Ahorros en los últimos 6 meses</p>
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
  <div v-if="hasData" class="recuadro-secundario">

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

.title {
  height: 5%;
  font-size: x-large;
  text-align: center;
  margin-bottom: 20px;
  font-weight: bold;
  color: #2C3E50;
  width: 100%;
}

.title-evolution{
  font-size: x-large;
}

.evolution-details {
  text-align: center;
  height: calc(95% - 80px);
  display: flex;
  flex-direction: row;
  padding: 20px;
  background-color: #f0f0f0;
}

.recuadro-tendencia {
  flex: 1 1 48%;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
  padding: 15px;
  margin-right: 4%; /* Espacio entre recuadro-categorias y recuadro-secundario */
  height: calc(100% - 16px); /* Ajusta según el padding de income-details */
}

.recuadro-secundario {
  flex: 1 1 48%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
  padding: 15px;
  height: calc(75% - 80px);
}

table {
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

#evolution{
  max-width: 100%;
  height: 80%;
  margin: auto;
}
</style>