package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CashierCompletePaymentRequestDTO(
        @NotBlank
        @Pattern(regexp = "^(CASH|CREDIT_CARD)$", message = "Phương thức thanh toán không hợp lệ")
        String paymentMethod,
        
        @NotNull
        @Min(value = 0, message = "Số tiền thanh toán phải lớn hơn hoặc bằng 0")
        Long totalAmount
) {}
