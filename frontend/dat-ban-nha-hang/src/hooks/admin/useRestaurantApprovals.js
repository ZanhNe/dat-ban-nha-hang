
import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';

const normalizeRestaurantSummary = (restaurant) => ({
    ...restaurant,
    restaurantId: restaurant?.restaurantId,
    restaurantName: restaurant?.restaurantName ?? 'N/A',
    managerName: restaurant?.managerName ?? 'Chưa có quản lý',
    status: restaurant?.status ?? 'PENDING',
    createdAt: restaurant?.createdAt ?? null
});

const normalizeRestaurantDetail = (detail) => ({
    ...detail,
    restaurantId: detail?.restaurantId,
    restaurantName: detail?.restaurantName ?? 'N/A',
    status: detail?.status ?? 'PENDING',
    logo: detail?.logo ?? '',
    description: detail?.description ?? '',
    address: detail?.address ?? '',
    manager: detail?.manager ?? null,
    legalDocs: Array.isArray(detail?.legalDocs) ? detail.legalDocs : []
});

export const useRestaurantApprovals = () => {
    const [pendingRestaurants, setPendingRestaurants] = useState([]);
    const [viewingDetail, setViewingDetail] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [limit] = useState(10);
    const [meta, setMeta] = useState(null);

    // Lấy danh sách ban đầu
    const fetchPendingList = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const response = await adminService.getPendingRestaurants(page, limit, 'PENDING');
            if (response.status === 200) {
                setPendingRestaurants((response.data || []).map(normalizeRestaurantSummary));
                setMeta(response.meta);
            }
        } catch (error) {
            console.error("Lỗi khi lấy danh sách:", error);
            setError(formatApiError(error, 'Không thể tải danh sách nhà hàng chờ duyệt.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    }, [page, limit]);

    useEffect(() => {
        fetchPendingList();
    }, [fetchPendingList]);

    // Xem chi tiết
    const fetchDetail = async (id) => {
        setIsLoading(true);
        setError('');
        try {
            const response = await adminService.getRestaurantDetail(id);
            if (response.status === 200) {
                setViewingDetail(normalizeRestaurantDetail(response.data));
            }
        } catch (error) {
            console.error("Lỗi khi lấy chi tiết:", error);
            setError(formatApiError(error, 'Không thể tải chi tiết nhà hàng.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    };

    const closeDetail = () => setViewingDetail(null);

    // Xử lý Duyệt / Từ chối
    const handleApprovalAction = async (id, status) => {
        setIsActionLoading(true);
        setError('');
        try {
            const payload = { status };

            const response = await adminService.updateApprovalStatus(id, payload);
            if (response.status === 200) {
                // Xóa nhà hàng khỏi danh sách pending
                setPendingRestaurants(prev => prev.filter(rest => rest.restaurantId !== id));
                setViewingDetail(null); // Đóng form chi tiết
                return {
                    success: true,
                    message: response?.message || 'Cập nhật trạng thái duyệt thành công.'
                };
            }
        } catch (error) {
            console.error("Lỗi khi cập nhật trạng thái:", error);
            const apiError = formatApiError(error, 'Thao tác duyệt nhà hàng thất bại.');
            setError(apiError.displayMessage);
            return {
                success: false,
                message: apiError.displayMessage
            };
        } finally {
            setIsActionLoading(false);
        }
        return {
            success: false,
            message: 'Không thể cập nhật trạng thái duyệt.'
        };
    };

    return {
        pendingRestaurants,
        viewingDetail,
        isLoading,
        isActionLoading,
        error,
        fetchDetail,
        closeDetail,
        handleApprovalAction,
        page,
        setPage,
        meta
    };
};