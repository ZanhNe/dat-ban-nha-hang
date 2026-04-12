package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ManagerFoodGroupRequestDTO() {

    public record CreateOrUpdateFoodGroup(
            @NotBlank(message = "Tên nhóm món ăn không được để trống")
            String name,

            @NotBlank(message = "Mô tả không được để trống")
            String description
    ) {}
}
