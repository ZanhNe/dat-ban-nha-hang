package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.PaymentInitResponseDTO;

import java.util.List;

public interface IPaymentService {
    PaymentInitResponseDTO initiatePayment(Long bookingId, Long userId);
    void handleStripeWebhook(String payload, String sigHeader);
    void approvePayment(Long bookingId, Long userId);
    void rejectPayment(Long bookingId, Long userId);
    List<BookingResponseDTO> getPendingBookingsForUser(Long userId);
}
