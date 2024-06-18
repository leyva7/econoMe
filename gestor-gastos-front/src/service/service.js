// Importación del router para navegar entre vistas
import router from "@/router";

// Función para manejar errores
export function handleError(error, errorMessage = 'Ocurrió un error. Por favor, inténtalo de nuevo.') {
    if (error.response) { // Si hay una respuesta del servidor
        if (error.response.status === 401) { // Si el estado de la respuesta es 401 (Unauthorized)
            console.error("Sesión expirada. Por favor, inicia sesión de nuevo.", error);
            alert("Sesión expirada. Por favor, inicia sesión de nuevo.");
            router.push({ name: 'login' }); // Redirige al usuario a la página de inicio de sesión
        } else if (error.response.data && error.response.data.error) { // Si hay un mensaje de error en los datos de la respuesta
            console.error("Error:", error);
            alert(`Ocurrió un error: ${error.response.data.error}`);
            throw error; // Lanza el error para manejo adicional si es necesario
        } else { // Si hay una respuesta pero no se maneja específicamente
            console.error("Error:", error);
            alert(errorMessage); // Muestra un mensaje genérico de error
        }
    } else { // Si no hay respuesta del servidor (por ejemplo, error de red)
        console.error("Error:", error);
        alert(errorMessage); // Muestra un mensaje genérico de error
    }
}

// Función para realizar llamadas a la API de forma segura
export async function callAPI(callArgument, call, errorMessage, successMessage, successCallback, assigneeObject) {
    try {
        const response = await call(callArgument); // Realiza la llamada a la API con el argumento proporcionado
        if (assigneeObject) {
            Object.assign(assigneeObject, response.data); // Asigna los datos de respuesta a un objeto si se proporciona
        }
        if (successMessage) {
            alert(successMessage); // Muestra un mensaje de éxito si se proporciona
        }
        if (successCallback) {
            successCallback(response.data); // Ejecuta una función de éxito si se proporciona, pasando los datos de respuesta
        }
    } catch (error) {
        handleError(error, errorMessage); // Captura cualquier error y llama a la función de manejo de errores
    }
}
