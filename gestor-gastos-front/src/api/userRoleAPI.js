import axios from "@/utils/axios";
import {getAuthHeaders} from "@/api/authAPI";

const API_URL = "http://localhost:8081/api/accounting/";

export const fetchUserRole = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/rol`, { headers: getAuthHeaders() });
};

export const addUser = async (accountingId, userData) => {
    return await axios.post(`${API_URL}${accountingId}/addUser`, userData, { headers: getAuthHeaders() });
};

export const fetchUsersAccounting = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/users`, { headers: getAuthHeaders() });
};

export const deleteUserAccounting = async (accountingId, username) => {
    return await axios.delete(`${API_URL}${accountingId}/deleteUser`, {
        data: { username },
        headers: getAuthHeaders(),
    });
};