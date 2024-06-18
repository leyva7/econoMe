import { ref } from "vue";

/**
 * Hook personalizado para gestionar la visibilidad con un retraso opcional.
 * @param {boolean} initialVisibility - Visibilidad inicial, por defecto false.
 * @param {number} delay - Retraso en milisegundos antes de cambiar la visibilidad, por defecto 1000ms (1 segundo).
 * @returns {object} Objeto con el estado de visibilidad y funciones para establecer la visibilidad.
 */
export function useDelayedVisibility(initialVisibility = false, delay = 1000) {
    const isVisible = ref(initialVisibility);  // Ref reactiva para almacenar el estado de visibilidad

    /**
     * Función para establecer directamente la visibilidad.
     * @param {boolean} visible - Nuevo estado de visibilidad.
     */
    const setVisibility = (visible) => {
        isVisible.value = visible;  // Asigna el nuevo estado de visibilidad
    };

    /**
     * Función para establecer la visibilidad después de un retraso.
     * @param {boolean} visible - Nuevo estado de visibilidad (opcional, por defecto true).
     */
    const setVisibilityAfterDelay = (visible = true) => {
        setTimeout(() => {
            isVisible.value = visible;  // Asigna el nuevo estado de visibilidad después del retraso
        }, delay);
    };

    return {
        isVisible,                 // Estado de visibilidad reactivo
        setVisibility,             // Función para establecer la visibilidad directamente
        setVisibilityAfterDelay    // Función para establecer la visibilidad después de un retraso
    };
}
