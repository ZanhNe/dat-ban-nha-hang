// src/pages/receptionist/BookingRequests.jsx
import React, { useState, useEffect } from 'react';
import { receptionistService } from '../../services/receptionistService';
import { formatApiError } from '../../services/apiShape';
import PageHeader from '../../components/ui/PageHeader';
import LoadingState from '../../components/ui/LoadingState';
import EmptyState from '../../components/ui/EmptyState';
import SectionCard from '../../components/ui/SectionCard';
import { formatDateTime } from '../../utils/dateTime';
import PaginationBar from '../../components/ui/PaginationBar';

function BookingRequests() {
    const [requests, setRequests] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const [page, setPage] = useState(0);
    const [meta, setMeta] = useState(null);
    const [rejectingId, setRejectingId] = useState(null);
    const [reason, setReason] = useState("");

    const fetchRequests = async (targetPage = 0) => {
        setIsLoading(true);
        setError('');
        try {
            const res = await receptionistService.getAwaitingBookings(targetPage, 10);
            if (res.status === 200) {
                setRequests(res.data);
                setMeta(res.meta);
                setPage(targetPage);
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể tải danh sách yêu cầu đặt bàn.').displayMessage);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        const t = setTimeout(() => {
            fetchRequests(0);
        }, 0);
        return () => clearTimeout(t);
    }, []);

    const handleConfirm = async (id) => {
        if (!window.confirm("Xác nhận đặt bàn cho khách này?")) return;
        setError('');
        try {
            const res = await receptionistService.confirmBooking(id);
            if (res.status === 200) {
                setRequests(prev => prev.filter(r => r.bookingId !== id));
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể xác nhận booking.').displayMessage);
        }
    };

    const handleRejectSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const res = await receptionistService.rejectBooking(rejectingId, reason);
            if (res.status === 200) {
                setRequests(prev => prev.filter(r => r.bookingId !== rejectingId));
                setRejectingId(null);
                setReason("");
            }
        } catch (error) {
            setError(formatApiError(error, 'Không thể từ chối booking.').displayMessage);
        }
    };

    return (
        <div>
            <PageHeader
                title="Yêu cầu đặt bàn mới"
                subtitle="Những yêu cầu đang chờ bạn xử lý"
            />
            {error ? <p className="status-error mb-4">{error}</p> : null}

            {isLoading ? <LoadingState message="Đang tải yêu cầu đặt bàn..." /> : null}

            {!isLoading && requests.length === 0 ? (
                <EmptyState message="Hiện không có yêu cầu mới nào." />
            ) : null}

            {!isLoading && requests.length > 0 ? (
                <div className="grid md:grid-cols-2 gap-4">
                    {requests.map(req => (
                        <SectionCard key={req.bookingId} className="p-4">
                            {(() => {
                                const { date, time } = formatDateTime(req.bookingTime);
                                return (
                                    <div className="flex justify-between text-sm text-gray-500 mb-3">
                                        <span>{time}</span>
                                        <span>{date}</span>
                                    </div>
                                );
                            })()}
                            <div className="space-y-1 mb-3">
                                <p className="font-semibold">{req.customerName}</p>
                                <p className="text-sm text-gray-600">SĐT: {req.customerPhone}</p>
                                <p className="text-sm text-gray-600">Số khách: {req.numberOfPeople}</p>
                            </div>
                            <div className="flex gap-2">
                                <button className="ui-btn ui-btn-primary" onClick={() => handleConfirm(req.bookingId)}>Xác nhận</button>
                                <button className="ui-btn ui-btn-danger" onClick={() => setRejectingId(req.bookingId)}>Từ chối</button>
                            </div>
                        </SectionCard>
                    ))}
                </div>
            ) : null}

            {meta && (
                <PaginationBar
                    page={page}
                    totalPages={meta.totalPages}
                    totalItems={meta.totalItems}
                    onPageChange={fetchRequests}
                    disabled={isLoading}
                />
            )}

            {/* Modal từ chối */}
            {rejectingId && (
                <div className="fixed inset-0 bg-black/30 flex items-center justify-center z-40 p-4">
                    <div className="ui-card w-full max-w-md p-4">
                        <h3>Lý do từ chối</h3>
                        <form onSubmit={handleRejectSubmit}>
                            <textarea
                                required
                                value={reason}
                                onChange={(e) => setReason(e.target.value)}
                                placeholder="Nhập lý do gửi cho khách (ví dụ: Hết bàn)..."
                                className="ui-input min-h-28 mt-2"
                            />
                            <div className="flex gap-2 mt-3">
                                <button type="submit" className="ui-btn ui-btn-danger">Gửi từ chối</button>
                                <button type="button" className="ui-btn" onClick={() => setRejectingId(null)}>Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default BookingRequests;