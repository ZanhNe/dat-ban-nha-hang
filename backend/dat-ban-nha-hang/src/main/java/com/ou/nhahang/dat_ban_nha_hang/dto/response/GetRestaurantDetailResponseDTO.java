package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.GeoCoordinateResponseDTO.Result.Geometry.Location;

import lombok.Builder;

@Builder
public record GetRestaurantDetailResponseDTO(
        Long restaurantId,
        String restaurantName,
        String restaurantImage,
        String restaurantLogo,
        String restaurantDescription,
        String restaurantAddress,
        Location restaurantLocation,
        List<String> restaurantCuisines,
        Double restaurantAvgRating,
        Integer restaurantTotalReviews,
        String restaurantDepositPolicy,
        Long restaurantBaseDeposit,
        List<OperationTimeDTO> restaurantOperationTimes,
        List<TableAreaDTO> restaurantTableAreas,
        GeoDirectionResponseDTO restaurantDirections) {


    @Builder
    public record OperationTimeDTO(
            String day,
            String open,
            String close) {
    }

    @Builder
    public record TableAreaDTO(
            String areaName,
            int availableTables,
            int maxCapacity) {
    }
}
