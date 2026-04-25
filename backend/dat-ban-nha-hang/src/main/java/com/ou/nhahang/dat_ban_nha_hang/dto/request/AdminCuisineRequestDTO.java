package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminCuisineRequestDTO() {
    public record Upsert(
            @NotBlank(message = "Tên danh mục không được để trống")
            String name
    ) {
    }
}

