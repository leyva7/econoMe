<template>
  <div v-if="isVisible" class="filters-container pb-4">
    <select v-model="selectedInterval" @change="handleSelectionChange" class="interval-select">
      <option value="last7days">Últimos 7 días</option>
      <option value="last30days">Últimos 30 días</option>
      <option value="custom">Personalizado</option>
    </select>
    <div v-if="selectedInterval === 'custom'" class="custom-interval-container me-2">
      <input type="date" v-model="customStart" class="date-input" />
      <input type="date" v-model="customEnd" class="date-input" />
      <button @click="emitSelection(true)" class="update-button">Actualizar</button>
    </div>
  </div>
</template>

<script>
import {onMounted, ref} from 'vue';

export default {
  name: 'IntervalSelector',
  props: {
    isVisible: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const selectedInterval = ref('last7days');
    const customStart = ref('');
    const customEnd = ref('');

    const emitSelection = (isCustom = false) => {
      let selection = {
        interval: selectedInterval.value,
        dates: isCustom ? { start: customStart.value, end: customEnd.value } : null
      };

      emit('update-selection', selection);
    };

    const handleSelectionChange = () => {
      if (selectedInterval.value === 'custom') {
        // No emitir automáticamente cuando es personalizado
      } else {
        emitSelection();
      }
    };

    onMounted(() => {
        emitSelection();
    });

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
