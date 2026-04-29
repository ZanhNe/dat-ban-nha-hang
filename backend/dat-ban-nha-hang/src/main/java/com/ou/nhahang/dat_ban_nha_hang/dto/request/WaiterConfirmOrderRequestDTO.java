package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WaiterConfirmOrderRequestDTO(
        @NotEmpty(message = "Danh sách món không được để trống")
        List<FoodItemRequestDTO> items
) {
    public record FoodItemRequestDTO(
            @NotNull
            Long foodDescriptionId,
            @NotNull
            @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
            Long quantity,
            List<Long> optionIds
    ) {}
}
