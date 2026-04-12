package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ManagerOptionGroupRequestDTO() {

    public record CreateOrUpdateOptionGroup(
            @NotBlank(message = "Tên nhóm tùy chọn không được để trống")
            String name,

            @NotBlank(message = "Mô tả không được để trống")
            String description,

            @NotBlank(message = "Trạng thái không được để trống")
            @Pattern(regexp = "^(OPENING|CLOSED)$", message = "Trạng thái phải là OPENING hoặc CLOSED")
            String status
    ) {}
}
