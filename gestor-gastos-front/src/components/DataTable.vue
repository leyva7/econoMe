<template>
  <!-- Contenedor principal de la tabla, con diseño responsive -->
  <div class="table-container table-responsive">
    <!-- Tabla con encabezados dinámicos basados en las columnas proporcionadas -->
    <table class="table">
      <thead>
      <tr>
        <!-- Itera sobre las columnas para crear los encabezados -->
        <th v-for="column in columns" :key="column.key">{{ column.label }}</th>
      </tr>
      </thead>
      <tbody>
      <!-- Itera sobre los datos paginados para crear filas -->
      <tr v-for="(item, index) in pagination.paginatedData.value" :key="index">
        <!-- Itera sobre las columnas para llenar las celdas -->
        <td v-for="column in columns" :key="column.key">
          <!-- Muestra el contenido de la celda, aplicando transformaciones según la clave de la columna -->
          {{ column.key === 'type' ? getTypeRepresentation(item[column.key]) : (column.key === 'quantity' || column.key === 'savings' || column.key === 'total' ? addEuroSymbol(item[column.key]) : item[column.key]) }}
        </td>
      </tr>
      </tbody>
    </table>

    <!-- Se muestra solo si hay más de una página de datos paginados -->
    <div v-if="pagination.totalPages.value > 1" class="pagination-container d-flex justify-content-center mb-4">
      <!-- Botón para ir a la página anterior, deshabilitado si estamos en la primera página -->
      <button @click="pagination.prevPage" class="btn btn-secondary me-2" :disabled="pagination.currentPage.value <= 1">Anterior</button>
      <!-- Muestra el número de página actual y el total de páginas -->
      <span class="me-2">Página {{ pagination.currentPage.value }} de {{ pagination.totalPages.value }}</span>
      <!-- Botón para ir a la página siguiente, deshabilitado si estamos en la última página -->
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
