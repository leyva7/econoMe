import axios from "@/utils/axios"; // Importación del cliente Axios configurado
import { getAuthHeaders } from "@/api/authAPI"; // Importación de la función para obtener los encabezados de autenticación

const API_URL = "http://localhost:8081/api/accounting/"; // URL base de la API de contabilidad

// Función para obtener el rol de un usuario en una contabilidad específica
export const fetchUserRole = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/rol`, { headers: getAuthHeaders() }); // Solicitud GET para obtener el rol, con encabezados de autenticación
};

// Función para agregar un usuario a una contabilidad específica
export const addUser = async (accountingId, userData) => {
    return await axios.post(`${API_URL}${accountingId}/addUser`, userData, { headers: getAuthHeaders() }); // Solicitud POST para agregar un usuario, con encabezados de autenticación
};

// Función para obtener todos los usuarios de una contabilidad específica
export const fetchUsersAccounting = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/users`, { headers: getAuthHeaders() }); // Solicitud GET para obtener usuarios, con encabezados de autenticación
};

// Función para eliminar un usuario de una contabilidad específica
export const deleteUserAccounting = async (accountingId, username) => {
    return await axios.delete(`${API_URL}${accountingId}/deleteUser`, {
        data: { username }, // Datos de la solicitud DELETE (se envían en el cuerpo)
        headers: getAuthHeaders(), // Encabezados de autenticación
    });
};
