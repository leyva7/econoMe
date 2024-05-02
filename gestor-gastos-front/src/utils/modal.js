import {ref} from "vue";

export const isModalOpen = ref(false);
export const isNewModalOpen = ref(false);
export const modalContentType = ref('');

export const operationToEdit = ref(null);
export const operationToShow = ref(null);

export const handleOpenModal = (type) => {
    modalContentType.value = type;
    isModalOpen.value = true;
};

export const openModal = (modalType) => {
    modalContentType.value = modalType;
    isModalOpen.value = true;
};

export const closeModal = (modalType) => {
    modalContentType.value = modalType;
    isModalOpen.value = false;
}

export const toggleModal = (value) => {
    if (typeof value === 'boolean') {
        isModalOpen.value = value;
        isNewModalOpen.value = value;
    } else {
        isModalOpen.value = !isModalOpen.value;
        isNewModalOpen.value = !isNewModalOpen.value;
    }
};

export const showOperation = (operation) => {
    operationToShow.value = operation;
    isNewModalOpen.value = true;
};

export const editOperation = (operation) => {
    operationToEdit.value = operation;
    isModalOpen.value = true; // Asegúrate de que esta variable controle la visibilidad del modal.
};

export const shouldShowButton = (router, userRole) => {
    const routeNamesToShowButton = ['home', 'income', 'spent', 'evolution'];
    return routeNamesToShowButton.includes(router.currentRoute.value.name) ||
        (router.currentRoute.value.name === 'shared' && userRole.value === 'EDITOR');
};