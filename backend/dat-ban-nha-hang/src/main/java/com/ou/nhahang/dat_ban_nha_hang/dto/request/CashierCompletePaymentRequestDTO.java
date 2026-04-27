package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CashierCompletePaymentRequestDTO(
        @NotBlank
        String paymentMethod,
        
        @NotNull
        Long totalAmount
) {}
