package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record AdminRestaurantDetailResponseDTO(
        Long restaurantId,
        String restaurantName,
        String status,
        ManagerDTO manager,
        List<LegalDocDTO> legalDocs
) {
    @Builder
    public record ManagerDTO(
            Long userId,
            String fullName,
            String phone
    ) {
    }

    @Builder
    public record LegalDocDTO(
            Long docId,
            String docUrl
    ) {
    }
}

