package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record AdminRestaurantDetailResponseDTO(
        Long restaurantId,
        String restaurantName,
        String status,
        String logo,
        String description,
        String address,
        String commissionType,
        Long baseCommissionValue,
        LocalDateTime createdAt,
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
            String docUrl,
            String docName,
            String docType,
            String docStatus,
            LocalDateTime expireDate
    ) {
    }
}

