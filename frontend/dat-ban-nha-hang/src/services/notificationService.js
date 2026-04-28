// src/services/notificationService.js
import apiClient from './apiClient';

const USE_MOCK = true;

export const notificationService = {
    // 5.1 Xem danh sách thông báo
    getNotifications: async (page = 0, limit = 10) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 300));
            return {
                status: 200,
                data: [
                    { notificationId: 1, title: "Đặt bàn được xác nhận", message: "Booking #1234 tại Haidilao đã được xác nhận.", isRead: false, createdAt: "2026-04-27T18:00:00" },
                    { notificationId: 2, title: "Nhắc lịch đặt bàn", message: "Bạn có lịch đặt bàn vào 19:00 hôm nay.", isRead: false, createdAt: "2026-04-27T10:00:00" },
                    { notificationId: 3, title: "Thanh toán thành công", message: "Thanh toán 500.000đ thành công.", isRead: true, createdAt: "2026-04-26T20:30:00" },
                ],
                meta: { page: 0, limit: 10, totalItems: 3, totalPages: 1 }
            };
        }
        return apiClient.get('/notifications', { params: { page, limit } });
    },

    // 5.2 Lấy số lượng thông báo chưa đọc
    getUnreadCount: async () => {
        if (USE_MOCK) {
            return { status: 200, data: { unreadCount: 2 } };
        }
        return apiClient.get('/notifications/unread-count');
    },

    // 5.3 Đánh dấu 1 thông báo là đã đọc
    markAsRead: async (notificationId) => {
        if (USE_MOCK) {
            return { status: 200, message: "Đã đánh dấu đã đọc" };
        }
        return apiClient.patch(`/notifications/${notificationId}/read`);
    },

    // 5.4 Đánh dấu tất cả thông báo là đã đọc
    markAllAsRead: async () => {
        if (USE_MOCK) {
            return { status: 200, message: "Đã đánh dấu tất cả đã đọc" };
        }
        return apiClient.patch('/notifications/read-all');
    }
};
