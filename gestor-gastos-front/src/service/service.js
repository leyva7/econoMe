import router from "@/router";


export function handleError(error, errorMessage = 'Ocurrió un error. Por favor, inténtalo de nuevo.') {
    if (error.response) {
        if (error.response.status === 401) {
            console.error("Sesión expirada. Por favor, inicia sesión de nuevo.", error);
            alert("Sesión expirada. Por favor, inicia sesión de nuevo.");
            router.push({ name: 'login' });
        } else if (error.response.data && error.response.data.error) {
            console.error("Error:", error);
            alert(`Ocurrió un error: ${error.response.data.error}`);
            throw error;
        } else {
            console.error("Error:", error);
            alert(errorMessage);
        }
    } else {
        console.error("Error:", error);
        alert(errorMessage);
    }
}

export async function callAPI(callArgument, call, errorMessage, successMessage, successCallback, assigneeObject) {
    try {
        const response = await call(callArgument);
        if (assigneeObject) {
            Object.assign(assigneeObject, response.data);
        }
        if (successMessage) {
            alert(successMessage);
        }
        if (successCallback) {
            successCallback(response.data);
        }
    } catch (error) {
        handleError(error, errorMessage);
    }
}
