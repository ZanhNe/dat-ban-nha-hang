import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';

const normalizeRestaurant = (rest) => ({
    ...rest,
    restaurantId: rest?.restaurantId ?? rest?.id,
    restaurantName: rest?.restaurantName ?? rest?.name ?? 'N/A',
    managerName: rest?.managerName ?? rest?.ownerName ?? 'N/A',
    commissionType: rest?.commissionType ?? 'PERCENTAGE',
    baseCommissionValue: Number(rest?.baseCommissionValue ?? 0),
    status: rest?.status ?? 'PENDING'
});

export const useRestaurantManagement = () => {
    const [restaurants, setRestaurants] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [limit] = useState(10);
    const [meta, setMeta] = useState(null);

    // Lấy dữ liệu lần đầu
    const fetchRestaurants = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await adminService.getAllRestaurants(page, limit);
            if (res.status === 200) {
                const list = Array.isArray(res.data?.data)
                    ? res.data.data
                    : Array.isArray(res.data)
                        ? res.data
                        : [];
                setRestaurants(list.map(normalizeRestaurant));
                setMeta(res.meta);
            }
        } catch (error) {
            console.error("Lỗi fetch nhà hàng:", error);
            setError(formatApiError(error, 'Không thể tải danh sách nhà hàng.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [page, limit]);

    useEffect(() => {
        fetchRestaurants();
    }, [fetchRestaurants]);

    // Xử lý Khóa / Mở Khóa
    const toggleStatus = async (id, currentStatus) => {
        const isOpening = currentStatus === "OPENING" || currentStatus === "ACTIVE";
        const newStatus = isOpening ? "SUSPENDED" : "OPENING";
        const actionText = isOpening ? "KHÓA" : "MỞ KHÓA";

        if (!window.confirm(`Bạn có chắc chắn muốn ${actionText} nhà hàng này?`)) return;

        setIsActionLoading(true);
        setError('');
        try {
            const reason = isOpening ? "Vi phạm chính sách (Admin khóa)" : "";
            const res = await adminService.updateRestaurantStatus(id, newStatus, reason);

            if (res.status === 200) {
                // Cập nhật lại UI không cần reload lại list
                setRestaurants(prev => prev.map(rest =>
                    rest.restaurantId === id ? { ...rest, status: newStatus } : rest
                ));
            }
        } catch (error) {
            console.error("Lỗi cập nhật trạng thái:", error);
            setError(formatApiError(error, 'Không thể cập nhật trạng thái nhà hàng.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    // Xử lý lưu cấu hình hoa hồng
    const updateCommission = async (id, commissionData) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await adminService.updateRestaurantCommission(id, commissionData);
            if (res.status === 200) {
                // Cập nhật lại UI
                setRestaurants(prev => prev.map(rest =>
                    rest.restaurantId === id ? { ...rest, ...commissionData } : rest
                ));
                return true; // Báo hiệu cập nhật thành công
            }
        } catch (error) {
            console.error("Lỗi cấu hình hoa hồng:", error);
            setError(formatApiError(error, 'Không thể cập nhật cấu hình hoa hồng.').displayMessage);
            return false;
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        restaurants,
        isLoading,
        isActionLoading,
        error,
        toggleStatus,
        updateCommission,
        page,
        setPage,
        meta
    };
};