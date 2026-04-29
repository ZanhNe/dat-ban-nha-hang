import apiClient from './apiClient';

export const notificationService = {
    getNotifications: async (cursor = null, limit = 10) => {
        const params = { limit };
        if (cursor) params.cursor = cursor;
        return apiClient.get('/notifications', { params });
    },

    getUnreadCount: async () => {
        return apiClient.get('/notifications/unread-count');
    },

    markAsRead: async (notificationId) => {
        return apiClient.patch(`/notifications/${notificationId}/read`);
    },

    markAllAsRead: async () => {
        return apiClient.patch('/notifications/read-all');
    }
};
