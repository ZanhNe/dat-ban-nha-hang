package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record ManagerFoodResponseDTO(
                Long foodId,
                String name,
                String description,
                String image,
                Long price,
                String status,
                List<OptionGroupResponse> optionGroups) {
        @Builder
        public record OptionGroupResponse(
                        Long optionGroupId,
                        String name,
                        String status) {
        }
}
