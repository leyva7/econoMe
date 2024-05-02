<template>
  <div id="window" class="d-flex vh-100 overflow-hidden bg-custom-blue-900">
    <!-- Sidebar con navegación y contabilidades compartidas -->
    <div style="flex: 0 0 15%; overflow-y: auto;">
      <SidebarPage :sharedAccountings="sharedAccountings" @openModal="handleOpenModal"/>
    </div>
    <AddAccountingModal :isVisible="isModalOpen && modalContentType === 'addAccounting'" @update:isVisible="toggleModal" />
    <AddOperationModal :isVisible="isModalOpen && modalContentType === 'addOperations'" @update:isVisible="toggleModal" />
    <AccountingInfoModal :isVisible="isModalOpen && modalContentType === 'infoAccounting'" @update:isVisible="toggleModal"/>

    <div style="flex: 0 0 85%; overflow-y: auto;">
      <TopBar/>
      <div class="dynamic-content p-3 overflow-auto position-relative">
        <button @click="openModal('infoAccounting')" class="btn button-custom position-absolute top-0 end-0 mt-3 me-3">
          <i class="fa-solid fa-circle-info me-2"></i>Información
        </button>
        <router-view @openModal="handleOpenModal"></router-view>
        <button v-if="shouldShowAddButton" @click="openModal('addOperations')" class="add-floating-button position-fixed" style="right: 25px; bottom: 30px;">
          <i class="fa-solid fa-plus "></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import TopBar from "@/components/TopBar.vue";
import SidebarPage from "@/components/AppSidebar.vue";
import AddAccountingModal from "@/components/AddAccountingModal.vue";
import AccountingInfoModal from "@/components/AccountingInfoModal.vue";
import AddOperationModal from "@/components/AddOperationModal.vue";
import { useAccountingStore } from '@/stores/accountingStore.js';
import { useToast } from "vue-toast-notification";
import { showToastFromStorage } from "@/utils/toastService";
import { isModalOpen, modalContentType, handleOpenModal, openModal, toggleModal, shouldShowButton } from "@/utils/modal"

export default {
  name: 'UserHome',
  components:{ TopBar, SidebarPage, AddAccountingModal, AccountingInfoModal, AddOperationModal },
  setup() {
    const { accountingId, sharedAccountings, loadAccountings, accountings, userRole, fetchUserRoleAsync, categories, fetchCategories} = useAccountingStore();
    const toast = useToast();
    const router = useRouter();

    onMounted(async () => {
      await loadAccountings();
      await fetchUserRoleAsync(accountingId.value);
      showToastFromStorage(toast);
    });

    return {
      userRole, fetchUserRoleAsync,
      categories, fetchCategories,
      sharedAccountings, accountings,
      handleOpenModal, isModalOpen, modalContentType, openModal, toggleModal,
      shouldShowAddButton: shouldShowButton(router, userRole)
    };
  },
};
</script>


<style scoped>

.dynamic-content {
  height: calc(95% - 40px);
  margin-top: 20px;
  overflow-y: hidden;
  flex-grow: 1;
  background-color: #ECF0F1;
  border-radius: 20px;
}

.button-custom {
  position: absolute;
  top: 0;
  end: 0;
  margin-top: 3rem;
  margin-right: 3rem;
}

.add-floating-button {
  position: fixed;
  right: 25px;
  bottom: 30px;
  z-index: 100;
  padding: 0.8em;
  font-size: 16px;
  color: #ffffff;
  background-color: #2C3E50;
  border: none;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
}

.add-floating-button:hover {
  background-color: #2C3E50;
  box-shadow: 0 15px 20px #2C3E50;
  color: #fff;
  transform: translateY(-7px);
}

.add-floating-button:active {
  transform: translateY(-1px);
}
</style>
