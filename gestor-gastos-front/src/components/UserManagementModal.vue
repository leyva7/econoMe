<template>
  <transition name="modal">
    <div v-if="isVisible" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>Gestión de Usuarios</h2>
        <button class="close-modal" @click="closeModal">X</button>
        <section class="user-list">
          <h3>Usuarios Actuales</h3>
          <ul>
            <li v-for="user in users" :key="user.id">
              {{ user.username }}
              <button @click="removeUser(user.username)">Eliminar</button>
            </li>
          </ul>
        </section>
        <section class="add-user">
          <h3>Agregar Usuario</h3>
          <form @submit.prevent="addUser">
            <input v-model="newUserName" placeholder="Nombre de usuario" required>
            <select v-model="newUserRole">
              <option value="EDITOR">Editor</option>
              <option value="VISUALIZER">Visualizador</option>
            </select>
            <button type="submit">Agregar</button>
          </form>
        </section>
      </div>
    </div>
  </transition>
</template>

<script>
import { onMounted, ref } from 'vue';
import { useAccountingStore } from "@/stores/accountingStore";
import { addUser as addUserApi, deleteUserAccounting } from "@/service/userRoleService";

export default {
  name: 'UserManagementModal',
  props: ['isVisible'],
  setup(props, { emit }) {
    const accountingStore = useAccountingStore();
    const { accountingId, usersAccounting } = accountingStore;

    // Define newUserName y newUserRole como refs para mantener su estado reactivamente
    const newUserName = ref('');
    const newUserRole = ref('EDITOR'); // Valor inicial predeterminado

    onMounted(async () => {
      await accountingStore.fetchUsersAccountingAsync(accountingId.value);
    });

    const removeUser = async(username) => {
      try{
        deleteUserAccounting(accountingId.value, username);
        console.log(`Eliminado usuario con username: ${username}`);
        closeModal();
        await accountingStore.fetchUsersAccountingAsync(accountingId.value);
      }
      catch (error){
        console.error('Error al borrar usuario:', error);
        alert('Ocurrió un error al borrar el usuario. Por favor, inténtalo de nuevo.');
      }
    };

    const addUser = async () => {
      const accountingData = {
        username: newUserName.value, // Usa .value para acceder al valor actual de la ref
        accountingId: accountingId.value, // Asegúrate de obtener el ID correctamente
        role: newUserRole.value // Usa .value para acceder al valor actual de la ref
      };
      try {
        await addUserApi(accountingData.accountingId, accountingData);
        alert("Usuario añadido con éxito");
        closeModal();
        await accountingStore.fetchUsersAccountingAsync(accountingId.value);
      } catch (error) {
        console.error('Error al añadir usuario:', error);
        alert('Ocurrió un error al añadir el usuario. Por favor, inténtalo de nuevo.');
      }
      newUserName.value = ''; // Reinicia el valor de newUserName después de añadir el usuario
    };

    const closeModal = () => {
      emit('close');
    };

    return {
      users: usersAccounting,
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
.modal-enter-active, .modal-leave-active {
  transition: opacity 0.5s;
}

.modal-enter, .modal-leave-to /* .modal-leave-active in <2.1.8 */
{
  opacity: 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: relative;
}

.close-modal {
  position: absolute;
  top: 10px;
  right: 10px;
  border: none;
  background: none;
  cursor: pointer;
}

h2{
  color: #000000;
}

.user-list, .add-user {
  margin-top: 20px;
}

.user-list ul {
  list-style: none;
  padding: 0;
}

.user-list li {
  margin-bottom: 10px;
}

.add-user form {
  display: flex;
  gap: 10px;
}

.add-user input {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
