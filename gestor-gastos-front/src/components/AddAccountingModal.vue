<template>
  <ModalWindow :isVisible="isVisible" @update:isVisible="updateVisibility">
    <h2>Añadir Contabilidad Compartida</h2>
    <form @submit.prevent="submitSharedAccounting">
      <div class="form-group">
        <label for="accountName">Nombre de contabilidad</label>
        <input type="text" id="accountName" v-model="accountName.value">
      </div>
      <div class="form-group">
        <label for="description">Descripción</label>
        <textarea id="description" v-model="description.value"></textarea>
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
import { ref, defineComponent } from 'vue';

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
        await axios.post('http://localhost:8081/api/accounting/register', accountingData, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('userToken')}`,
          },
        });
        alert("Contabilidad añadida con éxito");
        updateVisibility(false);
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
