// src/services/paymentService.js
import apiClient from './apiClient';

export const paymentService = {
    getPendingBookings: async () => {
        return apiClient.get('/users/me/bookings/pending-payment');
    },

    initiatePayment: async (bookingId) => {
        return apiClient.post(`/bookings/${bookingId}/payments/initiate`);
    }
};
