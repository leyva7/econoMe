import axios from "axios";
import {getAuthHeaders} from "@/service/auth";

const API_URL = "http://localhost:8081/api/accounting/";

export const createOperation = async (operationData) => {
    return axios.post(`${API_URL}operation/register`, operationData, { headers: getAuthHeaders() });
};
export const fetchOperations = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/all`, { headers: getAuthHeaders() });
};

export const fetchSpents = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/spent`, { headers: getAuthHeaders() });
};

export const fetchSpentsMonths = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/spentMonth`, { headers: getAuthHeaders() });
};