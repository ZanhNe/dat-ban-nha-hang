package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerTableAreaResponseDTO(
                Long tableAreaId,
                String name,
                String status) {
}
