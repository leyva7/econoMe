import axios from "axios";
import {getAuthHeaders} from "@/service/auth";

const API_URL = "http://localhost:8081/api/accounting/";

export const createOperation = async (operationData) => {
    return axios.post(`${API_URL}operation/register`, operationData, { headers: getAuthHeaders() });
};
export const fetchOperations = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/all`, { headers: getAuthHeaders() });
};

export const fetchAllOperations = async () => {
    return await axios.get(`${API_URL}operation/all`, { headers: getAuthHeaders() });
};

export const fetchSpents = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/spent`, { headers: getAuthHeaders() });
};

export const fetchSpentsMonths = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/spentMonth`, { headers: getAuthHeaders() });
};

export const fetchIncomes = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/income`, { headers: getAuthHeaders() });
};

export const fetchIncomesMonths = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/incomeMonth`, { headers: getAuthHeaders() });
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
    return await axios.delete(`${API_URL}/operation/${operationId}`, {
        headers: getAuthHeaders(),
    });
};