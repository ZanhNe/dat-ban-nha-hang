import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';

import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";

export const useManagerDashboard = () => {
    const user = useAtomValue(userAtom);
    const restaurantId = user?.workplace?.restaurantId || 1; // Fallback ID = 1 nếu đang test

    const [overview, setOverview] = useState(null);
    const [chartData, setChartData] = useState([]);
    const [topFoods, setTopFoods] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    const today = new Date();
    const lastWeek = new Date(today);
    lastWeek.setDate(lastWeek.getDate() - 7);

    const [filters, setFilters] = useState({
        fromDate: lastWeek.toISOString().split('T')[0],
        toDate: today.toISOString().split('T')[0],
        timeUnit: 'DAY'
    });

    const fetchDashboardData = useCallback(async () => {
        setIsLoading(true);
        try {
            const [overviewRes, chartRes, foodsRes] = await Promise.all([
                managerService.getOverview(restaurantId, filters.fromDate, filters.toDate),
                managerService.getRevenueChart(restaurantId, filters.fromDate, filters.toDate, filters.timeUnit),
                managerService.getTopFoods(restaurantId, filters.fromDate, filters.toDate, 5)
            ]);

            if (overviewRes.status === 200) setOverview(overviewRes.data);
            if (chartRes.status === 200) setChartData(chartRes.data);
            if (foodsRes.status === 200) setTopFoods(foodsRes.data);

        } catch (error) {
            console.error("Lỗi khi lấy dữ liệu dashboard:", error);
        } finally {
            setIsLoading(false);
        }
    }, [restaurantId, filters]);
    useEffect(() => {
        if (filters.fromDate && filters.toDate) {
            fetchDashboardData();
        }
    }, [fetchDashboardData]);


    return {
        overview,
        chartData,
        topFoods,
        isLoading,
        filters,
        setFilters
    };
};