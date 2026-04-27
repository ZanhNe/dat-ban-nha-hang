package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReceptionistBookingListResponseDTO(
        Long bookingId,
        String customerName,
        String customerPhone,
        LocalDateTime bookingTime,
        Integer numberOfPeople,
        String status
) {}
