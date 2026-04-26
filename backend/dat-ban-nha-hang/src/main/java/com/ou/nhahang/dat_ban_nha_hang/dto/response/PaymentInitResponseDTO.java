package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record PaymentInitResponseDTO(
                String clientSecret,
                Long transactionId,
                Long bookingId,
                Long amount,
                String currency,
                String status) {
}
