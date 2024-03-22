<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <h2>Nueva Operación</h2>
    <form @submit.prevent="submitOperations">
      <div class="form-group">
        <label for="optionSelect">Tipo de gasto</label>
        <select id="optionSelect" v-model="selectedOption" @change="onSelectChange">
          <option v-for="option in options" :key="option" :value="option">
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
import axios from "axios";
import {ref, defineComponent, computed, watchEffect, onMounted} from 'vue';
import { useRoute } from 'vue-router';

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean
  },
  setup(props, { emit }) {
    const route = useRoute(); // Utiliza useRoute para acceder al route

    const selectedOption = ref('');
    const customOption = ref('');
    const options = ref(['']);
    const accountingId = ref(null);
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
    });

    const isCustomOptionSelected = computed(() => selectedOption.value === 'custom');

    const onSelectChange = () => {
      if (!isCustomOptionSelected.value) {
        customOption.value = '';
      }
    };

    watchEffect(() => {
      if (props.isVisible && route.query.id) {
        fetchCategories(route.query.id);
      }
      accountingId.value = route.query.id;
    });

    const updateVisibility = (value) => {
      emit('update:isVisible', value);
    };

    const fetchCategories = async (accountingId) => {
      console.log(accountingId)
      try {
        const response = await axios.get(`http://localhost:8081/api/accounting/${accountingId}/categories`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        options.value = response.data;
        console.log(options.value);
      } catch (error) {
        console.error('Error al cargar las categorías:', error);
        options.value = [];
      }
    };


    const submitOperations = async () => {
      console.log(operation.value);
      console.log(customOption.value);

      let categoryValid = isCustomOptionSelected.value ? customOption.value.trim() !== '' : selectedOption.value !== '';
      if (!categoryValid || !operation.value.type || !operation.value.date || !operation.value.description || operation.value.quantity <= 0) {
        alert('Por favor, completa todos los campos.');
        return;
      }

      try {
        operation.value.accountingId = accountingId;
        operation.value.username = localStorage.getItem('username');
        if (operation.value.type.toLowerCase() === 'ingreso') { // Corregido para usar '()'
          operation.value.type = 'INCOME';
        } else {
          operation.value.type = 'SPENT';
        }

        // Asigna la categoría basada en si la opción personalizada está seleccionada o no
        operation.value.category = isCustomOptionSelected.value ? customOption.value : selectedOption.value;

        console.log(operation.value);
        const response = await axios.post('http://localhost:8081/api/accounting/operation/register', operation.value, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        console.log(response);
        alert('Operación registrada exitosamente.');
        await fetchCategories(accountingId);
        updateVisibility(false);
      } catch (error) {
        console.error('Error al registrar operación:', error);
        alert('Ocurrió un error al registrar la operación. Por favor, inténtalo de nuevo.');
      }
    };

    return {
      selectedOption,
      customOption,
      operation,
      options,
      updateVisibility,
      isCustomOptionSelected,
      onSelectChange,
      submitOperations,
      fetchCategories
    };
  }
});

</script>

<style>

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
}

.form-group input,
.form-group textarea {
  width: calc(100% - 20px); /* Ajustar para padding */
  padding: 10px;
  margin-top: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 4px;
  cursor: pointer;
}
</style>

