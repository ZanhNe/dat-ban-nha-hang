import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';

export const useAdminDashboard = () => {
    const [metrics, setMetrics] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    const fetchMetrics = useCallback(async (fromDate = null, toDate = null) => {
        setIsLoading(true);
        setError('');
        try {
            const res = await adminService.getDashboardMetrics(fromDate, toDate);
            if (res.status === 200) {
                setMetrics(res.data);
            }
        } catch (error) {
            console.error("Lỗi fetch dashboard metrics:", error);
            setError(formatApiError(error, 'Không thể tải dữ liệu báo cáo Admin.').displayMessage);
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
        error,
        fetchMetrics
    };
};

export default useAdminDashboard;