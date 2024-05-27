import router from "@/router";
import {changePassword, fetchUserDetails, registerUser, updateUserDetails} from "@/api/userAPI";
import {navigate, navigateHome} from "@/utils/global";
import {callAPI} from "@/service/service";
import {saveToastMessage} from "@/utils/toastService";

// Función para cargar los datos del usuario
export async function loadUserData(user) {
    await callAPI(null, fetchUserDetails, 'Error al obtener datos del usuario', null, null, user);
}

// Función para enviar cambios en los datos del usuario
export async function submitUserDataChanges(user, submitted, formRef, usernameError) {
    if (formRef.value.checkValidity()) {
        const successMessage = 'Usuario modificado exitosamente.';
        const errorMessage = 'Ocurrió un error al modificar los datos del usuario. Por favor, inténtalo de nuevo.';

        const successCallback = () => {
            localStorage.setItem('username', user.username);
            saveToastMessage('success', successMessage);
            navigateHome('/home-user');
        };

        await callAPI(
            user,
            updateUserDetails,
            errorMessage,
            null,
            successCallback,
            null
        ).catch(error => {
            submitted.value = false;
            if (error.response.data && error.response.data.error.includes('nombre de usuario')) {
                usernameError.value = 'Este nombre de usuario ya está registrado.';
                const usernameInput = formRef.value.querySelector('#username');
                usernameInput.classList.remove('is-valid');
                usernameInput.classList.add('is-invalid');
            }
        });
    } else {
        submitted.value = true;
    }
}

export async function submitPasswordChange(userPassword) {
    const successMessage = 'Contraseña modificada exitosamente.';
    const errorMessage = 'Ocurrió un error al modificar la contraseña. Por favor, inténtalo de nuevo.';

    const successCallback = () => {
        router.push({ name: 'login' });
    };

    await callAPI(userPassword, changePassword, errorMessage, successMessage, successCallback);
}

export async function submitRegisterUser(user, submitted, formRef, usernameError) {
    if(formRef.value.checkValidity()){
        const registrationData = {
            user: user,
            accounting: {
                name: 'Contabilidad personal',
                description: `Contabilidad de uso personal del usuario ${user.username}`,
                type: 'PERSONAL'
            }
        };

        await callAPI(
            registrationData,
            registerUser,
            'Hubo un error al registrar el usuario.',
            'Usuario y contabilidad registrados exitosamente.',
            () => navigate('/'),
            null
        ).catch(error => {
            submitted.value = false;
            if (error.response.data && error.response.data.error.includes('nombre de usuario')) {
                usernameError.value = 'Este nombre de usuario ya está registrado.';
                const usernameInput = formRef.value.querySelector('#username');
                usernameInput.classList.remove('is-valid');
                usernameInput.classList.add('is-invalid');
            }
        });
    } else {
        submitted.value = true;
    }
}