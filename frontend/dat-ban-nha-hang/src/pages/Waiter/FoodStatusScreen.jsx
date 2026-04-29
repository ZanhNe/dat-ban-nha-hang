import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useFoodStatus } from '../../hooks/waiter/useFoodStatus';
import './FoodStatusScreen.css';

function FoodStatusScreen() {
    const { sessionId } = useParams();
    const navigate = useNavigate();
    const {
        orderData, sessionData, isLoading, isActionLoading,
        error,
        handleUpdateItem, handleCompleteOrder, handleCompleteSession
    } = useFoodStatus(sessionId);

    if (isLoading) return <div className="waiter-loading">Đang tải danh sách món...</div>;

    if (!orderData || !orderData.items) {
        return (
            <div className="food-status-container empty">
                <button className="btn-back" onClick={() => navigate('/waiter')}>⬅ Quay lại</button>
                <div className="empty-msg">
                    <h3>Bàn này chưa có Order nào</h3>
                    <p>Hãy vào mục Ghi Món (POS) để chọn món cho khách trước nhé.</p>
                </div>
            </div>
        );
    }

    // Tách danh sách món thành 2 nhóm: Đang chờ và Đã xong
    const pendingItems = orderData.items.filter(i => i.status === 'PENDING');
    const completedItems = orderData.items.filter(i => i.status !== 'PENDING');

    return (
        <div className="food-status-container">
            <header className="status-header">
                <div>
                    <button className="btn-back" onClick={() => navigate('/waiter')}> Trở về</button>
                    <h2>Trạng thái món (Phiên #{sessionId})</h2>
                </div>
                <span className={`order-status ${orderData.status.toLowerCase()}`}>
                    Mã Order: #{orderData.orderId}
                </span>
            </header>
            {error && (
                <div className="mb-3 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                    {error}
                </div>
            )}

            <div className="status-content">
                {/* NHÓM 1: CẦN THAO TÁC (PENDING) */}
                <div className="item-group">
                    <h3 className="group-title text-warning"> Chờ phục vụ ({pendingItems.length})</h3>
                    <div className="items-list">
                        {pendingItems.map(item => (
                            <div key={item.itemId} className="food-task-card pending">
                                <div className="task-info">
                                    <span className="qty">{item.quantity}x</span>
                                    <div>
                                        <h4 className="food-name">{item.foodName}</h4>
                                        {item.selectedOptions?.length > 0 && (
                                            <span className="food-options">{item.selectedOptions.join(', ')}</span>
                                        )}
                                    </div>
                                </div>
                                <div className="task-actions">
                                    <button
                                        className="btn-serve"
                                        onClick={() => handleUpdateItem(item.itemId, 'SERVED')}
                                        disabled={isActionLoading}
                                    >
                                        BƯNG RA
                                    </button>
                                    <button
                                        className="btn-cancel-item"
                                        onClick={() => handleUpdateItem(item.itemId, 'CANCELLED')}
                                        disabled={isActionLoading}
                                    >
                                        HẾT MÓN
                                    </button>
                                </div>
                            </div>
                        ))}
                        {pendingItems.length === 0 && <p className="all-done-msg">Tất cả món đã được phục vụ!</p>}
                    </div>
                </div>

                {/* NHÓM 2: ĐÃ XỬ LÝ (SERVED / CANCELLED) */}
                <div className="item-group">
                    <h3 className="group-title text-success"> Đã xử lý ({completedItems.length})</h3>
                    <div className="items-list">
                        {completedItems.map(item => (
                            <div key={item.itemId} className={`food-task-card ${item.status.toLowerCase()}`}>
                                <div className="task-info">
                                    <span className="qty">{item.quantity}x</span>
                                    <div>
                                        <h4 className="food-name">{item.foodName}</h4>
                                        {item.selectedOptions?.length > 0 && (
                                            <span className="food-options">{item.selectedOptions.join(', ')}</span>
                                        )}
                                    </div>
                                </div>
                                <div className="task-status-badge">
                                    {item.status === 'SERVED' ? 'ĐÃ LÊN MÓN' : 'ĐÃ HỦY'}
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* NÚT CHỐT ORDER CUỐI MÀN HÌNH */}
            <div className="status-footer">
                <button
                    className="btn-complete-order"
                    onClick={handleCompleteOrder}
                    disabled={isActionLoading || pendingItems.length > 0}
                >
                    {pendingItems.length > 0 ? `CÒN ${pendingItems.length} MÓN CHƯA LÊN` : "⭐ CHỐT ORDER (HOÀN TẤT)"}
                </button>
                <button
                    className="btn-complete-order"
                    style={{ marginLeft: 12 }}
                    onClick={handleCompleteSession}
                    disabled={isActionLoading || orderData.status !== 'COMPLETED' || sessionData?.status !== 'SERVING'}
                >
                    ✅ HOÀN TẤT PHIÊN PHỤC VỤ
                </button>
            </div>
        </div>
    );
}

export default FoodStatusScreen;