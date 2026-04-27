package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReceptionistCancelBookingRequestDTO(
        @NotBlank(message = "Lý do hủy không được để trống")
        String reason
) {}
