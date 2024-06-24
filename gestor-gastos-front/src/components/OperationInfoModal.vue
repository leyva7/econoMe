<template>
  <!-- ModalWindow es un componente que maneja el comportamiento del modal -->
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <!-- Cabecera del modal con título y botón de cerrar -->
    <div class="modal-header">
      <h5 class="modal-title">Operación</h5>
      <button type="button" class="btn-close" @click="updateVisibility(false)"></button>
    </div>
    <!-- Cuerpo del modal con el formulario de detalles de la operación -->
    <form class="modal-body">
      <!-- Campo para mostrar el tipo de operación (readonly) -->
      <div class="mb-3">
        <label for="type" class="form-label">Tipo</label>
        <textarea id="type" v-model="computedType" class="form-control" disabled></textarea>
      </div>
      <!-- Campo para mostrar la categoría de la operación (readonly) -->
      <div class="mb-3">
        <label for="category" class="form-label">Categoría</label>
        <textarea id="category" v-model="operation.category" class="form-control" disabled></textarea>
      </div>
      <!-- Campo para mostrar la descripción de la operación (readonly) -->
      <div class="mb-3">
        <label for="description" class="form-label">Descripción</label>
        <textarea id="description" v-model="operation.description" class="form-control" disabled></textarea>
      </div>
      <!-- Campo para mostrar la fecha de la operación (readonly) -->
      <div class="mb-3">
        <label for="date" class="form-label">Fecha</label>
        <input type="date" id="date" v-model="computedDate" class="form-control" disabled>
      </div>
      <!-- Campo para mostrar la cantidad de la operación (readonly) -->
      <div class="mb-3">
        <label for="amount" class="form-label">Cantidad</label>
        <input type="number" id="amount" v-model="operation.quantity" step="0.01" class="form-control" disabled>
      </div>
      <!-- Pie del modal con botón de cerrar -->
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="updateVisibility(false)">Cerrar</button>
      </div>
    </form>
  </ModalWindow>
</template>

<script>
import ModalWindow from './ModalWindow.vue';
import { computed, defineComponent, toRefs } from 'vue';
import { formatAsYYYYMMDD } from "@/utils/functions";

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean, // Propiedad booleana que indica si el modal debe mostrarse o no
    operationToShow: { // Objeto que representa los detalles de la operación a mostrar en el modal
      type: Object,
      default: () => ({
        type: '',
        category: '',
        description: '',
        date: '',
        quantity: 0
      }),
    },
  },
  setup(props, { emit }) {
    const { operationToShow } = toRefs(props); // Desestructura las props para acceder a operationToShow

    // Función para emitir el evento que actualiza la visibilidad del modal
    const updateVisibility = (value) => {
      emit('update:isVisible', value); // Emite el evento 'update:isVisible' con el nuevo valor de visibilidad
    };

    function determineInitialType() {
      if (props.operationToShow) {
        return props.operationToShow.type === 'INCOME' ? 'Ingreso' : 'Gasto';
      }
      return '';
    }

    // Propiedades computadas para mostrar el tipo y fecha de manera formateada
    const computedType = computed(() => determineInitialType(operationToShow.value.type));
    const computedDate = computed(() => formatAsYYYYMMDD(operationToShow.value.date));

    return {
      operation: operationToShow, // Retorna operationToShow para acceder a sus propiedades dentro del componente
      computedType, // Retorna la propiedad computada computedType para mostrar el tipo formateado
      computedDate, // Retorna la propiedad computada computedDate para mostrar la fecha formateada
      updateVisibility // Retorna la función updateVisibility para manejar eventos de visibilidad del modal
    };
  }
});
</script>
