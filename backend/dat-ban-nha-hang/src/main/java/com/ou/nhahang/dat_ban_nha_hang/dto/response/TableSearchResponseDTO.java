package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TableSearchResponseDTO(
    Long restaurantId,
    RequestContextDTO requestContext,
    List<AreaDTO> areas
) {
    @Builder
    public record RequestContextDTO(
        LocalDateTime dateTime,
        Long guests
    ) {}

    @Builder
    public record AreaDTO(
        String areaName,
        List<TableSearchItemDTO> tables
    ) {}

    @Builder
    public record TableSearchItemDTO(
        Long tableId,
        String label,
        Long capacity,
        boolean isAvailable,
        String reason
    ) {}
}
