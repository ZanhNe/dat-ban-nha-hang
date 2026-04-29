package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record AdminUserSearchRequestDTO(
        @Min(value = 0, message = "Số trang phải >= 0")
        Integer page,

        @Min(value = 1, message = "Số lượng/trang phải >= 1")
        Integer limit,

        @Pattern(
                regexp = "^(ROLE_)?(ADMIN|MANAGER|CUSTOMER|RECEPTIONIST|WAITER|CASHIER)?$",
                message = "role không hợp lệ"
        )
        String role,
        Long restaurantId,
        @Pattern(regexp = "^(ACTIVE|BANNED)?$", message = "status phải là ACTIVE hoặc BANNED")
        String status,
        String search
) {
    public AdminUserSearchRequestDTO {
        if (page == null) page = 0;
        if (limit == null) limit = 10;
    }
}
