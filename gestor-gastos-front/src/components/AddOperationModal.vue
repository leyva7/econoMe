<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <h2>Nueva Operación</h2>
    <form @submit.prevent="submitOperations">
      <div class="form-group">
        <label for="type">Tipo</label>
        <select id="type" v-model="operation.type">
          <option value="ingreso">Ingreso</option>
          <option value="gasto">Gasto</option>
        </select>
      </div>
      <div class="form-group">
        <label for="optionSelect">Categoría</label>
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
import { createOperation, updateOperation } from "@/service/operationService";

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean,
    operationToEdit: {
      type: Object,
      default: () => null,
    },
  },
  setup(props, { emit }) {
    const { fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, categories, formatDateToDDMMYYYY } = useAccountingStore();
    const { accountingId } = globalStore();

    const isEditMode = computed(() => props.operationToEdit !== null);

    const selectedOption = ref('');
    const customOption = ref('');
    const operation = ref({
      id: '',
      type: determineInitialType(),
      description: '',
      category: '',
      date: '',
      quantity: ''
    });

    function determineInitialType() {
      // Esta función determina el valor inicial de "type"
      if (props.operationToEdit) {
        return props.operationToEdit.type === 'INCOME' ? 'ingreso' : 'gasto';
      }
      return ''; // Valor por defecto si no es modo de edición
    }

    const initializeForm = () => {
      console.log(isEditMode.value);
      if (isEditMode.value) {
        // Convertir el valor de type de INCOME/SPENT a ingreso/gasto para el formulario
        operation.value.type = props.operationToEdit.type === 'INCOME' ? 'ingreso' : 'gasto';

        // Establecer la fecha, descripción y cantidad directamente
        let dateParts = props.operationToEdit.date.split('-');
        operation.value.date = `${dateParts[2]}-${dateParts[1]}-${dateParts[0]}`;
        operation.value.quantity = props.operationToEdit.quantity;
        operation.value.description = props.operationToEdit.description;
        operation.value.id = props.operationToEdit.id;

        // Verificar si la categoría existe en la lista de categorías disponibles
        selectedOption.value = props.operationToEdit.category;
      } else {
        clearForm();
      }
    };

    const clearForm = () => {
      operation.value.type = '';
      operation.value.description = '';
      operation.value.category = '';
      operation.value.date = '';
      operation.value.quantity = '';
      customOption.value = ''; // Limpia también la opción personalizada si está seleccionada
      selectedOption.value = ''; // Limpia la opción seleccionada en el select
    };

    const updateCategories = () => {
      const currentAccountingId = isEditMode.value ? props.operationToEdit.accountingId : accountingId.value;
      if (operation.value.type === 'ingreso') {
        fetchCategoriesIncomeAsync(currentAccountingId);
      } else if (operation.value.type === 'gasto') {
        fetchCategoriesSpentAsync(currentAccountingId);
      }
    };

    onMounted(() => {
      clearForm();
      if (props.isVisible) {
        initializeForm()
        updateCategories();
      }
    });

    const isCustomOptionSelected = computed(() => selectedOption.value === 'custom');

    const onSelectChange = () => {
      if (!isCustomOptionSelected.value) {
        customOption.value = '';
      }
    };

    watch(() => operation.value.type, () => {
      updateCategories();
    });

    watch([() => props.operationToEdit, () => props.isVisible], initializeForm, { immediate: true });

    const updateVisibility = (value) => {
      emit('update:isVisible', value);
      clearForm();
    };
    const submitOperations = async () => {

      let categoryValid = isCustomOptionSelected.value ? customOption.value.trim() !== '' : selectedOption.value !== '';
      if (!categoryValid || !operation.value.type || !operation.value.date || !operation.value.description || operation.value.quantity <= 0) {
        alert('Por favor, completa todos los campos.');
        return;
      }

      operation.value.accountingId = accountingId;
      operation.value.username = localStorage.getItem('username');
      if (operation.value.type.toLowerCase() === 'ingreso') {
        operation.value.type = 'INCOME';
      } else {
        operation.value.type = 'SPENT';
      }
      // Asigna la categoría basada en si la opción personalizada está seleccionada o no
      operation.value.category = isCustomOptionSelected.value ? customOption.value : selectedOption.value;
      operation.value.date = formatDateToDDMMYYYY(operation.value.date);

      try {

        if (isEditMode.value) {
          // Suponiendo que tienes una función updateOperation disponible para actualizar la operación
          operation.value.id = props.operationToEdit.id;
          operation.value.accountingId = props.operationToEdit.accountingId;
          console.log(operation.value);
          await updateOperation(operation.value);
          alert('Operación actualizada exitosamente.');
        } else {
          await createOperation(operation.value);
          alert('Operación registrada exitosamente.');
        }

        await fetchCategoriesSpentAsync(accountingId);
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
      categories,
      initializeForm
    };
  }
});

</script>

<style>

</style>

