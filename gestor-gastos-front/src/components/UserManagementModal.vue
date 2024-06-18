<script>
import { onMounted, ref } from 'vue'; // Importa las funciones 'onMounted' y 'ref' de Vue
import { useAccountingStore } from "@/stores/accountingStore"; // Importa el hook 'useAccountingStore' desde el store accountingStore
import { addUser as addUserApi, deleteUserAccounting } from "@/api/userRoleAPI"; // Importa funciones específicas para añadir usuario y eliminar usuario desde userRoleAPI
import { saveToastMessage } from "@/utils/toastService"; // Importa la función 'saveToastMessage' desde toastService

export default {
  name: 'UserManagementModal', // Nombre del componente
  props: ['isVisible'], // Propiedad que indica si el modal es visible
  setup(props, { emit }) { // Configuración del componente usando el hook 'setup'

    // Acceso al store de accounting usando el hook 'useAccountingStore'
    const accountingStore = useAccountingStore();
    const { accountingId, fetchUsersAccountingAsync, usersAccounting } = accountingStore; // Desestructura las propiedades necesarias del store

    // Variables reactivas para manejar el estado del formulario
    const newUserName = ref(''); // Nombre del nuevo usuario a agregar
    const newUserRole = ref('EDITOR'); // Rol predeterminado del nuevo usuario a agregar
    const selectedUsers = ref([]); // Lista de usuarios seleccionados para eliminar

    // Método ejecutado al montar el componente
    onMounted(async () => {
      await fetchUsersAccountingAsync(accountingId.value); // Carga inicial de usuarios de la contabilidad al montar el componente
    });

    // Cierra el modal emitiento un evento para actualizar la visibilidad
    const closeModal = () => {
      emit('update:isVisible', false); // Emite un evento para actualizar 'isVisible' a falso, cerrando el modal
    };

    // Elimina los usuarios seleccionados
    const removeSelectedUsers = async () => {
      try {
        for (const username of selectedUsers.value) { // Itera sobre los usuarios seleccionados
          await deleteUserAccounting(accountingId.value, username); // Llama a la API para eliminar cada usuario seleccionado
        }
        saveToastMessage('success', 'Usuarios borrados con éxito'); // Muestra un mensaje de éxito
        await fetchUsersAccountingAsync(accountingId.value); // Vuelve a cargar la lista de usuarios después de eliminar
        selectedUsers.value = []; // Limpia la lista de usuarios seleccionados
        location.reload(); // Recarga la página para reflejar los cambios
        closeModal(); // Cierra el modal
      } catch (error) {
        console.error('Error al borrar usuarios:', error); // Manejo de errores al eliminar usuarios
        saveToastMessage('error', 'Ocurrió un error al borrar usuarios'); // Muestra un mensaje de error
      }
    };

    // Añade un nuevo usuario
    const addUser = async () => {
      const accountingData = {
        username: newUserName.value, // Nombre del nuevo usuario
        accountingId: accountingId.value, // ID de la contabilidad
        role: newUserRole.value // Rol del nuevo usuario
      };
      try {
        await addUserApi(accountingData.accountingId, accountingData); // Llama a la API para añadir un nuevo usuario
        saveToastMessage('success', 'Usuario añadido'); // Muestra un mensaje de éxito
        await fetchUsersAccountingAsync(accountingId.value); // Vuelve a cargar la lista de usuarios después de añadir uno nuevo
        location.reload(); // Recarga la página para reflejar los cambios
        closeModal(); // Cierra el modal
      } catch (error) {
        console.error('Error al añadir usuario:', error); // Manejo de errores al añadir usuario
        saveToastMessage('error', 'Ocurrión un error al añadir el usuario'); // Muestra un mensaje de error
      }
      newUserName.value = ''; // Limpia el campo del nombre del nuevo usuario después de añadirlo
    };

    // Retorna todas las variables y métodos que deben estar disponibles en el template del componente
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
