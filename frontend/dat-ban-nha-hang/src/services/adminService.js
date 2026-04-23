
const USE_MOCK = true;
const BASE_URL = 'http://localhost:8080/api/v1/admin';

const getAuthHeaders = () => {
    const token = localStorage.getItem('token');
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
    }
};

export default adminService;