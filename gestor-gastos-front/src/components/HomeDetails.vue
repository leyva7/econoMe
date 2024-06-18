<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Resumen</h2>

    <!-- Selector de Intervalo -->
    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Contenedor para Gastos VS Ingresos y Detalles -->
    <div class="row">
      <!-- Columna para Gastos VS Ingresos -->
      <div class="col-lg-6 mb-3">
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Gastos VS Ingresos</h3>
          <!-- Mostrar gráfico si hay datos -->
          <div v-if="hasData">
            <div class="chart-container">
              <canvas id="comparativeChart"></canvas>
            </div>
          </div>
          <!-- Mostrar mensaje si no hay datos -->
          <NoDataMessage v-else/>
        </div>
      </div>

      <!-- Columna para Detalles de Ingresos y Gastos -->
      <div class="col-lg-6 mb-3">
        <div class="row">
          <div class="col-12 mb-3">
            <div class="bg-white rounded shadow p-3">
              <!-- Mostrar total de Gastos si hay datos -->
              <h4 class="text-center" v-if="hasDataSpent">Gastos Totales: {{ totalSpentMonth.toFixed(2) }} €</h4>
              <!-- Mostrar mensaje si no hay datos de Gastos -->
              <NoDataMessage v-else message="Gastos Totales: No hay datos disponibles"/>
              <!-- Detalles de categorías de Gastos si hay datos -->
              <div v-if="hasDataSpent" class="row g-3">
                <div class="col-md-6">
                  <div class="card text-center">
                    <div v-if="categoriesDifferences && categoriesDifferences.spentDifferences" class="card-body">
                      <h5 class="card-title">{{ Object.keys(categoriesDifferences.spentDifferences)[0] }}</h5>
                      <div class="card-text">
                        <div class="arrow-up arrow-up-spent"></div>
                        <h5>+{{ addEuroSymbol(categoriesDifferences.spentDifferences[Object.keys(categoriesDifferences.spentDifferences)[0]]) }}</h5>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="card text-center">
                    <div v-if="categoriesDifferences && categoriesDifferences.spentDifferences" class="card-body">
                      <h5 class="card-title">{{ Object.keys(categoriesDifferences.spentDifferences)[1] }}</h5>
                      <div class="card-text font-weight-bold">
                        <div class="arrow-down arrow-down-spent"></div>
                        <h5>{{ addEuroSymbol(categoriesDifferences.spentDifferences[Object.keys(categoriesDifferences.spentDifferences)[1]]) }}</h5>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12">
            <div class="bg-white rounded shadow p-3">
              <!-- Mostrar total de Ingresos si hay datos -->
              <h4 class="text-center" v-if="hasDataIncome">Ingresos Totales: {{ addEuroSymbol(totalIncomeMonth) }} </h4>
              <!-- Mostrar mensaje si no hay datos de Ingresos -->
              <NoDataMessage v-else message="Ingresos Totales: No hay datos disponibles"/>
              <!-- Detalles de categorías de Ingresos si hay datos -->
              <div v-if="hasDataIncome" class="row g-3">
                <div class="col-md-6">
                  <div class="card text-center">
                    <div v-if="categoriesDifferences && categoriesDifferences.incomeDifferences" class="card-body">
                      <h5 class="card-title">{{ Object.keys(categoriesDifferences.incomeDifferences)[0] }}</h5>
                      <div class="card-text">
                        <div class="arrow-up arrow-up-income"></div>
                        <h5>+{{ addEuroSymbol(categoriesDifferences.incomeDifferences[Object.keys(categoriesDifferences.incomeDifferences)[0]]) }}</h5>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="card text-center">
                    <div v-if="categoriesDifferences && categoriesDifferences.incomeDifferences" class="card-body">
                      <h5 class="card-title">{{ Object.keys(categoriesDifferences.incomeDifferences)[1] }}</h5>
                      <div class="card-text font-weight-bold">
                        <div class="arrow-down arrow-down-income"></div>
                        <h5>{{ addEuroSymbol(categoriesDifferences.incomeDifferences[Object.keys(categoriesDifferences.incomeDifferences)[1]]) }}</h5>
                      </div>
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
    <div class="mt-3">
      <div class="bg-white rounded shadow p-3">
        <h3 class="text-center mb-3">Últimas operaciones</h3>
        <!-- Mostrar tabla de Últimas Operaciones si hay datos -->
        <div v-if="hasData">
          <DataTable :pagination="paginations[0]" :columns="tableColumnsOperations" />
        </div>
        <!-- Mostrar mensaje si no hay operaciones recientes -->
        <div v-else>
          <NoDataMessage message="No hay operaciones recientes disponibles."/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// Importación de utilidades, componentes y servicios necesarios
import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick } from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import { useMultiplePagination } from "@/utils/usePagination";
import { addEuroSymbol, processFilterSelection } from "@/utils/functions";
import IntervalSelector from "./IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import { commonOptions, pieOptions, hasData, hasDataSpent, hasDataIncome, tableColumnsOperations } from "@/utils/global";
import { createChart } from "@/utils/chartService";
import DataTable from "@/components/DataTable.vue";

// Registrar plugins necesarios para Chart.js
Chart.register(...registerables);

export default {
  name: "HomeDetails",
  methods: { addEuroSymbol },
  components: { IntervalSelector, NoDataMessage, DataTable },
  setup() {
    // Acceso a las funciones y datos del store
    const { accountingId, fetchSpentsInterval, totalSpentMonth, fetchIncomeInterval, totalIncomeMonth, categoriesDifferences, categoryDifferencesAsync, operations, fetchOperationsAsync } = useAccountingStore();

    // Configuración de paginación para las operaciones
    const paginationConfigs = [
      { data: operations, reduced: false },
    ];
    const paginations = useMultiplePagination(paginationConfigs);

    // Referencias y estado local
    const chart = ref(null);
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

      // Simular una carga con retraso para mostrar un indicador de carga
      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      // Procesar selección de filtro
      const { filterType, startDate, endDate } = processFilterSelection(selection);

      // Llamar a las funciones del store para obtener datos
      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);
      await categoryDifferencesAsync(accountingId.value, filterType, startDate, endDate);

      // Determinar si hay datos disponibles
      hasData.value = totalSpentMonth.value > 0 || totalIncomeMonth.value > 0;
      hasDataSpent.value = totalSpentMonth.value > 0;
      hasDataIncome.value = totalIncomeMonth.value > 0;

      // Esperar al próximo ciclo de renderizado para inicializar el gráfico
      await nextTick();
      initChart();
    };

    // Función para inicializar el gráfico de comparación de Gastos vs Ingresos
    const initChart = () => {
      showElement.value = false;
      const ctx = document.getElementById('comparativeChart');
      if (ctx && ctx.getContext) {
        if (chart.value) {
          chart.value.destroy();
        }
        const summary = {
          labels: ['Ingresos', 'Gastos'],
          datasets: [{
            data: [totalIncomeMonth.value, totalSpentMonth.value],
            backgroundColor: ['#297243', '#a01414'],
          }]
        };

        // Crear el gráfico usando
        chart.value = createChart(ctx, 'doughnut', summary, commonOptions, pieOptions);
      }
    };

    return {
      // Datos del store y funciones disponibles para el template
      accountingId, totalSpentMonth, fetchIncomeInterval, totalIncomeMonth, categoriesDifferences, categoryDifferencesAsync, fetchOperationsAsync, operations,
      updateData,
      showElement,
      hasData, hasDataIncome, hasDataSpent,
      paginations,
      tableColumnsOperations
    };
  },
};
</script>

