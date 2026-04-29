import React, { useCallback, useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { cashierService } from '../../services/cashierService';
import { formatApiError, unwrapData } from '../../services/apiShape';
import PageHeader from '../../components/ui/PageHeader';
import SectionCard from '../../components/ui/SectionCard';
import LoadingState from '../../components/ui/LoadingState';
import EmptyState from '../../components/ui/EmptyState';

function CashierPayment() {
    const { sessionId } = useParams();
    const navigate = useNavigate();
    const [detail, setDetail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('CASH');
    const [error, setError] = useState('');

    const fetchDetail = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const result = await cashierService.getSessionDetail(sessionId);
            setDetail(unwrapData(result));
        } catch (err) {
            setError(formatApiError(err, 'Không thể tải chi tiết phiên thanh toán.').displayMessage);
        } finally {
            setLoading(false);
        }
    }, [sessionId]);

    useEffect(() => {
        fetchDetail();
    }, [fetchDetail]);

    const formatPrice = (price) => {
        return new Intl.NumberFormat('vi-VN').format(price) + 'đ';
    };

    const handleInitiatePayment = async () => {
        setProcessing(true);
        setError('');
        try {
            await cashierService.initiatePayment(sessionId);
            await fetchDetail();
        } catch (err) {
            setError(formatApiError(err, 'Không thể khởi tạo thanh toán.').displayMessage);
        } finally {
            setProcessing(false);
        }
    };

    const handleCompletePayment = async () => {
        if (!detail) return;
        setProcessing(true);
        setError('');
        try {
            await cashierService.completePayment(sessionId, {
                paymentMethod,
                totalAmount: detail.amountToPay ?? 0
            });
            await fetchDetail();
        } catch (err) {
            setError(formatApiError(err, 'Không thể hoàn tất thanh toán.').displayMessage);
        } finally {
            setProcessing(false);
        }
    };

    if (loading) return <LoadingState message="Đang tải chi tiết phiên thanh toán..." />;
    if (!detail) return <EmptyState message="Không tìm thấy phiên bàn." />;

    const amountToPay = detail.amountToPay ?? 0;

    return (
        <div>
            <PageHeader
                title={`Phiên #${detail.sessionId}`}
                subtitle="Chi tiết phiên thanh toán"
                rightSlot={<button className="ui-btn" onClick={() => navigate('/cashier')}>Quay lại</button>}
            />

            <SectionCard className="p-4">
                {error && (
                    <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-700 mb-4">
                        {error}
                    </div>
                )}
                <div className="flex justify-between items-center mb-4">
                    <span className="text-sm text-gray-600">
                        {detail.status === 'SERVED' ? 'Chờ thanh toán' : detail.status === 'PAYING' ? 'Đang thanh toán' : 'Hoàn tất'}
                    </span>
                </div>

                <div className="grid md:grid-cols-2 gap-3 text-sm mb-4">
                    <div>Khách hàng: <strong>{detail.customerName}</strong></div>
                    <div>Số khách: <strong>{detail.numberOfPeople} người</strong></div>
                    <div>Bàn: <strong>{detail.tableLabels?.join(', ')}</strong></div>
                    <div>Tiền cọc: <strong>{formatPrice(detail.depositAmount || 0)}</strong></div>
                </div>

                <div className="mb-4">
                    <h3 className="font-semibold mb-2">Chi tiết món ăn</h3>
                    <div className="space-y-3">
                        {(detail.orders || []).map((order) => (
                            <div key={order.orderId} className="rounded-xl border border-gray-200 p-3">
                                <div className="flex items-center justify-between mb-2">
                                    <div className="font-medium">Order #{order.orderId}</div>
                                    <div className="text-xs text-gray-600">
                                        {order.status}
                                    </div>
                                </div>
                                <div className="overflow-x-auto">
                                    <table className="w-full text-sm">
                                        <thead>
                                            <tr className="text-left border-b border-gray-200">
                                                <th className="py-2">Món</th>
                                                <th>SL</th>
                                                <th>Đơn giá</th>
                                                <th>Thành tiền</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {(order.items || []).map((item, idx) => (
                                                <tr key={`${order.orderId}-${idx}`} className="border-b border-gray-100 last:border-b-0">
                                                    <td className="py-2">{item.foodName}</td>
                                                    <td>{item.quantity}</td>
                                                    <td>{formatPrice(item.price)}</td>
                                                    <td>{formatPrice(item.totalItemPrice)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        ))}
                        {(detail.orders || []).length === 0 && (
                            <div className="text-sm text-gray-500">Chưa có order phục vụ để thanh toán.</div>
                        )}
                    </div>
                </div>

                <div className="ui-card p-3 bg-gray-50 mb-4">
                    <div className="flex justify-between text-sm">
                        <span>Tổng tiền món</span>
                        <span>{formatPrice(detail.totalAmount)}</span>
                    </div>
                    <div className="flex justify-between text-sm mt-1">
                        <span>Đã cọc</span>
                        <span>- {formatPrice(detail.depositAmount || 0)}</span>
                    </div>
                    <div className="flex justify-between font-semibold mt-2">
                        <span>Còn phải thanh toán</span>
                        <span>{formatPrice(amountToPay)}</span>
                    </div>
                </div>

                {detail.status === 'COMPLETED' ? (
                    <div className="ui-state">Thanh toán hoàn tất, bàn đã trống.</div>
                ) : (
                    <div className="flex gap-2 items-center">
                        {detail.status === 'SERVED' && (
                            <button className="ui-btn ui-btn-primary" onClick={handleInitiatePayment} disabled={processing}>
                                {processing ? 'Đang xử lý...' : 'Khởi tạo thanh toán'}
                            </button>
                        )}
                        {detail.status === 'PAYING' && (
                            <>
                                <select
                                    className="ui-input max-w-40"
                                    value={paymentMethod}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                >
                                    <option value="CASH">Tiền mặt</option>
                                    <option value="CREDIT_CARD">Thẻ</option>
                                </select>
                                <button className="ui-btn ui-btn-primary" onClick={handleCompletePayment} disabled={processing}>
                                    {processing ? 'Đang xử lý...' : `Xác nhận thu ${formatPrice(amountToPay)}`}
                                </button>
                            </>
                        )}
                    </div>
                )}
            </SectionCard>
        </div>
    );
}

export default CashierPayment;
