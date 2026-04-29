package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record CashierInitiatePaymentResponseDTO(
        Long sessionId,
        String status,
        Long transactionId,
        Long amountToPay
) {}
