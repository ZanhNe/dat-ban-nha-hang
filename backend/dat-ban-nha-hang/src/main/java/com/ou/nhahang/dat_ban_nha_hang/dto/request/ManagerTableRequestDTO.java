package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ManagerTableRequestDTO() {

    public record CreateOrUpdateTable(
            @NotBlank(message = "Tên bàn ăn không được để trống")
            String name,

            @NotNull(message = "Sức chứa không được thiếu")
            @Min(value = 1, message = "Sức chứa tối thiểu phải từ 1 người trở lên")
            Integer capacity,

            @NotBlank(message = "Trạng thái bàn ăn không được để trống")
            @Pattern(regexp = "^(AVAILABLE|OCCUPIED|MAINTENANCE|PRIVATE_EVENT)$", message = "Trạng thái không hợp lệ")
            String status
    ) {}
}
