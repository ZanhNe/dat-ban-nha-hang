package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReceptionistRejectBookingRequestDTO(
        @NotBlank(message = "Lý do từ chối không được để trống")
        String reason
) {
}
