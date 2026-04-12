package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record ManagerStaffResponseDTO(
                Long userId,
                String fullName,
                String username,
                String email,
                String phone,
                List<RoleResponse> roles,
                String status) {
        @Builder
        public record RoleResponse(
                        Long id,
                        String name) {
        }
}
