package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerReportOverviewResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerRevenueChartPointResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerTopFoodResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.repository.FoodItemRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TransactionRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ManagerReportService implements IManagerReportService {

    private final RestaurantRepository restaurantRepository;
    private final TransactionRepository transactionRepository;
    private final FoodItemRepository foodItemRepository;

    private Restaurant getRestaurantIfManager(Long restaurantId, Long managerId) {
        return restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new BusinessException("Bạn không có quyền quản lý nhà hàng này"));
    }

    @Override
    @Transactional(readOnly = true)
    public ManagerReportOverviewResponseDTO getOverview(Long restaurantId, Long managerId, LocalDateTime from, LocalDateTime toExclusive) {
        getRestaurantIfManager(restaurantId, managerId);

        long totalRevenue = transactionRepository.sumCapturedRevenueByRestaurant(restaurantId, from, toExclusive);
        long commission = transactionRepository.sumCommissionByRestaurant(restaurantId, from, toExclusive);
        long netRevenue = Math.max(0, totalRevenue - commission);
        long totalBookings = transactionRepository.countCapturedBookingsByRestaurant(restaurantId, from, toExclusive);
        long totalCustomers = transactionRepository.countDistinctCustomersByRestaurant(restaurantId, from, toExclusive);
        long averageBill = totalBookings == 0 ? 0 : totalRevenue / totalBookings;

        return ManagerReportOverviewResponseDTO.builder()
                .totalRevenue(totalRevenue)
                .netRevenue(netRevenue)
                .totalBookings(totalBookings)
                .totalCustomers(totalCustomers)
                .averageBill(averageBill)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerRevenueChartPointResponseDTO> getRevenueChart(
            Long restaurantId,
            Long managerId,
            LocalDateTime from,
            LocalDateTime toExclusive,
            String timeUnit) {
        getRestaurantIfManager(restaurantId, managerId);
        if (from == null || toExclusive == null) {
            throw new BusinessException("fromDate và toDate là bắt buộc");
        }

        String unit = timeUnit == null ? "DAY" : timeUnit.toUpperCase();
        if (!unit.equals("DAY") && !unit.equals("WEEK") && !unit.equals("MONTH")) {
            throw new BusinessException("timeUnit phải là DAY, WEEK hoặc MONTH");
        }

        List<Transaction> txs = transactionRepository.findCapturedByRestaurant(restaurantId, from, toExclusive);
        long commissionRateOrFixed = transactionRepository.sumCommissionByRestaurant(restaurantId, from, toExclusive);
        long totalRevenue = transactionRepository.sumCapturedRevenueByRestaurant(restaurantId, from, toExclusive);
        double commissionRateApprox = totalRevenue == 0 ? 0.0 : ((double) commissionRateOrFixed / (double) totalRevenue);

        Map<String, long[]> bucket = new LinkedHashMap<>();
        // long[] = [revenue, netRevenue, bookingsCount]
        for (Transaction t : txs) {
            String key = toBucket(t.getCreatedAt().toLocalDate(), unit);
            long[] arr = bucket.computeIfAbsent(key, k -> new long[] {0, 0, 0});
            arr[0] += t.getAmount();
            arr[2] += 1;
        }

        List<ManagerRevenueChartPointResponseDTO> result = new ArrayList<>();
        for (Map.Entry<String, long[]> e : bucket.entrySet()) {
            long revenue = e.getValue()[0];
            long netRevenue = Math.max(0, revenue - Math.round(revenue * commissionRateApprox));
            result.add(ManagerRevenueChartPointResponseDTO.builder()
                    .date(e.getKey())
                    .revenue(revenue)
                    .netRevenue(netRevenue)
                    .bookingsCount(e.getValue()[2])
                    .build());
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerTopFoodResponseDTO> getTopFoods(Long restaurantId, Long managerId, LocalDateTime from,
                                                        LocalDateTime toExclusive, int limit) {
        getRestaurantIfManager(restaurantId, managerId);
        int actualLimit = limit <= 0 ? 5 : Math.min(limit, 50);
        return foodItemRepository.findTopFoodsByRestaurant(restaurantId, from, toExclusive, PageRequest.of(0, actualLimit))
                .stream()
                .map(p -> ManagerTopFoodResponseDTO.builder()
                        .foodId(p.getFoodId())
                        .foodName(p.getFoodName())
                        .quantitySold(p.getQuantitySold())
                        .revenue(p.getRevenue())
                        .build())
                .toList();
    }

    private String toBucket(LocalDate date, String unit) {
        return switch (unit) {
            case "MONTH" -> date.getYear() + "-" + String.format("%02d", date.getMonthValue());
            case "WEEK" -> date.getYear() + "-W" + String.format("%02d", date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR));
            default -> date.toString();
        };
    }
}

