import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const getAuthHeaders = () => {
    const token = localStorage.getItem('accessToken');
    return {
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    };
};

export const paymentService = {
    getPendingBookings: async () => {
        const res = await axios.get(`${API_BASE_URL}/users/me/bookings/pending-payment`, getAuthHeaders());
        return res.data;
    },
    
    initiatePayment: async (bookingId) => {
        const res = await axios.post(`${API_BASE_URL}/bookings/${bookingId}/payments/initiate`, {}, getAuthHeaders());
        return res.data;
    }
};
