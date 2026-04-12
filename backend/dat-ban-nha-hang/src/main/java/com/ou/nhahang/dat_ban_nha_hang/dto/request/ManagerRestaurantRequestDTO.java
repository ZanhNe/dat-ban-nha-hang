package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ManagerRestaurantRequestDTO() {

    public record UpdateRestaurant(
            @NotBlank(message = "Tên nhà hàng không được để trống")
            String name,

            String logo,

            String description,

            @NotBlank(message = "Địa chỉ nhà hàng không được để trống")
            String address,

            @NotNull(message = "Giá trị đặt cọc cơ bản không được thiếu")
            @Min(value = 0, message = "Giá trị đặt cọc không được âm")
            Long baseDepositValue,

            @NotBlank(message = "Chính sách cọc không được để trống")
            @Pattern(regexp = "^(FIXED|PER_GUEST|NONE)$", message = "Chính sách cọc phải là FIXED, PER_GUEST, hoặc NONE")
            String depositPolicy,

            @NotNull(message = "Số ngày áp dụng không được thiếu")
            @Min(value = 0, message = "Số ngày không hợp lệ")
            Integer dayOfWeek
    ) {}
}
