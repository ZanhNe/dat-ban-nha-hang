package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record CashierSessionDetailResponseDTO(
        Long sessionId,
        List<String> tableLabels,
        String customerName,
        Integer numberOfPeople,
        Long depositAmount,
        Long totalAmount,
        String status,
        List<CashierFoodItemSummaryDTO> items
) {
    @Builder
    public record CashierFoodItemSummaryDTO(
            String foodName,
            Long quantity,
            Long price,
            Long subtotal
    ) {}
}
