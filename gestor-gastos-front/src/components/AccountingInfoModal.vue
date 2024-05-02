<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <div class="modal-header">
      <h5 class="modal-title">{{ isEditing ? 'Editar Contabilidad' : 'Detalles de la Contabilidad' }}</h5>
      <button type="button" class="btn-close" @click="updateVisibility(false)"></button>
    </div>
    <form class="modal-body" @submit.prevent="isEditing ? updateAccount() : updateVisibility(false)">
      <div class="mb-3">
        <label for="accountName" class="form-label">Nombre de contabilidad</label>
        <input type="text" id="accountName" v-model="accountName" class="form-control" :disabled="!isEditing">
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">Descripción</label>
        <textarea id="description" v-model="description" class="form-control" :disabled="!isEditing"></textarea>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="toggleEditing">{{ isEditing ? 'Cancelar' : 'Editar' }}</button>
        <button type="submit" class="btn btn-primary text-white">{{ isEditing ? 'Guardar' : 'Cerrar' }}</button>
      </div>
    </form>
  </ModalWindow>
</template>

<script>
import ModalWindow from './ModalWindow.vue';
import {defineComponent, onMounted, ref, watch} from 'vue';
import {useAccountingStore} from '@/stores/accountingStore';
import {updateAccounting} from "@/api/accountingAPI";

export default defineComponent({
  components: {
    ModalWindow
  },
  props: {
    isVisible: Boolean,
    initialData: {
      type: Object,
      default: () => ({})
    }
  },
  emits: ['update:isVisible', 'save'],
  setup(props, {emit}) {
    const {loadAccountings, currentAccounting} = useAccountingStore();
    const isEditing = ref(false);
    const accountName = ref('');
    const description = ref('');

    onMounted(async () => {
      await loadAccountings();
    });

    watch(currentAccounting, (newData) => {
      if (newData) {
        accountName.value = newData.name || '';
        description.value = newData.description || '';
      }
    }, {immediate: true});

    const updateVisibility = (value) => {
      emit('update:isVisible', value);
      if (!value) {
        isEditing.value = false; // Reset editing state when modal closes
      }
    };

    const toggleEditing = () => {
      if (isEditing.value) {
        accountName.value = currentAccounting.value.name || '';
        description.value = currentAccounting.value.description || '';
        isEditing.value = false;
      } else {
        isEditing.value = true;
      }
    };

    const updateAccount = async () => {
      // Validar que el nombre no esté vacío
      if (!accountName.value.trim()) {
        alert('El nombre de la contabilidad no puede estar vacío.');
        return; // Salir del método si el nombre está vacío
      }

      try {
        const newData = {
          name: accountName.value,
          description: description.value,
          type: currentAccounting.value.type,
          userCreator: currentAccounting.value.userCreator
        };

        // Llamar a la función de actualización
        await updateAccounting(currentAccounting.value.id, newData);
        emit('save', newData); // Emitir evento con los nuevos datos
        isEditing.value = false;
        updateVisibility(false);
        alert('La contabilidad se ha actualizado correctamente.');
        await loadAccountings(); // Recargar contabilidades para reflejar los cambios
        location.reload(); // Recargar la página para asegurar que todos los componentes se actualicen
      } catch (error) {
        console.error('Error al actualizar la contabilidad:', error);
        alert('Se produjo un error al actualizar la contabilidad. Por favor, inténtalo de nuevo.');
      }
    };

    return {
      accountName,
      description,
      isEditing,
      updateVisibility,
      toggleEditing,
      updateAccount,
      currentAccounting,
    };
  }
});
</script>

