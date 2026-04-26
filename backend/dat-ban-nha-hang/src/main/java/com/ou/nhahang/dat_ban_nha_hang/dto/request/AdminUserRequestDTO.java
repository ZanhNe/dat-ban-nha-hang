package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminUserRequestDTO() {

    public record Create(
            @NotBlank(message = "username không được để trống")
            String username,

            @NotBlank(message = "password không được để trống")
            String password,

            @NotBlank(message = "fullName không được để trống")
            String fullName,

            String email,

            @NotBlank(message = "phone không được để trống")
            String phone,

            @NotNull(message = "roleId không được thiếu")
            Long roleId
    ) {
    }

    public record Update(
            @NotBlank(message = "fullName không được để trống")
            String fullName,

            @NotBlank(message = "email không được để trống")
            String email,

            @NotBlank(message = "phone không được để trống")
            String phone,

            @NotBlank(message = "status không được để trống")
            @Pattern(regexp = "^(ACTIVE|BANNED)$", message = "status không hợp lệ")
            String status,

            @NotNull(message = "roleId không được thiếu")
            Long roleId
    ) {
    }

    public record AssignManager(
            @NotNull(message = "userId không được thiếu")
            Long userId
    ) {
    }

    public record UpdateWorkplace(
            Long restaurantId,

            @NotNull(message = "roleId không được thiếu")
            Long roleId
    ) {
    }
}

