// src/services/cashierService.js
import apiClient from './apiClient';

const USE_MOCK = true;

export const cashierService = {
    // 10.1 Lấy danh sách phiên bàn chờ thanh toán
    getServedSessions: async (page = 0, limit = 10, status = 'SERVED') => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            return {
                status: 200,
                message: "Thành công",
                data: [
                    { sessionId: 1001, tableLabels: ["Bàn 01"], customerName: "Nguyễn Văn A", numberOfPeople: 4, status: "SERVED" },
                    { sessionId: 1002, tableLabels: ["Bàn 03", "Bàn 04"], customerName: "Trần Thị B", numberOfPeople: 6, status: "SERVED" },
                ],
                meta: { page: 0, limit: 10, totalItems: 2, totalPages: 1 }
            };
        }
        return apiClient.get('/cashier/sessions', { params: { page, limit, status } });
    },

    // 10.2 Lấy danh sách phiên bàn đang thanh toán
    getPayingSessions: async (page = 0, limit = 10) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            return {
                status: 200,
                message: "Lấy danh sách phiên bàn đang thanh toán thành công",
                data: [
                    { sessionId: 1003, tableLabels: ["VIP 01"], customerName: "Lê Văn C", numberOfPeople: 2, status: "PAYING" },
                ],
                meta: { page: 0, limit: 10, totalItems: 1, totalPages: 1 }
            };
        }
        return apiClient.get('/cashier/sessions/paying', { params: { page, limit } });
    },

    // 10.3a Xem chi tiết phiên bàn để thanh toán
    getSessionDetail: async (sessionId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 500));
            return {
                status: 200,
                message: "Thành công",
                data: {
                    sessionId: sessionId,
                    tableLabels: ["Bàn 01"],
                    customerName: "Nguyễn Văn A",
                    numberOfPeople: 4,
                    depositAmount: 200000,
                    totalAmount: 1500000,
                    status: "SERVED",
                    items: [
                        { foodName: "Bò Wagyu Nướng", quantity: 2, price: 500000, totalItemPrice: 1000000 },
                        { foodName: "Lẩu Thái Tomyum", quantity: 1, price: 350000, totalItemPrice: 350000 },
                        { foodName: "Trà Đào Cam Sả", quantity: 2, price: 45000, totalItemPrice: 90000 },
                    ]
                }
            };
        }
        return apiClient.get(`/cashier/sessions/${sessionId}`);
    },

    // 10.3b Khởi tạo thanh toán
    initiatePayment: async (sessionId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 600));
            return {
                status: 201,
                message: "Đã chuyển sang trạng thái chờ thanh toán",
                data: { sessionId: sessionId, status: "PAYING" }
            };
        }
        return apiClient.post(`/cashier/sessions/${sessionId}/payments`);
    },

    // 10.4 Hoàn tất thanh toán
    completePayment: async (sessionId, paymentMethod, totalAmount) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 600));
            return {
                status: 200,
                message: "Thanh toán hoàn tất, bàn đã trống.",
                data: { sessionId: sessionId, status: "COMPLETED" }
            };
        }
        return apiClient.patch(`/cashier/sessions/${sessionId}/payments/complete`, { paymentMethod, totalAmount });
    }
};
