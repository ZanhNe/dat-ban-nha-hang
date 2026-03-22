package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record GetRestaurantMenuResponseDTO(
        Long restaurantId,
        String restaurantName,
        List<MenuDTO> restaurantMenus) {
    @Builder
    public record MenuDTO(
            Long menuId,
            String menuName,
            String menuDescription,
            List<FoodGroupDTO> restaurantMenu) {
    }

    @Builder
    public record FoodGroupDTO(
            Long groupId,
            String groupName,
            String groupDescription,
            List<FoodItemDTO> items) {
    }

    @Builder
    public record FoodItemDTO(
            Long itemId,
            String itemName,
            String itemDescription,
            Long itemPrice,
            String itemImage) {
    }
}
