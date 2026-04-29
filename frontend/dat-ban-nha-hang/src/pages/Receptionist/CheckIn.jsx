import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCheckIn } from '../../hooks/receptionist/useCheckIn';
import PageHeader from '../../components/ui/PageHeader';
import LoadingState from '../../components/ui/LoadingState';
import EmptyState from '../../components/ui/EmptyState';
import SectionCard from '../../components/ui/SectionCard';
import { formatDateTime } from '../../utils/dateTime';
import PaginationBar from '../../components/ui/PaginationBar';

const statusLabelMap = {
    CONFIRMED: 'Đã xác nhận',
    CUSTOMER_ARRIVED: 'Khách đã đến',
    SERVING: 'Đang phục vụ',
    SERVED: 'Đã phục vụ',
    COMPLETED: 'Hoàn tất',
    CANCELLED: 'Đã hủy',
    REJECTED: 'Đã từ chối',
    EXPIRED: 'Hết hạn',
    FAILED: 'Thất bại',
    PENDING_PAYMENT: 'Chờ thanh toán',
    AWAITING_CONFIRMATION: 'Chờ xác nhận'
};

function CheckIn() {
    const { bookings, isLoading, isActionLoading, error, page, meta, handleCheckIn, fetchConfirmed } = useCheckIn();
    const navigate = useNavigate();
    const [searchTerm, setSearchTerm] = useState("");

    // Lọc danh sách theo tên hoặc SĐT khách
    const filteredBookings = bookings.filter(b =>
        b.customerName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        b.customerPhone?.includes(searchTerm)
    );

    return (
        <div>
            <PageHeader
                title="Danh sách đón khách"
                subtitle="Các lịch đặt đã xác nhận để check-in."
                rightSlot={
                    <button className="ui-btn" onClick={() => fetchConfirmed(page)} disabled={isLoading}>
                        Làm mới
                    </button>
                }
            />
            {error ? <p className="status-error mb-4">{error}</p> : null}

            <div className="mb-4">
                <input
                    type="text"
                    placeholder="🔍 Tìm theo tên khách hoặc số điện thoại..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="ui-input max-w-md"
                />
            </div>

            {isLoading ? <LoadingState message="Đang tải lịch đặt..." /> : (
                <div className="space-y-3">
                    {filteredBookings.map(b => (
                        <SectionCard key={b.bookingId} className="p-4 flex justify-between items-center gap-3">
                            <div>
                                {(() => {
                                    const { date, time } = formatDateTime(b.bookingTime);
                                    return <div className="text-sm text-gray-500 mb-1">{time} • {date}</div>;
                                })()}
                                <div className="font-semibold">{b.customerName}</div>
                                <div className="text-sm text-gray-600">SĐT: {b.customerPhone}</div>
                                <div className="text-sm text-gray-600">
                                    {b.numberOfPeople} khách • {statusLabelMap[b.status] || b.status}
                                </div>
                            </div>
                            <div className="flex gap-2">
                                <button
                                    className="ui-btn"
                                    onClick={() => navigate(`/receptionist/bookings/${b.bookingId}`)}
                                >
                                    Xem chi tiết
                                </button>
                                <button
                                    className="ui-btn ui-btn-primary"
                                    onClick={() => handleCheckIn(b.bookingId)}
                                    disabled={isActionLoading}
                                >
                                    {isActionLoading ? "..." : "DẪN KHÁCH VÀO BÀN"}
                                </button>
                            </div>
                        </SectionCard>
                    ))}

                    {filteredBookings.length === 0 && (
                        <EmptyState message="Không tìm thấy lịch đặt nào khớp với tìm kiếm." />
                    )}
                </div>
            )}

            {meta && (
                <PaginationBar
                    page={page}
                    totalPages={meta.totalPages}
                    totalItems={meta.totalItems}
                    onPageChange={fetchConfirmed}
                    disabled={isLoading}
                />
            )}
        </div>
    );
}

export default CheckIn;