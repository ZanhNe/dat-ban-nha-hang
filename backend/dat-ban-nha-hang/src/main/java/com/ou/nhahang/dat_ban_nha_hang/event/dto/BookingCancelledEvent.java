package com.ou.nhahang.dat_ban_nha_hang.event.dto;

import lombok.Builder;

@Builder
public record BookingCancelledEvent(
        Long userId,
        Long bookingId,
        String customerName) {

}
