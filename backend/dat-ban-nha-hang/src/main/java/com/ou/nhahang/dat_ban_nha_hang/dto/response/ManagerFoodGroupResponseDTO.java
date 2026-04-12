package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerFoodGroupResponseDTO(
        Long groupId,
        String name,
        String description
) {
}
