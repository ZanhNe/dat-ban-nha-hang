import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';

export const useAdminDashboard = () => {
    const [metrics, setMetrics] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    const fetchMetrics = useCallback(async (fromDate = null, toDate = null) => {
        setIsLoading(true);
        try {
            const res = await adminService.getDashboardMetrics(fromDate, toDate);
            if (res.status === 200) {
                setMetrics(res.data);
            }
        } catch (error) {
            console.error("Lỗi fetch dashboard metrics:", error);
            alert("Lỗi tải dữ liệu báo cáo!");
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchMetrics();
    }, [fetchMetrics]);

    return {
        metrics,
        isLoading,
        fetchMetrics
    };
};

export default useAdminDashboard;