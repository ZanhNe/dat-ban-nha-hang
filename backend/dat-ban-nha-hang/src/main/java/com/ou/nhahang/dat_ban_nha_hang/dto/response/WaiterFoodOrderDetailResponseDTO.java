package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record WaiterFoodOrderDetailResponseDTO(
        Long orderId,
        String status,
        List<FoodItemDetailDTO> items
) {
    @Builder
    public record FoodItemDetailDTO(
            Long itemId,
            String foodName,
            Long quantity,
            List<String> selectedOptions,
            String status
    ) {}
}
