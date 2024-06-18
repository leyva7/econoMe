import { ref } from "vue";

// Variables reactivas para controlar el estado de los modales y la información de las operaciones
export const isModalOpen = ref(false);        // Variable que indica si el modal está abierto
export const isNewModalOpen = ref(false);     // Variable que indica si un nuevo modal está abierto
export const modalContentType = ref('');      // Tipo de contenido del modal actual

export const operationToEdit = ref(null);     // Operación que se está editando en el modal
export const operationToShow = ref(null);     // Operación que se está mostrando en un modal

/**
 * Función para abrir un modal específico.
 * @param {string} type - Tipo de contenido del modal a abrir.
 */
export const handleOpenModal = (type) => {
    modalContentType.value = type;  // Establece el tipo de contenido del modal
    isModalOpen.value = true;       // Abre el modal
};

/**
 * Función para abrir un modal específico.
 * @param {string} modalType - Tipo de contenido del modal a abrir.
 */
export const openModal = (modalType) => {
    modalContentType.value = modalType;  // Establece el tipo de contenido del modal
    isModalOpen.value = true;            // Abre el modal
};

/**
 * Función para cerrar un modal específico.
 * @param {string} modalType - Tipo de contenido del modal a cerrar.
 */
export const closeModal = (modalType) => {
    modalContentType.value = modalType;  // Establece el tipo de contenido del modal
    isModalOpen.value = false;           // Cierra el modal
};

/**
 * Función para alternar la visibilidad de los modales.
 * @param {boolean} value - Valor booleano para establecer la visibilidad de los modales.
 */
export const toggleModal = (value) => {
    if (typeof value === 'boolean') {
        isModalOpen.value = value;      // Establece la visibilidad del modal principal
        isNewModalOpen.value = value;   // Establece la visibilidad del nuevo modal
    } else {
        isModalOpen.value = !isModalOpen.value;        // Alterna la visibilidad del modal principal
        isNewModalOpen.value = !isNewModalOpen.value;  // Alterna la visibilidad del nuevo modal
    }
};

/**
 * Función para mostrar una operación específica en un modal.
 * @param {object} operation - Operación que se desea mostrar en el modal.
 */
export const showOperation = (operation) => {
    operationToShow.value = operation;  // Establece la operación que se va a mostrar
    isNewModalOpen.value = true;       // Abre el nuevo modal
};

/**
 * Función para editar una operación específica en el modal de edición.
 * @param {object} operation - Operación que se desea editar.
 */
export const editOperation = (operation) => {
    operationToEdit.value = operation;  // Establece la operación que se va a editar
    isModalOpen.value = true;           // Abre el modal principal
};

/**
 * Función para determinar si se debe mostrar un botón en función de la ruta y el rol del usuario.
 * @param {object} router - Objeto del router que contiene la ruta actual.
 * @param {object} userRole - Rol actual del usuario.
 * @returns {boolean} - True si se debe mostrar el botón, de lo contrario False.
 */
export const shouldShowButton = (router, userRole) => {
    const routeNamesToShowButton = ['home', 'income', 'spent', 'evolution'];
    return routeNamesToShowButton.includes(router.currentRoute.value.name) ||
        (router.currentRoute.value.name === 'shared' && userRole.value === 'EDITOR');
};
