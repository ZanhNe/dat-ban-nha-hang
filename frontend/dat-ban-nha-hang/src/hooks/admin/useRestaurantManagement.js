import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';

export const useRestaurantManagement = () => {
    const [restaurants, setRestaurants] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);

    // Lấy dữ liệu lần đầu
    const fetchRestaurants = useCallback(async () => {
        setIsLoading(true);
        try {
            const res = await adminService.getAllRestaurants();
            if (res.status === 200) {
                setRestaurants(res.data);
            }
        } catch (error) {
            console.error("Lỗi fetch nhà hàng:", error);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchRestaurants();
    }, [fetchRestaurants]);

    // Xử lý Khóa / Mở Khóa
    const toggleStatus = async (id, currentStatus) => {
        const newStatus = currentStatus === "ACTIVE" ? "SUSPENDED" : "ACTIVE";
        const actionText = currentStatus === "ACTIVE" ? "KHÓA" : "MỞ KHÓA";

        if (!window.confirm(`Bạn có chắc chắn muốn ${actionText} nhà hàng này?`)) return;

        setIsActionLoading(true);
        try {
            const reason = currentStatus === "ACTIVE" ? "Vi phạm chính sách (Admin khóa)" : "";
            const res = await adminService.updateRestaurantStatus(id, newStatus, reason);

            if (res.status === 200) {
                // Cập nhật lại UI không cần reload lại list
                setRestaurants(prev => prev.map(rest =>
                    rest.restaurantId === id ? { ...rest, status: newStatus } : rest
                ));
            }
        } catch (error) {
            console.error("Lỗi cập nhật trạng thái:", error);
            alert("Lỗi thao tác!");
        } finally {
            setIsActionLoading(false);
        }
    };

    // Xử lý lưu cấu hình hoa hồng
    const updateCommission = async (id, commissionData) => {
        setIsActionLoading(true);
        try {
            const res = await adminService.updateRestaurantCommission(id, commissionData);
            if (res.status === 200) {
                alert(res.message);
                // Cập nhật lại UI
                setRestaurants(prev => prev.map(rest =>
                    rest.restaurantId === id ? { ...rest, ...commissionData } : rest
                ));
                return true; // Báo hiệu cập nhật thành công
            }
        } catch (error) {
            console.error("Lỗi cấu hình hoa hồng:", error);
            alert("Lỗi cấu hình!");
            return false;
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        restaurants,
        isLoading,
        isActionLoading,
        toggleStatus,
        updateCommission
    };
};