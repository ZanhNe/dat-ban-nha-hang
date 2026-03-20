package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record TableSearchRequestDTO(
    @NotBlank(message = "Ngày không được để trống")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Ngày phải có định dạng YYYY-MM-DD")
    String date,

    @NotBlank(message = "Giờ không được để trống")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Giờ phải có định dạng HH:mm")
    String time,

    @NotNull(message = "Số lượng khách không được để trống")
    @Min(value = 1, message = "Số lượng khách phải lớn hơn 0")
    Long guests
) {}
