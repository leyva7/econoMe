import axios from "axios";
import {getAuthHeaders} from "@/service/auth";

const API_URL = "http://localhost:8081/api/accounting/";

export const fetchUserRole = async (accountingId) => {
    return await axios.get(`${API_URL}${accountingId}/rol`, { headers: getAuthHeaders() });
};
