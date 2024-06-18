<template>
  <!-- Contenedor del modal principal -->
  <div v-if="isVisible" class="modal fade show d-block" tabindex="-1" role="dialog" @click.self="close">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-body">
          <slot></slot> <!-- Slot para contenido dinámico dentro del modal -->
        </div>
      </div>
    </div>
  </div>

  <!-- Fondo oscuro del modal -->
  <div v-if="isVisible" class="modal-backdrop fade show"></div>
</template>


<script>
export default {
  props: {
    isVisible: Boolean, // Propiedad booleana para controlar la visibilidad del modal
  },
  emits: ['update:isVisible'], // Evento emitido para actualizar la propiedad isVisible desde el componente padre
  methods: {
    close() {
      this.$emit('update:isVisible', false); // Emite evento para indicar que el modal debe cerrarse
    },
  },
};
</script>