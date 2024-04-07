<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <div class="modal-header">
      <h5 class="modal-title">Añadir Contabilidad Compartida</h5>
      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="updateVisibility(false)"></button>
    </div>
    <form @submit.prevent="submitSharedAccounting" class="modal-body">
      <div class="mb-3">
        <label for="accountName" class="form-label">Nombre de contabilidad</label>
        <input type="text" id="accountName" v-model="accountName" class="form-control">
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">Descripción</label>
        <textarea id="description" v-model="description" class="form-control"></textarea>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="updateVisibility(false)">Cancelar</button>
        <button type="submit" class="btn bg-custom-blue-900 text-white">Aceptar</button>
      </div>
    </form>
  </ModalWindow>
</template>

<script>
import ModalWindow from './ModalWindow.vue';
import { ref, defineComponent } from 'vue';
import { createAccounting } from "@/service/accountingService";

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean
  },
  setup(props, { emit }) {
    const accountName = ref('');
    const description = ref('');

    const updateVisibility = (value) => {
      emit('update:isVisible', value);
    };

    const submitSharedAccounting = async () => {
      if (!accountName.value || !description.value) {
        alert('Por favor, rellena todos los campos del formulario.');
        return;
      }

      if (accountName.value.trim().toLowerCase() === 'contabilidad personal') {
        alert('No se puede añadir una contabilidad compartida con el nombre "Contabilidad personal". Por favor, elige otro nombre.');
        return;
      }

      const accountingData = {
        name: accountName.value,
        description: description.value,
        userCreator: localStorage.getItem('username'),
        type: 'SHARED'
      };

      try {
        await createAccounting(accountingData);
        alert("Contabilidad añadida con éxito");
        updateVisibility(false);
        location.reload();
      } catch (error) {
        console.error('Error al añadir contabilidad:', error);
        alert('Ocurrió un error al añadir la contabilidad. Por favor, inténtalo de nuevo.');
      }
    };

    return {
      accountName,
      description,
      updateVisibility,
      submitSharedAccounting
    };
  }
});
</script>

<style>

</style>
