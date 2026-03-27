package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitResponseDTO {
    private String clientSecret;
    private Long transactionId;
    private Long bookingId;
    private Long amount;
    private String currency;
    private String status;
}
