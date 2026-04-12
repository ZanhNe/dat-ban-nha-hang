package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerTableResponseDTO(
                Long tableId,
                String name,
                Integer capacity,
                String status) {
}
