package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerMenuResponseDTO(
                Long menuId,
                String name,
                String description) {
}
