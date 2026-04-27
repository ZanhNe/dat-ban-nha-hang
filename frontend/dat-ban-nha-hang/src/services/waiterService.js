// src/services/waiterService.js
const USE_MOCK = true;
const BASE_URL = 'http://localhost:8080/api/v1/waiter';

const getHeaders = () => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
});

export const waiterService = {
    // [Hàm bổ sung cho UI] Lấy danh sách phiên bàn
    getSessions: async (restaurantId, status) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            if (status === 'ACTIVE') {
                return { status: 200, data: [{ sessionId: 1001, tableNames: "Bàn 1, Bàn 2", guestCount: 4, status: "ACTIVE" }, { sessionId: 1002, tableNames: "VIP 1", guestCount: 2, status: "ACTIVE" }] };
            }
            return { status: 200, data: [{ sessionId: 1003, tableNames: "Bàn 5", guestCount: 3, status: "SERVING", waiterId: 15 }] };
        }
        const res = await fetch(`${BASE_URL}/restaurants/${restaurantId}/sessions?status=${status}`, { headers: getHeaders() });
        return res.json();
    },

    // 9.1. Đảm nhận phiên bàn
    assignSession: async (sessionId) => {
        if (USE_MOCK) return { status: 200, message: "Nhận bàn thành công", data: { sessionId, status: "SERVING" } };
        const res = await fetch(`${BASE_URL}/sessions/${sessionId}/assign`, { method: 'PATCH', headers: getHeaders() });
        return res.json();
    },

    // 9.2. Tạo FoodOrder mới
    createOrder: async (sessionId) => {
        if (USE_MOCK) return { status: 201, data: { orderId: 5001, status: "TAKING_ORDER" } };
        const res = await fetch(`${BASE_URL}/sessions/${sessionId}/orders`, { method: 'POST', headers: getHeaders() });
        return res.json();
    },

    // 9.3. Gửi Bếp
    submitOrder: async (orderId, payload) => {
        if (USE_MOCK) return { status: 200, message: "Đã gửi bếp thành công", data: { orderId, status: "CONFIRMED" } };
        const res = await fetch(`${BASE_URL}/orders/${orderId}/confirm-items`, { method: 'POST', headers: getHeaders(), body: JSON.stringify(payload) });
        return res.json();
    },

    // 9.4. Cập nhật trạng thái món (Bưng ra / Hủy)
    updateItemStatus: async (itemId, status) => {
        if (USE_MOCK) return { status: 200, message: "Cập nhật thành công" };
        const res = await fetch(`${BASE_URL}/food-items/${itemId}/status`, { method: 'PATCH', headers: getHeaders(), body: JSON.stringify({ status }) });
        return res.json();
    },

    // 9.5. Chốt Order
    completeOrder: async (orderId) => {
        if (USE_MOCK) return { status: 200, message: "Đã hoàn tất Order" };
        const res = await fetch(`${BASE_URL}/orders/${orderId}/complete`, { method: 'PATCH', headers: getHeaders() });
        return res.json();
    },



    // 9. MÀN HÌNH POS (GHI MÓN)

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
        const res = await fetch(`${BASE_URL}/restaurants/${restaurantId}/menu`, { headers: getHeaders() });
        return res.json();
    },

    createOrder: async (sessionId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 300));
            return { status: 201, message: "Tạo Order thành công", data: { orderId: Math.floor(Math.random() * 10000), status: "TAKING_ORDER" } };
        }
        const res = await fetch(`${BASE_URL}/sessions/${sessionId}/orders`, { method: 'POST', headers: getHeaders() });
        return res.json();
    },

    // 9.3. Xác nhận và Gửi bếp
    submitOrder: async (orderId, payload) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 500));
            return { status: 200, message: "Đã gửi bếp thành công", data: { orderId, status: "CONFIRMED", itemsCount: payload.items.length } };
        }
        const res = await fetch(`${BASE_URL}/orders/${orderId}/confirm-items`, {
            method: 'POST', headers: getHeaders(), body: JSON.stringify(payload)
        });
        return res.json();
    },

    // 9.4 & 9.5 QUẢN LÝ TRẠNG THÁI MÓN & CHỐT ORDER

    // Lấy chi tiết Order (danh sách món ăn) của Phiên bàn
    getSessionOrderDetails: async (sessionId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            return {
                status: 200,
                data: {
                    orderId: 5001,
                    status: "CONFIRMED", // Order đã gửi bếp
                    items: [
                        { itemId: 8001, foodName: "Bò Wagyu Nướng", quantity: 2, options: "Chín vừa", status: "PENDING" },
                        { itemId: 8002, foodName: "Lẩu Thái Tomyum", quantity: 1, options: "", status: "SERVED" },
                        { itemId: 8003, foodName: "Trà Đào Cam Sả", quantity: 2, options: "Ít đá", status: "PENDING" },
                    ]
                }
            };
        }
        const res = await fetch(`${BASE_URL}/sessions/${sessionId}/orders/current`, { headers: getHeaders() });
        return res.json();
    },


};