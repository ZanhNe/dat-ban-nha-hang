
import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';

export const useRestaurantApprovals = () => {
    const [pendingRestaurants, setPendingRestaurants] = useState([]);
    const [viewingDetail, setViewingDetail] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);

    // Lấy danh sách ban đầu
    const fetchPendingList = useCallback(async () => {
        setIsLoading(true);
        try {
            const response = await adminService.getPendingRestaurants(0, 10, 'PENDING');
            if (response.status === 200) {
                setPendingRestaurants(response.data);
            }
        } catch (error) {
            console.error("Lỗi khi lấy danh sách:", error);
            alert("Lỗi tải dữ liệu");
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchPendingList();
    }, [fetchPendingList]);

    // Xem chi tiết
    const fetchDetail = async (id) => {
        setIsLoading(true);
        try {
            const response = await adminService.getRestaurantDetail(id);
            if (response.status === 200) {
                setViewingDetail(response.data);
            }
        } catch (error) {
            console.error("Lỗi khi lấy chi tiết:", error);
        } finally {
            setIsLoading(false);
        }
    };

    const closeDetail = () => setViewingDetail(null);

    // Xử lý Duyệt / Từ chối
    const handleApprovalAction = async (id, status, rejectReason = "") => {
        setIsActionLoading(true);
        try {
            const payload = { status };
            if (status === "REJECTED") payload.rejectReason = rejectReason;

            const response = await adminService.updateApprovalStatus(id, payload);
            if (response.status === 200) {
                alert(response.message);
                // Xóa nhà hàng khỏi danh sách pending
                setPendingRestaurants(prev => prev.filter(rest => rest.restaurantId !== id));
                setViewingDetail(null); // Đóng form chi tiết
            }
        } catch (error) {
            console.error("Lỗi khi cập nhật trạng thái:", error);
            alert("Thao tác thất bại!");
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        pendingRestaurants,
        viewingDetail,
        isLoading,
        isActionLoading,
        fetchDetail,
        closeDetail,
        handleApprovalAction
    };
};