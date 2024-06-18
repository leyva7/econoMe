import axios from "@/utils/axios";  // Importamos Axios configurado para nuestra API

import { getAuthHeaders } from "@/api/authAPI";  // Importamos la función para obtener los encabezados de autenticación

const API_URL = 'http://localhost:8081/api/accounting/';  // URL base de la API para la gestión contable

// Función para crear una nueva contabilidad
export const createAccounting = async (accountingData) => {
    return axios.post(`${API_URL}register`, accountingData, { headers: getAuthHeaders() });
};

// Función para obtener todas las contabilidades
export const fetchAccountings = async () => {
    return await axios.get(`${API_URL}accountingUser`, { headers: getAuthHeaders() });
};

// Función para obtener la contabilidad personal de un usuario
export const fetchAccountingPersonal = async () => {
    return await axios.get(`${API_URL}accountingPersonal`, { headers: getAuthHeaders() });
};

// Función para obtener las categorías de gastos de una contabilidad específica
export const fetchCategoriesSpent = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/categoriesSpent`, { headers: getAuthHeaders() });
};

// Función para obtener las categorías de ingresos de una contabilidad específica
export const fetchCategoriesIncome = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/categoriesIncome`, { headers: getAuthHeaders() });
};

// Función para obtener las diferencias en categorías de una contabilidad según un filtro y rango de fechas
export const fetchCategoriesDifferences = async (params) => {
    const { accountingId, filterType, startDate = null, endDate = null } = params;

    let queryParams = `?filterType=${encodeURIComponent(filterType)}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}`;
    }

    return await axios.get(`${API_URL}${accountingId}/categoryDifferences${queryParams}`, {
        headers: getAuthHeaders()
    });
};

// Función para actualizar los detalles de una contabilidad existente
export const updateAccounting = async (id, accountingRegistration) => {
    return await axios.put(`${API_URL}${id}`, accountingRegistration, {
        headers: {
            ...getAuthHeaders(),
            'Content-Type': 'application/json'
        }
    });
}

// Función para eliminar la contabilidad de un usuario
export const deleteUserAccounting = async (accountingId, username) => {
    return await axios.delete(`${API_URL}${accountingId}/${username}`, {
        headers: getAuthHeaders()
    });
};
