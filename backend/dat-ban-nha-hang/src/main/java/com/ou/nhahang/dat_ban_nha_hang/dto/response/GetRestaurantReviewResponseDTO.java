package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetRestaurantReviewResponseDTO(
        Long reviewId,
        String userName,
        String userAvatar,
        Integer rating,
        String comment,
        LocalDateTime createdAt) {
}
