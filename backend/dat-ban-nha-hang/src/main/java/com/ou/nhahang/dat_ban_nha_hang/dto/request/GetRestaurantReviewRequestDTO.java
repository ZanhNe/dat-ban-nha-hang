package com.ou.nhahang.dat_ban_nha_hang.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetRestaurantReviewRequestDTO(
    @Min(1) @Max(5) Integer rating,
    String sort,
    Integer limit,
    Long cursor
) {
    public GetRestaurantReviewRequestDTO {
        if (limit == null || limit <= 0) limit = 10;
        if (limit > 50) limit = 50;
        if (sort == null) sort = "NEWEST";
    }
}
