// src/pages/receptionist/BookingRequests.jsx
import React, { useState, useEffect } from 'react';
import { receptionistService } from '../../services/receptionistService';
import './BookingRequests.css';

function BookingRequests() {
    const [requests, setRequests] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [rejectingId, setRejectingId] = useState(null);
    const [reason, setReason] = useState("");

    useEffect(() => {
        fetchRequests();
    }, []);

    const fetchRequests = async () => {
        const res = await receptionistService.getBookings("PENDING");
        if (res.status === 200) setRequests(res.data);
        setIsLoading(false);
    };

    const handleConfirm = async (id) => {
        if (!window.confirm("Xác nhận đặt bàn cho khách này?")) return;
        const res = await receptionistService.confirmBooking(id);
        if (res.status === 200) {
            alert(res.message);
            setRequests(prev => prev.filter(r => r.bookingId !== id));
        }
    };

    const handleRejectSubmit = async (e) => {
        e.preventDefault();
        const res = await receptionistService.rejectBooking(rejectingId, reason);
        if (res.status === 200) {
            alert(res.message);
            setRequests(prev => prev.filter(r => r.bookingId !== rejectingId));
            setRejectingId(null);
            setReason("");
        }
    };

    return (
        <div className="reception-page">
            <div className="page-header">
                <h2>Yêu cầu đặt bàn mới</h2>
                <p>Những yêu cầu đang chờ bạn xử lý</p>
            </div>

            {isLoading ? <p>Đang tải...</p> : (
                <div className="request-grid">
                    {requests.map(req => (
                        <div key={req.bookingId} className="booking-card pending">
                            <div className="card-header">
                                <span className="time">{req.bookingTime}</span>
                                <span className="date">{req.bookingDate}</span>
                            </div>
                            <div className="card-body">
                                <h4>{req.fullName}</h4>
                                <p>📞 {req.phone}</p>
                                <p>👥 <strong>{req.guestCount} người</strong></p>
                                {req.note && <p className="note">💬 {req.note}</p>}
                            </div>
                            <div className="card-actions">
                                <button className="btn-confirm" onClick={() => handleConfirm(req.bookingId)}>Xác nhận</button>
                                <button className="btn-reject" onClick={() => setRejectingId(req.bookingId)}>Từ chối</button>
                            </div>
                        </div>
                    ))}
                    {requests.length === 0 && <p className="empty-msg">Hiện không có yêu cầu mới nào.</p>}
                </div>
            )}

            {/* Modal từ chối */}
            {rejectingId && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>Lý do từ chối</h3>
                        <form onSubmit={handleRejectSubmit}>
                            <textarea
                                required
                                value={reason}
                                onChange={(e) => setReason(e.target.value)}
                                placeholder="Nhập lý do gửi cho khách (ví dụ: Hết bàn)..."
                            />
                            <div className="modal-actions">
                                <button type="submit" className="btn-danger">Gửi từ chối</button>
                                <button type="button" onClick={() => setRejectingId(null)}>Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default BookingRequests;