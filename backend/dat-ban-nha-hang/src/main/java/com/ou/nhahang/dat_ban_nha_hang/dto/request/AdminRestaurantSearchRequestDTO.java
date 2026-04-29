package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record AdminRestaurantSearchRequestDTO(
        @Min(value = 0, message = "Số trang phải >= 0")
        Integer page,

        @Min(value = 1, message = "Số lượng/trang phải >= 1")
        Integer limit,

        @Pattern(
                regexp = "^(APPROVED|PENDING|REJECTED|OPENING|CLOSED|SUSPENDED)?$",
                message = "status phải là APPROVED, PENDING, REJECTED, OPENING, CLOSED hoặc SUSPENDED"
        )
        String status,
        String search
) {
    public AdminRestaurantSearchRequestDTO {
        if (page == null) page = 0;
        if (limit == null) limit = 10;
    }
}
