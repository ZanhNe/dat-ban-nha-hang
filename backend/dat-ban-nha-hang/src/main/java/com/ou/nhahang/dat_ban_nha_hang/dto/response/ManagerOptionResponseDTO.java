package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerOptionResponseDTO(
                Long optionId,
                String name,
                String description,
                Long price,
                String status) {
}
