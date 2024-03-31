<template>
  <p v-if="hasData" class="title">Resumen</p>
  <p v-else class="title">No hay datos disponibles</p>
  <div class="home-details">
    <div v-if="hasData" class="recuadro-comparative">
      <p class ="title-home">Gastos VS Ingresos</p>
      <canvas id="comparativeChart"></canvas>
      <div v-if="hasData" class="financial-summary">
        <div class="income-details">
          <h3>Ingresos Totales: {{ totalIncomeMonth.toFixed(2) }} €</h3>
          <p>Principales diferencias:</p>
          <ul>
            <li v-for="(value, key) in categoriesDifferences.incomeDifferences" :key="`income-${key}`">
              {{ key }}: {{ value }}
            </li>
          </ul>
        </div>
        <div class="spent-details">
          <h3>Gastos Totales: {{ totalSpentMonth.toFixed(2) }} €</h3>
          <p>Principales diferencias:</p>
          <ul>
            <li v-for="(value, key) in categoriesDifferences.spentDifferences" :key="`spent-${key}`">
              {{ key }}: {{ value }}
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div v-if="hasData" class="recuadro-secundario">
      <p class="title-home">Últimas operaciones</p>
      <table class="table">
        <thead>
          <tr>
            <th>Categoría</th>
            <th>Cantidad</th>
            <th>Tipo de operación</th>
            <th>Fecha</th>
          </tr>
        </thead>
        <tbody>
        <tr v-for="operation in latestOperations" :key="operation.id">
          <td>{{ operation.category }}</td>
          <td>{{ operation.quantity.toFixed(2) }}</td>
          <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
          <td>{{ operation.date }}</td>
        </tr>
        </tbody>
      </table>
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

.title {
  height: 5%;
  font-size: x-large;
  text-align: center;
  margin-bottom: 20px;
  font-weight: bold;
  color: #2C3E50;
  width: 100%;
}

.title-home{
  font-size: x-large;
  height: 10%;
}

.home-details {
  text-align: center;
  height: calc(95% - 80px);
  display: flex;
  flex-direction: row;
  padding: 20px;
  background-color: #f0f0f0;
}

.recuadro-comparative {
  display: flex;
  flex-direction: column;
  align-items: center;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
  padding: 15px;
  height: 100%; /* Ajusta para que ocupe toda la altura disponible de su contenedor */
  width: 48%; /* Ajuste opcional si necesitas controlar el ancho */
  margin-right: 4%; /* Ajuste opcional para el margen derecho */
}

.financial-summary {
  display: flex;
  width: 100%; /* Asegura que use todo el ancho disponible de su padre */
  justify-content: space-between; /* Mantiene los elementos separados */
  margin-top: 20px; /* Espaciado superior */
  height: 40%; /* Altura relativa dentro de recuadro-comparative */
}

.income-details, .spent-details {
  flex: 1; /* Asegura que ambos elementos ocupen el espacio disponible por igual */
  padding: 10px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border-radius: 10px;
  border: 2px solid #2C3E50;
  background-color: #fff;
  margin: 0 10px; /* Ajusta el margen para mantener separación */
  height: 82%;
}

.title {
  margin-bottom: 20px;
  font-weight: bold;
  color: #2C3E50;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin: 5px 0;
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

#comparativeChart{
  max-width: 100%;
  height: 45%;
  margin: auto;
}
</style>