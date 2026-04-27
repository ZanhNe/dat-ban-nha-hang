
const USE_MOCK = true;
const BASE_URL = 'http://localhost:8080/api/v1/admin';

const getAuthHeaders = () => {
    const token = localStorage.getItem('accessToken');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
};


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


        const response = await fetch(`${BASE_URL}/restaurants?status=${status}&page=${page}&limit=${limit}`, {
            method: 'GET',
            headers: getAuthHeaders(),
        });
        return response.json();
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


        const response = await fetch(`${BASE_URL}/restaurants/${id}`, {
            method: 'GET',
            headers: getAuthHeaders(),
        });
        return response.json();
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


        const response = await fetch(`${BASE_URL}/restaurants/${id}/approval`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
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

        let url = `${BASE_URL}/restaurants?page=${page}&limit=${limit}`;
        if (search) url += `&search=${search}`;

        const response = await fetch(url, { method: 'GET', headers: getAuthHeaders() });
        return response.json();
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

        const response = await fetch(`${BASE_URL}/restaurants/${id}/status`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({ status, reason })
        });
        return response.json();
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

        const response = await fetch(`${BASE_URL}/restaurants/${id}/commission`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
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
        const response = await fetch(`${BASE_URL}/cuisines`, { method: 'GET', headers: getAuthHeaders() });
        return response.json();
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
        const response = await fetch(`${BASE_URL}/cuisines`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
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
        const response = await fetch(`${BASE_URL}/cuisines/${id}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
    },

    deleteCuisine: async (id) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Xóa thành công (MOCK)", data: null };
        }
        const response = await fetch(`${BASE_URL}/cuisines/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        return response.json();
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
                    totalRestaurants: 45, // Tổng nhà hàng thường cố định
                    totalNewUsers: isFiltered ? 45 : 120
                }
            };
        }


        let url = `${BASE_URL}/reports/dashboard`;
        const params = new URLSearchParams();
        if (fromDate) params.append('fromDate', fromDate);
        if (toDate) params.append('toDate', toDate);

        const queryString = params.toString();
        if (queryString) {
            url += `?${queryString}`;
        }

        const response = await fetch(url, { method: 'GET', headers: getAuthHeaders() });
        return response.json();
    },

    // 7.10. QUẢN LÝ NGƯỜI DÙNG (USER MANAGEMENT)

    //  Lấy danh sách người dùng
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
        const query = new URLSearchParams(params).toString();
        const response = await fetch(`${BASE_URL}/users?${query}`, { method: 'GET', headers: getAuthHeaders() });
        return response.json();
    },

    //  Tạo / Cập nhật người dùng
    saveUser: async (payload, userId = null) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: userId ? 200 : 201, message: "Thành công (MOCK)", data: { userId: userId || 999, ...payload } };
        }
        const url = userId ? `${BASE_URL}/users/${userId}` : `${BASE_URL}/users`;
        const method = userId ? 'PUT' : 'POST';
        const response = await fetch(url, { method, headers: getAuthHeaders(), body: JSON.stringify(payload) });
        return response.json();
    },

    // Điều chuyển nhân sự (Gán workplace)
    assignWorkplace: async (userId, restaurantId, roleId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Điều chuyển nhân sự thành công (MOCK)" };
        }
        const response = await fetch(`${BASE_URL}/users/${userId}/workplace`, {
            method: 'PATCH',
            headers: getAuthHeaders(),
            body: JSON.stringify({ restaurantId, roleId })
        });
        return response.json();
    },
    // TRUNG TÂM THÔNG BÁO

    // Gửi thông báo chung (Broadcast / Role)
    sendBroadcastNotification: async (payload) => {
        // payload: { title, content, type, targetRole? }
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return { status: 200, message: "Đã đưa vào hàng đợi gửi thông báo chung (MOCK)" };
        }
        const response = await fetch(`${BASE_URL}/notifications/broadcast`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
    },

    //  Gửi thông báo cá nhân
    sendPersonalNotification: async (payload) => {
        // payload: { userId, title, content, type }
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return { status: 200, message: `Đã gửi thông báo cá nhân tới User ID ${payload.userId} thành công (MOCK)` };
        }
        const response = await fetch(`${BASE_URL}/notifications/send`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
    }

};

export default adminService;