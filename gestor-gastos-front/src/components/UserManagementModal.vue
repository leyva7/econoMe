<template>
  <div class="modal fade" :class="{ show: isVisible }" tabindex="-1" role="dialog" style="display: block;" aria-hidden="true" v-if="isVisible">
    <div class="modal-dialog modal-dialog-centered" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Gestionar usuarios</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" @click="closeModal(false)"></button>
        </div>
        <div class="modal-body">
          <section class="add-user mb-3">
            <h3>Agregar Usuario</h3>
            <form @submit.prevent="addUser" class="mb-3">
              <div class="mb-3">
                <input v-model="newUserName" placeholder="Nombre de usuario" required class="form-control">
              </div>
              <div class="mb-3">
                <select v-model="newUserRole" class="form-select">
                  <option value="EDITOR">Editor</option>
                  <option value="VISUALIZER">Visualizador</option>
                </select>
              </div>
              <button type="submit" class="btn btn-primary w-100">Agregar</button>
            </form>
          </section>
          <section class="user-list">
            <h3>Usuarios Actuales</h3>
            <div class="mb-3">
              <label for="userSelect" class="form-label">Seleccionar usuario para eliminar:</label>
              <select id="userSelect" v-model="selectedUser" class="form-select mb-2">
                <option v-for="user in usersAccounting" :key="user.id" :value="user.username">{{ user.username }}</option>
              </select>
            </div>
            <button class="btn btn-danger w-100" @click="removeUser(selectedUser)">Eliminar Usuario</button>
          </section>
        </div>
      </div>
    </div>
  </div>
  <div class="modal-backdrop fade" :class="{ show: isVisible }" v-if="isVisible"></div>
</template>

<script>
import { onMounted, ref } from 'vue';
import { useAccountingStore } from "@/stores/accountingStore";
import {addUser as addUserApi, deleteUserAccounting} from "@/service/userRoleService";

export default {
  name: 'UserManagementModal',
  props: ['isVisible'],
  setup(props, { emit }) {

    const accountingStore = useAccountingStore();
    const { accountingId, fetchUsersAccountingAsync, usersAccounting } = accountingStore;

    const newUserName = ref('');
    const newUserRole = ref('EDITOR');

    onMounted(async () => {
      await fetchUsersAccountingAsync(accountingId.value);
    });

    const closeModal = () => {
      emit('update:isVisible', false);
    };


    const removeUser = async(username) => {
      try{
        deleteUserAccounting(accountingId.value, username);
        console.log(`Eliminado usuario con username: ${username}`);
        alert("Usuario borrado con éxito");
        await accountingStore.fetchUsersAccountingAsync(accountingId.value);
        closeModal();
      }
      catch (error){
        console.error('Error al borrar usuario:', error);
        alert('Ocurrió un error al borrar el usuario. Por favor, inténtalo de nuevo.');
      }
    };

    const addUser = async () => {
      const accountingData = {
        username: newUserName.value,
        accountingId: accountingId.value,
        role: newUserRole.value
      };
      try {
        await addUserApi(accountingData.accountingId, accountingData);
        alert("Usuario añadido con éxito");
        await accountingStore.fetchUsersAccountingAsync(accountingId.value);
        closeModal();
      } catch (error) {
        console.error('Error al añadir usuario:', error);
        alert('Ocurrió un error al añadir el usuario. Por favor, inténtalo de nuevo.');
      }
      newUserName.value = ''; // Reinicia el valor de newUserName después de añadir el usuario
    };

    return {
      usersAccounting,
      newUserName,
      newUserRole,
      removeUser,
      addUser,
      closeModal
    };
  }
}
</script>



<style scoped>

</style>
