package com.ou.nhahang.dat_ban_nha_hang.event.dto;

import lombok.Builder;

@Builder
public record BookingRejectedEvent(
        Long userId,
        Long bookingId,
        String customerName,
        String reason) {
}
