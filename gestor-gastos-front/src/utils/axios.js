import axios from 'axios';
import router from '../router';
import { createToast } from 'mosha-vue-toastify';

// Configura la instancia base de Axios
const axiosInstance = axios.create({
    baseURL: 'http://localhost:8081/api',
    timeout: 10000,
});

// Agregar un interceptor de respuestas
axiosInstance.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401 && error.response.data.error === "El token JWT ha expirado. Por favor, inicia sesión nuevamente.") {
            createToast(error.response.data.error, {
                type: 'danger',
                position: 'top-right'
            });
            router.push('/login');
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;