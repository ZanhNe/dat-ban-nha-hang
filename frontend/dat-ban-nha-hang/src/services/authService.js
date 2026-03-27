import axios from 'axios';
import { USE_MOCK } from './restaurantService';

const API_BASE_URL = 'http://localhost:8080/api/v1';

export const authService = {
    login: async (username, password) => {
        if (USE_MOCK) {
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    if (username === 'admin' && password === '123456') {
                        resolve({
                            accessToken: "mock.jwt.token",
                            user: {
                                userId: 1,
                                username: "admin",
                                fullName: "Admin System",
                                email: "admin@example.com",
                                roles: ["ADMIN", "CUSTOMER"]
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

        const res = await axios.post(`${API_BASE_URL}/auth/login`, { username, password });
        return res.data.data;
    },

    register: async (userData) => {
        if (USE_MOCK) {
            return new Promise((resolve) => setTimeout(() => resolve({
                userId: 99,
                username: userData.username,
                fullName: userData.fullName,
                roles: ["CUSTOMER"]
            }), 800));
        }

        const res = await axios.post(`${API_BASE_URL}/auth/register`, userData);
        return res.data.data;
    }
};
