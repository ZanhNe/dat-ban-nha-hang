import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import apiClient from '../../services/apiClient';
import './BookingDetail.css';

const USE_MOCK = true;

function BookingDetail() {
    const { bookingId } = useParams();
    const navigate = useNavigate();
    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [cancelReason, setCancelReason] = useState('');
    const [cancelling, setCancelling] = useState(false);

    useEffect(() => {
        const fetchDetail = async () => {
            try {
                if (USE_MOCK) {
                    await new Promise(r => setTimeout(r, 400));
                    setBooking({
                        bookingId: Number(bookingId),
                        customerName: "Nguyễn Văn A",
                        customerPhone: "0901234567",
                        bookingTime: "2026-04-28T19:00:00",
                        numberOfPeople: 4,
                        status: "CONFIRMED",
                        tables: [
                            { tableId: 1, label: "Bàn 01" },
                            { tableId: 2, label: "Bàn 02" }
                        ],
                        note: "Bàn gần cửa sổ, có ghế trẻ em"
                    });
                } else {
                    const result = await apiClient.get(`/receptionist/bookings/${bookingId}`);
                    setBooking(result.data);
                }
            } catch (err) {
                console.error('Lỗi tải chi tiết:', err);
            } finally {
                setLoading(false);
            }
        };
        fetchDetail();
    }, [bookingId]);

    const handleCancel = async () => {
        if (!cancelReason.trim()) return alert('Vui lòng nhập lý do hủy');
        setCancelling(true);
        try {
            if (USE_MOCK) {
                await new Promise(r => setTimeout(r, 600));
            } else {
                await apiClient.patch(`/receptionist/bookings/${bookingId}/cancel`, { reason: cancelReason });
            }
            setBooking(prev => ({ ...prev, status: 'CANCELLED' }));
        } catch (err) {
            console.error('Lỗi hủy booking:', err);
        } finally {
            setCancelling(false);
        }
    };

    const formatDateTime = (dateStr) => {
        const d = new Date(dateStr);
        return `${d.toLocaleDateString('vi-VN')} — ${d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}`;
    };

    if (loading) return <div className="loading-state">Đang tải...</div>;
    if (!booking) return <div className="empty-state">Không tìm thấy booking</div>;

    return (
        <div className="booking-detail">
            <button className="btn-back" onClick={() => navigate('/receptionist')}>← Quay lại</button>

            <div className="detail-card">
                <div className="detail-header">
                    <h2>Booking #{booking.bookingId}</h2>
                    <span className="status-badge">{booking.status}</span>
                </div>

                <div className="detail-body">
                    <div className="info-grid">
                        <div className="info-item">
                            Khách hàng
                            <strong>{booking.customerName}</strong>
                        </div>
                        <div className="info-item">
                            Số điện thoại
                            <strong>{booking.customerPhone}</strong>
                        </div>
                        <div className="info-item">
                            Thời gian
                            <strong>{formatDateTime(booking.bookingTime)}</strong>
                        </div>
                        <div className="info-item">
                            Số khách
                            <strong>{booking.numberOfPeople} người</strong>
                        </div>
                    </div>

                    {booking.note && (
                        <div className="info-item" style={{ marginBottom: 16 }}>
                            Ghi chú: <strong>{booking.note}</strong>
                        </div>
                    )}

                    {booking.tables && booking.tables.length > 0 && (
                        <div className="tables-info">
                            Bàn đã xếp:
                            {booking.tables.map(t => (
                                <span key={t.tableId} className="table-tag">{t.label}</span>
                            ))}
                        </div>
                    )}

                    {booking.status === 'CONFIRMED' && (
                        <div className="cancel-section">
                            <h3>Hủy booking (khách không đến)</h3>
                            <textarea
                                placeholder="Nhập lý do hủy booking..."
                                value={cancelReason}
                                onChange={(e) => setCancelReason(e.target.value)}
                            />
                            <button className="btn-cancel-booking" onClick={handleCancel} disabled={cancelling}>
                                {cancelling ? 'Đang xử lý...' : 'Xác nhận hủy booking'}
                            </button>
                        </div>
                    )}

                    {booking.status === 'CANCELLED' && (
                        <div style={{ textAlign: 'center', color: '#c62828', fontWeight: 600, padding: 20 }}>
                            ❌ Booking đã bị hủy
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default BookingDetail;
