import React, { useState, useEffect } from 'react';
import { paymentService } from '../../services/paymentService';
import { PaymentModal } from '../../components/PaymentModal/PaymentModal';
import { Clock, Users, Calendar, ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const PendingBookingsPage = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedBooking, setSelectedBooking] = useState(null);
    const [successMessage, setSuccessMessage] = useState('');
    const navigate = useNavigate();

    const fetchBookings = async () => {
        try {
            setLoading(true);
            const data = await paymentService.getPendingBookings();
            setBookings(data);
        } catch (err) {
            if (err.response?.status === 401 || err.response?.status === 403) {
                navigate('/login');
            }
            setError('Không thể tải danh sách đặt bàn. Vui lòng thử lại.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchBookings();
    }, []);

    const handlePaymentSuccess = () => {
        setSuccessMessage('Đã khởi tạo giao dịch đặt cọc thành công, vui lòng chờ nhà hàng duyệt.');
        setSelectedBooking(null);
        // Refresh the list after successful payment
        fetchBookings();
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
            <div className="max-w-3xl mx-auto">
                <div className="mb-8 items-center flex justify-between">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">Bookings cần thanh toán</h1>
                        <p className="mt-1 text-sm text-gray-500">
                            Thanh toán tiền cọc để giữ chỗ cho các đơn đặt bàn của bạn.
                        </p>
                    </div>
                </div>

                {error && (
                    <div className="mb-6 p-4 bg-red-50 text-red-700 rounded-lg">
                        {error}
                    </div>
                )}

                {successMessage && (
                    <div className="mb-6 p-4 bg-green-50 text-green-700 rounded-lg flex items-center justify-between shadow-sm">
                        <span>{successMessage}</span>
                        <button onClick={() => setSuccessMessage('')} className="text-green-700 font-bold ml-4 hover:underline">Đóng</button>
                    </div>
                )}

                <div className="space-y-4">
                    {bookings.length === 0 && !error ? (
                        <div className="text-center py-12 bg-white rounded-xl shadow-sm border border-gray-100">
                            <Calendar className="mx-auto h-12 w-12 text-gray-300 mb-3" />
                            <h3 className="text-lg font-medium text-gray-900">Không có hóa đơn nào</h3>
                            <p className="mt-1 text-gray-500">Bạn chưa có đơn đặt bàn nào đang chờ thanh toán.</p>
                        </div>
                    ) : (
                        bookings.map((booking) => (
                            <div key={booking.bookingId} className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-md transition-shadow">
                                <div className="p-6 sm:flex sm:items-center sm:justify-between">
                                    <div className="mb-4 sm:mb-0">
                                        <div className="flex items-center gap-2 mb-2">
                                            <span className="px-2.5 py-0.5 rounded-full text-xs font-medium bg-amber-100 text-amber-800">
                                                Chờ thanh toán
                                            </span>
                                            <h3 className="text-lg font-semibold text-gray-900">{booking.restaurantName}</h3>
                                        </div>
                                        <div className="flex flex-col sm:flex-row sm:items-center gap-4 text-sm text-gray-500 mt-3">
                                            <div className="flex items-center gap-1.5">
                                                <Calendar className="w-4 h-4" />
                                                <span>{new Date(booking.bookingTime).toLocaleDateString('vi-VN')}</span>
                                            </div>
                                            <div className="flex items-center gap-1.5">
                                                <Clock className="w-4 h-4" />
                                                <span>{new Date(booking.bookingTime).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}</span>
                                            </div>
                                            <div className="flex items-center gap-1.5">
                                                <Users className="w-4 h-4" />
                                                <span>{booking.guestCount} người</span>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="flex flex-col items-start sm:items-end gap-3">
                                        <div className="text-left sm:text-right">
                                            <p className="text-sm text-gray-500 mb-0.5">Tiền cọc</p>
                                            <p className="text-xl font-bold text-blue-600">
                                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(booking.depositAmount)}
                                            </p>
                                        </div>
                                        <button
                                            onClick={() => setSelectedBooking(booking)}
                                            className="w-full sm:w-auto flex items-center justify-center gap-1.5 bg-blue-600 hover:bg-blue-700 text-white px-5 py-2.5 rounded-lg font-medium transition-colors"
                                        >
                                            Thanh toán ngay
                                            <ChevronRight className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>

            {selectedBooking && (
                <PaymentModal 
                    booking={selectedBooking} 
                    onClose={() => setSelectedBooking(null)} 
                    onPaymentSuccess={handlePaymentSuccess} 
                />
            )}
        </div>
    );
};

export default PendingBookingsPage;
