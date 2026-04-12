package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ManagerTableAreaRequestDTO() {

    public record CreateOrUpdateTableArea(
            @NotBlank(message = "Tên khu vực bàn ăn không được để trống")
            String name,

            @NotBlank(message = "Mô tả khu vực không được để trống")
            String description,

            @NotBlank(message = "Trạng thái không được để trống")
            @Pattern(regexp = "^(ACTIVE|CLOSED|MAINTENANCE|PRIVATE_EVENT)$", message = "Trạng thái không hợp lệ")
            String status
    ) {}
}
