package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record BookingResponseDTO(
        Long bookingId,
        Long restaurantId,
        String restaurantName,
        LocalDateTime bookingTime,
        Long guestCount,
        Long depositAmount,
        String status,
        String note) {
}
