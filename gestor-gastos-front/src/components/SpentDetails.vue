<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">{{ hasData ? 'Este mes has gastado: ' + totalSpentMonth.toFixed(2) + ' €' : 'No hay datos disponibles' }}</h2>

    <div v-if="hasData" class="row g-4">
      <!-- Gráfico por Categorías y Últimos Gastos en pantallas grandes; se apilan en pantallas más pequeñas -->
      <div class="col-12 col-lg-6">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Gastos por categorías</h3>
          <div style="width: 100%; height: auto;">
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
                <span class="category-color" :style="{ backgroundColor: categoryColors[index] }"></span>
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
          <h3 class="mb-3 text-center">Evolución de gastos por semanas</h3>
          <div style="width: 100%; height: 100%;">
            <canvas id="lineChart"></canvas>
          </div>
        </div>
      </div>
      <div class="col-12">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Últimos gastos</h3>
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
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: false,
                position: 'left',
                align: 'center',
                padding: '100',
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
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                display: true,
                position: 'top',
                align: 'center',
                padding: '100',
                labels: {
                  color: 'black',
                  font: {
                    size: 16 // Tamaño de la letra en la leyenda
                  }
                }
              },
            }
          }
        });
      }
    };

    return {
      spentsMonths, fetchSpentsMonthsAsync, accountingId, processedSpents, hasData, totalSpentMonth, latestSpents, weeklySpentData, categoryColors: ['#480707', '#a01414', '#e61c1c', '#ff6a6a', '#ffc7c7', '#a4b0be']
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

.category-color {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 4px;
  vertical-align: middle;
}

</style>
