package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ManagerStaffManagementRequestDTO() {

    public record ListStaffs(
            @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0")
            Integer page,

            @Min(value = 1, message = "Số lượng trên mỗi trang phải lớn hơn 0")
            @Max(value = 50, message = "Số lượng trên mỗi trang phải nhỏ hơn hoặc bằng 50")
            Integer limit,

            @Pattern(regexp = "^(WAITER|RECEPTIONIST|CASHIER)$", message = "role không hợp lệ")
            String role
    ) {
        public ListStaffs {
            if (page == null || page < 0) page = 0;
            if (limit == null || limit <= 0) limit = 10;
            if (limit > 50) limit = 50;
            if (role != null && role.isBlank()) role = null;
        }
    }

    public record CreateStaff(
            @NotBlank(message = "username không được để trống")
            String username,

            @NotBlank(message = "password không được để trống")
            String password,

            @NotBlank(message = "fullName không được để trống")
            String fullName,

            @NotBlank(message = "phone không được để trống")
            String phone,

            String email,

            @NotBlank(message = "role không được để trống")
            @Pattern(regexp = "^(WAITER|RECEPTIONIST|CASHIER)$", message = "role không hợp lệ")
            String role
    ) {
    }
}

