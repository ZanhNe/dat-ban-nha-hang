import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { restaurantService } from '../../services/restaurantService';
import { formatApiError, unwrapData, unwrapMeta } from '../../services/apiShape';
import { formatDateTime, getBookingStartTime } from '../../utils/dateTime';
import PaginationBar from '../../components/ui/PaginationBar';
import './BookingHistory.css';

function BookingHistory() {
    const [bookings, setBookings] = useState([]);
    const [meta, setMeta] = useState({ page: 0, limit: 10, totalPages: 1, totalItems: 0 });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [filters, setFilters] = useState({ page: 0, limit: 10, status: '', fromDate: '', toDate: '' });
    const navigate = useNavigate();

    useEffect(() => {
        const fetchBookings = async () => {
            try {
                setLoading(true);
                const params = {
                    page: filters.page,
                    limit: filters.limit,
                    status: filters.status || undefined,
                    fromDate: filters.fromDate ? `${filters.fromDate}T00:00:00` : undefined,
                    toDate: filters.toDate ? `${filters.toDate}T00:00:00` : undefined,
                };
                const result = await restaurantService.getBookingHistory(params);
                setBookings(unwrapData(result) || []);
                setMeta(unwrapMeta(result) || { page: 0, limit: 10, totalPages: 1, totalItems: 0 });
            } catch (err) {
                const apiError = formatApiError(err, 'Không thể tải lịch sử đặt bàn.');
                setError(apiError.displayMessage);
            } finally {
                setLoading(false);
            }
        };
        fetchBookings();
    }, [filters]);

    const statusLabel = {
        AWAITING_CONFIRMATION: 'Chờ lễ tân xác nhận',
        PENDING_PAYMENT: 'Chờ thanh toán cọc',
        CONFIRMED: 'Đã xác nhận',
        CUSTOMER_ARRIVED: 'Khách đã đến',
        COMPLETED: 'Hoàn thành',
        CANCELLED: 'Đã hủy',
        SERVING: 'Đang phục vụ',
        SERVED: 'Đã phục vụ xong',
        PENDING: 'Chờ xác nhận',
        REJECTED: 'Bị từ chối',
        EXPIRED: 'Hết hạn',
        FAILED: 'Thanh toán thất bại'
    };

    if (loading) return <div className="loading-state">Đang tải lịch sử đặt bàn...</div>;

    return (
        <div className="booking-history">
            <h2>Lịch sử đặt bàn</h2>
            <div className="history-filters" style={{ display: 'grid', gap: 12, gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', marginBottom: 16 }}>
                <select
                    value={filters.status}
                    onChange={(e) => setFilters((prev) => ({ ...prev, page: 0, status: e.target.value }))}
                    className="form-control"
                >
                    <option value="">Tất cả trạng thái</option>
                    {Object.entries(statusLabel).map(([value, label]) => (
                        <option key={value} value={value}>{label}</option>
                    ))}
                </select>
                <input
                    type="date"
                    value={filters.fromDate}
                    onChange={(e) => setFilters((prev) => ({ ...prev, page: 0, fromDate: e.target.value }))}
                    className="form-control"
                />
                <input
                    type="date"
                    value={filters.toDate}
                    onChange={(e) => setFilters((prev) => ({ ...prev, page: 0, toDate: e.target.value }))}
                    className="form-control"
                />
            </div>
            {error && <div className="empty-state"><p>{error}</p></div>}
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
                                <h3>{b.restaurant?.restaurantName || `Đơn #${b.bookingId}`}</h3>
                                <p>📅 {formatDateTime(getBookingStartTime(b)).date} — 🕐 {formatDateTime(getBookingStartTime(b)).time}</p>
                                <p>👥 {b.quantity} người</p>
                                {b.status === 'COMPLETED' && (
                                    <div className="history-actions">
                                        <button className="btn-review" onClick={() => navigate(`/customer/review/${b.restaurant?.restaurantId}`)}>
                                            ⭐ Đánh giá
                                        </button>
                                    </div>
                                )}
                            </div>
                            <div className="history-status">
                                <span className={`status-badge ${String(b.status || 'unknown').toLowerCase()}`}>
                                    {statusLabel[b.status] || b.status}
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
            {meta.totalPages > 1 && (
                <PaginationBar
                    page={meta.page}
                    totalPages={meta.totalPages}
                    totalItems={meta.totalItems}
                    onPageChange={(newPage) => setFilters((prev) => ({ ...prev, page: newPage }))}
                    disabled={loading}
                />
            )}
        </div>
    );
}

export default BookingHistory;
