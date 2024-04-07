<template>
  <div v-if="isVisible" class="modal fade show d-block" tabindex="-1" role="dialog" @click.self="close">
    <!-- Añadir 'modal-dialog-centered' para centrar verticalmente -->
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-body">
          <slot></slot>
        </div>
      </div>
    </div>
  </div>
  <div v-if="isVisible" class="modal-backdrop fade show"></div>
</template>

<script>
export default {
  props: {
    isVisible: Boolean,
  },
  emits: ['update:isVisible'],
  methods: {
    close() {
      this.$emit('update:isVisible', false);
    },
  },
};
</script>

<style scoped>

</style>
