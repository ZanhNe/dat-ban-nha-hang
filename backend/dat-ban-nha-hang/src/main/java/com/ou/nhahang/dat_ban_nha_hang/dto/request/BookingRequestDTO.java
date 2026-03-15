package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingRequestDTO(
        @NotNull(message = "Thời gian đặt là bắt buộc") @Future(message = "Thời gian đặt phải là trong tương lai") LocalDateTime bookingTime,
        @NotNull(message = "Số lượng khách là bắt buộc") @Min(value = 1, message = "Số lượng khách phải lớn hơn 0") Long quantity,
        @NotNull(message = "ID nhà hàng là bắt buộc") Long restaurantId,
        @NotNull(message = "Ít nhất 1 bàn được chọn") List<Long> tableIds) {

}
