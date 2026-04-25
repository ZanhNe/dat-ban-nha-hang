package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminDashboardReportResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TransactionRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminReportService implements IAdminReportService {

    private final TransactionRepository transactionRepository;
    private final BookingRepository bookingRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardReportResponseDTO getDashboard(LocalDateTime from, LocalDateTime toExclusive) {
        long totalRevenue = transactionRepository.sumCommissionRevenueCaptured(from, toExclusive);
        long totalBookings = bookingRepository.countCreatedInRange(from, toExclusive);
        long totalRestaurants = restaurantRepository.countCreatedInRange(from, toExclusive);
        long totalNewUsers = userRepository.countCreatedInRange(from, toExclusive);

        return AdminDashboardReportResponseDTO.builder()
                .totalRevenue(totalRevenue)
                .totalBookings(totalBookings)
                .totalRestaurants(totalRestaurants)
                .totalNewUsers(totalNewUsers)
                .build();
    }
}

