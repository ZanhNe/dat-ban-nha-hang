import { USE_MOCK } from './restaurantService';
import apiClient from './apiClient';

export const authService = {
    login: async (username, password) => {
        if (USE_MOCK) {
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    if (username === 'admin' && password === '123456') {
                        resolve({
                            data: {
                                accessToken: "mock.jwt.token",
                                user: {
                                    userId: 1,
                                    username: "admin",
                                    fullName: "Admin System",
                                    email: "admin@example.com",
                                    roles: ["ROLE_ADMIN", "ROLE_CUSTOMER"]
                                }
                            }
                        });
                    } else {
                        const error = new Error();
                        error.response = { data: { message: "Sai tên đăng nhập hoặc mật khẩu" } };
                        reject(error);
                    }
                }, 800);
            });
        }

        // const res = await axios.post(`${API_BASE_URL}/auth/login`, { username, password });
        const res = await apiClient.post('/auth/login', { username, password });
        return res;
    },

    register: async (userData) => {
        if (USE_MOCK) {
            return new Promise((resolve) => setTimeout(() => resolve({
                data: {
                    userId: 99,
                    username: userData.username,
                    fullName: userData.fullName,
                    roles: ["CUSTOMER"]
                }
            }), 800));
        }

        // const res = await axios.post(`${API_BASE_URL}/auth/register`, userData);
        const res = await apiClient.post('/auth/register', userData);
        return res;
    }
};
