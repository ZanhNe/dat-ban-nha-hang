package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record ManagerFoodRequestDTO() {

    public record CreateOrUpdateFood(
            @NotBlank(message = "Tên món ăn không được để trống")
            String name,

            @NotBlank(message = "Mô tả không được để trống")
            String description,

            String image,

            @NotNull(message = "Giá tiền không được thiếu")
            @Min(value = 0, message = "Giá tiền không được âm")
            Long price,

            @NotBlank(message = "Trạng thái không được để trống")
            @Pattern(regexp = "^(OPENING|CLOSED)$", message = "Trạng thái món ăn phải là OPENING hoặc CLOSED")
            String status,

            // Tùy chọn, không bắt buộc (Món ăn có thể không có Option Group nào)
            List<Long> optionGroupIds
    ) {}
}
