<template>
  <div class="shared-accounting-details">
    <UserManagementModal :isVisible="isModalOpen" @close="isModalOpen = false"/>
    <div v-if="isUserCreator" class="management-buttons">
      <button @click="openUserManagement">Gestionar Usuarios</button>
      <button @click="deleteAccounting">Eliminar Contabilidad</button>
    </div>
  </div>
</template>

<script>
import { useAccountingStore } from '@/stores/accountingStore.js';
import {deleteUserAccounting as deleteAccountingApi} from "@/service/accountingService"
import { ref, onMounted } from "vue";
import UserManagementModal from './UserManagementModal.vue';
import { useRouter } from 'vue-router';
export default {
  name: "SharedAccountings",
  components: {
    UserManagementModal
  },
  setup() {
    const {accountings, sharedAccountings, fetchAccountingsAsync, accountingId} = useAccountingStore();
    const router = useRouter();
    const isUserCreator = ref(false);
    const isModalOpen = ref(false); // Añadido para controlar la visibilidad del modal

    onMounted(async () => {
      await fetchAccountingsAsync();
      calculateIsUserCreator();
    });

    const calculateIsUserCreator = () => {
      const sharedAccounting = sharedAccountings.value.find(accounting => accounting.id === Number(accountingId.value));
      if (sharedAccounting) {
        isUserCreator.value = sharedAccounting.userCreator === localStorage.getItem('username');
      } else {
        isUserCreator.value = false;
      }
    };

    const openUserManagement = () => {
      isModalOpen.value = true; // Abre el modal
    };

    const deleteAccounting = async () => {
      if (confirm("¿Estás seguro de que quieres eliminar esta contabilidad?")) {
        try {
          await deleteAccountingApi(accountingId.value, localStorage.getItem('username'));
          console.log("Eliminada la contabilidad");
          // Recargar las contabilidades para actualizar el sidebar
          await fetchAccountingsAsync();
          router.push({
            path: '/home-user', // Utiliza 'path' en lugar de 'name'
            query: { id: localStorage.getItem('personalAccountingId') }
          });
          window.location.reload();
        } catch (error) {
          console.error("Error al eliminar la contabilidad:", error);
        }
      }
    };

    return {
      accountings,
      sharedAccountings,
      isUserCreator, openUserManagement, deleteAccounting, isModalOpen // Añadido isModalOpen al return
    };
  }
}
</script>

<style scoped>
.shared-accounting-details {
  position: relative;
}

.management-buttons {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
  padding: 10px;
}

.management-buttons button {
  width: 150px; /* Ancho fijo para ambos botones */
  height: 40px; /* Alto fijo para ambos botones */
  padding: 5px 10px; /* Padding para el contenido interno, opcional si necesitas más espacio */
  border: 1px solid #ccc; /* Borde sutil */
  border-radius: 5px; /* Bordes redondeados para suavizar la apariencia */
  background-color: #f2f2f2; /* Color de fondo suave */
  cursor: pointer; /* Cambia el cursor para indicar que son clickeables */
  font-size: 14px; /* Tamaño de texto adecuado */
  color: #333; /* Color de texto oscuro para contraste */
  box-shadow: 0 2px 4px rgba(0,0,0,0.1); /* Sombra sutil para dar profundidad */
  transition: background-color 0.3s; /* Transición suave al cambiar de estado */
}

.management-buttons button:hover {
  background-color: #e9e9e9; /* Cambio de color al pasar el ratón */
}

.management-buttons button:active {
  background-color: #ddd; /* Cambio de color al hacer clic */
}
</style>

