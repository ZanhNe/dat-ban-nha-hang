import apiClient from './apiClient';

export const receptionistService = {
    getBookings: async (params = {}) => {
        return apiClient.get('/receptionist/bookings', { params });
    },

    confirmBooking: async (id) => {
        return apiClient.patch(`/receptionist/bookings/${id}/confirm`);
    },

    rejectBooking: async (id, reason) => {
        return apiClient.patch(`/receptionist/bookings/${id}/reject`, { reason });
    },

    getConfirmedBookings: async (page = 0, limit = 10) => {
        return apiClient.get('/receptionist/bookings', { params: { status: 'CONFIRMED', page, limit } });
    },

    getAwaitingBookings: async (page = 0, limit = 10) => {
        return apiClient.get('/receptionist/bookings/awaiting-confirmation', { params: { page, limit } });
    },

    getBookingDetail: async (bookingId) => {
        return apiClient.get(`/receptionist/bookings/${bookingId}`);
    },

    checkInBooking: async (bookingId) => {
        return apiClient.patch(`/receptionist/bookings/${bookingId}/check-in`);
    },

    cancelBooking: async (bookingId) => {
        return apiClient.patch(`/receptionist/bookings/${bookingId}/cancel`);
    },
};