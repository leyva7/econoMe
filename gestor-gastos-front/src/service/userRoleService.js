import axios from "axios";

const API_URL = "http://localhost:8081/api/accounting/";
const AUTH_HEADERS = { Authorization: `Bearer ${localStorage.getItem('userToken')}` };

export const fetchUserRole = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/rol`, { headers: AUTH_HEADERS });
};
