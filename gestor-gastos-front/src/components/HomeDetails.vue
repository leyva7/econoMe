<template>
  <div class="container mt-5">
    <h2 v-if="hasData" class="text-center mb-4">Resumen</h2>
    <h2 v-else class="text-center mb-4">No hay datos disponibles</h2>

    <!-- Contenedor para Gastos VS Ingresos y Detalles -->
    <div v-if="hasData" class="row">
      <div class="col-lg-6 mb-3">
        <!-- Gastos VS Ingresos -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Gastos VS Ingresos</h3>
          <div style="width: 100%; height: auto;">
            <canvas id="comparativeChart"></canvas>
          </div>
        </div>
      </div>

      <div class="col-lg-6 mb-3">
        <!-- Detalles de Ingresos y Gastos -->
        <div class="row">
          <div class="col-12 mb-3">
            <div class="bg-white rounded shadow p-3">
              <h4 class="text-center">Gastos Totales: {{ totalSpentMonth.toFixed(2) }} €</h4>
              <div class="row g-3"> <!-- g-3 proporciona un espaciado/gap entre las columnas -->
                <div class="col-md-6">
                  <div class="card text-center">
                    <div class="card-body">
                      <h5 class="card-title">
                        {{ Object.keys(categoriesDifferences.spentDifferences)[0] }}
                      </h5>
                      <div class="card-text">
                        <div class="arrow-up arrow-up-spent"></div><h5>+{{categoriesDifferences.spentDifferences[Object.keys(categoriesDifferences.spentDifferences)[0]]}}</h5>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="card text-center">
                    <div class="card-body">
                      <h5 class="card-title">
                        {{ Object.keys(categoriesDifferences.spentDifferences)[1] }}
                      </h5>
                      <div class="card-text font-weight-bold">
                        <div class="arrow-down arrow-down-spent"></div><h5>{{categoriesDifferences.spentDifferences[Object.keys(categoriesDifferences.spentDifferences)[1]]}}</h5>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12">
            <div class="bg-white rounded shadow p-3">
              <h4 class="text-center">Ingresos Totales: {{ totalIncomeMonth.toFixed(2) }} €</h4>
              <div class="row g-3">
              <div class="col-md-6">
                <div class="card text-center">
                  <div class="card-body">
                    <h5 class="card-title">
                      {{ Object.keys(categoriesDifferences.incomeDifferences)[0] }}
                    </h5>
                    <div class="card-text">
                      <div class="arrow-up arrow-up-income"></div><h5>+{{categoriesDifferences.incomeDifferences[Object.keys(categoriesDifferences.incomeDifferences)[0]]}}</h5>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card text-center">
                  <div class="card-body">
                    <h5 class="card-title">
                      {{ Object.keys(categoriesDifferences.incomeDifferences)[1] }}
                    </h5>
                    <div class="card-text font-weight-bold">
                      <div class="arrow-down arrow-down-income"></div><h5>{{categoriesDifferences.incomeDifferences[Object.keys(categoriesDifferences.incomeDifferences)[1]]}}</h5>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Últimas operaciones -->
    <div v-if="hasData" class="mt-3">
      <div class="bg-white rounded shadow p-3">
        <h3 class="text-center mb-3">Últimas operaciones</h3>
        <!-- Tabla de Últimas Operaciones -->
        <div class="table-responsive">
          <table class="table">
            <thead>
            <tr>
              <th>Tipo de operación</th>
              <th>Categoría</th>
              <th>Cantidad</th>
              <th>Fecha</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="operation in latestOperations" :key="operation.id">
              <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
              <td>{{ operation.category }}</td>
              <td>{{ operation.quantity.toFixed(2) }}</td>
              <td>{{ operation.date }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>

import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '../stores/accountingStore';

Chart.register(...registerables);
export default {
  name: "HomeDetails",
  setup() {
    const { accountingId, fetchSpentsMonthsAsync, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, latestOperations, fetchOperationsAsync } = useAccountingStore();
    const chart = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

    onMounted(async () => {
      await fetchSpentsMonthsAsync(accountingId.value);
      await fetchIncomeMonthsAsync(accountingId.value);
      await categoryDifferencesAsync(accountingId.value);
      await fetchOperationsAsync(accountingId.value);
      // Revisa después de obtener los datos si hay gastos
      hasData.value = totalSpentMonth.value > 0;
      nextTick(() => {
        if (hasData.value) {
          initChart();
        }
      });
    });

    const initChart = () => {
      const ctx = document.getElementById('comparativeChart');
      if (ctx && chart.value) {
        chart.value.destroy();
      }
      if (ctx) {
        // Asegurándose de que los totales de gastos e ingresos están actualizados
        const spentTotal = totalSpentMonth.value;
        const incomeTotal = totalIncomeMonth.value;

        chart.value = new Chart(ctx, {
          type: 'pie', // Tipo de gráfico circular
          data: {
            // Etiquetas para los segmentos del gráfico
            labels: ['Ingresos', 'Gastos'],
            datasets: [{
              // Los datos a mostrar, correspondientes a los totales de gastos e ingresos
              data: [incomeTotal, spentTotal],
              // Colores para cada segmento del gráfico
              backgroundColor: ['#297243', '#a01414'],
            }]
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
              legend: {
                position: 'top',
                labels: {
                  color: 'black', // Asegurando que el color de la leyenda sea negro
                  font: {
                    size: 16
                  }
                }
              },
            }
          },
        });
      }
    };


    return {
      fetchSpentsMonthsAsync, accountingId, hasData, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, fetchOperationsAsync, latestOperations
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