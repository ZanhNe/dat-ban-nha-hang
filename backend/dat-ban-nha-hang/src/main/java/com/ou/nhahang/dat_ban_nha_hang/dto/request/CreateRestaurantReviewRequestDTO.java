package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRestaurantReviewRequestDTO(
    @NotNull(message = "Vui lòng cung cấp số sao đánh giá")
    @Min(value = 1, message = "Đánh giá tối thiểu là 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa là 5 sao")
    Integer rating,

    @Size(max = 500, message = "Bình luận không được vượt quá 500 ký tự")
    String comment
) {}
