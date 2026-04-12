package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ManagerMenuRequestDTO() {

    public record CreateOrUpdateMenu(
            @NotBlank(message = "Tên menu không được để trống")
            String name,

            @NotBlank(message = "Mô tả không được để trống")
            String description
    ) {}
}
