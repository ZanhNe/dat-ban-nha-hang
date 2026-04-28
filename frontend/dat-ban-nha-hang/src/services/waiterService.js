// src/services/waiterService.js
import apiClient from './apiClient';

const USE_MOCK = true;

export const waiterService = {
    // Lấy danh sách phiên bàn
    getSessions: async (restaurantId, status) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            if (status === 'ACTIVE') {
                return { status: 200, data: [{ sessionId: 1001, tableNames: "Bàn 1, Bàn 2", guestCount: 4, status: "ACTIVE" }, { sessionId: 1002, tableNames: "VIP 1", guestCount: 2, status: "ACTIVE" }] };
            }
            return { status: 200, data: [{ sessionId: 1003, tableNames: "Bàn 5", guestCount: 3, status: "SERVING", waiterId: 15 }] };
        }
        return apiClient.get(`/waiter/restaurants/${restaurantId}/sessions`, { params: { status } });
    },

    // 9.1. Đảm nhận phiên bàn
    assignSession: async (sessionId) => {
        if (USE_MOCK) return { status: 200, message: "Nhận bàn thành công", data: { sessionId, status: "SERVING" } };
        return apiClient.patch(`/waiter/sessions/${sessionId}/assign`);
    },

    // 9.2. Tạo FoodOrder mới
    createOrder: async (sessionId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 300));
            return { status: 201, message: "Tạo Order thành công", data: { orderId: Math.floor(Math.random() * 10000), status: "TAKING_ORDER" } };
        }
        return apiClient.post(`/waiter/sessions/${sessionId}/orders`);
    },

    // 9.3. Xác nhận và Gửi bếp
    submitOrder: async (orderId, payload) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 500));
            return { status: 200, message: "Đã gửi bếp thành công", data: { orderId, status: "CONFIRMED", itemsCount: payload.items.length } };
        }
        return apiClient.post(`/waiter/orders/${orderId}/confirm-items`, payload);
    },

    // 9.4. Cập nhật trạng thái món (Bưng ra / Hủy)
    updateItemStatus: async (itemId, status) => {
        if (USE_MOCK) return { status: 200, message: "Cập nhật thành công" };
        return apiClient.patch(`/waiter/food-items/${itemId}/status`, { status });
    },

    // 9.5. Chốt Order
    completeOrder: async (orderId) => {
        if (USE_MOCK) return { status: 200, message: "Đã hoàn tất Order" };
        return apiClient.patch(`/waiter/orders/${orderId}/complete`);
    },

    // Lấy menu cho POS
    getMenuForPos: async (restaurantId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            return {
                status: 200,
                data: [
                    {
                        groupId: 1, name: "Món Chính",
                        foods: [
                            { foodId: 12, name: "Bò Wagyu Nướng", price: 500000, options: [{ optionId: 1, name: "Chín vừa (Medium)", price: 0 }, { optionId: 2, name: "Chín kỹ (Well-done)", price: 0 }] },
                            { foodId: 15, name: "Lẩu Thái Tomyum", price: 350000, options: [] }
                        ]
                    },
                    {
                        groupId: 2, name: "Đồ Uống",
                        foods: [
                            { foodId: 20, name: "Trà Đào Cam Sả", price: 45000, options: [{ optionId: 5, name: "Ít đá", price: 0 }, { optionId: 6, name: "Thêm trân châu", price: 10000 }] }
                        ]
                    }
                ]
            };
        }
        return apiClient.get(`/waiter/restaurants/${restaurantId}/menu`);
    },

    // Lấy chi tiết Order (danh sách món ăn) của Phiên bàn
    getSessionOrderDetails: async (sessionId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            return {
                status: 200,
                data: {
                    orderId: 5001,
                    status: "CONFIRMED",
                    items: [
                        { itemId: 8001, foodName: "Bò Wagyu Nướng", quantity: 2, options: "Chín vừa", status: "PENDING" },
                        { itemId: 8002, foodName: "Lẩu Thái Tomyum", quantity: 1, options: "", status: "SERVED" },
                        { itemId: 8003, foodName: "Trà Đào Cam Sả", quantity: 2, options: "Ít đá", status: "PENDING" },
                    ]
                }
            };
        }
        return apiClient.get(`/waiter/sessions/${sessionId}/orders/current`);
    },
};