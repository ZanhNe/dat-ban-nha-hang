import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { cashierService } from '../../services/cashierService';
import './CashierPayment.css';

function CashierPayment() {
    const { sessionId } = useParams();
    const navigate = useNavigate();
    const [detail, setDetail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState('CASH');

    const fetchDetail = async () => {
        setLoading(true);
        try {
            const result = await cashierService.getSessionDetail(sessionId);
            setDetail(result.data);
        } catch (err) {
            console.error('Lỗi tải chi tiết:', err);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDetail();
    }, [sessionId]);

    const formatPrice = (price) => {
        return new Intl.NumberFormat('vi-VN').format(price) + 'đ';
    };

    const handleInitiatePayment = async () => {
        setProcessing(true);
        try {
            await cashierService.initiatePayment(sessionId);
            await fetchDetail();
        } catch (err) {
            console.error('Lỗi khởi tạo thanh toán:', err);
        } finally {
            setProcessing(false);
        }
    };

    const handleCompletePayment = async () => {
        if (!detail) return;
        const amountToPay = Math.max(0, detail.totalAmount - (detail.depositAmount || 0));
        setProcessing(true);
        try {
            await cashierService.completePayment(sessionId, paymentMethod, amountToPay);
            setDetail(prev => ({ ...prev, status: 'COMPLETED' }));
        } catch (err) {
            console.error('Lỗi hoàn tất thanh toán:', err);
        } finally {
            setProcessing(false);
        }
    };

    if (loading) return <div className="loading-state">Đang tải chi tiết...</div>;
    if (!detail) return <div className="empty-state">Không tìm thấy phiên bàn</div>;

    const amountToPay = Math.max(0, detail.totalAmount - (detail.depositAmount || 0));

    return (
        <div className="cashier-payment">
            <button className="btn-back" onClick={() => navigate('/cashier')}>← Quay lại</button>

            <div className="payment-card">
                <div className="payment-header">
                    <h2>Phiên #{detail.sessionId}</h2>
                    <span className={`status-badge ${detail.status.toLowerCase()}`}>
                        {detail.status === 'SERVED' ? 'Chờ thanh toán' : detail.status === 'PAYING' ? 'Đang thanh toán' : 'Hoàn tất'}
                    </span>
                </div>

                <div className="payment-info">
                    <div className="info-item">Khách hàng: <strong>{detail.customerName}</strong></div>
                    <div className="info-item">Số khách: <strong>{detail.numberOfPeople} người</strong></div>
                    <div className="info-item">
                        Bàn: <strong>{detail.tableLabels?.join(', ')}</strong>
                    </div>
                    <div className="info-item">Tiền cọc: <strong className="format-price">{formatPrice(detail.depositAmount || 0)}</strong></div>
                </div>

                <div className="items-section">
                    <h3>Chi tiết món ăn</h3>
                    <table className="items-table">
                        <thead>
                            <tr>
                                <th>Món</th>
                                <th>SL</th>
                                <th className="price-col">Đơn giá</th>
                                <th className="price-col">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            {detail.items?.map((item, idx) => (
                                <tr key={idx}>
                                    <td>{item.foodName}</td>
                                    <td>{item.quantity}</td>
                                    <td className="price-col format-price">{formatPrice(item.price)}</td>
                                    <td className="price-col format-price">{formatPrice(item.totalItemPrice)}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                <div className="payment-summary">
                    <div className="summary-row">
                        <span>Tổng tiền món:</span>
                        <span className="format-price">{formatPrice(detail.totalAmount)}</span>
                    </div>
                    <div className="summary-row">
                        <span>Đã cọc:</span>
                        <span className="format-price">- {formatPrice(detail.depositAmount || 0)}</span>
                    </div>
                    <div className="summary-row total">
                        <span>Còn phải thanh toán:</span>
                        <span className="format-price">{formatPrice(amountToPay)}</span>
                    </div>
                </div>

                {detail.status === 'COMPLETED' ? (
                    <div className="completed-banner">✅ Thanh toán hoàn tất — Bàn đã trống</div>
                ) : (
                    <div className="payment-actions">
                        {detail.status === 'SERVED' && (
                            <button className="btn-initiate" onClick={handleInitiatePayment} disabled={processing}>
                                {processing ? 'Đang xử lý...' : 'Khởi tạo thanh toán'}
                            </button>
                        )}
                        {detail.status === 'PAYING' && (
                            <>
                                <select
                                    className="payment-method-select"
                                    value={paymentMethod}
                                    onChange={(e) => setPaymentMethod(e.target.value)}
                                >
                                    <option value="CASH">Tiền mặt</option>
                                    <option value="BANK_TRANSFER">Chuyển khoản</option>
                                </select>
                                <button className="btn-complete" onClick={handleCompletePayment} disabled={processing}>
                                    {processing ? 'Đang xử lý...' : `Xác nhận thu ${formatPrice(amountToPay)}`}
                                </button>
                            </>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}

export default CashierPayment;
