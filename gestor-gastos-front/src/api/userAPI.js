import axios from '@/utils/axios'; // Importación del cliente Axios configurado
import { getAuthHeaders } from "@/api/authAPI"; // Importación de la función para obtener los encabezados de autenticación

const AUTH_API_URL = 'http://localhost:8081/api/auth'; // URL base de la API de autenticación
const USER_API_URL = 'http://localhost:8081/api/users'; // URL base de la API de usuarios

// Función para iniciar sesión
export const login = async (userCredentials) => {
    const response = await axios.post(`${AUTH_API_URL}/login`, userCredentials); // Solicitud POST para iniciar sesión
    return response.data; // Devuelve los datos de la respuesta
};

// Función para registrar un nuevo usuario
export const registerUser = async (userData) => {
    return await axios.post(`${AUTH_API_URL}/register`, userData ); // Solicitud POST para registrar un usuario
};

// Función para cambiar la contraseña del usuario
export const changePassword = async (passwordData) => {
    return await axios.put(`${USER_API_URL}/modifyPassword`, passwordData, { headers: getAuthHeaders() }); // Solicitud PUT para cambiar la contraseña, con encabezados de autenticación
};

// Función para actualizar los detalles del usuario
export const updateUserDetails = async (userDetails) => {
    return await axios.put(`${USER_API_URL}/modifyDetails`, userDetails, { headers: getAuthHeaders() }); // Solicitud PUT para actualizar detalles del usuario, con encabezados de autenticación
};

// Función para obtener los detalles del usuario
export const fetchUserDetails = async () => {
    return await axios.get(`${USER_API_URL}/details`, { headers: getAuthHeaders() }); // Solicitud GET para obtener los detalles del usuario, con encabezados de autenticación
};
