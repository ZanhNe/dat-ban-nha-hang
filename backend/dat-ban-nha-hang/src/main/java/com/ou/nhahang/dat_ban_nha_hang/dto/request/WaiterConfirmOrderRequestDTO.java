package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WaiterConfirmOrderRequestDTO(
        @NotNull
        List<FoodItemRequestDTO> items
) {
    public record FoodItemRequestDTO(
            @NotNull
            Long foodDescriptionId,
            @NotNull
            Long quantity,
            List<Long> optionIds
    ) {}
}
