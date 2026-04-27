package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WaiterCancelOrderRequestDTO(
        @NotBlank
        String reason
) {}
