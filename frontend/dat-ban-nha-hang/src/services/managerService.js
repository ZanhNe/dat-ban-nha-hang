// src/services/managerService.js

const USE_MOCK = true;
const BASE_URL = 'http://localhost:8080/api/v1/manager/restaurants';

const getAuthHeaders = () => ({
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
});

export const managerService = {

    getOverview: async (restaurantId, fromDate, toDate) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                data: {
                    totalRevenue: 25000000,
                    netRevenue: 22500000,
                    totalBookings: 120,
                    totalCustomers: 350,
                    averageBill: 208333
                }
            };
        }
        let url = `${BASE_URL}/${restaurantId}/reports/overview?`;
        if (fromDate) url += `fromDate=${fromDate}&`;
        if (toDate) url += `toDate=${toDate}`;

        const response = await fetch(url, { headers: getAuthHeaders() });
        return response.json();
    },


    getRevenueChart: async (restaurantId, fromDate, toDate, timeUnit = 'DAY') => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                data: [
                    { date: "2026-04-20", revenue: 4000000, netRevenue: 3600000, bookingsCount: 15 },
                    { date: "2026-04-21", revenue: 5000000, netRevenue: 4500000, bookingsCount: 20 },
                    { date: "2026-04-22", revenue: 3500000, netRevenue: 3150000, bookingsCount: 12 },
                    { date: "2026-04-23", revenue: 7000000, netRevenue: 6300000, bookingsCount: 35 },
                    { date: "2026-04-24", revenue: 5500000, netRevenue: 4950000, bookingsCount: 38 },
                ]
            };
        }

        const url = `${BASE_URL}/${restaurantId}/reports/revenue-chart?fromDate=${fromDate}&toDate=${toDate}&timeUnit=${timeUnit}`;
        const response = await fetch(url, { headers: getAuthHeaders() });
        return response.json();
    },


    getTopFoods: async (restaurantId, fromDate, toDate, limit = 5) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                data: [
                    { foodId: 12, foodName: "Lẩu Tứ Xuyên", quantitySold: 120, revenue: 24000000 },
                    { foodId: 5, foodName: "Bò Wagyu A5", quantitySold: 50, revenue: 15000000 },
                    { foodId: 8, foodName: "Sashimi Cá Hồi", quantitySold: 85, revenue: 8500000 },
                    { foodId: 3, foodName: "Salad Trứng Cá", quantitySold: 45, revenue: 3150000 },
                    { foodId: 9, foodName: "Rượu Sake Yamadanishiki", quantitySold: 15, revenue: 9000000 },
                ]
            };
        }
        let url = `${BASE_URL}/${restaurantId}/reports/top-foods?limit=${limit}&`;
        if (fromDate) url += `fromDate=${fromDate}&`;
        if (toDate) url += `toDate=${toDate}`;

        const response = await fetch(url, { headers: getAuthHeaders() });
        return response.json();
    },
    //  QUẢN LÝ THÔNG TIN NHÀ HÀNG

    // Lấy thông tin nhà hàng
    getRestaurantInfo: async (restaurantId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 200,
                message: "Thành công",
                data: {
                    restaurantId: restaurantId,
                    name: "Haidilao - Chi nhánh Hùng Vương Plaza",
                    logo: "https://via.placeholder.com/150",
                    description: "Thương hiệu lẩu nổi tiếng với dịch vụ chăm sóc khách hàng tận tâm...",
                    address: "126 Hùng Vương, Quận 5, TP.HCM",
                    status: "OPENING",
                    baseDepositValue: 200000,
                    depositPolicy: "FIXED",
                    dayOfWeek: 7
                }
            };
        }
        const response = await fetch(`${BASE_URL}/${restaurantId}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        return response.json();
    },

    // Cập nhật thông tin nhà hàng
    updateRestaurantInfo: async (restaurantId, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return {
                status: 200,
                message: "Cập nhật thành công (MOCK)",
                data: { restaurantId, ...payload, status: "OPENING" }
            };
        }
        const response = await fetch(`${BASE_URL}/${restaurantId}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
    },

    // Xóa nhà hàng
    deleteRestaurant: async (restaurantId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return { status: 200, message: "Xóa thành công (MOCK)", data: null };
        }
        const response = await fetch(`${BASE_URL}/${restaurantId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        return response.json();
    },

    // 4.2. QUẢN LÝ NHÂN SỰ (STAFF)

    getStaffs: async (restaurantId, page = 0, size = 10) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 400));
            return {
                status: 200,
                message: "Thành công",
                data: [
                    { userId: 201, fullName: "Nguyễn Văn B", username: "waiter_b", email: "b@example.com", phone: "0981234567", roles: [{ id: 2, name: "ROLE_WAITER" }], status: "ACTIVE" },
                    { userId: 202, fullName: "Trần Thị Thu", username: "cashier_thu", email: "thu@example.com", phone: "0911111111", roles: [{ id: 4, name: "ROLE_CASHIER" }], status: "ACTIVE" }
                ],
                meta: { page, size, totalElements: 2, totalPages: 1 }
            };
        }
        const response = await fetch(`${BASE_URL}/${restaurantId}/staffs?page=${page}&size=${size}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });
        return response.json();
    },

    createStaff: async (restaurantId, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return {
                status: 201,
                message: "Thêm nhân viên thành công",
                data: { userId: Math.floor(Math.random() * 1000), ...payload, roles: [{ id: payload.roleId, name: "ROLE_STAFF" }], status: "ACTIVE" }
            };
        }
        const response = await fetch(`${BASE_URL}/${restaurantId}/staffs`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
    },

    updateStaff: async (staffId, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Cập nhật nhân viên thành công", data: { userId: staffId, ...payload } };
        }

        const URL_STAFF = BASE_URL.replace('/restaurants', '');
        const response = await fetch(`${URL_STAFF}/staffs/${staffId}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(payload)
        });
        return response.json();
    },

    deleteStaff: async (staffId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Xóa nhân viên thành công", data: null };
        }
        const URL_STAFF = BASE_URL.replace('/restaurants', '');
        const response = await fetch(`${URL_STAFF}/staffs/${staffId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        return response.json();
    },

    kickStaff: async (staffId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Đã gỡ nhân viên khỏi nhà hàng thành công", data: null };
        }
        const URL_STAFF = BASE_URL.replace('/restaurants', '');
        const response = await fetch(`${URL_STAFF}/staffs/${staffId}/kick`, {
            method: 'PATCH',
            headers: getAuthHeaders()
        });
        return response.json();
    },

    //  QUẢN LÝ THỰC ĐƠN (MENU, GROUPS, FOODS)

    // --- MENUS ---
    getMenus: async () => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ menuId: 1, name: "Thực đơn chính", description: "Dùng cho cả ngày" }, { menuId: 2, name: "Thực đơn sáng", description: "Từ 6h - 10h" }] };
        }
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/menus`, { headers: getAuthHeaders() });
        return res.json();
    },
    saveMenu: async (payload, menuId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu menu thành công", data: { menuId: menuId || Date.now(), ...payload } };
        const url = menuId ? `${BASE_URL.replace('/restaurants', '')}/menus/${menuId}` : `${BASE_URL.replace('/restaurants', '')}/menus`;
        const res = await fetch(url, { method: menuId ? 'PUT' : 'POST', headers: getAuthHeaders(), body: JSON.stringify(payload) });
        return res.json();
    },
    deleteMenu: async (menuId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa menu thành công", data: null };
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/menus/${menuId}`, { method: 'DELETE', headers: getAuthHeaders() });
        return res.json();
    },

    // --- FOOD GROUPS ---
    getFoodGroups: async (menuId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ groupId: 10, name: "Món Chính", description: "Các loại steak và salad" }, { groupId: 11, name: "Đồ Uống", description: "Nước ép, Sinh tố" }] };
        }
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/menus/${menuId}/food-groups`, { headers: getAuthHeaders() });
        return res.json();
    },
    saveFoodGroup: async (menuId, payload, groupId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu nhóm món thành công", data: { groupId: groupId || Date.now(), ...payload } };
        const url = groupId ? `${BASE_URL.replace('/restaurants', '')}/food-groups/${groupId}` : `${BASE_URL.replace('/restaurants', '')}/menus/${menuId}/food-groups`;
        const res = await fetch(url, { method: groupId ? 'PUT' : 'POST', headers: getAuthHeaders(), body: JSON.stringify(payload) });
        return res.json();
    },
    deleteFoodGroup: async (groupId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa nhóm món thành công", data: null };
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/food-groups/${groupId}`, { method: 'DELETE', headers: getAuthHeaders() });
        return res.json();
    },

    // --- FOODS (MÓN ĂN) ---
    getFoods: async (groupId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ foodId: 50, name: "Bò lúc lắc", description: "Bò Mỹ sốt tiêu", price: 150000, status: "OPENING", image: "" }] };
        }
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/food-groups/${groupId}/foods`, { headers: getAuthHeaders() });
        return res.json();
    },
    saveFood: async (groupId, payload, foodId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu món ăn thành công", data: { foodId: foodId || Date.now(), ...payload } };
        const url = foodId ? `${BASE_URL.replace('/restaurants', '')}/foods/${foodId}` : `${BASE_URL.replace('/restaurants', '')}/food-groups/${groupId}/foods`;
        const res = await fetch(url, { method: foodId ? 'PUT' : 'POST', headers: getAuthHeaders(), body: JSON.stringify(payload) });
        return res.json();
    },
    deleteFood: async (foodId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa món ăn thành công", data: null };
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/foods/${foodId}`, { method: 'DELETE', headers: getAuthHeaders() });
        return res.json();
    },

    // 4.4. QUẢN LÝ SƠ ĐỒ BÀN (TABLE LAYOUT)

    // --- KHU VỰC BÀN (TABLE AREAS) ---
    getTableAreas: async () => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ tableAreaId: 1, name: "Tầng 1", status: "ACTIVE" }, { tableAreaId: 2, name: "Sân thượng", status: "ACTIVE" }] };
        }
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/table-areas`, { headers: getAuthHeaders() });
        return res.json();
    },

    saveTableArea: async (payload, areaId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu khu vực thành công", data: { tableAreaId: areaId || Date.now(), ...payload } };
        const url = areaId ? `${BASE_URL.replace('/restaurants', '')}/table-areas/${areaId}` : `${BASE_URL.replace('/restaurants', '')}/table-areas`;
        const res = await fetch(url, { method: areaId ? 'PUT' : 'POST', headers: getAuthHeaders(), body: JSON.stringify(payload) });
        return res.json();
    },

    deleteTableArea: async (areaId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa khu vực thành công" };
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/table-areas/${areaId}`, { method: 'DELETE', headers: getAuthHeaders() });
        return res.json();
    },

    // --- BÀN ĂN (TABLES) ---
    getTablesByArea: async (areaId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return {
                status: 200,
                data: [
                    { tableId: 10, name: "Bàn 01", capacity: 4, status: "AVAILABLE" },
                    { tableId: 11, name: "Bàn 02", capacity: 2, status: "OCCUPIED" },
                    { tableId: 12, name: "Bàn 03", capacity: 6, status: "RESERVED" },
                    { tableId: 13, name: "Bàn 04", capacity: 4, status: "CLEANING" },
                ]
            };
        }
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/table-areas/${areaId}/tables`, { headers: getAuthHeaders() });
        return res.json();
    },

    saveTable: async (areaId, payload, tableId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu bàn thành công", data: { tableId: tableId || Date.now(), ...payload } };
        const url = tableId ? `${BASE_URL.replace('/restaurants', '')}/tables/${tableId}` : `${BASE_URL.replace('/restaurants', '')}/table-areas/${areaId}/tables`;
        const res = await fetch(url, { method: tableId ? 'PUT' : 'POST', headers: getAuthHeaders(), body: JSON.stringify(payload) });
        return res.json();
    },

    deleteTable: async (tableId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa bàn thành công" };
        const res = await fetch(`${BASE_URL.replace('/restaurants', '')}/tables/${tableId}`, { method: 'DELETE', headers: getAuthHeaders() });
        return res.json();
    }
};