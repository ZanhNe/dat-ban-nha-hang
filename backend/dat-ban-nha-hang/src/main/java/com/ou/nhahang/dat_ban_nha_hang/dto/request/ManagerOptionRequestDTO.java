package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ManagerOptionRequestDTO() {

    public record CreateOrUpdateOption(
            @NotBlank(message = "Tên tùy chọn không được để trống")
            String name,

            @NotBlank(message = "Mô tả không được để trống")
            String description,

            @NotNull(message = "Giá tùy chọn không được thiếu")
            @Min(value = 0, message = "Giá không được âm")
            Long price,

            @NotBlank(message = "Trạng thái không được để trống")
            @Pattern(regexp = "^(OPENING|CLOSED)$", message = "Trạng thái phải là OPENING hoặc CLOSED")
            String status
    ) {}
}
