import apiClient from './apiClient';
import { toLocalDateTimeParam } from './apiShape';

const adminService = {
    getPendingRestaurants: async (page = 0, limit = 10, status = 'PENDING') => {
        return apiClient.get('/admin/restaurants', { params: { status, page, limit } });
    },

    getRestaurantDetail: async (id) => {
        return apiClient.get(`/admin/restaurants/${id}`);
    },

    updateApprovalStatus: async (id, payload) => {
        return apiClient.patch(`/admin/restaurants/${id}/approval`, payload);
    },

    getAllRestaurants: async (page = 0, limit = 10, status, search = '') => {
        const params = { page, limit };
        if (status) params.status = status;
        if (search) params.search = search;
        return apiClient.get('/admin/restaurants', { params });
    },

    updateRestaurantStatus: async (id, status, reason = "") => {
        return apiClient.patch(`/admin/restaurants/${id}/status`, { status, reason });
    },

    updateRestaurantCommission: async (id, payload) => {
        return apiClient.patch(`/admin/restaurants/${id}/commission`, payload);
    },

    getAllCuisines: async () => {
        return apiClient.get('/admin/cuisines');
    },

    createCuisine: async (payload) => {
        return apiClient.post('/admin/cuisines', payload);
    },

    updateCuisine: async (id, payload) => {
        return apiClient.put(`/admin/cuisines/${id}`, payload);
    },

    deleteCuisine: async (id) => {
        return apiClient.delete(`/admin/cuisines/${id}`);
    },

    getDashboardMetrics: async (fromDate = null, toDate = null) => {
        const params = {};
        if (fromDate) params.fromDate = toLocalDateTimeParam(fromDate);
        if (toDate) params.toDate = toLocalDateTimeParam(toDate, { endOfDay: true });
        return apiClient.get('/admin/reports/dashboard', { params });
    },

    getAllUsers: async (params = {}) => {
        return apiClient.get('/admin/users', { params });
    },

    saveUser: async (payload, userId = null) => {
        return userId
            ? apiClient.put(`/admin/users/${userId}`, payload)
            : apiClient.post('/admin/users', payload);
    },

    assignWorkplace: async (userId, restaurantId, roleId) => {
        return apiClient.patch(`/admin/users/${userId}/workplace`, { restaurantId, roleId });
    },

    sendBroadcastNotification: async (payload) => {
        return apiClient.post('/admin/notifications/broadcast', payload);
    },

    sendPersonalNotification: async (payload) => {
        return apiClient.post('/admin/notifications/send', payload);
    },

    assignManager: async (restaurantId, userId) => {
        return apiClient.patch(`/admin/restaurants/${restaurantId}/manager`, { userId });
    },
};

export default adminService;