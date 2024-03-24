import axios from "axios";

const API_URL = "http://localhost:8081/api/accounting/";
const AUTH_HEADERS = { Authorization: `Bearer ${localStorage.getItem('userToken')}` };

export const createOperation = async (operationData) => {
    return axios.post(`${API_URL}operation/register`, operationData, { headers: AUTH_HEADERS });
};
export const fetchOperations = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/all`, { headers: AUTH_HEADERS });
};

export const fetchSpents = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/operation/spent`, { headers: AUTH_HEADERS });
};
