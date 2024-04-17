import axios from '@/utils/axios';
import {getAuthHeaders} from "@/service/auth";

const AUTH_API_URL = 'http://localhost:8081/api/auth';
const USER_API_URL = 'http://localhost:8081/api/users';
export const login = async (userCredentials) => {
    const response = await axios.post(`${AUTH_API_URL}/login`, userCredentials);
    return response.data;
};

export const registerUser = async (userData) => {
    return await axios.post(`${AUTH_API_URL}/register`, userData);
};

export const changePassword = async (passwordData) => {
    return await axios.put(`${USER_API_URL}/modifyPassword`, passwordData, getAuthHeaders());
};

export const updateUserDetails = async (userDetails) => {
    return await axios.put(`${USER_API_URL}/modifyDetails`, userDetails, getAuthHeaders());
};

export const fetchUserDetails = async () => {
    return await axios.get(`${USER_API_URL}/details`, getAuthHeaders());
};