package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;

public record BookingResponseDTO(
                Long bookingId,
                Long restaurantId,
                String restaurantName,
                LocalDateTime bookingTime,
                Long guestCount,
                String status,
                String note) {
}
