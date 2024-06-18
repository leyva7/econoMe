<template>
  <!-- Contenedor principal -->
  <div class="container mt-5">
    <!-- Título de Ingresos totales si hay datos -->
    <h2 class="text-center mb-4" v-if="hasDataIncome">Ingresos totales: {{ totalIncomeMonth.toFixed(2) }} €</h2>

    <!-- Selector de Intervalo -->
    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Contenido principal si hay datos de ingresos -->
    <div v-if="hasDataIncome" class="row g-4">
      <!-- Columna izquierda en pantallas grandes -->
      <div class="col-12 col-lg-6">
        <!-- Gráfico y tabla de Ingresos por Categorías -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Ingresos por categorías</h3>
          <!-- Contenedor del gráfico de categorías -->
          <div class="chart-container">
            <canvas id="topCategoriesChart"></canvas>
          </div>
          <!-- Tabla de categorías debajo del gráfico -->
          <table class="table mt-3">
            <thead>
            <tr>
              <th>Categoría</th>
              <th>Total</th>
            </tr>
            </thead>
            <tbody>
            <!-- Renderización de filas de la tabla -->
            <tr v-for="(income, index) in processedIncomes" :key="income.category">
              <td>
                <!-- Indicador de color de categoría -->
                <span class="category-color" :style="{ backgroundColor: incomeCategoryColors[index] }"></span>
                {{ income.category }}
              </td>
              <td>{{ addEuroSymbol(income.total) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Columna derecha en pantallas grandes -->
      <div class="col-12 col-lg-6">
        <!-- Gráfico de Evolución de Ingresos por Meses -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Evolución de ingresos por meses</h3>
          <div class="chart-container">
            <canvas id="lineChart"></canvas>
          </div>
        </div>
      </div>

      <!-- Sección de Últimas Operaciones -->
      <div class="mt-3 col-12">
        <div class="bg-white rounded shadow p-3">
          <h3 class="text-center mb-3">Últimas operaciones</h3>
          <!-- Tabla de Últimas Operaciones -->
          <DataTable :pagination="paginations[0]" :columns="tableColumnsOperations" />
        </div>
      </div>
    </div>

    <!-- Mensaje de No Data si no hay datos de ingresos -->
    <NoDataMessage v-else/>
  </div>
</template>

<script>
// Importación de utilidades, componentes y servicios necesarios
import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import { commonOptions, incomeCategoryColors, pieOptions, hasDataIncome, tableColumnsOperations } from "@/utils/global";
import { useMultiplePagination } from "@/utils/usePagination";
import { addEuroSymbol, processFilterSelection } from "@/utils/functions";
import IntervalSelector from "@/components/IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import { createChart } from "@/utils/chartService";
import DataTable from "@/components/DataTable.vue";

Chart.register(...registerables);

export default {
  name: "IncomeDetails",
  methods: { addEuroSymbol },
  components: { IntervalSelector, NoDataMessage, DataTable },
  setup() {
    // Uso del store para obtener datos y funciones relacionadas con ingresos
    const { accountingId, processedIncomes, fetchIncomeAsync, fetchIncomeInterval, incomesFiltered, latestIncomes, monthlyIncomeData, totalIncomeMonth, processDailyIncomeData } = useAccountingStore();

    // Configuración de paginación para las operaciones de ingresos
    const paginationConfigs = [
      { data: incomesFiltered, reduced: false },
    ];
    const paginations = useMultiplePagination(paginationConfigs);

    // Referencias para los gráficos
    const chart = ref(null);
    const linesChart = ref(null);

    // Estado para mostrar elementos
    const showElement = ref(false);

    // Hook de montaje para cargar datos iniciales
    onMounted(async () => {
      await updateData();
    });

    // Función para actualizar datos según el intervalo seleccionado
    const updateData = async (selection) => {
      if (!selection || !selection.interval) {
        return;
      }

      // Simular un retraso para mostrar el indicador de carga
      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      // Procesar el filtro de selección
      const { filterType, startDate, endDate } = processFilterSelection(selection);

      // Obtener datos de ingresos según el intervalo seleccionado
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);

      // Actualizar estado de hasDataIncome
      hasDataIncome.value = totalIncomeMonth.value > 0;

      // Esperar hasta el próximo tick para inicializar los gráficos
      await nextTick();
      initChart();
      initLineChart();
    };

    // Función para inicializar el gráfico de categorías de ingresos
    const initChart = () => {
      showElement.value = false;
      const pieCtx = document.getElementById('topCategoriesChart');
      if (pieCtx) {
        // Destruir el gráfico existente si ya existe
        if (chart.value) {
          chart.value.destroy();
        }
        // Configuración de datos y opciones para el gráfico de categorías
        const pieData = {
          labels: processedIncomes.value.map(s => s.category),
          datasets: [{
            data: processedIncomes.value.map(s => s.total),
            backgroundColor: incomeCategoryColors,
          }]
        };
        // Crear el gráfico de tipo pie utilizando Chart.js y la función createChart
        chart.value = createChart(pieCtx, 'pie', pieData, commonOptions, pieOptions);
      }
    };

    // Función para inicializar el gráfico de evolución de ingresos por meses
    const initLineChart = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('lineChart');
      if (lineCtx) {
        // Destruir el gráfico existente si ya existe
        if (linesChart.value) linesChart.value.destroy();
        // Configuración de datos y opciones para el gráfico de evolución de ingresos
        const lineData = {
          labels: processDailyIncomeData.value.map(item => item.date),
          datasets: [{
            label: 'Ingresos totales',
            data: processDailyIncomeData.value.map(item => item.total),
            fill: false,
            borderColor: '#297243',
            tension: 0.1
          }]
        };
        // Crear el gráfico de tipo línea utilizando Chart.js y la función createChart
        linesChart.value = createChart(lineCtx, 'line', lineData, commonOptions)
      }
    };

    // Retornar los datos y funciones necesarios para el template
    return {
      processDailyIncomeData,
      totalIncomeMonth,
      incomesFiltered,
      fetchIncomeAsync,
      fetchIncomeInterval,
      processedIncomes,
      hasDataIncome,
      latestIncomes,
      monthlyIncomeData,
      incomeCategoryColors,
      paginations,
      showElement,
      updateData,
      tableColumnsOperations
    };
  },
};
</script>