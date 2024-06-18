import axios from 'axios';
import router from '@/router';

let alertShown = false;

const axiosInstance = axios.create({
    baseURL: 'http://localhost:8081/api', // Reemplaza con la IP del servidor y el puerto correcto
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    },
});

axiosInstance.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401 && error.response.data.error === "El token JWT ha expirado. Por favor, inicia sesión nuevamente.") {
            if (!alertShown) {
                alert("Sesión expirada. Por favor, inicia sesión de nuevo");
                alertShown = true;
                router.push('/');
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
