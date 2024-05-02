<template>
  <div class="container mt-5">
    <h2 class="text-center mb-4">Resumen</h2>

    <IntervalSelector :isVisible="showElement" @update-selection="updateData" />

    <!-- Contenedor para Gastos VS Ingresos y Detalles -->
    <div class="row">
      <div class="col-lg-6 mb-3">
        <!-- Gastos VS Ingresos -->
        <div class="p-3 bg-white rounded shadow">
          <h3 class="text-center mb-3">Gastos VS Ingresos</h3>
          <div v-if="hasData">
            <div class="chart-container" v-show="hasData">
              <canvas id="comparativeChart"></canvas>
            </div>
          </div>
          <NoDataMessage v-else/>
        </div>
      </div>

      <div class="col-lg-6 mb-3">
        <!-- Detalles de Ingresos y Gastos -->
        <div class="row">
          <div class="col-12 mb-3">
            <div class="bg-white rounded shadow p-3">
              <h4 class="text-center" v-if="hasDataSpent">Gastos Totales: {{ totalSpentMonth.toFixed(2) }} €</h4>
              <NoDataMessage v-else message="Gastos Totales: No hay datos disponibles"/>
              <div v-if="hasDataSpent" class="row g-3">
                <div class="col-md-6">
                  <div class="card text-center">
                    <div v-if="categoriesDifferences && categoriesDifferences.spentDifferences" class="card-body">
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
                    <div v-if="categoriesDifferences && categoriesDifferences.spentDifferences" class="card-body">
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
              <h4 class="text-center" v-if="hasDataIncome">Ingresos Totales: {{ addEuroSymbol(totalIncomeMonth) }} </h4>
              <NoDataMessage v-else message="Ingresos Totales: No hay datos disponibles"/>
              <div v-if="hasDataIncome" class="row g-3">
                <div class="col-md-6">
                  <div class="card text-center">
                    <div v-if="categoriesDifferences && categoriesDifferences.incomeDifferences" class="card-body">
                      <h5  class="card-title">
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
                    <div v-if="categoriesDifferences && categoriesDifferences.incomeDifferences" class="card-body">
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
    </div>

    <!-- Últimas operaciones -->
    <div class="mt-3">
      <div class="bg-white rounded shadow p-3">
        <h3 class="text-center mb-3">Últimas operaciones</h3>
        <!-- Tabla de Últimas Operaciones -->
        <div v-if="hasData">
          <DataTable :pagination="paginations[0]" :columns="tableColumnsOperations" />
        </div>
        <div v-else>
          <NoDataMessage message="No hay operaciones recientes disponibles."/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import { Chart, registerables } from 'chart.js';
import { onMounted, ref, nextTick} from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import {useMultiplePagination} from "@/utils/usePagination";
import {addEuroSymbol, processFilterSelection} from "@/utils/functions";
import IntervalSelector from "./IntervalSelector.vue";
import NoDataMessage from "@/components/NoDataMessage.vue";
import {commonOptions, pieOptions, hasData, hasDataSpent, hasDataIncome, tableColumnsOperations} from "@/utils/global";
import {createChart} from "@/utils/chartService";
import DataTable from "@/components/DataTable.vue";

Chart.register(...registerables);
export default {
  name: "HomeDetails",
  methods: {addEuroSymbol},
  components:{ IntervalSelector, NoDataMessage, DataTable },
  setup() {
    const { accountingId, fetchSpentsInterval, totalSpentMonth, fetchIncomeInterval, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, operations, fetchOperationsAsync } = useAccountingStore();

    const paginationConfigs = [
      { data: operations, reduced: false },
    ];
    const paginations = useMultiplePagination(paginationConfigs);

    const chart = ref(null);

    const showElement = ref(false);

    onMounted(async () => {
      await updateData();
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
      await fetchIncomeInterval(accountingId.value, filterType, startDate, endDate);
      await fetchOperationsAsync(accountingId.value, filterType, startDate, endDate);
      await categoryDifferencesAsync(accountingId.value, filterType, startDate, endDate);

      hasData.value = totalSpentMonth.value > 0 || totalIncomeMonth.value > 0;
      hasDataSpent.value = totalSpentMonth.value > 0;
      hasDataIncome.value = totalIncomeMonth.value > 0;

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
            data: [totalIncomeMonth.value, totalSpentMonth.value],
            backgroundColor: ['#297243', '#a01414'],
          }]
        };

        chart.value = createChart(ctx, 'doughnut', summary, commonOptions, pieOptions);
      }
    };


    return {
      accountingId, totalSpentMonth, fetchIncomeInterval, totalIncomeMonth, topSpentCategory, topIncomeCategory, categoriesDifferences, categoryDifferencesAsync, fetchOperationsAsync, operations,
      updateData,
      showElement,
      hasData, hasDataIncome, hasDataSpent,
      paginations,
      tableColumnsOperations
    };
  },
};

</script>