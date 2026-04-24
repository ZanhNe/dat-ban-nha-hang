package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminDashboardReportResponseDTO;

import java.time.LocalDateTime;

public interface IAdminReportService {
    AdminDashboardReportResponseDTO getDashboard(LocalDateTime from, LocalDateTime toExclusive);
}

