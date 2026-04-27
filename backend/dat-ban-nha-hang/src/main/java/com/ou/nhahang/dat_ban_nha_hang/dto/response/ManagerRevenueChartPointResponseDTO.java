package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerRevenueChartPointResponseDTO(
        String date,
        Long revenue,
        Long netRevenue,
        Long bookingsCount
) {
}

