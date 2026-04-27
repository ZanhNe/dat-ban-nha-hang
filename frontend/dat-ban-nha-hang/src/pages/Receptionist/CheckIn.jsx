import React, { useState } from 'react';
import { useCheckIn } from '../../hooks/receptionist/useCheckIn';
import './CheckIn.css';

function CheckIn() {
    const { bookings, isLoading, isActionLoading, handleCheckIn, fetchConfirmed } = useCheckIn();
    const [searchTerm, setSearchTerm] = useState("");

    // Lọc danh sách theo tên hoặc SĐT khách
    const filteredBookings = bookings.filter(b =>
        b.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        b.phone.includes(searchTerm)
    );

    return (
        <div className="reception-page checkin-page">
            <header className="page-header flex-between">
                <div>
                    <h2>Danh sách đón khách hôm nay</h2>
                    <p>Chỉ hiển thị những lịch đặt đã được xác nhận (Confirmed)</p>
                </div>
                <button className="btn-refresh" onClick={fetchConfirmed} disabled={isLoading}>
                    Làm mới
                </button>
            </header>

            <div className="search-container">
                <input
                    type="text"
                    placeholder="🔍 Tìm theo tên khách hoặc số điện thoại..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </div>

            {isLoading ? <div className="loading">Đang tải lịch đặt...</div> : (
                <div className="checkin-list">
                    {filteredBookings.map(b => (
                        <div key={b.bookingId} className="checkin-card">
                            <div className="checkin-info">
                                <div className="time-badge">{b.bookingTime}</div>
                                <div className="customer-detail">
                                    <h4>{b.fullName}</h4>
                                    <span>📞 {b.phone}</span>
                                </div>
                                <div className="booking-detail">
                                    <span>👥 {b.guestCount} khách</span>
                                    <span>📍 {b.tableInfo || "Chưa gán bàn"}</span>
                                </div>
                            </div>
                            <button
                                className="btn-checkin-action"
                                onClick={() => handleCheckIn(b.bookingId)}
                                disabled={isActionLoading}
                            >
                                {isActionLoading ? "..." : "DẪN KHÁCH VÀO BÀN"}
                            </button>
                        </div>
                    ))}

                    {filteredBookings.length === 0 && (
                        <div className="empty-state">
                            <p>Không tìm thấy lịch đặt nào khớp với tìm kiếm.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
}

export default CheckIn;