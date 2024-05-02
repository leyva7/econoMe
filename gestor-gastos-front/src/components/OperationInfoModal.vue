<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <div class="modal-header">
      <h5 class="modal-title">Operación</h5>
      <button type="button" class="btn-close" @click="updateVisibility(false)"></button>
    </div>
    <form class="modal-body">
      <div class="mb-3">
        <label for="type" class="form-label">Tipo</label>
        <textarea id="type" v-model="operation.type" class="form-control" disabled></textarea>
      </div>
      <div class="mb-3">
        <label for="category" class="form-label">Categoría</label>
        <textarea id="category" v-model="operation.category" class="form-control" disabled></textarea>
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">Descripción</label>
        <textarea id="description" v-model="operation.description" class="form-control" disabled></textarea>
      </div>
      <div class="mb-3">
        <label for="date" class="form-label">Fecha</label>
        <input type="date" id="date" v-model="operation.date" class="form-control" disabled>
      </div>
      <div class="mb-3">
        <label for="amount" class="form-label">Cantidad</label>
        <input type="number" id="amount" v-model="operation.quantity" step="0.01" class="form-control" disabled>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="updateVisibility(false)">Cerrar</button>
      </div>
    </form>
  </ModalWindow>
</template>

<script>
import ModalWindow from './ModalWindow.vue';
import { defineComponent, toRefs } from 'vue';

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean,
    operationToShow: {
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
  setup(props, {emit}) {
    const {operationToShow} = toRefs(props);

    const updateVisibility = (value) => {
      emit('update:isVisible', value);
    };

    return {
      operation: operationToShow,
      updateVisibility
    };
  }
});

</script>

<style>

</style>
