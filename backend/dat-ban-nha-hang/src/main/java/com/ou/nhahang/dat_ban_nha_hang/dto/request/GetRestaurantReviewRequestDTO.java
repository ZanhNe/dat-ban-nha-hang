package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetRestaurantReviewRequestDTO(
        @Min(1) @Max(5) Integer rating,
        String sort,
        @Min(value = 10, message = "Số lượng trên mỗi trang phải lớn hơn 0") @Max(value = 50, message = "Số lượng trên mỗi trang phải nhỏ hơn hoặc bằng 50") Integer limit,
        Long cursor) {
    public GetRestaurantReviewRequestDTO {
        if (limit == null || limit <= 0)
            limit = 10;
        if (limit > 50)
            limit = 50;
        if (sort == null)
            sort = "NEWEST";
    }
}
