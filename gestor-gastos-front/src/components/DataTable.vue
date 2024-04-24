<template>
  <div class="table-container table-responsive">
    <table class="table">
      <thead>
      <tr>
        <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in data" :key="index">
        <td v-for="column in columns" :key="column.key">
          {{ column.key === 'type' ? getTypeRepresentation(item[column.key]) : (column.key === 'quantity' || column.key === 'savings' || column.key === 'total' ? addEuroSymbol(item[column.key]) : item[column.key]) }}
        </td>
      </tr>
      </tbody>
    </table>
    <div v-if="totalPages > 1" class="pagination-container d-flex justify-content-center mb-4">
      <button @click="$emit('prev-page')" class="btn btn-secondary me-2" :disabled="currentPage <= 1">Anterior</button>
      <span class="me-2">Página {{ currentPage }} de {{ totalPages }}</span>
      <button @click="$emit('next-page')" class="btn btn-secondary" :disabled="currentPage >= totalPages">Siguiente</button>
    </div>
  </div>
</template>

<script>
import { getTypeRepresentation, addEuroSymbol } from "../utils/functions";

export default {
  methods: { addEuroSymbol, getTypeRepresentation },
  props: {
    data: {
      type: Array,
      required: true
    },
    columns: {
      type: Array,
      required: true
    },
    currentPage: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    }
  }
};
</script>
