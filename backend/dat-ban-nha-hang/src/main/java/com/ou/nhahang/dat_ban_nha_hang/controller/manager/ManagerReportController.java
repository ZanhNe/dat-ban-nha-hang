package com.ou.nhahang.dat_ban_nha_hang.controller.manager;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerReportOverviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRevenueChartPointResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTopFoodResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/restaurants")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ManagerReportController {

    private final IManagerReportService managerReportService;

    @GetMapping("/{restaurantId}/reports/overview")
    public ResponseEntity<ApiResponse<ManagerReportOverviewResponseDTO>> getOverview(
            @PathVariable Long restaurantId,
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate,
            Authentication authentication) {
        Long managerId = (Long) authentication.getCredentials();
        LocalDateTime from = (fromDate == null || fromDate.isBlank()) ? null : LocalDateTime.parse(fromDate);
        LocalDateTime toExclusive = (toDate == null || toDate.isBlank()) ? null : LocalDateTime.parse(toDate);

        ManagerReportOverviewResponseDTO data = managerReportService.getOverview(restaurantId, managerId, from, toExclusive);
        ApiResponse<ManagerReportOverviewResponseDTO> response = ApiResponse.<ManagerReportOverviewResponseDTO>builder()
                .status(200)
                .message("Thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{restaurantId}/reports/revenue-chart")
    public ResponseEntity<ApiResponse<List<ManagerRevenueChartPointResponseDTO>>> getRevenueChart(
            @PathVariable Long restaurantId,
            @RequestParam(name = "fromDate") String fromDate,
            @RequestParam(name = "toDate") String toDate,
            @RequestParam(name = "timeUnit", defaultValue = "DAY") String timeUnit,
            Authentication authentication) {
        Long managerId = (Long) authentication.getCredentials();
        LocalDateTime from = LocalDateTime.parse(fromDate);
        LocalDateTime toExclusive = LocalDateTime.parse(toDate);

        List<ManagerRevenueChartPointResponseDTO> data = managerReportService.getRevenueChart(
                restaurantId, managerId, from, toExclusive, timeUnit);
        ApiResponse<List<ManagerRevenueChartPointResponseDTO>> response = ApiResponse
                .<List<ManagerRevenueChartPointResponseDTO>>builder()
                .status(200)
                .message("Thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{restaurantId}/reports/top-foods")
    public ResponseEntity<ApiResponse<List<ManagerTopFoodResponseDTO>>> getTopFoods(
            @PathVariable Long restaurantId,
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate,
            @RequestParam(name = "limit", defaultValue = "5") Integer limit,
            Authentication authentication) {
        Long managerId = (Long) authentication.getCredentials();
        LocalDateTime from = (fromDate == null || fromDate.isBlank()) ? null : LocalDateTime.parse(fromDate);
        LocalDateTime toExclusive = (toDate == null || toDate.isBlank()) ? null : LocalDateTime.parse(toDate);

        List<ManagerTopFoodResponseDTO> data = managerReportService.getTopFoods(
                restaurantId, managerId, from, toExclusive, limit);
        ApiResponse<List<ManagerTopFoodResponseDTO>> response = ApiResponse.<List<ManagerTopFoodResponseDTO>>builder()
                .status(200)
                .message("Thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}

