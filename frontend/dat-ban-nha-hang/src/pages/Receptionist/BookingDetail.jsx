import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { receptionistService } from '../../services/receptionistService';
import { formatApiError } from '../../services/apiShape';
import { formatDateTime } from '../../utils/dateTime';
import './BookingDetail.css';

const statusLabelMap = {
    AWAITING_CONFIRMATION: 'Chờ xác nhận',
    PENDING_PAYMENT: 'Chờ thanh toán',
    CONFIRMED: 'Đã xác nhận',
    CUSTOMER_ARRIVED: 'Khách đã đến',
    SERVING: 'Đang phục vụ',
    SERVED: 'Đã phục vụ',
    COMPLETED: 'Hoàn tất',
    REJECTED: 'Đã từ chối',
    EXPIRED: 'Hết hạn',
    CANCELLED: 'Đã hủy',
    FAILED: 'Thất bại'
};

function BookingDetail() {
    const { bookingId } = useParams();
    const navigate = useNavigate();
    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [cancelling, setCancelling] = useState(false);
    const [checkingIn, setCheckingIn] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchDetail = async () => {
            try {
                const result = await receptionistService.getBookingDetail(bookingId);
                setBooking(result.data);
            } catch (err) {
                setError(formatApiError(err, 'Không thể tải chi tiết booking.').displayMessage);
            } finally {
                setLoading(false);
            }
        };
        fetchDetail();
    }, [bookingId]);

    const handleCancel = async () => {
        setCancelling(true);
        setError('');
        try {
            await receptionistService.cancelBooking(bookingId);
            setBooking(prev => ({ ...prev, status: 'CANCELLED' }));
        } catch (err) {
            setError(formatApiError(err, 'Không thể hủy booking.').displayMessage);
        } finally {
            setCancelling(false);
        }
    };

    const handleCheckIn = async () => {
        setCheckingIn(true);
        setError('');
        try {
            await receptionistService.checkInBooking(bookingId);
            setBooking((prev) => ({ ...prev, status: 'CUSTOMER_ARRIVED' }));
        } catch (err) {
            setError(formatApiError(err, 'Không thể xác nhận khách đến.').displayMessage);
        } finally {
            setCheckingIn(false);
        }
    };

    if (loading) return <div className="loading-state">Đang tải...</div>;
    if (!booking) return <div className="empty-state">Không tìm thấy booking</div>;

    const dateTime = formatDateTime(booking.bookingTime);
    const isCancellable = ['AWAITING_CONFIRMATION', 'PENDING_PAYMENT', 'CONFIRMED'].includes(booking.status);
    const isCheckInAllowed = booking.status === 'CONFIRMED';

    return (
        <div className="booking-detail">
            <button className="btn-back" onClick={() => navigate('/receptionist')}>← Quay lại</button>
            {error ? <p className="status-error mb-4">{error}</p> : null}

            <div className="detail-card">
                <div className="detail-header">
                    <h2>Booking #{booking.bookingId}</h2>
                    <span className="status-badge">{statusLabelMap[booking.status] || booking.status}</span>
                </div>

                <div className="detail-body">
                    <div className="info-grid">
                        <div className="info-item">
                            Khách hàng
                            <strong>{booking.customer?.fullName}</strong>
                        </div>
                        <div className="info-item">
                            Số điện thoại
                            <strong>{booking.customer?.phone}</strong>
                        </div>
                        <div className="info-item">
                            Thời gian
                            <strong>{dateTime.date} - {dateTime.time}</strong>
                        </div>
                        <div className="info-item">
                            Số khách
                            <strong>{booking.numberOfPeople} người</strong>
                        </div>
                    </div>

                    {booking.note && (
                        <div className="info-item mb-4">
                            Ghi chú: <strong>{booking.note}</strong>
                        </div>
                    )}

                    {booking.assignedTables?.length > 0 && (
                        <div className="tables-info">
                            Bàn đã xếp:
                            {booking.assignedTables.map(t => (
                                <span key={t.tableId} className="table-tag">{t.label}</span>
                            ))}
                        </div>
                    )}

                    {isCheckInAllowed && (
                        <div className="cancel-section">
                            <h3>Xác nhận khách đến</h3>
                            <p>Kiểm tra thông tin booking trước khi chuyển trạng thái khách đã đến.</p>
                            <button className="ui-btn ui-btn-primary" onClick={handleCheckIn} disabled={checkingIn}>
                                {checkingIn ? 'Đang xử lý...' : 'XÁC NHẬN KHÁCH ĐẾN'}
                            </button>
                        </div>
                    )}

                    {isCancellable && (
                        <div className="cancel-section">
                            <h3>Hủy booking</h3>
                            <p>Booking ở trạng thái này có thể bị hủy trực tiếp bởi lễ tân.</p>
                            <button className="btn-cancel-booking" onClick={handleCancel} disabled={cancelling}>
                                {cancelling ? 'Đang xử lý...' : 'Xác nhận hủy booking'}
                            </button>
                        </div>
                    )}

                    {booking.status === 'CANCELLED' && (
                        <div className="text-center text-red-700 font-semibold p-5">
                            ❌ Booking đã bị hủy
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default BookingDetail;
