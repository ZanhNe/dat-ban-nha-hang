import { useState, useEffect, useCallback } from 'react';
import { waiterService } from '../../services/waiterService';
import { useNavigate } from 'react-router-dom';
import { formatApiError, unwrapData } from '../../services/apiShape';

export const useFoodStatus = (sessionId) => {
    const navigate = useNavigate();
    const [orderData, setOrderData] = useState(null);
    const [sessionData, setSessionData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');

    const fetchOrderDetails = useCallback(async () => {
        if (!sessionId) return;
        setIsLoading(true);
        setError('');
        try {
            const sessionRes = await waiterService.getSessionDetail(sessionId);
            const session = unwrapData(sessionRes);
            setSessionData(session);
            const latestOrder = session?.foodOrders?.[0];
            if (!latestOrder?.orderId) {
                setOrderData(null);
                return;
            }
            const res = await waiterService.getOrderDetails(latestOrder.orderId);
            if (res.status === 200) setOrderData(unwrapData(res));
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải trạng thái món ăn.').displayMessage);
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
        setError('');
        try {
            const res = await waiterService.updateItemStatus(itemId, status);
            if (res.status === 200) {
                fetchOrderDetails();
            }
        } catch (error) {
            setError(formatApiError(error, 'Lỗi cập nhật trạng thái món!').displayMessage);
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
        setError('');
        try {
            const res = await waiterService.completeOrder(orderData.orderId);
            if (res.status === 200) {
                fetchOrderDetails();
            }
        } catch (error) {
            setError(formatApiError(error, 'Lỗi chốt Order!').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    const handleCompleteSession = async () => {
        if (!sessionId) return;
        if (!window.confirm("Xác nhận hoàn tất phiên phục vụ để cashier có thể tạo thanh toán?")) return;

        setIsActionLoading(true);
        setError('');
        try {
            const res = await waiterService.serveComplete(sessionId);
            if (res.status === 200) {
                navigate('/waiter');
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể hoàn tất phiên phục vụ.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        orderData,
        sessionData,
        isLoading,
        isActionLoading,
        error,
        handleUpdateItem,
        handleCompleteOrder,
        handleCompleteSession
    };
};