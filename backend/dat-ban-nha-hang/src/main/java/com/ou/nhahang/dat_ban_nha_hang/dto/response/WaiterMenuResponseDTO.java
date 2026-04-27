package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record WaiterMenuResponseDTO(
        Long menuId,
        String menuName,
        List<FoodGroupDTO> foodGroups
) {
    @Builder
    public record FoodGroupDTO(
            Long groupId,
            String name,
            List<FoodDescriptionDTO> foods
    ) {}

    @Builder
    public record FoodDescriptionDTO(
            Long foodDescriptionId,
            String name,
            Long price,
            List<OptionGroupDTO> optionGroups
    ) {}

    @Builder
    public record OptionGroupDTO(
            Long optionGroupId,
            String name,
            List<OptionDTO> options
    ) {}

    @Builder
    public record OptionDTO(
            Long optionId,
            String name,
            Long price
    ) {}
}
