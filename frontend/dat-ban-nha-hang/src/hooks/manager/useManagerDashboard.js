import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { formatApiError } from '../../services/apiShape';

export const useManagerDashboard = () => {
    const [overview, setOverview] = useState(null);
    const [chartData, setChartData] = useState([]);
    const [topFoods, setTopFoods] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

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
        setError('');
        try {
            const [overviewRes, chartRes, foodsRes] = await Promise.all([
                managerService.getOverview(filters.fromDate, filters.toDate),
                managerService.getRevenueChart(filters.fromDate, filters.toDate, filters.timeUnit),
                managerService.getTopFoods(filters.fromDate, filters.toDate, 5)
            ]);

            if (overviewRes.status === 200) setOverview(overviewRes.data);
            if (chartRes.status === 200) setChartData(chartRes.data);
            if (foodsRes.status === 200) setTopFoods(foodsRes.data);

        } catch (error) {
            console.error("Lỗi khi lấy dữ liệu dashboard:", error);
            setError(formatApiError(error, 'Không thể tải dữ liệu tổng quan nhà hàng.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [filters]);
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
        error,
        filters,
        setFilters
    };
};