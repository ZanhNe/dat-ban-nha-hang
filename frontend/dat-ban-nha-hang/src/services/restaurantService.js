import apiClient from './apiClient';

const CUSTOMER_RESTAURANT_PREFIX = '/customer/restaurants';

export const restaurantService = {
    getRestaurantDetail: async (id, origin) => {
        const params = {};
        if (origin) params.origin = origin;
        return apiClient.get(`${CUSTOMER_RESTAURANT_PREFIX}/${id}`, { params });
    },

    getRestaurantMenu: async (id) => {
        return apiClient.get(`${CUSTOMER_RESTAURANT_PREFIX}/${id}/menu`);
    },

    getRestaurantReviews: async (id, { limit = 10, cursor = null, rating = null, sort = null } = {}) => {
        const params = {};
        if (limit) params.limit = limit;
        if (cursor) params.cursor = cursor;
        if (rating) params.rating = rating;
        if (sort) params.sort = sort;
        return apiClient.get(`${CUSTOMER_RESTAURANT_PREFIX}/${id}/reviews`, { params });
    },

    getAvailableTables: async (id, date, time, guests) => {
        return apiClient.get(`${CUSTOMER_RESTAURANT_PREFIX}/${id}/tables`, {
            params: { date, time, guests }
        });
    },

    createBooking: async (id, payload) => {
        return apiClient.post(`${CUSTOMER_RESTAURANT_PREFIX}/${id}/bookings`, payload);
    },

    getBookingHistory: async (params = {}) => {
        return apiClient.get(`${CUSTOMER_RESTAURANT_PREFIX}/bookings/my-history`, { params });
    },

    createReview: async (id, payload) => {
        return apiClient.post(`${CUSTOMER_RESTAURANT_PREFIX}/${id}/reviews`, payload);
    },

    registerRestaurant: async (formData) => {
        return apiClient.post(`${CUSTOMER_RESTAURANT_PREFIX}/register`, formData);
    },
};
