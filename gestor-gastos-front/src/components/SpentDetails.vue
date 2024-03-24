<template>
  <div class="spent-details">
    <div class="recuadro">
      <canvas id="topCategoriesChart"></canvas>
    </div>
  </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import { onMounted, computed, ref, watch } from 'vue';
import { useAccountingStore } from '../stores/accountingStore';

Chart.register(...registerables);

export default {
  name: "SpentDetails",
  setup() {
    const { accountingId, spents, fetchSpentsAsync } = useAccountingStore();
    const chart = ref(null);

    onMounted(async () => {
      await fetchSpentsAsync(accountingId.value);
    });

    const processedSpents = computed(() => {
      const sumsByCategory = spents.value.reduce((acc, { category, quantity }) => {
        acc[category] = (acc[category] || 0) + parseFloat(quantity);
        return acc;
      }, {});

      const sortedCategories = Object.entries(sumsByCategory)
          .sort((a, b) => b[1] - a[1])
          .map(([category, total]) => ({category, total}));

      const topCategories = sortedCategories.slice(0, 5);
      const otherTotal = sortedCategories.slice(5).reduce((acc, {total}) => acc + total, 0);

      if (otherTotal > 0) {
        topCategories.push({category: 'Otros', total: otherTotal});
      }

      return topCategories;
    });

    watch(processedSpents, (newVal) => {
      if (chart.value) {
        chart.value.destroy();
      }
      chart.value = new Chart(document.getElementById('topCategoriesChart'), {
        type: 'pie',
        data: {
          labels: newVal.map(({category}) => category),
          datasets: [{
            data: newVal.map(({total}) => total),
            backgroundColor: [
              '#FF6384', '#36A2EB', '#FFCE56', '#cc65fe', '#ff7f7f', '#a4b0be'
            ],
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'top',
            },
          }
        },
      });
    }, {immediate: true});

    return {
      spents, fetchSpentsAsync, accountingId, processedSpents
    };
  },
};
</script>

<style scoped>
.spent-details {
  padding: 8px;
  background-color: #f0f0f0;
}

.recuadro {
  margin: 10px;
  padding: 15px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 20px;
  border: 2px solid #2C3E50;
}

#topCategoriesChart {
  max-width: 400px; /* Ajusta el ancho máximo según tus necesidades */
  max-height: 300px; /* Ajusta la altura máxima según tus necesidades */
  margin: auto; /* Centra el gráfico si es más pequeño que el recuadro */
}

.spents {
  margin-bottom: 10px;
  padding: 10px;
  background-color: #eaeaea;
  border-radius: 10px;
}

.operation p {
  margin: 5px 0;
}
</style>
