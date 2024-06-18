import axios from "@/utils/axios"; // Importación del cliente Axios configurado
import { getAuthHeaders } from "@/api/authAPI"; // Importación de la función para obtener los encabezados de autenticación

const API_URL = "http://localhost:8081/api/accounting/"; // URL base de la API

// Función para crear una operación
export const createOperation = async (operationData) => {
    return axios.post(`${API_URL}operation/register`, operationData, { headers: getAuthHeaders() });
};

// Función para actualizar una operación
export const updateOperation = async (operationData) => {
    return axios.put(`${API_URL}operation`, operationData, { headers: getAuthHeaders() });
};

// Función para obtener operaciones filtradas
export const fetchOperations = async (params) => {
    const { accountingId, filterType, startDate = null, endDate = null } = params;

    // Construcción de parámetros de consulta
    let queryParams = `?filterType=${encodeURIComponent(filterType)}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`;
    }

    // Solicitud GET para obtener operaciones filtradas
    return await axios.get(`${API_URL}${accountingId}/operation/all${queryParams}`, {
        headers: getAuthHeaders()
    });
};

// Función para obtener gastos filtrados
export const fetchSpentsFiltered = async (params) => {
    const { accountingId, filterType, startDate = null, endDate = null } = params;

    // Construcción de parámetros de consulta
    let queryParams = `?filterType=${encodeURIComponent(filterType)}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`;
    }

    // Solicitud GET para obtener gastos filtrados
    return await axios.get(`${API_URL}${accountingId}/operation/spentFiltered${queryParams}`, {
        headers: getAuthHeaders()
    });
};

// Función para obtener ingresos filtrados
export const fetchIncomesFiltered = async (params) => {
    const { accountingId, filterType, startDate = null, endDate = null } = params;

    // Construcción de parámetros de consulta
    let queryParams = `?filterType=${encodeURIComponent(filterType)}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`;
    }

    // Solicitud GET para obtener ingresos filtrados
    return await axios.get(`${API_URL}${accountingId}/operation/incomeFiltered${queryParams}`, {
        headers: getAuthHeaders()
    });
};

// Función para obtener todas las operaciones de todas las contabilidades del usuario
export const fetchAllAccountingsUserOperations = async () => {
    return await axios.get(`${API_URL}operationAccountings/all`, { headers: getAuthHeaders() });
};

// Función para obtener operaciones filtradas según el filtro proporcionado
export const fetchFilteredOperations = async (filter) => {
    const params = new URLSearchParams(filter).toString(); // Conversión del filtro a parámetros de URL
    return await axios.get(`${API_URL}operation/filterOperation?${params}`, {
        headers: getAuthHeaders()
    });
};

// Función para eliminar una operación por su ID
export const deleteOperation = async (operationId) => {
    return await axios.delete(`${API_URL}operation/${operationId}`, {
        headers: getAuthHeaders(),
    });
};
