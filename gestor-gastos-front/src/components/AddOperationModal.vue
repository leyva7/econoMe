<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <!-- Encabezado del modal -->
    <div class="modal-header">
      <h5 v-if="isEditMode" class="modal-title">Editar operación</h5>
      <h5 v-else class="modal-title">Nueva Operación</h5>
      <button type="button" class="btn-close" @click="updateVisibility(false)"></button>
    </div>
    <!-- Cuerpo del modal con formulario -->
    <form @submit.prevent="submitOperations" class="modal-body" v-if="operation">
      <div class="mb-3">
        <label for="type" class="form-label">Tipo</label>
        <select id="type" v-model="operation.type" class="form-select">
          <option value="ingreso">Ingreso</option>
          <option value="gasto">Gasto</option>
        </select>
      </div>
      <div class="mb-3">
        <label for="optionSelect" class="form-label">Categoría</label>
        <select id="optionSelect" v-model="selectedOption" @change="onSelectChange" class="form-select">
          <option v-for="option in categories" :key="option" :value="option">
            {{ option }}
          </option>
          <option value="custom">Otra (Especificar)</option>
        </select>
      </div>
      <div class="mb-3" v-if="isCustomOptionSelected">
        <label for="customOption" class="form-label">Especifique</label>
        <input
            type="text"
            id="customOption"
            placeholder="Escriba su opción"
            v-model="customOption"
            class="form-control"
        />
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">Descripción</label>
        <textarea id="description" v-model="operation.description" class="form-control"></textarea>
      </div>
      <div class="mb-3">
        <label for="date" class="form-label">Fecha</label>
        <input type="date" id="date" v-model="operation.date" class="form-control">
        <button class="btn btn-outline-secondary mt-2" type="button" @click="setToday(operation)">Hoy</button>
      </div>
      <div class="mb-3">
        <label for="amount" class="form-label">Cantidad</label>
        <input type="number" id="amount" v-model="operation.quantity" step="0.01" class="form-control">
      </div>
      <!-- Pie del modal con botones -->
      <div class="modal-footer">
        <button type="submit" class="btn btn-primary">Aceptar</button>
        <button type="button" @click="updateVisibility(false)" class="btn btn-secondary">Cancelar</button>
      </div>
    </form>
  </ModalWindow>
</template>

<script>
import ModalWindow from './ModalWindow.vue'; // Importar componente ModalWindow
import {ref, defineComponent, computed, watch, onMounted} from 'vue';
import { useAccountingStore } from '@/stores/accountingStore';
import { globalStore } from "@/stores/globalStore";
import { createOperation, updateOperation } from "@/api/operationAPI"; // Importar API de operaciones
import { formatDateToDDMMYYYY } from "@/utils/functions";
import { saveToastMessage } from "@/utils/toastService";
import { setToday } from "@/utils/global";

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
    const {fetchCategoriesSpentAsync, fetchCategoriesIncomeAsync, categories} = useAccountingStore();
    const {accountingId} = globalStore();

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

    // Determinar el tipo inicial de la operación
    function determineInitialType() {
      if (props.operationToEdit) {
        return props.operationToEdit.type === 'INCOME' ? 'ingreso' : 'gasto';
      }
      return '';
    }

    // Inicializar el formulario
    const initializeForm = () => {
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

    // Limpiar el formulario
    const clearForm = () => {
      operation.value.type = '';
      operation.value.description = '';
      operation.value.category = '';
      operation.value.date = '';
      operation.value.quantity = '';
      customOption.value = '';
      selectedOption.value = '';
    };

    // Actualizar categorías basadas en el tipo
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
        initializeForm();
        updateCategories();
      }
    });

    const isCustomOptionSelected = computed(() => selectedOption.value === 'custom');

    // Manejar cambios en la selección de categoría
    const onSelectChange = () => {
      if (!isCustomOptionSelected.value) {
        customOption.value = '';
      }
    };

    watch(() => operation.value.type, () => {
      updateCategories();
    });

    watch([() => props.operationToEdit, () => props.isVisible], initializeForm, {immediate: true});

    // Actualizar visibilidad del modal
    const updateVisibility = (value) => {
      emit('update:isVisible', value);
      clearForm();
    };

    // Enviar datos de la operación
    const submitOperations = async () => {
      let categoryValid = isCustomOptionSelected.value ? customOption.value.trim() !== '' : selectedOption.value !== '';
      if (!categoryValid || !operation.value.type || !operation.value.date || operation.value.quantity <= 0) {
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
      // Asignar categoría basada en la opción seleccionada
      operation.value.category = isCustomOptionSelected.value ? customOption.value : selectedOption.value;
      operation.value.date = formatDateToDDMMYYYY(operation.value.date);

      try {
        if (isEditMode.value) {
          // Actualizar operación existente
          operation.value.id = props.operationToEdit.id;
          operation.value.accountingId = props.operationToEdit.accountingId;
          await updateOperation(operation.value);
          saveToastMessage('success', 'Operación modificada exitosamente');
        } else {
          await createOperation(operation.value);
          saveToastMessage('success', 'Operación añadida exitosamente');
        }

        await clearForm();
        updateVisibility(false);
        location.reload();
      } catch (error) {
        console.error('Error al registrar operación:', error);
        saveToastMessage('error', 'Algo falló en la operación. Por favor inténtelo de nuevo');
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
      initializeForm,
      isEditMode,
      setToday
    };
  }
});

</script>