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
            <div v-for="(user) in usersAccounting" :key="user.id" class="form-check">
              <input class="form-check-input" type="checkbox" :value="user.username" v-model="selectedUsers">
              <label class="form-check-label" for="user.username">
                {{ user.username }}
              </label>
            </div>
            <button class="btn btn-danger w-100 mt-3" @click="removeSelectedUsers">Eliminar seleccionados</button>
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
import { addUser as addUserApi, deleteUserAccounting } from "@/api/userRoleAPI";

export default {
  name: 'UserManagementModal',
  props: ['isVisible'],
  setup(props, { emit }) {
    const accountingStore = useAccountingStore();
    const { accountingId, fetchUsersAccountingAsync, usersAccounting } = accountingStore;

    const newUserName = ref('');
    const newUserRole = ref('EDITOR');
    const selectedUsers = ref([]);

    onMounted(async () => {
      await fetchUsersAccountingAsync(accountingId.value);
      console.log(usersAccounting);
    });

    const closeModal = () => {
      emit('update:isVisible', false);
    };

    const removeSelectedUsers = async () => {
      try {
        for (const username of selectedUsers.value) {
          await deleteUserAccounting(accountingId.value, username);
          console.log(`Eliminado usuario con username: ${username}`);
        }
        alert("Usuarios borrados con éxito");
        await fetchUsersAccountingAsync(accountingId.value);
        selectedUsers.value = [];
        closeModal();
      } catch (error) {
        console.error('Error al borrar usuarios:', error);
        alert('Ocurrió un error al borrar los usuarios. Por favor, inténtalo de nuevo.');
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
        await fetchUsersAccountingAsync(accountingId.value);
        closeModal();
      } catch (error) {
        console.error('Error al añadir usuario:', error);
        alert('Ocurrió un error al añadir el usuario. Por favor, inténtalo de nuevo.');
      }
      newUserName.value = '';
    };

    return {
      usersAccounting,
      newUserName,
      newUserRole,
      selectedUsers,
      removeSelectedUsers,
      addUser,
      closeModal
    };
  }
}
</script>



<style scoped>

</style>
