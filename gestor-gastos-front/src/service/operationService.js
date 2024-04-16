import axios from "@/utils/axios";
import {getAuthHeaders} from "@/service/auth";

const API_URL = "http://localhost:8081/api/accounting/";

export const createOperation = async (operationData) => {
    return axios.post(`${API_URL}operation/register`, operationData, { headers: getAuthHeaders() });
};
export const updateOperation = async (operationData) => {
    return axios.put(`${API_URL}operation`, operationData, { headers: getAuthHeaders() });
};
export const fetchOperations = async (accountingId, filterType, startDate = null, endDate = null) => {
    let queryParams = `?filterType=${filterType}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${startDate}&endDate=${endDate}`;
    }

    return await axios.get(`${API_URL}${accountingId}/operation/all${queryParams}`, {
        headers: getAuthHeaders()
    });
};

export const fetchAllOperations = async () => {
    return await axios.get(`${API_URL}operation/all`, { headers: getAuthHeaders() });
};

export const fetchSpents = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/spent`, { headers: getAuthHeaders() });
};

export const fetchSpentsFiltered = async (accountingId, filterType, startDate = null, endDate = null) => {
    let queryParams = `?filterType=${filterType}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${startDate}&endDate=${endDate}`;
    }

    return await axios.get(`${API_URL}${accountingId}/operation/spentFiltered${queryParams}`, {
        headers: getAuthHeaders()
    });
};

export const fetchIncomes = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/income`, { headers: getAuthHeaders() });
};

export const fetchIncomesMonths = async (accountingId, filterType, startDate = null, endDate = null) => {
    let queryParams = `?filterType=${filterType}`;
    if (startDate && endDate) {
        queryParams += `&startDate=${startDate}&endDate=${endDate}`;
    }

    return await axios.get(`${API_URL}${accountingId}/operation/incomeFiltered${queryParams}`, {
        headers: getAuthHeaders()
    });
};

export const fetchAllAccountingsUserOperations = async () => {
    return await axios.get(`${API_URL}operationAccountings/all`, { headers: getAuthHeaders() });
};

export const fetchFilteredOperations = async (filter) => {
    const params = new URLSearchParams(filter).toString();
    return await axios.get(`${API_URL}operation/filterOperation?${params}`, {
        headers: getAuthHeaders()
    });
};
export const deleteOperation = async (operationId) => {
    return await axios.delete(`${API_URL}operation/${operationId}`, {
        headers: getAuthHeaders(),
    });
};