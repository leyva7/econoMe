<template>
  <div class="container mt-5">
    <!-- Título de gasto total si hay datos -->
    <h2 class="text-center mb-4 " v-if=hasDataSpent>{{'Gasto total: ' + totalSpentMonth.toFixed(2) + ' €'}}</h2>

    <!-- Selector de intervalo -->
    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Contenido principal si hay datos de gastos -->
    <div v-if="hasDataSpent" class="row g-4">
      <!-- Gráfico de gastos por categorías (mitad izquierda en pantallas grandes) -->
      <div class="col-12 col-lg-6">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Gastos por categorías</h3>
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
            <tr v-for="(spent, index) in processedSpents" :key="spent.category">
              <td>
                <span class="category-color" :style="{ backgroundColor: spentCategoryColors[index] }"></span>
                {{ spent.category }}
              </td>
              <td>{{ addEuroSymbol(spent.total) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Gráfico de evolución de gastos (mitad derecha en pantallas grandes) -->
      <div class="col-12 col-lg-6">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="mb-3 text-center">Evolución de gastos</h3>
          <div class="chart-container">
            <canvas id="lineChart"></canvas>
          </div>
        </div>
      </div>

      <!-- Sección de últimas operaciones debajo de los gráficos -->
      <div class="mt-3 col-12">
        <div class="bg-white rounded shadow p-3">
          <h3 class="text-center mb-3">Últimas operaciones</h3>
          <!-- Tabla de Últimas Operaciones -->
          <DataTable :pagination="paginations[0]" :columns="tableColumnsOperations" />
        </div>
      </div>
    </div>

    <!-- Mensaje de no datos si no hay gastos -->
    <NoDataMessage v-else/>
  </div>
</template>

<script>
// Importación de utilidades, componentes y servicios necesarios
import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import { spentCategoryColors, hasDataSpent, commonOptions, pieOptions, tableColumnsOperations } from "@/utils/global";
import { createChart } from "@/utils/chartService";
import { addEuroSymbol, processFilterSelection } from "@/utils/functions";
import { useMultiplePagination } from "@/utils/usePagination";
import IntervalSelector from "./IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import DataTable from "@/components/DataTable.vue";


Chart.register(...registerables); // Registra los módulos de Chart.js

export default {
  name: "SpentDetails", // Nombre del componente
  methods: { addEuroSymbol }, // Métodos del componente
  components: { IntervalSelector, NoDataMessage, DataTable }, // Componentes utilizados en el template
  setup() {
    // Acceso al store de contabilidad y desestructuración de las variables necesarias
    const { accountingId, processedSpents, fetchSpentsInterval, spentsFiltered, totalSpentMonth, processDailySpentData } = useAccountingStore();

    // Configuración de la paginación para los datos filtrados de gastos
    const paginationConfigs = [
      {data: spentsFiltered, reduced: false},
    ];
    const paginations = useMultiplePagination(paginationConfigs); // Utiliza la función de paginación múltiple

    const chart = ref(null); // Referencia reactiva para el gráfico de gastos por categorías
    const linesChart = ref(null); // Referencia reactiva para el gráfico de evolución de gastos

    const showElement = ref(false); // Estado reactiva para controlar la visibilidad de elementos

    // Función que se ejecuta cuando el componente se monta
    onMounted(async () => {
      await updateData(); // Actualiza los datos al montar el componente
    });

    // Función para actualizar los datos basados en la selección de intervalo
    const updateData = async (selection) => {
      if (!selection || !selection.interval) {
        return;
      }

      // Simula un pequeño retraso antes de mostrar los elementos
      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      const {filterType, startDate, endDate} = processFilterSelection(selection); // Procesa la selección del filtro

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate); // Obtiene los gastos según el intervalo seleccionado

      hasDataSpent.value = totalSpentMonth.value > 0; // Verifica si hay datos de gastos

      await nextTick(); // Espera a que Vue actualice el DOM
      initChart(); // Inicializa el gráfico de gastos por categorías
      initLineChart(); // Inicializa el gráfico de evolución de gastos
    };

    // Inicializa el gráfico de gastos por categorías
    const initChart = () => {
      showElement.value = false; // Oculta el elemento antes de actualizarlo
      const pieCtx = document.getElementById('topCategoriesChart'); // Obtiene el contexto del gráfico de gastos por categorías
      const pieData = {
        labels: processedSpents.value.map(s => s.category), // Etiquetas para las categorías de gastos
        datasets: [{
          data: processedSpents.value.map(s => s.total), // Datos de gastos por categoría
          backgroundColor: spentCategoryColors, // Colores de fondo para las categorías
        }]
      };
      if (pieCtx) {
        if (chart.value) chart.value.destroy(); // Destruye el gráfico existente si hay uno
        chart.value = createChart(pieCtx, 'pie', pieData, commonOptions, pieOptions); // Crea un nuevo gráfico de tipo pie
      }
    };

    // Inicializa el gráfico de evolución de gastos
    const initLineChart = () => {
      showElement.value = false; // Oculta el elemento antes de actualizarlo
      const lineCtx = document.getElementById('lineChart'); // Obtiene el contexto del gráfico de evolución de gastos
      const lineData = {
        labels: processDailySpentData.value.map(item => item.date), // Etiquetas para las fechas de evolución de gastos
        datasets: [{
          label: 'Gasto total', // Etiqueta del conjunto de datos
          data: processDailySpentData.value.map(item => item.total), // Datos de evolución de gastos
          borderColor: '#a01414', // Color del borde del gráfico
          tension: 0.1 // Tensión de la curva
        }]
      };
      if (lineCtx) {
        if (linesChart.value) linesChart.value.destroy(); // Destruye el gráfico existente si hay uno
        linesChart.value = createChart(lineCtx, 'line', lineData, commonOptions); // Crea un nuevo gráfico de tipo línea
      }
    };

    // Retorna las propiedades y funciones necesarias para el componente
    return {
      spentsFiltered, accountingId, processedSpents, hasDataSpent, totalSpentMonth, spentCategoryColors,
      paginations, showElement, updateData, processDailySpentData, tableColumnsOperations
    };
  },
};
</script>
