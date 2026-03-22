package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO.Result.Geometry.Location;

import lombok.Builder;

@Builder
public record SearchRestaurantResponseDTO(
        Long restaurantId,
        String restaurantName,
        String restaurantLogo,
        List<String> restaurantCuisines,
        Double restaurantAvgRating,
        Integer restaurantTotalReviews,
        String restaurantAddress,
        Double restaurantDistance,
        String restaurantDepositType,
        Double restaurantBaseDeposit,
        Boolean restaurantIsOpen,
        Location restaurantLocation) {

}
