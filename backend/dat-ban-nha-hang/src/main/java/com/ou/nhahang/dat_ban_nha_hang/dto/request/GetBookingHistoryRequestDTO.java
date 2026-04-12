package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.ou.nhahang.dat_ban_nha_hang.entity.Booking.BookingStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetBookingHistoryRequestDTO(
        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0") Integer page,
        @Min(value = 10, message = "Số lượng trên mỗi trang phải lớn hơn 0") @Max(value = 50, message = "Số lượng trên mỗi trang phải nhỏ hơn hoặc bằng 50") Integer limit,
        BookingStatus status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
    public GetBookingHistoryRequestDTO {
        if (page == null || page < 0)
            page = 0;
        if (limit == null || limit <= 0)
            limit = 10;
        if (limit > 50)
            limit = 50;
    }
}
