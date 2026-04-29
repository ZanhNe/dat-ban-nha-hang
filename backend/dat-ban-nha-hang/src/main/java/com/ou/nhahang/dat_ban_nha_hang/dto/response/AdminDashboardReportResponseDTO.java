package com.ou.nhahang.dat_ban_nha_hang.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AdminDashboardReportResponseDTO(
        Long totalRevenue,
        Long totalBookings,
        Long totalRestaurants,
        Long totalNewUsers,
        Long totalNewCustomers,
        LocalDateTime fromDate,
        LocalDateTime toDate,
        LocalDateTime generatedAt
) {
}

