package com.ou.nhahang.dat_ban_nha_hang.event.dto;

import lombok.Builder;

@Builder
public record RequestBookingEvent(
        Long bookingId,
        String customerName) {
}
