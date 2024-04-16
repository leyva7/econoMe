import axios from "@/utils/axios";

import {getAuthHeaders} from "@/service/auth";

const API_URL = 'http://localhost:8081/api/accounting/';

export const createAccounting = async (accountingData) => {
    return axios.post(`${API_URL}register`, accountingData, { headers: getAuthHeaders() });
};

export const fetchAccountings = async () => {
    return await axios.get(`${API_URL}accountingUser`, { headers: getAuthHeaders() });
};

export const fetchAccountingPersonal = async () => {
    return await axios.get(`${API_URL}accountingPersonal`, { headers: getAuthHeaders() });
};
export const fetchCategoriesSpent = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/categoriesSpent`, { headers: getAuthHeaders() });
};

export const fetchCategoriesIncome = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/categoriesIncome`, { headers: getAuthHeaders() });
};

export const fetchCategoriesDifferences = async (accountingId, filterType, startDate = null, endDate = null) => {
    let queryParams = `?filterType=${filterType}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${startDate}&endDate=${endDate}`;
    }

    return await axios.get(`${API_URL}${accountingId}/categoryDifferences${queryParams}`, {
        headers: getAuthHeaders()
    });
};

export const deleteUserAccounting = async (accountingId, username) => {
    return await axios.delete(`${API_URL}${accountingId}/${username}`, {
        headers: getAuthHeaders()
    });
};