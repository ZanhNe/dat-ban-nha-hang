package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SearchRestaurantRequestDTO(
        @NotBlank(message = "Địa chỉ không được để trống") String address,
        @NotBlank(message = "Loại hình ẩm thực không được để trống") String cuisine,
        @NotNull(message = "Bán kính tìm kiếm không được để trống") @Min(value = 1, message = "Bán kính tìm kiếm phải lớn hơn 0") Integer radius,
        @Min(value = 0, message = "Số trang phải lớn hơn hoặc bằng 0") Integer page,
        @Min(value = 10, message = "Số lượng trên mỗi trang phải lớn hơn 0") @Max(value = 100, message = "Số lượng trên mỗi trang phải nhỏ hơn hoặc bằng 100") Integer limit) {

    public SearchRestaurantRequestDTO {
        if (page == null)
            page = 1;
        if (limit == null)
            limit = 10;
    }
}
