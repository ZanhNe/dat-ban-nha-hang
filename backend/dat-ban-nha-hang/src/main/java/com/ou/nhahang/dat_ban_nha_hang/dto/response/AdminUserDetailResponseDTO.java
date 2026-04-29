package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminUserDetailResponseDTO(
        Long userId,
        String fullName,
        String username,
        String email,
        String phone,
        List<AdminUserResponseDTO.RoleResponse> roles,
        String status,
        String address,
        String avatar,
        WorkplaceDTO workplace
) {
    @Builder
    public record WorkplaceDTO(
            Long restaurantId,
            String name,
            String restaurantName,
            String status,
            String avatar
    ) {
    }
}

