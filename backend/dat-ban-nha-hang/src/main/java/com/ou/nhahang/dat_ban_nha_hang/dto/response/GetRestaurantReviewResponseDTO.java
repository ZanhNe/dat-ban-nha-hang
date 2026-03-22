package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.time.LocalDateTime;

public record GetRestaurantReviewResponseDTO(
    Long id,
    String userName,
    String userAvatar,
    Integer rating,
    String comment,
    LocalDateTime createdAt
) {}
