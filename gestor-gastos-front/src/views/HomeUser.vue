<template>
  <div id="window" class="d-flex vh-100 overflow-hidden bg-custom-blue-900">
    <!-- Sidebar con navegación y contabilidades compartidas -->
    <div style="flex: 0 0 15%; overflow-y: auto;">
      <SidebarPage :sharedAccountings="sharedAccountings" @openModal="handleOpenModal"/>
    </div>
    <AddAccountingModal :isVisible="isModalOpen && modalContentType === 'addAccounting'" @update:isVisible="toggleModal" />
    <AddOperationModal :isVisible="isModalOpen && modalContentType === 'addOperations'" @update:isVisible="toggleModal" />

    <div style="flex: 0 0 85%; overflow-y: auto;">
      <TopBar />
      <div class="dynamic-content p-3 overflow-auto">
        <router-view></router-view>
        <button v-if="shouldShowAddButton" @click="openOperationsModal" class="add-floating-button position-fixed" style="right: 25px; bottom: 30px;">
          <i class="fa-solid fa-plus "></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { provide, ref, onMounted, watch} from 'vue';
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import SidebarPage from "@/components/AppSidebar.vue";
import AddAccountingModal from "@/components/AddAccountingModal.vue";
import AddOperationModal from "@/components/AddOperationModal.vue";
import TopBar from "@/components/TopBar.vue";
import { useAccountingStore } from '@/stores/accountingStore.js';
import { globalStore } from '@/stores/globalStore';

export default {
  name: 'UserHome',
  components:{
    SidebarPage,
    AddAccountingModal,
    AddOperationModal,
    TopBar,
  },
  setup() {
    const isModalOpen = ref(false);
    const { accountings, sharedAccountings, fetchAccountingsAsync, userRole, fetchUserRoleAsync, categories, fetchCategories} = useAccountingStore();
    const {accountingId} = globalStore();
    const modalContentType = ref('');
    const router = useRouter();

    onMounted(async () => {
      await fetchAccountingsAsync();
      await fetchUserRoleAsync(accountingId.value);
    });

    watch(accountingId, async (newId, oldId) => {
      if (newId !== oldId) {
        await fetchUserRoleAsync(newId);
      }
    });

    const handleOpenModal = (type) => {
      modalContentType.value = type;
      isModalOpen.value = true;
    };

    const openOperationsModal = () => {
      modalContentType.value = 'addOperations';
      isModalOpen.value = true;
    };

    const toggleModal = (value) => {
      if (typeof value === 'boolean') {
        isModalOpen.value = value;
      } else {
        isModalOpen.value = !isModalOpen.value;
      }
    };

    const shouldShowAddButton = computed(() => {
      const routeNamesToShowButton = ['home', 'income', 'spent', 'evolution'];
      if (routeNamesToShowButton.includes(router.currentRoute.value.name)) {
        return true;
      }
      if (router.currentRoute.value.name === 'shared' && userRole.value === 'EDITOR') {
        return true;
      }
      return false;
    });

    provide('modalData', { isModalOpen, modalContentType, toggleModal });

    return {
      isModalOpen,
      toggleModal,
      accountings,
      userRole,
      shouldShowAddButton,
      fetchUserRoleAsync,
      openOperationsModal,
      modalContentType,
      categories,
      fetchCategories,
      sharedAccountings,
      handleOpenModal
    };
  },
};
</script>


<style scoped>

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

.content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  overflow-y: auto; /* Permite desplazamiento vertical dentro del content si es necesario */
  padding: 20px;
  background-color: #2C3E50;
}

.dynamic-content {
  height: calc(95% - 40px);
  margin-top: 20px;
  overflow-y: hidden; /* Permite desplazamiento vertical dentro del dynamic-content si es necesario */
  flex-grow: 1; /* Asegura que este contenedor llene el espacio disponible en .content */
  background-color: #ECF0F1;
  border-radius: 20px;
}

.user-actions-container:hover .tooltip, .user-actions:hover .tooltip {
  opacity: 1;
  visibility: visible;
}

.tooltip a {
  display: block;
  color: #2C3E50;
  text-align: center;
  text-decoration: none;
  margin: 5px 0;
}

.user-actions-container:hover .tooltip, .user-actions:hover .tooltip {
  display: block;
}

.add-floating-button {
  position: fixed;
  right: 25px;
  bottom: 30px;
  z-index: 100;
  padding: 0.8em; /* Ajustado para ser un poco más pequeño y proporcional */
  font-size: 16px; /* Tamaño de fuente general del botón reducido */
  color: #ffffff;
  background-color: #2C3E50;
  border: none;
  border-radius: 50%; /* Círculo perfecto */
  width: 60px; /* Ancho fijo para mantener el círculo perfecto */
  height: 60px; /* Altura fija para mantener el círculo perfecto */
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease 0s;
  cursor: pointer;
  outline: none;
  display: flex; /* Para centrar el ícono "+" */
  justify-content: center; /* Centrado horizontal */
  align-items: center; /* Centrado vertical */
}

.add-floating-button:hover {
  background-color: #2C3E50;
  box-shadow: 0px 15px 20px #2C3E50;
  color: #fff;
  transform: translateY(-7px);
}

.add-floating-button:active {
  transform: translateY(-1px);
}

.form-group, window.h2{
  color: #ECF0F1;
}

</style>

