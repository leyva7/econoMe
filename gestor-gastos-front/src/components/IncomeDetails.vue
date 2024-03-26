<template>
  <p v-if="hasData" class="total-spent">Este mes has ingresado: {{ totalIncomeMonth.toFixed(2) }} €</p>
  <div class="spent-details">
    <!-- Condición para mostrar el recuadro del gráfico solo si hay datos -->
    <div v-if="hasData" class="recuadro-categorias">
      <p class ="title-spent">Ingresos por categorías</p>
      <canvas id="topCategoriesChart"></canvas>

      <table class="table">
        <thead>
        <tr>
          <th>Categoría</th>
          <th>Total</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="income in processedIncomes" :key="income.category">
          <td>{{ income.category }}</td>
          <td>{{ income.total.toFixed(2) }}</td>
        </tr>
        </tbody>
      </table>

    </div>
    <div v-if="hasData" class="recuadro-secundario">
      <div v-if="hasData" class="recuadro-recientes">
        <p class ="title-spent">Últimos ingresos</p>
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

      <div v-if="hasData" class="recuadro-evolucion">
        <p class ="title-spent">Evolución de ingresos por meses</p>
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
                  font: {
                    size: 16 // Tamaño de la letra en el eje Y
                  }
                }
              },
              x: {
                ticks: {
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
                  font: {
                    size: 16 // Tamaño de la letra en el eje Y
                  }
                }
              },
              x: {
                ticks: {
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
      incomesMonths, fetchIncomeAsync, fetchIncomeMonthsAsync, accountingId, processedIncomes, hasData, totalIncomeMonth, latestIncomes, monthlyIncomeData
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
  text-align: left;
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
