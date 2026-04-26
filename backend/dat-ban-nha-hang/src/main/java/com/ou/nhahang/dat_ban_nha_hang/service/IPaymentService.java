package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.BookingResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.PaymentInitResponseDTO;

import java.util.List;
import java.util.Map;

public interface IPaymentService {
    PaymentInitResponseDTO initiateTransaction(Long bookingId, Long userId, String ipAddress);

    Map<String, String> handleWebhook(Map<String, String> params);

    List<BookingResponseDTO> getPendingBookingsForUser(Long userId);
}
