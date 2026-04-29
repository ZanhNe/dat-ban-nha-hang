package com.ou.nhahang.dat_ban_nha_hang.controller.admin;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminDashboardReportResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ApiResponse;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ou.nhahang.dat_ban_nha_hang.utils.RequestDateParsers;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/reports")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminReportController {

    private final IAdminReportService adminReportService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardReportResponseDTO>> getDashboard(
            @RequestParam(name = "fromDate", required = false) String fromDate,
            @RequestParam(name = "toDate", required = false) String toDate) {

        LocalDateTime from = RequestDateParsers.parseFlexibleDateTime(fromDate, "fromDate", false);
        LocalDateTime toExclusive = RequestDateParsers.parseFlexibleDateTime(toDate, "toDate", true);

        AdminDashboardReportResponseDTO data = adminReportService.getDashboard(from, toExclusive);

        ApiResponse<AdminDashboardReportResponseDTO> response = ApiResponse.<AdminDashboardReportResponseDTO>builder()
                .status(200)
                .message("Lấy dữ liệu dashboard thành công")
                .data(data)
                .build();
        return ResponseEntity.ok(response);
    }
}
