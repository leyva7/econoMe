import axios from "axios";

const API_URL = 'http://localhost:8081/api/accounting';
const AUTH_HEADERS = { Authorization: `Bearer ${localStorage.getItem('userToken')}` };

export const createAccounting = async (accountingData) => {
    console.log(accountingData);
    return axios.post(`${API_URL}/register`, accountingData, { headers: AUTH_HEADERS });
};

export const fetchAccountings = async () => {
    return await axios.get(`${API_URL}/accountingUser`, { headers: AUTH_HEADERS });
};

export const fetchAccountingPersonal = async () => {
    return await axios.get(`${API_URL}/accountingPersonal`, { headers: AUTH_HEADERS });
};
export const fetchCategories = async (accountingId) => {
    return await axios.get(`${API_URL}/${accountingId}/categories`, { headers: AUTH_HEADERS });
};

