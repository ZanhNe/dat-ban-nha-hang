package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerReportOverviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRevenueChartPointResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTopFoodResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IManagerReportService {
    ManagerReportOverviewResponseDTO getOverview(Long restaurantId, Long managerId, LocalDateTime from, LocalDateTime toExclusive);

    List<ManagerRevenueChartPointResponseDTO> getRevenueChart(
            Long restaurantId,
            Long managerId,
            LocalDateTime from,
            LocalDateTime toExclusive,
            String timeUnit);

    List<ManagerTopFoodResponseDTO> getTopFoods(Long restaurantId, Long managerId, LocalDateTime from, LocalDateTime toExclusive, int limit);
}

