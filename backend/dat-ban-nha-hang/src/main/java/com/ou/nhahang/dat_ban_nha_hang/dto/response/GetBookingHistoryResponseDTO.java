package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking.BookingStatus;

import lombok.Builder;

@Builder
public record GetBookingHistoryResponseDTO(
        Long bookingId,
        BookingTimeDTO bookingTime,
        Long quantity,
        BookingStatus status,
        Long depositAmount,
        List<TableSummaryDTO> tables,
        RestaurantSummaryDTO restaurant,
        LocalDateTime createdAt) {

    @Builder
    public record BookingTimeDTO(
            LocalDateTime startTime,
            LocalDateTime endTime) {
    }

    @Builder
    public record TableSummaryDTO(
            Long tableId,
            String tableLabel) {
    }

    @Builder
    public record RestaurantSummaryDTO(
            Long restaurantId,
            String restaurantName,
            String restaurantLogo,
            String restaurantAddress) {
    }
}
