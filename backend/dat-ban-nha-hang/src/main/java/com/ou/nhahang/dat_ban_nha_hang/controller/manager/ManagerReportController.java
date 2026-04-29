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
import org.springframework.web.bind.annotation.RestController;

import com.ou.nhahang.dat_ban_nha_hang.utils.RequestDateParsers;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/reports")
@PreAuthorize("hasAnyAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
public class ManagerReportController {

        private final IManagerReportService managerReportService;

        @GetMapping("/overview")
        public ResponseEntity<ApiResponse<ManagerReportOverviewResponseDTO>> getOverview(
                        @RequestParam(name = "fromDate", required = false) String fromDate,
                        @RequestParam(name = "toDate", required = false) String toDate,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                LocalDateTime from = RequestDateParsers.parseFlexibleDateTime(fromDate, "fromDate", false);
                LocalDateTime toExclusive = RequestDateParsers.parseFlexibleDateTime(toDate, "toDate", true);

                ManagerReportOverviewResponseDTO data = managerReportService.getOverview(managerId, from, toExclusive);
                ApiResponse<ManagerReportOverviewResponseDTO> response = ApiResponse
                                .<ManagerReportOverviewResponseDTO>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/revenue-chart")
        public ResponseEntity<ApiResponse<List<ManagerRevenueChartPointResponseDTO>>> getRevenueChart(
                        @RequestParam(name = "fromDate") String fromDate,
                        @RequestParam(name = "toDate") String toDate,
                        @RequestParam(name = "timeUnit", defaultValue = "DAY") String timeUnit,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                LocalDateTime from = RequestDateParsers.parseFlexibleDateTime(fromDate, "fromDate", false);
                LocalDateTime toExclusive = RequestDateParsers.parseFlexibleDateTime(toDate, "toDate", true);

                List<ManagerRevenueChartPointResponseDTO> data = managerReportService.getRevenueChart(
                                managerId, from, toExclusive, timeUnit);
                ApiResponse<List<ManagerRevenueChartPointResponseDTO>> response = ApiResponse
                                .<List<ManagerRevenueChartPointResponseDTO>>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }

        @GetMapping("/top-foods")
        public ResponseEntity<ApiResponse<List<ManagerTopFoodResponseDTO>>> getTopFoods(
                        @RequestParam(name = "fromDate", required = false) String fromDate,
                        @RequestParam(name = "toDate", required = false) String toDate,
                        @RequestParam(name = "limit", defaultValue = "5") Integer limit,
                        Authentication authentication) {
                Long managerId = (Long) authentication.getCredentials();
                LocalDateTime from = RequestDateParsers.parseFlexibleDateTime(fromDate, "fromDate", false);
                LocalDateTime toExclusive = RequestDateParsers.parseFlexibleDateTime(toDate, "toDate", true);

                List<ManagerTopFoodResponseDTO> data = managerReportService.getTopFoods(
                                managerId, from, toExclusive, limit);
                ApiResponse<List<ManagerTopFoodResponseDTO>> response = ApiResponse
                                .<List<ManagerTopFoodResponseDTO>>builder()
                                .status(200)
                                .message("Thành công")
                                .data(data)
                                .build();
                return ResponseEntity.ok(response);
        }
}
