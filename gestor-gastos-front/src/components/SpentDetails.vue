<template>
  <p v-if="hasData" class="total-spent">Este mes has gastado: {{ totalSpentMonth.toFixed(2) }} €</p>
  <div class="spent-details">
    <!-- Condición para mostrar el recuadro del gráfico solo si hay datos -->
    <div v-if="hasData" class="recuadro-categorias">
      <p class ="title-spent">Gastos por categorías</p>
      <canvas id="topCategoriesChart"></canvas>

      <table class="table">
        <thead>
        <tr>
          <th>Categoría</th>
          <th>Total</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="spent in processedSpents" :key="spent.category">
          <td>{{ spent.category }}</td>
          <td>{{ spent.total.toFixed(2) }}</td>
        </tr>
        </tbody>
      </table>

    </div>
    <div v-if="hasData" class="recuadro-secundario">
      <div v-if="hasData" class="recuadro-recientes">
        <p class ="title-spent">Últimos gastos</p>
        <table class="table">
          <thead>
          <tr>
            <th>Categoría</th>
            <th>Cantidad</th>
            <th>Fecha</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="spent in latestSpents" :key="spent.id">
            <td>{{ spent.category }}</td>
            <td>{{ spent.quantity.toFixed(2) }}</td>
            <td>{{ spent.date }}</td>
          </tr>
          </tbody>
        </table>
      </div>

      <div v-if="hasData" class="recuadro-evolucion">
        <p class ="title-spent">Evolución de gastos por semanas</p>
        <canvas id="lineChart"></canvas>
      </div>
    </div>
    <!-- Muestra un mensaje cuando no hay datos -->
    <p v-else>No hay datos disponibles</p>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '../stores/accountingStore';

Chart.register(...registerables);

export default {
  name: "SpentDetails",
  setup() {
    const { accountingId, processedSpents, fetchSpentsMonthsAsync, spentsMonths, totalSpentMonth, latestSpents, weeklySpentData} = useAccountingStore();
    const chart = ref(null);
    const linesChart = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

    onMounted(async () => {
      await fetchSpentsMonthsAsync(accountingId.value);
      // Revisa después de obtener los datos si hay gastos
      hasData.value = spentsMonths.value.length > 0;
      nextTick(() => {
        if (hasData.value) {
          initChart();
          initLineChart();
        }
      });
    });

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
                position: 'top',
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

    const initLineChart = () => {
      const ctx = document.getElementById('lineChart');
      console.log(weeklySpentData.value);
      if (ctx) {
        if(linesChart.value) linesChart.value.destroy(); // Destruye el chart anterior si existe
        linesChart.value = new Chart(ctx, {
          type: 'line',
          data: {
            labels: weeklySpentData.value.map(data => `${data.week}`),
            datasets: [{
              label: 'Gastos por semana',
              data: weeklySpentData.value.map(data => data.total),
              fill: false,
              borderColor: '#a01414',
              tension: 0.1
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
                ticks: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en el eje X
                  }
                }
              }
            },
            plugins: {
              legend: {
                display: false
              }
            }
          }
        });
      }
    };

    return {
      spentsMonths, fetchSpentsMonthsAsync, accountingId, processedSpents, hasData, totalSpentMonth, latestSpents, weeklySpentData
    };
  },
};
</script>

<style scoped>

.total-spent {
  height: 5%;
  font-size: 24px;
  text-align: center;
  margin-bottom: 20px;
  font-weight: bold;
  color: #2C3E50;
  width: 100%;
}

.spent-details {
  text-align: center;
  height: calc(95% - 80px);
  display: flex;
  flex-direction: row;
  padding: 20px;
  background-color: #f0f0f0;
}

.recuadro-categorias {
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
  height: calc(100% - 16px); /* Ajusta según el padding de spent-details */
}

.recuadro-secundario {
  flex: 1 1 48%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  height: calc(95% - 80px);;
}

.recuadro-recientes, .recuadro-evolucion {
  flex: 1;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
  padding: 15px;
  margin-bottom: 10px;
}

p.title-spent{
  font-size: x-large;
  margin-bottom: 5px;
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

#topCategoriesChart {
  max-width: 100%;
  height: 50%;
  margin: auto;
}

#lineChart{
  max-width: 100%;
  height: 80%;
  margin: auto;
}



</style>
