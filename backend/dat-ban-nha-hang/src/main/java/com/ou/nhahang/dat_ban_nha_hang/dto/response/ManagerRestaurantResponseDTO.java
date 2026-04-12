package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerRestaurantResponseDTO(
                Long restaurantId,
                String name,
                String logo,
                String description,
                String address,
                String status,
                Long baseDepositValue,
                String depositPolicy,
                Integer dayOfWeek) {
}
