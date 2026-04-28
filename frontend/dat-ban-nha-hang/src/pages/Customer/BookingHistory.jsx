import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import apiClient from '../../services/apiClient';
import './BookingHistory.css';

const USE_MOCK = true;

function BookingHistory() {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchBookings = async () => {
            try {
                if (USE_MOCK) {
                    await new Promise(r => setTimeout(r, 500));
                    setBookings([
                        { bookingId: 101, restaurantName: "Haidilao - Hùng Vương", bookingTime: "2026-04-20T19:00:00", numberOfPeople: 4, status: "COMPLETED", restaurantId: 1 },
                        { bookingId: 102, restaurantName: "King BBQ - Lê Văn Sỹ", bookingTime: "2026-04-22T18:30:00", numberOfPeople: 2, status: "CONFIRMED", restaurantId: 2 },
                        { bookingId: 103, restaurantName: "Gogi House - Quận 7", bookingTime: "2026-04-25T20:00:00", numberOfPeople: 6, status: "CANCELLED", restaurantId: 3 },
                        { bookingId: 104, restaurantName: "Nhà hàng Phố Xưa", bookingTime: "2026-04-27T19:30:00", numberOfPeople: 3, status: "SERVING", restaurantId: 4 },
                    ]);
                } else {
                    const result = await apiClient.get('/users/me/bookings');
                    setBookings(result.data || []);
                }
            } catch (err) {
                console.error('Lỗi tải lịch sử:', err);
            } finally {
                setLoading(false);
            }
        };
        fetchBookings();
    }, []);

    const formatDate = (dateStr) => {
        const d = new Date(dateStr);
        return d.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
    };

    const formatTime = (dateStr) => {
        const d = new Date(dateStr);
        return d.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    };

    const statusLabel = {
        CONFIRMED: 'Đã xác nhận',
        COMPLETED: 'Hoàn thành',
        CANCELLED: 'Đã hủy',
        SERVING: 'Đang phục vụ',
        PENDING: 'Chờ xác nhận',
        REJECTED: 'Bị từ chối',
    };

    if (loading) return <div className="loading-state">Đang tải lịch sử đặt bàn...</div>;

    return (
        <div className="booking-history">
            <h2>Lịch sử đặt bàn</h2>
            {bookings.length === 0 ? (
                <div className="empty-state">
                    <div className="empty-icon">📖</div>
                    <p>Bạn chưa có lịch sử đặt bàn nào</p>
                </div>
            ) : (
                <div className="history-list">
                    {bookings.map(b => (
                        <div key={b.bookingId} className="history-card">
                            <div className="history-info">
                                <h3>{b.restaurantName}</h3>
                                <p>📅 {formatDate(b.bookingTime)} — 🕐 {formatTime(b.bookingTime)}</p>
                                <p>👥 {b.numberOfPeople} người</p>
                                {b.status === 'COMPLETED' && (
                                    <div className="history-actions">
                                        <button className="btn-review" onClick={() => navigate(`/customer/review/${b.restaurantId}`)}>
                                            ⭐ Đánh giá
                                        </button>
                                    </div>
                                )}
                            </div>
                            <div className="history-status">
                                <span className={`status-badge ${b.status.toLowerCase()}`}>
                                    {statusLabel[b.status] || b.status}
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default BookingHistory;
