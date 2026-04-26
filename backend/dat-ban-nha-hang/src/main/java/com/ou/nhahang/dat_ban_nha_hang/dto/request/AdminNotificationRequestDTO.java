package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AdminNotificationRequestDTO() {

    public record Broadcast(
            @NotBlank(message = "Tiêu đề không được để trống")
            String title,

            @NotBlank(message = "Nội dung không được để trống")
            String content,

            @NotBlank(message = "targetRole không được để trống")
            @Pattern(regexp = "^(ALL|CUSTOMER|MANAGER)$", message = "targetRole không hợp lệ")
            String targetRole,

            @NotBlank(message = "Loại thông báo không được để trống")
            @Pattern(regexp = "^(SYSTEM_ALERT|SYSTEM_MESSAGE)$", message = "Loại thông báo không hợp lệ")
            String type
    ) {
    }

    public record SendToUser(
            @NotNull(message = "userId không được thiếu")
            Long userId,

            @NotBlank(message = "Tiêu đề không được để trống")
            String title,

            @NotBlank(message = "Nội dung không được để trống")
            String content,

            @NotBlank(message = "Loại thông báo không được để trống")
            @Pattern(regexp = "^(SYSTEM_ALERT|SYSTEM_MESSAGE)$", message = "Loại thông báo không hợp lệ")
            String type
    ) {
    }
}

