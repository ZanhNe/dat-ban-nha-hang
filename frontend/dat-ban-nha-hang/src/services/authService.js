import apiClient from './apiClient';

export const authService = {
    login: async (username, password) => {
        return apiClient.post('/auth/login', { username, password });
    },

    register: async (userData) => {
        return apiClient.post('/auth/register', userData);
    }
};
