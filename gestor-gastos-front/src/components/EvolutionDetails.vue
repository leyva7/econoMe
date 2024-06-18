<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Evolución</h2>

    <!-- Componente IntervalSelector -->
    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Sección de gráficos y tabla de datos -->
    <div v-if="hasData" class="row">
      <!-- Gráfico de Evolución de Gastos e Ingresos -->
      <div class="col-12 mb-3">
        <div class="p-3 bg-white rounded shadow h-100">
          <h3 class="text-center mb-3">Evolución gastos e ingresos</h3>
          <div class="chart-container">
            <canvas id="evolution"></canvas>
          </div>
        </div>
      </div>

      <!-- Gráfico de Ahorros -->
      <div class="col-12 col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Ahorro</h3>
          <div class="chart-container">
            <canvas id="savings"></canvas>
          </div>
        </div>
      </div>

      <!-- Tabla de Evolución de Ahorros -->
      <div class="col-12 col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow">
          <DataTable :pagination="paginations[0]" :columns="tableColumnEvolution" />
        </div>
      </div>
    </div>

    <!-- Mensaje de datos no disponibles -->
    <NoDataMessage v-else/>
  </div>
</template>

<script>
// Importación de utilidades, componentes y servicios necesarios
import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import IntervalSelector from "@/components/IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import { processFilterSelection } from "@/utils/functions";
import { commonOptions, hasData } from "@/utils/global";
import { createChart } from "@/utils/chartService";
import { useMultiplePagination } from "@/utils/usePagination";
import DataTable from "@/components/DataTable.vue";

Chart.register(...registerables); // Registra módulos de Chart.js

export default {
  name: "EvolutionDetails",
  components: { IntervalSelector, NoDataMessage, DataTable }, // Componentes utilizados en el template
  setup() {
    // Variables y funciones obtenidas del store mediante Vuex
    const {
      accountingId, fetchSpentsInterval, fetchIncomeInterval, totalIncomeMonth, totalSpentMonth,
      combinedDataProcessed, fetchOperationsAsync, processDailySpentData, savingsData
    } = useAccountingStore();

    // Configuraciones de paginación para diferentes datos
    const paginationConfigs = [
      { data: savingsData, reduced: false },
    ];
    const paginations = useMultiplePagination(paginationConfigs); // Maneja múltiples configuraciones de paginación

    const evolution = ref(null); // Referencia para el gráfico de evolución
    const savings = ref(null); // Referencia para el gráfico de ahorros

    const showElement = ref(false); // Controla la visibilidad de elementos en el template

    // Se ejecuta al montar el componente
    onMounted(async () => {
      updateData();
    });

    // Actualiza los datos basados en la selección del intervalo de fechas
    const updateData = async (selection) => {
      if (!selection || !selection.interval) {
        return;
      }

      // Simula un retardo antes de mostrar elementos
      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      // Procesa la selección de filtro para obtener datos específicos
      const { filterType, startDate, endDate } = processFilterSelection(selection);

      // Obtiene datos de gastos e ingresos basados en el intervalo seleccionado
      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);

      // Actualiza la variable global que indica si hay datos disponibles
      hasData.value = totalSpentMonth.value > 0 || totalIncomeMonth.value > 0;

      // Espera hasta el próximo ciclo de actualización para iniciar los gráficos
      await nextTick();
      initLineChart();
      initLineSavings();
    };

    // Inicializa el gráfico de evolución de gastos e ingresos
    const initLineChart = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('evolution');
      if (lineCtx && combinedDataProcessed.value) {
        if(evolution.value) evolution.value.destroy();
        const lineData = {
          labels: combinedDataProcessed.value.map(item => item.date),
          datasets: [
            {
              label: 'Ingresos',
              data: combinedDataProcessed.value.map(item => item.income),
              fill: false,
              borderColor: '#297243',
              tension: 0.1,
              backgroundColor: 'rgba(41, 114, 67, 0.5)' // Color semi transparente para ingresos
            },
            {
              label: 'Gastos',
              data: combinedDataProcessed.value.map(item => item.spent),
              fill: false,
              borderColor: '#c45850',
              tension: 0.1,
              backgroundColor: 'rgba(196, 88, 80, 0.5)' // Color semi transparente para gastos
            }
          ]
        };
        evolution.value = createChart(lineCtx, 'line', lineData, commonOptions);
      }
    };

    // Inicializa el gráfico de ahorros
    const initLineSavings = () => {
      showElement.value = false;
      const lineCtx = document.getElementById('savings');
      if (lineCtx && savingsData.value) {
        if(savings.value) savings.value.destroy();
        const lineData = {
          labels: savingsData.value.map(item => item.date),
          datasets: [
            {
              label: 'Ahorro',
              data: savingsData.value.map(item => item.savings),
              fill: false,
              borderColor: '#35526f',
              tension: 0.1,
              backgroundColor: '#35526f' // Color semi transparente para ahorros
            }
          ]
        };
        savings.value = createChart(lineCtx, 'bar', lineData, commonOptions);
      }
    };

    return {
      // Variables y funciones disponibles para el template
      hasData, fetchSpentsInterval, fetchIncomeInterval, processDailySpentData, accountingId, showElement, updateData,
      combinedDataProcessed, savingsData, paginations,
      tableColumnEvolution: [
        { key: "date", label: "Fecha" },
        { key: "savings", label: "Ahorro" }
      ]
    };
  },
};
</script>
