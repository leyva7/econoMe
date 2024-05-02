<template>
  <div class="table-container table-responsive">
    <table class="table">
      <thead>
      <tr>
        <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in pagination.paginatedData.value" :key="index">
        <td v-for="column in columns" :key="column.key">
          {{ column.key === 'type' ? getTypeRepresentation(item[column.key]) : (column.key === 'quantity' || column.key === 'savings' || column.key === 'total' ? addEuroSymbol(item[column.key]) : item[column.key]) }}
        </td>
      </tr>
      </tbody>
    </table>
    <div v-if="pagination.totalPages.value > 1" class="pagination-container d-flex justify-content-center mb-4">
      <button @click="pagination.prevPage" class="btn btn-secondary me-2" :disabled="pagination.currentPage.value <= 1">Anterior</button>
      <span class="me-2">Página {{ pagination.currentPage.value }} de {{ pagination.totalPages.value }}</span>
      <button @click="pagination.nextPage" class="btn btn-secondary" :disabled="pagination.currentPage.value >= pagination.totalPages.value">Siguiente</button>
    </div>
  </div>
</template>

<script>
import { getTypeRepresentation, addEuroSymbol } from "../utils/functions";

export default {
  methods: { addEuroSymbol, getTypeRepresentation },
  props: {
    pagination: {
      type: Object,
      required: true
    },
    columns: {
      type: Array,
      required: true
    }
  }
};
</script>
