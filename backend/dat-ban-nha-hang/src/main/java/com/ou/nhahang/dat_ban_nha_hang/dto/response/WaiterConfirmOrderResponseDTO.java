package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record WaiterConfirmOrderResponseDTO(
        Long orderId,
        String status,
        Integer itemsCount
) {}
