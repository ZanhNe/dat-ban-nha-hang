package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerStaffManagementResponseDTO(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String role,
        String status,
        Long workplaceRestaurantId
) {
}

