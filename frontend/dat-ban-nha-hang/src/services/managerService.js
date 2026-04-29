import apiClient from './apiClient';
import { toLocalDateTimeParam } from './apiShape';

const PREFIX = '/manager';

export const managerService = {
    getOverview: async (fromDate, toDate) => {
        const params = {};
        if (fromDate) params.fromDate = toLocalDateTimeParam(fromDate);
        if (toDate) params.toDate = toLocalDateTimeParam(toDate, { endOfDay: true });
        return apiClient.get(`${PREFIX}/reports/overview`, { params });
    },

    getRevenueChart: async (fromDate, toDate, timeUnit = 'DAY') => {
        return apiClient.get(`${PREFIX}/reports/revenue-chart`, {
            params: {
                fromDate: toLocalDateTimeParam(fromDate),
                toDate: toLocalDateTimeParam(toDate, { endOfDay: true }),
                timeUnit
            }
        });
    },

    getTopFoods: async (fromDate, toDate, limit = 5) => {
        const params = { limit };
        if (fromDate) params.fromDate = toLocalDateTimeParam(fromDate);
        if (toDate) params.toDate = toLocalDateTimeParam(toDate, { endOfDay: true });
        return apiClient.get(`${PREFIX}/reports/top-foods`, { params });
    },

    getRestaurantInfo: async () => {
        return apiClient.get(`${PREFIX}/restaurant`);
    },

    updateRestaurantInfo: async (payload) => {
        return apiClient.put(`${PREFIX}/restaurant`, payload);
    },

    getStaffs: async (page = 0, limit = 10) => {
        return apiClient.get(`${PREFIX}/staffs`, { params: { page, limit } });
    },

    createStaff: async (payload) => {
        return apiClient.post(`${PREFIX}/staffs`, payload);
    },

    updateStaff: async (staffId, payload) => {
        return apiClient.put(`${PREFIX}/staffs/${staffId}`, payload);
    },

    deleteStaff: async (staffId) => {
        return apiClient.delete(`${PREFIX}/staffs/${staffId}`);
    },

    kickStaff: async (staffId) => {
        return apiClient.patch(`${PREFIX}/staffs/${staffId}/kick`);
    },

    getMenus: async () => {
        return apiClient.get(`${PREFIX}/menus`);
    },

    saveMenu: async (payload, menuId = null) => {
        return menuId
            ? apiClient.put(`${PREFIX}/menus/${menuId}`, payload)
            : apiClient.post(`${PREFIX}/menus`, payload);
    },

    deleteMenu: async (menuId) => {
        return apiClient.delete(`${PREFIX}/menus/${menuId}`);
    },

    getFoodGroups: async (menuId) => {
        return apiClient.get(`${PREFIX}/menus/${menuId}/food-groups`);
    },

    saveFoodGroup: async (menuId, payload, groupId = null) => {
        return groupId
            ? apiClient.put(`${PREFIX}/food-groups/${groupId}`, payload)
            : apiClient.post(`${PREFIX}/menus/${menuId}/food-groups`, payload);
    },

    deleteFoodGroup: async (groupId) => {
        return apiClient.delete(`${PREFIX}/food-groups/${groupId}`);
    },

    getFoods: async (groupId) => {
        return apiClient.get(`${PREFIX}/food-groups/${groupId}/foods`);
    },

    saveFood: async (groupId, payload, foodId = null) => {
        return foodId
            ? apiClient.put(`${PREFIX}/foods/${foodId}`, payload)
            : apiClient.post(`${PREFIX}/food-groups/${groupId}/foods`, payload);
    },

    deleteFood: async (foodId) => {
        return apiClient.delete(`${PREFIX}/foods/${foodId}`);
    },

    getTableAreas: async () => {
        return apiClient.get(`${PREFIX}/table-areas`);
    },

    saveTableArea: async (payload, areaId = null) => {
        return areaId
            ? apiClient.put(`${PREFIX}/table-areas/${areaId}`, payload)
            : apiClient.post(`${PREFIX}/table-areas`, payload);
    },

    deleteTableArea: async (areaId) => {
        return apiClient.delete(`${PREFIX}/table-areas/${areaId}`);
    },

    getTablesByArea: async (areaId) => {
        return apiClient.get(`${PREFIX}/table-areas/${areaId}/tables`);
    },

    saveTable: async (areaId, payload, tableId = null) => {
        return tableId
            ? apiClient.put(`${PREFIX}/tables/${tableId}`, payload)
            : apiClient.post(`${PREFIX}/table-areas/${areaId}/tables`, payload);
    },

    deleteTable: async (tableId) => {
        return apiClient.delete(`${PREFIX}/tables/${tableId}`);
    },

    getOptionGroups: async () => apiClient.get(`${PREFIX}/option-groups`),
    saveOptionGroup: async (payload, optionGroupId = null) =>
        optionGroupId
            ? apiClient.put(`${PREFIX}/option-groups/${optionGroupId}`, payload)
            : apiClient.post(`${PREFIX}/option-groups`, payload),
    deleteOptionGroup: async (optionGroupId) => apiClient.delete(`${PREFIX}/option-groups/${optionGroupId}`),
    getOptions: async (optionGroupId) => apiClient.get(`${PREFIX}/option-groups/${optionGroupId}/options`),
    saveOption: async (optionGroupId, payload, optionId = null) =>
        optionId
            ? apiClient.put(`${PREFIX}/options/${optionId}`, payload)
            : apiClient.post(`${PREFIX}/option-groups/${optionGroupId}/options`, payload),
    deleteOption: async (optionId) => apiClient.delete(`${PREFIX}/options/${optionId}`),
};