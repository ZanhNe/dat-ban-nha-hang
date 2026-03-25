package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(
    @NotBlank(message = "Tên đăng nhập không được để trống")
    String username,

    @NotBlank(message = "Mật khẩu không được để trống")
    String password
) {}
