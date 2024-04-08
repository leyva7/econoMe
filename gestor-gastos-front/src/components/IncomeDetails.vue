<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">{{ hasData ? 'Este mes has ingresado: ' + totalIncomeMonth.toFixed(2) + ' €' : 'No hay datos disponibles' }}</h2>

    <div v-if="hasData" class="row g-4">
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
                <span class="category-color" :style="{ backgroundColor: categoryColors[index] }"></span>
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
      <div class="col-12">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Últimos ingresos</h3>
          <table class="table">
            <thead>
            <tr>
              <th>Categoría</th>
              <th>Cantidad</th>
              <th>Fecha</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="income in latestIncomes" :key="income.id">
              <td>{{ income.category }}</td>
              <td>{{ income.quantity.toFixed(2) }}</td>
              <td>{{ income.date }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <!-- Mensaje si no hay datos -->
    <p v-else class="text-center">No hay datos disponibles</p>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '../stores/accountingStore';

Chart.register(...registerables);

export default {
  name: "IncomeDetails",
  setup() {
    const { accountingId, processedIncomes, fetchIncomeAsync,fetchIncomeMonthsAsync, incomesMonths, totalIncomeMonth, latestIncomes, monthlyIncomeData} = useAccountingStore();
    const chart = ref(null);
    const linesChart = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

    onMounted(async () => {
      await fetchIncomeMonthsAsync(accountingId.value);
      await fetchIncomeAsync(accountingId.value);
      console.log(monthlyIncomeData.value);
      // Revisa después de obtener los datos si hay gastos
      hasData.value = incomesMonths.value.length > 0;
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
        chart.value.destroy(); // Destruye el gráfico anterior si existe
      }
      if (ctx) {
        chart.value = new Chart(ctx, {
          type: 'bar', // Cambia 'pie' por 'bar'
          data: {
            labels: processedIncomes.value.map(({ category }) => category), // Categorías
            datasets: [{
              label: 'Total por categoría',
              data: processedIncomes.value.map(({ total }) => total), // Totales
              backgroundColor: [
                '#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be'
              ],
              borderColor: [
                '#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be'
              ],
              borderWidth: 1
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
                display:false,
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
                display: false // Puedes cambiarlo a true si deseas mostrar la leyenda
              }
            }
          }
        });
      }
    };

    const initLineChart = () => {
      const ctx = document.getElementById('lineChart');
      if (ctx) {
        if(linesChart.value) linesChart.value.destroy(); // Destruye el chart anterior si existe
        linesChart.value = new Chart(ctx, {
          type: 'line',
          data: {
            labels: monthlyIncomeData.value.map(data => `${data.month}`),
            datasets: [{
              label: 'Ingresos por mes',
              data: monthlyIncomeData.value.map(data => data.total),
              fill: false,
              borderColor: '#297243',
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
            responsive: true,
            plugins: {
              legend: {
                display: false // Esto desactivará la leyenda del gráfico
              }
            }
          }
        });
      }
    };

    return {
      incomesMonths, fetchIncomeAsync, fetchIncomeMonthsAsync, accountingId, processedIncomes, hasData, totalIncomeMonth, latestIncomes, monthlyIncomeData, categoryColors: [
        '#183c27', '#297243', '#5dac75', '#5dac75', '#5dac75', '#a4b0be'
      ],
    };
  },
};
</script>

<style scoped>

</style>
