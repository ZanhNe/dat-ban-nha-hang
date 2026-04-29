package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CashierSessionDetailResponseDTO(
        Long sessionId,
        List<String> tableLabels,
        String customerName,
        Integer numberOfPeople,
        Long depositAmount,
        Long totalAmount,
        Long amountToPay,
        String status,
        List<CashierOrderSummaryDTO> orders
) {
    @Builder
    public record CashierOrderSummaryDTO(
            Long orderId,
            String status,
            LocalDateTime createdAt,
            List<CashierFoodItemSummaryDTO> items
    ) {}

    @Builder
    public record CashierFoodItemSummaryDTO(
            String foodName,
            Long quantity,
            Long price,
            Long totalItemPrice
    ) {}
}
