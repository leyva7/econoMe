<template>
  <div class="container mt-5">
    <h2 v-if="hasData" class="text-center mb-4">Resumen</h2>
    <h2 v-else class="text-center mb-4">No hay datos disponibles</h2>

    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Contenedor para Gastos VS Ingresos y Detalles -->
    <div v-if="hasData" class="row">
      <div class="col-lg-6 mb-3">
        <!-- Gastos VS Ingresos -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Gastos VS Ingresos</h3>
          <div class="chart-container" v-show="hasData">
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
        <div class="table-container table-responsive">
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
            <tr v-for="operation in paginatedOperations" :key="operation.id">
              <td>{{ operation.type === 'INCOME' ? 'Ingreso' : 'Gasto' }}</td>
              <td>{{ operation.category }}</td>
              <td>{{ operation.quantity.toFixed(2) }}</td>
              <td>{{ operation.date }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="pagination-container d-flex justify-content-center mb-4">
          <button @click="prevPage" class="btn btn-secondary me-2" :disabled="currentPage <= 1">Anterior</button>
          <span class="me-2">Página {{ currentPage }} de {{ totalPages }}</span>
          <button @click="nextPage" class="btn btn-secondary" :disabled="currentPage >= totalPages">Siguiente</button>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script>

import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick} from 'vue';
import { useAccountingStore } from '../stores/accountingStore';
import {usePagination} from "@/utils/usePagination";
import {processFilterSelection} from "@/utils/functions";
import IntervalSelector from "./IntervalSelector.vue";
import {commonOptions, pieOptions} from "@/utils/global";
import {createChart} from "@/utils/chartService";

Chart.register(...registerables);
export default {
  name: "HomeDetails",
  components:{ IntervalSelector },
  setup() {
    const { accountingId, fetchSpentsInterval, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, operations, fetchOperationsAsync } = useAccountingStore();
    const { currentPage, totalPages, paginatedOperations, nextPage, prevPage } = usePagination(operations);

    const chart = ref(null);

    const hasData = ref(false);

    const showElement = ref(false);

    onMounted(async () => {
      updateData();
    });

    const updateData = async (selection) => {
      if (!selection || !selection.interval) {
        return;
      }

      setTimeout(() => {
        showElement.value = true;
      }, 1000);

      const { filterType, startDate, endDate } = processFilterSelection(selection);

      console.log(`Fetching with accountingId: ${accountingId.value}, filterType: ${filterType}, startDate: ${startDate}, endDate: ${endDate}`);

      await fetchSpentsInterval(accountingId.value, filterType, startDate, endDate);
      await fetchIncomeMonthsAsync(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);
      await categoryDifferencesAsync(accountingId.value, filterType, startDate, endDate);

      console.log("Category differences:", categoriesDifferences.value);

      hasData.value = (totalSpentMonth.value > 0 || Object.keys(categoriesDifferences.spentDifferences).length > 0 || Object.keys(categoriesDifferences.incomeDifferences).length > 0);

      await nextTick();
      initChart();
    };

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
            data: [totalSpentMonth.value, totalIncomeMonth.value],
            backgroundColor: ['#a01414', '#297243'],
          }]
        };

        chart.value = createChart(ctx, 'pie', summary, commonOptions, pieOptions);
      }
    };


    return {
      accountingId, hasData, totalSpentMonth, fetchIncomeMonthsAsync, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, fetchOperationsAsync, operations,
      updateData,
      showElement,
      paginatedOperations, totalPages, currentPage, nextPage, prevPage
    };
  },
};

</script>

<style scoped>

</style>