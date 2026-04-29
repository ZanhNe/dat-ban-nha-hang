import React, { useState } from 'react';
import { paymentService } from '../../services/paymentService';
import { X } from 'lucide-react';

export const PaymentModal = ({ booking, onClose, onPaymentSuccess }) => {
    const [error, setError] = useState('');
    const [isProcessing, setIsProcessing] = useState(false);

    if (!booking) return null;

    const handlePay = async () => {
        setIsProcessing(true);
        setError('');
        try {
            const res = await paymentService.initiatePayment(booking.bookingId);
            if (res?.data?.url) {
                window.location.href = res.data.url;
                return;
            }
            setError('Không lấy được URL thanh toán VNPay.');
        } catch (err) {
            setError(err.response?.data?.message || 'Có lỗi xảy ra khi khởi tạo thanh toán');
        } finally {
            setIsProcessing(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
            <div className="bg-white rounded-xl shadow-xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in duration-200">
                <div className="flex items-center justify-between p-4 border-b">
                    <h2 className="text-xl font-semibold text-gray-800">Thanh toán tiền cọc</h2>
                    <button onClick={onClose} className="p-1 hover:bg-gray-100 rounded-full transition-colors hidden sm:block">
                        <X className="w-5 h-5 text-gray-500" />
                    </button>
                </div>

                <div className="p-6">
                    <div className="mb-6 bg-gray-50 p-4 rounded-lg flex flex-col items-center">
                        <p className="text-sm text-gray-500 mb-1">Mã Booking</p>
                        <p className="font-medium text-gray-800 mb-3">#{booking.bookingId}</p>

                        <p className="text-sm text-gray-500 mb-1">Số tiền cọc</p>
                        <p className="text-2xl font-bold text-blue-600">
                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(booking.depositAmount)}
                        </p>
                    </div>

                    {error && (
                        <div className="mb-6 p-3 bg-red-50 text-red-600 text-sm rounded-lg border border-red-100">
                            {error}
                        </div>
                    )}

                    <button
                        type="button"
                        onClick={handlePay}
                        disabled={isProcessing}
                        className={`w-full py-3 px-4 rounded-lg font-medium text-white transition-colors
                            ${isProcessing ? 'bg-blue-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700'}`}
                    >
                        {isProcessing ? 'Đang xử lý...' : 'Thanh toán qua VNPay'}
                    </button>
                </div>
                <div className="p-4 border-t flex justify-end sm:hidden">
                    <button
                        onClick={() => {
                            onClose();
                            onPaymentSuccess?.();
                        }}
                        className="px-4 py-2 border rounded-md text-gray-700 w-full hover:bg-gray-50"
                    >
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    );
};
