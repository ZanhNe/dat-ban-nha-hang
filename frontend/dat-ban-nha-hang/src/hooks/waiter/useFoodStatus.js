import { useState, useEffect, useCallback } from 'react';
import { waiterService } from '../../services/waiterService';
import { useNavigate } from 'react-router-dom';

export const useFoodStatus = (sessionId) => {
    const navigate = useNavigate();
    const [orderData, setOrderData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isActionLoading, setIsActionLoading] = useState(false);

    const fetchOrderDetails = useCallback(async () => {
        if (!sessionId) return;
        setIsLoading(true);
        try {
            const res = await waiterService.getSessionOrderDetails(sessionId);
            if (res.status === 200) {
                setOrderData(res.data);
            }
        } catch (error) {
            console.error("Lỗi lấy thông tin món ăn:", error);
        } finally {
            setIsLoading(false);
        }
    }, [sessionId]);

    useEffect(() => {
        fetchOrderDetails();
    }, [fetchOrderDetails]);

    const handleUpdateItem = async (itemId, status) => {
        if (status === 'CANCELLED' && !window.confirm("Bếp báo hết món này và bạn muốn HỦY?")) return;

        setIsActionLoading(true);
        try {
            const res = await waiterService.updateItemStatus(itemId, status);
            if (res.status === 200) {
                fetchOrderDetails();
            }
        } catch (error) {
            alert("Lỗi cập nhật trạng thái món!");
        } finally {
            setIsActionLoading(false);
        }
    };

    // Chốt Order
    const handleCompleteOrder = async () => {
        if (!orderData?.orderId) return;

        // Kiểm tra xem còn món nào PENDING không
        const hasPending = orderData.items.some(item => item.status === 'PENDING');
        if (hasPending) {
            alert("Vẫn còn món đang chờ bếp làm, chưa thể chốt Order!");
            return;
        }

        if (!window.confirm("Tất cả món đã lên đủ. Bạn muốn Chốt Order cho bàn này?")) return;

        setIsActionLoading(true);
        try {
            const res = await waiterService.completeOrder(orderData.orderId);
            if (res.status === 200) {
                alert("Đã chốt Order thành công!");
                navigate('/waiter'); // Trở về Dashboard
            }
        } catch (error) {
            alert("Lỗi chốt Order!");
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        orderData,
        isLoading,
        isActionLoading,
        handleUpdateItem,
        handleCompleteOrder
    };
};