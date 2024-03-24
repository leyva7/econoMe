<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <h2>Nueva Operación</h2>
    <form @submit.prevent="submitOperations">
      <div class="form-group">
        <label for="optionSelect">Tipo de gasto</label>
        <select id="optionSelect" v-model="selectedOption" @change="onSelectChange">
          <option v-for="option in categories" :key="option" :value="option">
            {{ option }}
          </option>
          <option value="custom">Otra (Especificar)</option>
        </select>
      </div>
      <div class="form-group" v-if="isCustomOptionSelected">
        <label for="customOption">Especifique</label>
        <input
            type="text"
            id="customOption"
            placeholder="Escriba su opción"
            v-model="customOption"
        />
      </div>
      <div class="form-group">
        <label for="description">Descripción</label>
        <textarea id="description" v-model="operation.description"></textarea>
      </div>
      <div class="form-group">
        <label for="type">Tipo</label>
        <select id="type" v-model="operation.type">
          <option value="ingreso">Ingreso</option>
          <option value="gasto">Gasto</option>
        </select>
      </div>
      <div class="form-group">
        <label for="date">Fecha</label>
        <input type="date" id="date" v-model="operation.date">
      </div>
      <div class="form-group">
        <label for="amount">Cantidad</label>
        <input type="number" id="amount" v-model="operation.quantity" step="0.01">
      </div>
      <div class="modal-actions">
        <button type="submit" class="btn-primary">Aceptar</button>
        <button type="button" @click="updateVisibility(false)" class="btn-secondary">Cancelar</button>
      </div>
    </form>
  </ModalWindow>
</template>

<script>
import ModalWindow from './ModalWindow.vue';
import {ref, defineComponent, computed, watch, onMounted} from 'vue';
import { useAccountingStore } from '../stores/accountingStore';
import { globalStore} from "@/stores/globalStore";
import { createOperation } from "@/service/operationService";

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean
  },
  setup(props, { emit }) {
    const { fetchCategoriesAsync, categories} = useAccountingStore();
    const { accountingId } = globalStore();

    const selectedOption = ref('');
    const customOption = ref('');
    const operation = ref({
      type: '',
      description: '',
      category: '',
      date: '',
      quantity: ''
    });

    const clearForm = () => {
      operation.value.type = '';
      operation.value.description = '';
      operation.value.category = '';
      operation.value.date = '';
      operation.value.quantity = '';
      customOption.value = ''; // Limpia también la opción personalizada si está seleccionada
      selectedOption.value = ''; // Limpia la opción seleccionada en el select
    };

    onMounted(() => {
      clearForm();

      if (props.isVisible) {
        fetchCategoriesAsync(accountingId.value);
      }
    });

    const isCustomOptionSelected = computed(() => selectedOption.value === 'custom');

    const onSelectChange = () => {
      if (!isCustomOptionSelected.value) {
        customOption.value = '';
      }
    };

    watch(() => props.isVisible, (isVisible) => {
      if (isVisible) {
        fetchCategoriesAsync(accountingId.value);
      }
    });

    const updateVisibility = (value) => {
      emit('update:isVisible', value);
    };
    const submitOperations = async () => {

      let categoryValid = isCustomOptionSelected.value ? customOption.value.trim() !== '' : selectedOption.value !== '';
      if (!categoryValid || !operation.value.type || !operation.value.date || !operation.value.description || operation.value.quantity <= 0) {
        alert('Por favor, completa todos los campos.');
        return;
      }
      try {
        operation.value.accountingId = accountingId;
        operation.value.username = localStorage.getItem('username');
        if (operation.value.type.toLowerCase() === 'ingreso') {
          operation.value.type = 'INCOME';
        } else {
          operation.value.type = 'SPENT';
        }
        // Asigna la categoría basada en si la opción personalizada está seleccionada o no
        operation.value.category = isCustomOptionSelected.value ? customOption.value : selectedOption.value;

        await createOperation(operation.value);
        alert('Operación registrada exitosamente.');
        await fetchCategoriesAsync(accountingId);
        await clearForm();
        updateVisibility(false);
        location.reload();
      } catch (error) {
        console.error('Error al registrar operación:', error);
        alert('Ocurrió un error al registrar la operación. Por favor, inténtalo de nuevo.');
      }
    };

    return {
      selectedOption,
      customOption,
      operation,
      updateVisibility,
      isCustomOptionSelected,
      onSelectChange,
      submitOperations,
      categories
    };
  }
});

</script>

<style>

</style>

