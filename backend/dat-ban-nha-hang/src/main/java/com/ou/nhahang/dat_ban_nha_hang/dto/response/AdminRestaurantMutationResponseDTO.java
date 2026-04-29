package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record AdminRestaurantMutationResponseDTO(
        Long restaurantId,
        String status,
        String approvalStatus,
        String restaurantStatus,
        String commissionType,
        Long baseCommissionValue,
        Long managerUserId
) {
}
