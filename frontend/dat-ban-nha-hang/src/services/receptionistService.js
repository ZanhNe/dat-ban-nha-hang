// src/services/receptionistService.js
import apiClient from './apiClient';

const USE_MOCK = true;

export const receptionistService = {
    // Lấy danh sách booking (PENDING để xác nhận, CONFIRMED để check-in)
    getBookings: async (status) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 400));
            return {
                status: 200,
                data: [
                    { bookingId: 1234, fullName: "Anh Tuấn", phone: "0901234567", guestCount: 4, bookingDate: "2026-04-27", bookingTime: "19:00", status: status, note: "Bàn gần cửa sổ" }
                ]
            };
        }
        return apiClient.get('/receptionist/bookings', { params: { status } });
    },

    // 6.1 Xác nhận
    confirmBooking: async (id) => {
        if (USE_MOCK) return { status: 200, message: "Xác nhận thành công", data: { bookingId: id, status: "CONFIRMED" } };
        return apiClient.patch(`/receptionist/bookings/${id}/confirm`);
    },

    // 6.2 Từ chối
    rejectBooking: async (id, reason) => {
        if (USE_MOCK) return { status: 200, message: "Đã từ chối", data: { bookingId: id, status: "REJECTED" } };
        return apiClient.patch(`/receptionist/bookings/${id}/reject`, { reason });
    },

    // Lấy danh sách khách sắp đến (Trạng thái CONFIRMED)
    getConfirmedBookings: async () => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 500));
            return {
                status: 200,
                data: [
                    { bookingId: 1234, fullName: "Anh Tuấn", phone: "0901234567", guestCount: 4, bookingTime: "19:00", tableInfo: "Bàn T1-05", status: "CONFIRMED" },
                    { bookingId: 1236, fullName: "Chị Lan", phone: "0988888888", guestCount: 2, bookingTime: "19:30", tableInfo: "Bàn T1-02", status: "CONFIRMED" }
                ]
            };
        }
        return apiClient.get('/receptionist/bookings', { params: { status: 'CONFIRMED' } });
    },

    // 6.3. Thực hiện Check-in
    checkInBooking: async (bookingId) => {
        if (USE_MOCK) {
            await new Promise(r => setTimeout(r, 600));
            return {
                status: 200,
                message: "Check-in thành công",
                data: { sessionId: 1001, tableIds: [5], status: "ACTIVE" }
            };
        }
        return apiClient.patch(`/receptionist/bookings/${bookingId}/check-in`);
    }
};