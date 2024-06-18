<template>
  <!-- Contenedor de filtros, visible si isVisible es verdadero -->
  <div v-if="isVisible" class="filters-container pb-4">
    <!-- Selección de intervalo de tiempo -->
    <select v-model="selectedInterval" @change="handleSelectionChange" class="interval-select">
      <option value="last7days">Últimos 7 días</option>
      <option value="last30days">Últimos 30 días</option>
      <option value="custom">Personalizado</option>
    </select>

    <!-- Contenedor para intervalo personalizado, visible si selectedInterval es 'custom' -->
    <div v-if="selectedInterval === 'custom'" class="custom-interval-container me-2">
      <input type="date" v-model="customStart" class="date-input" /> <!-- Input para fecha de inicio -->
      <input type="date" v-model="customEnd" class="date-input" /> <!-- Input para fecha de fin -->
      <button @click="emitSelection(true)" class="update-button">Actualizar</button> <!-- Botón para actualizar selección -->
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';

export default {
  name: 'IntervalSelector',
  props: {
    isVisible: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    // Variables reactivas para el estado del componente
    const selectedInterval = ref('last7days'); // Valor inicial para el intervalo seleccionado
    const customStart = ref(''); // Fecha de inicio para intervalo personalizado
    const customEnd = ref(''); // Fecha de fin para intervalo personalizado

    // Función para emitir la selección al padre
    const emitSelection = (isCustom = false) => {
      let selection = {
        interval: selectedInterval.value, // Intervalo seleccionado (last7days, last30days, custom)
        dates: isCustom ? { start: customStart.value, end: customEnd.value } : null // Fechas personalizadas si es necesario
      };

      emit('update-selection', selection); // Emitir evento 'update-selection' con la selección actualizada
    };

    // Manejar cambio en la selección del intervalo
    const handleSelectionChange = () => {
      if (selectedInterval.value === 'custom') {
        // No emitir automáticamente cuando el intervalo es personalizado
      } else {
        emitSelection(); // Emitir selección si no es personalizado
      }
    };

    // Hook mounted: emitir selección inicial al montar el componente
    onMounted(() => {
      emitSelection(); // Emitir la selección inicial al montar el componente
    });

    // Retornar variables y funciones necesarias para el template
    return {
      selectedInterval,
      customStart,
      customEnd,
      emitSelection,
      handleSelectionChange
    };
  }
};
</script>
