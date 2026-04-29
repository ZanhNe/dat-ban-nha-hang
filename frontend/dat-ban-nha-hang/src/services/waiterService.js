import apiClient from './apiClient';

export const waiterService = {
    getSessions: async (params = {}) => {
        return apiClient.get('/waiter/sessions', { params });
    },

    getMySessions: async (page = 0, limit = 10) => {
        return apiClient.get('/waiter/sessions/me', { params: { page, limit } });
    },

    getSessionDetail: async (sessionId) => {
        return apiClient.get(`/waiter/sessions/${sessionId}`);
    },

    assignSession: async (sessionId) => {
        return apiClient.patch(`/waiter/sessions/${sessionId}/assign`);
    },

    createOrder: async (sessionId) => {
        return apiClient.post(`/waiter/sessions/${sessionId}/orders`);
    },

    submitOrder: async (orderId, payload) => {
        return apiClient.post(`/waiter/orders/${orderId}/confirm-items`, payload);
    },

    updateItemStatus: async (itemId, status) => {
        return apiClient.patch(`/waiter/food-items/${itemId}/status`, { status });
    },

    completeOrder: async (orderId) => {
        return apiClient.patch(`/waiter/orders/${orderId}/complete`);
    },

    getMenuForPos: async () => {
        return apiClient.get('/waiter/menu');
    },

    getOrderDetails: async (orderId) => {
        return apiClient.get(`/waiter/orders/${orderId}`);
    },

    cancelOrder: async (orderId) => apiClient.patch(`/waiter/orders/${orderId}/cancel`),
    serveComplete: async (sessionId) => apiClient.patch(`/waiter/sessions/${sessionId}/serve-complete`),
};