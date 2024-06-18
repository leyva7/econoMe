// Importación del router para navegar entre vistas
import router from "@/router";

// Importación de las funciones de la API de usuario
import { changePassword, fetchUserDetails, registerUser, updateUserDetails } from "@/api/userAPI";

// Importación de funciones de utilidad global y servicios
import { navigate, navigateHome } from "@/utils/global";
import { callAPI } from "@/service/service";
import { saveToastMessage } from "@/utils/toastService";

// Función para cargar los datos del usuario
export async function loadUserData(user) {
    await callAPI(null, fetchUserDetails, 'Error al obtener datos del usuario', null, null, user);
}

// Función para enviar cambios en los datos del usuario
export async function submitUserDataChanges(user, submitted, formRef, usernameError) {
    // Verifica si el formulario es válido antes de enviar los cambios
    if (formRef.value.checkValidity()) {
        const successMessage = 'Usuario modificado exitosamente.';
        const errorMessage = 'Ocurrió un error al modificar los datos del usuario. Por favor, inténtalo de nuevo.';

        // Función de éxito para ejecutar después de la actualización exitosa
        const successCallback = () => {
            localStorage.setItem('username', user.username); // Actualiza el nombre de usuario en el almacenamiento local
            saveToastMessage('success', successMessage); // Muestra un mensaje de éxito
            navigateHome('/home-user'); // Navega de regreso a la página de inicio del usuario
        };

        // Llama a la API para actualizar los detalles del usuario
        await callAPI(
            user, // Datos del usuario a enviar
            updateUserDetails, // Función de la API para actualizar detalles de usuario
            errorMessage, // Mensaje de error si falla la llamada
            null, // Mensaje de éxito (no se muestra aquí)
            successCallback, // Función a ejecutar si la llamada tiene éxito
            null // No se asigna ningún objeto en este caso
        ).catch(error => {
            submitted.value = false; // Marca el estado como no enviado
            // Verifica si hay un error específico relacionado con el nombre de usuario
            if (error.response.data && error.response.data.error.includes('nombre de usuario')) {
                usernameError.value = 'Este nombre de usuario ya está registrado.'; // Establece el mensaje de error
                const usernameInput = formRef.value.querySelector('#username');
                usernameInput.classList.remove('is-valid'); // Remueve las clases de validación
                usernameInput.classList.add('is-invalid'); // Agrega la clase de invalidación
            }
        });
    } else {
        submitted.value = true; // Marca el formulario como enviado
    }
}

// Función para enviar cambios de contraseña
export async function submitPasswordChange(userPassword) {
    const successMessage = 'Contraseña modificada exitosamente.';
    const errorMessage = 'Ocurrió un error al modificar la contraseña. Por favor, inténtalo de nuevo.';

    // Función de éxito para redirigir al usuario a la página de inicio de sesión después del cambio de contraseña
    const successCallback = () => {
        router.push({ name: 'login' });
    };

    // Llama a la API para cambiar la contraseña del usuario
    await callAPI(userPassword, changePassword, errorMessage, successMessage, successCallback);
}

// Función para registrar un nuevo usuario
export async function submitRegisterUser(user, submitted, formRef, usernameError) {
    // Verifica si el formulario es válido antes de registrar al usuario
    if (formRef.value.checkValidity()) {
        const registrationData = {
            user: user, // Datos del usuario a registrar
            accounting: { // Datos de la contabilidad asociada al usuario
                name: 'Contabilidad personal',
                description: `Contabilidad de uso personal del usuario ${user.username}`,
                type: 'PERSONAL'
            }
        };

        // Llama a la API para registrar al usuario junto con la contabilidad personal
        await callAPI(
            registrationData, // Datos completos para el registro
            registerUser, // Función de la API para registrar usuarios
            'Hubo un error al registrar el usuario.', // Mensaje de error si falla la llamada
            'Usuario y contabilidad registrados exitosamente.', // Mensaje de éxito (no se muestra aquí)
            () => navigate('/'), // Función a ejecutar si la llamada tiene éxito
            null // No se asigna ningún objeto en este caso
        ).catch(error => {
            submitted.value = false; // Marca el estado como no enviado
            // Verifica si hay un error específico relacionado con el nombre de usuario
            if (error.response.data && error.response.data.error.includes('nombre de usuario')) {
                usernameError.value = 'Este nombre de usuario ya está registrado.'; // Establece el mensaje de error
                const usernameInput = formRef.value.querySelector('#username');
                usernameInput.classList.remove('is-valid'); // Remueve las clases de validación
                usernameInput.classList.add('is-invalid'); // Agrega la clase de invalidación
            }
        });
    } else {
        submitted.value = true; // Marca el formulario como enviado
    }
}
