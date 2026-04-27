package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record WaiterSessionDetailResponseDTO(
        Long sessionId,
        List<TableDTO> tables,
        Integer numberOfPeople,
        String status,
        List<FoodOrderSummaryDTO> foodOrders
) {
    @Builder
    public record TableDTO(
            Long tableId,
            String label
    ) {}

    @Builder
    public record FoodOrderSummaryDTO(
            Long orderId,
            String status,
            Integer itemsCount,
            LocalDateTime createdAt
    ) {}
}
