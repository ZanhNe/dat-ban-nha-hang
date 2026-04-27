package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerTopFoodResponseDTO(
        Long foodId,
        String foodName,
        Long quantitySold,
        Long revenue
) {
}

