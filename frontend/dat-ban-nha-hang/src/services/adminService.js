// src/services/adminService.js
import apiClient from './apiClient';

const USE_MOCK = true;

const adminService = {

    // Lấy danh sách nhà hàng (chỉ lấy PENDING)
    getPendingRestaurants: async (page = 0, limit = 10, status = 'PENDING') => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Lấy danh sách nhà hàng thành công (MOCK)",
                data: [
                    {
                        restaurantId: 105,
                        restaurantName: "Haidilao",
                        managerName: "Nguyễn Văn A",
                        status: "PENDING",
                        createdAt: "2026-03-01T10:00:00"
                    },
                    {
                        restaurantId: 106,
                        restaurantName: "Phở Gánh Hà Nội",
                        managerName: "Trần Thị B",
                        status: "PENDING",
                        createdAt: "2026-03-02T14:30:00"
                    }
                ],
                meta: { page: page, limit: limit, totalItems: 2, totalPages: 1 }
            };
        }
        return apiClient.get('/admin/restaurants', { params: { status, page, limit } });
    },

    // Xem chi tiết nhà hàng
    getRestaurantDetail: async (id) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Thành công (MOCK)",
                data: {
                    restaurantId: id,
                    restaurantName: id === 105 ? "Haidilao" : "Phở Gánh Hà Nội",
                    status: "PENDING",
                    manager: {
                        userId: id === 105 ? 10 : 11,
                        fullName: id === 105 ? "Nguyễn Văn A" : "Trần Thị B",
                        phone: id === 105 ? "0987654321" : "0901234567"
                    },
                    legalDocs: [
                        {
                            docId: 1,
                            docUrl: "https://via.placeholder.com/600x400?text=Giay+Phep+Kinh+Doanh"
                        }
                    ]
                }
            };
        }
        return apiClient.get(`/admin/restaurants/${id}`);
    },

    // Duyệt nhà hàng mới
    updateApprovalStatus: async (id, payload) => {
        // payload: { status: "APPROVED" | "REJECTED", rejectReason?: "..." }
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 800));
            return {
                status: 200,
                message: payload.status === "APPROVED"
                    ? "Đã phê duyệt nhà hàng thành công (MOCK)."
                    : "Đã từ chối nhà hàng thành công (MOCK).",
                data: {
                    restaurantId: id,
                    status: payload.status
                }
            };
        }
        return apiClient.patch(`/admin/restaurants/${id}/approval`, payload);
    },

    // Lấy TẤT CẢ danh sách nhà hàng (Quản lý chung)
    getAllRestaurants: async (page = 0, limit = 100, search = '') => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Lấy danh sách nhà hàng thành công",
                data: [
                    { restaurantId: 101, restaurantName: "Haidilao Hotpot", managerName: "Công ty Haidilao", status: "ACTIVE", commissionType: "PERCENTAGE", baseCommissionValue: 15 },
                    { restaurantId: 102, restaurantName: "El Gaucho Steakhouse", managerName: "John Doe", status: "ACTIVE", commissionType: "FIXED", baseCommissionValue: 50000 },
                    { restaurantId: 103, restaurantName: "Quán Nướng ABC", managerName: "Nguyễn Văn C", status: "SUSPENDED", commissionType: "PERCENTAGE", baseCommissionValue: 10 },
                ]
            };
        }
        const params = { page, limit };
        if (search) params.search = search;
        return apiClient.get('/admin/restaurants', { params });
    },

    // 7.4. Khóa / Mở khóa nhà hàng
    updateRestaurantStatus: async (id, status, reason = "") => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Cập nhật trạng thái thành công (MOCK)",
                data: { restaurantId: id, status: status }
            };
        }
        return apiClient.patch(`/admin/restaurants/${id}/status`, { status, reason });
    },

    // 7.6. Cấu hình Commission (Hoa hồng)
    updateRestaurantCommission: async (id, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Cấu hình hoa hồng thành công (MOCK)",
                data: { restaurantId: id, ...payload }
            };
        }
        return apiClient.patch(`/admin/restaurants/${id}/commission`, payload);
    },

    // 7.7. QUẢN LÝ DANH MỤC ẨM THỰC (CUISINES)
    getAllCuisines: async () => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return {
                status: 200,
                message: "Thành công",
                data: [
                    { cuisineId: 1, name: "Lẩu (Hotpot)", description: "Các món lẩu truyền thống và hiện đại", isActive: true },
                    { cuisineId: 2, name: "Đồ Nướng (BBQ)", description: "Thịt nướng Hàn Quốc, Nhật Bản", isActive: true },
                    { cuisineId: 3, name: "Hải sản", description: "Các món ăn từ hải sản tươi sống", isActive: true },
                    { cuisineId: 4, name: "Món Chay", description: "Ẩm thực chay thanh đạm", isActive: false },
                ]
            };
        }
        return apiClient.get('/admin/cuisines');
    },

    createCuisine: async (payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 201,
                message: "Tạo danh mục thành công (MOCK)",
                data: { cuisineId: Math.floor(Math.random() * 1000), ...payload }
            };
        }
        return apiClient.post('/admin/cuisines', payload);
    },

    updateCuisine: async (id, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Cập nhật thành công (MOCK)",
                data: { cuisineId: id, ...payload }
            };
        }
        return apiClient.put(`/admin/cuisines/${id}`, payload);
    },

    deleteCuisine: async (id) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Xóa thành công (MOCK)", data: null };
        }
        return apiClient.delete(`/admin/cuisines/${id}`);
    },

    // 7.8. BÁO CÁO TOÀN HỆ THỐNG (DASHBOARD)
    getDashboardMetrics: async (fromDate = null, toDate = null) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            const isFiltered = fromDate || toDate;
            return {
                status: 200,
                message: "Lấy dữ liệu dashboard thành công (MOCK)",
                data: {
                    totalRevenue: isFiltered ? 5000000 : 15000000,
                    totalBookings: isFiltered ? 320 : 1250,
                    totalRestaurants: 45,
                    totalNewUsers: isFiltered ? 45 : 120
                }
            };
        }
        const params = {};
        if (fromDate) params.fromDate = fromDate;
        if (toDate) params.toDate = toDate;
        return apiClient.get('/admin/reports/dashboard', { params });
    },

    // 7.10. QUẢN LÝ NGƯỜI DÙNG (USER MANAGEMENT)
    getAllUsers: async (params = {}) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 400));
            return {
                status: 200,
                data: [
                    { userId: 201, fullName: "Nguyễn Văn B", username: "waiter_b", email: "b@example.com", phone: "0981234567", roles: [{ id: 2, name: "ROLE_WAITER" }], status: "ACTIVE" },
                    { userId: 202, fullName: "Lê Thị C", username: "manager_c", email: "c@example.com", phone: "0912345678", roles: [{ id: 1, name: "ROLE_MANAGER" }], status: "ACTIVE" },
                    { userId: 203, fullName: "Khách hàng D", username: "customer_d", email: "d@example.com", phone: "0900000000", roles: [{ id: 3, name: "ROLE_CUSTOMER" }], status: "BANNED" }
                ],
                meta: { totalItems: 50, totalPages: 5, page: 0, limit: 10 }
            };
        }
        return apiClient.get('/admin/users', { params });
    },

    saveUser: async (payload, userId = null) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: userId ? 200 : 201, message: "Thành công (MOCK)", data: { userId: userId || 999, ...payload } };
        }
        if (userId) {
            return apiClient.put(`/admin/users/${userId}`, payload);
        }
        return apiClient.post('/admin/users', payload);
    },

    // Điều chuyển nhân sự (Gán workplace)
    assignWorkplace: async (userId, restaurantId, roleId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Điều chuyển nhân sự thành công (MOCK)" };
        }
        return apiClient.patch(`/admin/users/${userId}/workplace`, { restaurantId, roleId });
    },

    // TRUNG TÂM THÔNG BÁO
    // Gửi thông báo chung (Broadcast / Role)
    sendBroadcastNotification: async (payload) => {
        // payload: { title, content, type, targetRole? }
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return { status: 200, message: "Đã đưa vào hàng đợi gửi thông báo chung (MOCK)" };
        }
        return apiClient.post('/admin/notifications/broadcast', payload);
    },

    // Gửi thông báo cá nhân
    sendPersonalNotification: async (payload) => {
        // payload: { userId, title, content, type }
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return { status: 200, message: `Đã gửi thông báo cá nhân tới User ID ${payload.userId} thành công (MOCK)` };
        }
        return apiClient.post('/admin/notifications/send', payload);
    }

};

export default adminService;