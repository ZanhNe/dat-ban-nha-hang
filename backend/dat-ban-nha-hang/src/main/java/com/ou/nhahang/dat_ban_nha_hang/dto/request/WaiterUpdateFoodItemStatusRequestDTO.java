package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record WaiterUpdateFoodItemStatusRequestDTO(
        @NotBlank
        @Pattern(regexp = "^(SERVED|CANCELLED)$", message = "Trạng thái món ăn chỉ được SERVED hoặc CANCELLED")
        String status
) {}
