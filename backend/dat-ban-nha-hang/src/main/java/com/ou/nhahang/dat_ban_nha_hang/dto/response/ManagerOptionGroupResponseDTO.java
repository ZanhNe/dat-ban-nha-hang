package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerOptionGroupResponseDTO(
                Long optionGroupId,
                String name,
                String description,
                String status) {
}
