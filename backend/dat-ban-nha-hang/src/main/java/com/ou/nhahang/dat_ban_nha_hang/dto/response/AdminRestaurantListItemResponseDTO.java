package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record AdminRestaurantListItemResponseDTO(
        Long restaurantId,
        String restaurantName,
        String managerName,
        String status,
        LocalDateTime createdAt
) {
}

