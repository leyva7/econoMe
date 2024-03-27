<template>
  <div class="home-details">
    <div v-if="hasData" class="recuadro-comparative">
      <p class="title">Ingresos VS Gastos</p>
      <canvas id="comparativeChart"></canvas>
      <div class="totals">
        <p>Total Gastos: {{ totalSpentMonth.toFixed(2) }} €</p>
        <p>Total Ingresos: {{ totalIncomeMonth.toFixed(2) }} €</p>
      </div>
      <div class="top-categories">
        <p v-if="topSpentCategory">Principal categoría de gasto: {{ topSpentCategory.category }} con un total de {{ topSpentCategory.total.toFixed(2) }} €</p>
        <p v-if="topIncomeCategory">Principal categoría de ingreso: {{ topIncomeCategory.category }} con un total de {{ topIncomeCategory.total.toFixed(2) }} €</p>
      </div>
    </div>
    <p v-else>No hay datos disponibles</p>
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
    const { accountingId, fetchSpentsMonthsAsync, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory } = useAccountingStore();
    const chart = ref(null);
    // Variable reactiva para determinar si hay datos
    const hasData = ref(false);

    onMounted(async () => {
      await fetchSpentsMonthsAsync(accountingId.value);
      await fetchIncomeMonthsAsync(accountingId.value);
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
            labels: ['Gastos', 'Ingresos'],
            datasets: [{
              // Los datos a mostrar, correspondientes a los totales de gastos e ingresos
              data: [spentTotal, incomeTotal],
              // Colores para cada segmento del gráfico
              backgroundColor: ['#a01414', '#297243'],
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
      fetchSpentsMonthsAsync, accountingId, hasData, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory
    };
  },
};
</script>

<style scoped>
.home-details {
  text-align: center;
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: #f0f0f0;
}

.title {
  font-size: x-large;
  margin-bottom: 20px;
  font-weight: bold;
  color: #000000;
}

.recuadro-comparative {
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
  padding: 15px;
  margin-bottom: 20px;
}

.totals {
  display: flex;
  justify-content: space-around;
  margin: 20px 0;
}

.categories-comparison {
  display: flex;
  justify-content: space-around;
}

.spents-categories, .incomes-categories {
  width: 45%;
}

.spents-categories ul, .incomes-categories ul {
  list-style-type: none;
  padding: 0;
}

#comparativeChart {
  max-width: 100%;
  height: 50%;
  margin: auto;
}

</style>
