package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReceptionistBookingDetailResponseDTO(
        Long bookingId,
        CustomerDTO customer,
        LocalDateTime bookingTime,
        Integer numberOfPeople,
        String note,
        Long depositAmount,
        String status,
        List<AssignedTableDTO> assignedTables
) {
    @Builder
    public record CustomerDTO(
            Long userId,
            String fullName,
            String phone
    ) {}

    @Builder
    public record AssignedTableDTO(
            Long tableId,
            String label
    ) {}
}
