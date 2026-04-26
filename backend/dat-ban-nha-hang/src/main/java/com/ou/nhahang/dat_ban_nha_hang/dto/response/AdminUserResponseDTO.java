package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record AdminUserResponseDTO(
        Long userId,
        String fullName,
        String username,
        String email,
        String phone,
        List<RoleResponse> roles,
        String status
) {
    public record RoleResponse(Long id, String name) {
    }
}

