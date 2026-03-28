import apiClient from './apiClient';

const getAuthHeaders = () => {
    const token = localStorage.getItem('accessToken');
    return {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    };
};

export const paymentService = {
    getPendingBookings: async () => {
        const res = await apiClient.get(`/users/me/bookings/pending-payment`, getAuthHeaders());
        return res;
    },

    initiatePayment: async (bookingId) => {
        const res = await apiClient.post(`/bookings/${bookingId}/payments/initiate`, {}, getAuthHeaders());
        return res;
    }
};
