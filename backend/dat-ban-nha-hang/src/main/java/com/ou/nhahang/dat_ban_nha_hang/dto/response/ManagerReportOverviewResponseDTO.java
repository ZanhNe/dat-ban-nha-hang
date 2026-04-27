package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

@Builder
public record ManagerReportOverviewResponseDTO(
        Long totalRevenue,
        Long netRevenue,
        Long totalBookings,
        Long totalCustomers,
        Long averageBill
) {
}

