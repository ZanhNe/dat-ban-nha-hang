package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;

public record AdminRestaurantSearchRequestDTO(
        @Min(value = 0, message = "Số trang phải >= 0")
        Integer page,

        @Min(value = 1, message = "Số lượng/trang phải >= 1")
        Integer limit,

        String status,
        String search
) {
    public AdminRestaurantSearchRequestDTO {
        if (page == null) page = 0;
        if (limit == null) limit = 10;
    }
}
