import apiClient from './apiClient';

export const cashierService = {
    getServedSessions: async ({ page = 0, limit = 10, status = 'SERVED' } = {}) => {
        return apiClient.get('/cashier/sessions', { params: { page, limit, status } });
    },

    getPayingSessions: async ({ page = 0, limit = 10 } = {}) => {
        return apiClient.get('/cashier/sessions/paying', { params: { page, limit } });
    },

    getSessionDetail: async (sessionId) => {
        return apiClient.get(`/cashier/sessions/${sessionId}`);
    },

    initiatePayment: async (sessionId) => {
        return apiClient.post(`/cashier/sessions/${sessionId}/payments`);
    },

    completePayment: async (sessionId, payload) => {
        return apiClient.patch(`/cashier/sessions/${sessionId}/payments/complete`, payload);
    }
};
