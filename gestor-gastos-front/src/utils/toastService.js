import { useToast } from "vue-toast-notification";
const $toast = useToast();  // Instancia del servicio de toasts

/**
 * Función para guardar un mensaje de toast en el almacenamiento local.
 * @param {string} type - Tipo de mensaje (success, error, etc.).
 * @param {string} message - Mensaje a mostrar en el toast.
 */
export function saveToastMessage(type, message) {
    localStorage.setItem('toastType', type);      // Guarda el tipo de mensaje en el almacenamiento local
    localStorage.setItem('toastMessage', message);  // Guarda el mensaje en el almacenamiento local
}

/**
 * Función para eliminar el mensaje de toast del almacenamiento local.
 */
export function clearToastMessage() {
    localStorage.removeItem('toastType');       // Elimina el tipo de mensaje del almacenamiento local
    localStorage.removeItem('toastMessage');    // Elimina el mensaje del almacenamiento local
}

/**
 * Función para mostrar un mensaje de toast guardado en el almacenamiento local.
 * Llama al método de toast apropiado según el tipo guardado.
 */
export function showToastFromStorage() {
    const type = localStorage.getItem('toastType');        // Obtiene el tipo de mensaje guardado
    const message = localStorage.getItem('toastMessage');  // Obtiene el mensaje guardado
    if (message) {
        $toast[type](message, {     // Llama al método de toast correspondiente (success, error, etc.)
            duration: 5000,         // Duración del toast en milisegundos (5 segundos)
            position: 'top'         // Posición del toast en la pantalla
        });
        clearToastMessage();        // Limpia el mensaje de toast del almacenamiento local después de mostrarlo
    }
}
