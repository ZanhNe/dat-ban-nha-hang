// src/services/managerService.js
import apiClient from './apiClient';

const USE_MOCK = true;
const PREFIX = '/manager';

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
        const params = {};
        if (fromDate) params.fromDate = fromDate;
        if (toDate) params.toDate = toDate;
        return apiClient.get(`${PREFIX}/restaurants/${restaurantId}/reports/overview`, { params });
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
        return apiClient.get(`${PREFIX}/restaurants/${restaurantId}/reports/revenue-chart`, {
            params: { fromDate, toDate, timeUnit }
        });
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
        const params = { limit };
        if (fromDate) params.fromDate = fromDate;
        if (toDate) params.toDate = toDate;
        return apiClient.get(`${PREFIX}/restaurants/${restaurantId}/reports/top-foods`, { params });
    },

    //  QUẢN LÝ THÔNG TIN NHÀ HÀNG
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
        return apiClient.get(`${PREFIX}/restaurants/${restaurantId}`);
    },

    updateRestaurantInfo: async (restaurantId, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return {
                status: 200,
                message: "Cập nhật thành công (MOCK)",
                data: { restaurantId, ...payload, status: "OPENING" }
            };
        }
        return apiClient.put(`${PREFIX}/restaurants/${restaurantId}`, payload);
    },

    deleteRestaurant: async (restaurantId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 600));
            return { status: 200, message: "Xóa thành công (MOCK)", data: null };
        }
        return apiClient.delete(`${PREFIX}/restaurants/${restaurantId}`);
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
        return apiClient.get(`${PREFIX}/restaurants/${restaurantId}/staffs`, { params: { page, size } });
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
        return apiClient.post(`${PREFIX}/restaurants/${restaurantId}/staffs`, payload);
    },

    updateStaff: async (staffId, payload) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Cập nhật nhân viên thành công", data: { userId: staffId, ...payload } };
        }
        return apiClient.put(`${PREFIX}/staffs/${staffId}`, payload);
    },

    deleteStaff: async (staffId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Xóa nhân viên thành công", data: null };
        }
        return apiClient.delete(`${PREFIX}/staffs/${staffId}`);
    },

    kickStaff: async (staffId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 500));
            return { status: 200, message: "Đã gỡ nhân viên khỏi nhà hàng thành công", data: null };
        }
        return apiClient.patch(`${PREFIX}/staffs/${staffId}/kick`);
    },

    //  QUẢN LÝ THỰC ĐƠN (MENU, GROUPS, FOODS)

    // --- MENUS ---
    getMenus: async () => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ menuId: 1, name: "Thực đơn chính", description: "Dùng cho cả ngày" }, { menuId: 2, name: "Thực đơn sáng", description: "Từ 6h - 10h" }] };
        }
        return apiClient.get(`${PREFIX}/menus`);
    },

    saveMenu: async (payload, menuId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu menu thành công", data: { menuId: menuId || Date.now(), ...payload } };
        if (menuId) {
            return apiClient.put(`${PREFIX}/menus/${menuId}`, payload);
        }
        return apiClient.post(`${PREFIX}/menus`, payload);
    },

    deleteMenu: async (menuId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa menu thành công", data: null };
        return apiClient.delete(`${PREFIX}/menus/${menuId}`);
    },

    // --- FOOD GROUPS ---
    getFoodGroups: async (menuId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ groupId: 10, name: "Món Chính", description: "Các loại steak và salad" }, { groupId: 11, name: "Đồ Uống", description: "Nước ép, Sinh tố" }] };
        }
        return apiClient.get(`${PREFIX}/menus/${menuId}/food-groups`);
    },

    saveFoodGroup: async (menuId, payload, groupId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu nhóm món thành công", data: { groupId: groupId || Date.now(), ...payload } };
        if (groupId) {
            return apiClient.put(`${PREFIX}/food-groups/${groupId}`, payload);
        }
        return apiClient.post(`${PREFIX}/menus/${menuId}/food-groups`, payload);
    },

    deleteFoodGroup: async (groupId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa nhóm món thành công", data: null };
        return apiClient.delete(`${PREFIX}/food-groups/${groupId}`);
    },

    // --- FOODS (MÓN ĂN) ---
    getFoods: async (groupId) => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ foodId: 50, name: "Bò lúc lắc", description: "Bò Mỹ sốt tiêu", price: 150000, status: "OPENING", image: "" }] };
        }
        return apiClient.get(`${PREFIX}/food-groups/${groupId}/foods`);
    },

    saveFood: async (groupId, payload, foodId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu món ăn thành công", data: { foodId: foodId || Date.now(), ...payload } };
        if (foodId) {
            return apiClient.put(`${PREFIX}/foods/${foodId}`, payload);
        }
        return apiClient.post(`${PREFIX}/food-groups/${groupId}/foods`, payload);
    },

    deleteFood: async (foodId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa món ăn thành công", data: null };
        return apiClient.delete(`${PREFIX}/foods/${foodId}`);
    },

    // 4.4. QUẢN LÝ SƠ ĐỒ BÀN (TABLE LAYOUT)

    // --- KHU VỰC BÀN (TABLE AREAS) ---
    getTableAreas: async () => {
        if (USE_MOCK) {
            await new Promise(resolve => setTimeout(resolve, 300));
            return { status: 200, data: [{ tableAreaId: 1, name: "Tầng 1", status: "ACTIVE" }, { tableAreaId: 2, name: "Sân thượng", status: "ACTIVE" }] };
        }
        return apiClient.get(`${PREFIX}/table-areas`);
    },

    saveTableArea: async (payload, areaId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu khu vực thành công", data: { tableAreaId: areaId || Date.now(), ...payload } };
        if (areaId) {
            return apiClient.put(`${PREFIX}/table-areas/${areaId}`, payload);
        }
        return apiClient.post(`${PREFIX}/table-areas`, payload);
    },

    deleteTableArea: async (areaId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa khu vực thành công" };
        return apiClient.delete(`${PREFIX}/table-areas/${areaId}`);
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
        return apiClient.get(`${PREFIX}/table-areas/${areaId}/tables`);
    },

    saveTable: async (areaId, payload, tableId = null) => {
        if (USE_MOCK) return { status: 200, message: "Lưu bàn thành công", data: { tableId: tableId || Date.now(), ...payload } };
        if (tableId) {
            return apiClient.put(`${PREFIX}/tables/${tableId}`, payload);
        }
        return apiClient.post(`${PREFIX}/table-areas/${areaId}/tables`, payload);
    },

    deleteTable: async (tableId) => {
        if (USE_MOCK) return { status: 200, message: "Xóa bàn thành công" };
        return apiClient.delete(`${PREFIX}/tables/${tableId}`);
    }
};