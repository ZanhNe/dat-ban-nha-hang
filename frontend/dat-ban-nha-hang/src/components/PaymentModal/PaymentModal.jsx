import React, { useState, useEffect } from 'react';
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';
import { paymentService } from '../../services/paymentService';
import { X } from 'lucide-react';

const stripePromise = loadStripe('pk_test_your_publishable_key_here'); // Sử dụng key test của Stripe ở đây

const CheckoutForm = ({ clientSecret, onSuccess, onError, isProcessing, setIsProcessing }) => {
    const stripe = useStripe();
    const elements = useElements();

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (!stripe || !elements) {
            return;
        }

        setIsProcessing(true);

        const result = await stripe.confirmCardPayment(clientSecret, {
            payment_method: {
                card: elements.getElement(CardElement),
            }
        });

        if (result.error) {
            onError(result.error.message);
            setIsProcessing(false);
        } else {
            if (result.paymentIntent.status === 'requires_capture') {
                onSuccess();
            } else {
                onError("Trạng thái giao dịch không hợp lệ: " + result.paymentIntent.status);
                setIsProcessing(false);
            }
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <div className="p-4 border border-gray-200 rounded-lg bg-white">
                <CardElement options={{
                    style: {
                        base: {
                            fontSize: '16px',
                            color: '#424770',
                            '::placeholder': {
                                color: '#aab7c4',
                            },
                        },
                        invalid: {
                            color: '#e63946',
                        },
                    },
                }} />
            </div>
            <button
                type="submit"
                disabled={!stripe || isProcessing}
                className={`w-full py-3 px-4 rounded-lg font-medium text-white transition-colors
                    ${(!stripe || isProcessing)
                        ? 'bg-blue-400 cursor-not-allowed'
                        : 'bg-blue-600 hover:bg-blue-700'}`}
            >
                {isProcessing ? 'Đang xử lý...' : 'Xác nhận thanh toán'}
            </button>
        </form>
    );
};

export const PaymentModal = ({ booking, onClose, onPaymentSuccess }) => {
    const [clientSecret, setClientSecret] = useState('');
    const [error, setError] = useState('');
    const [isProcessing, setIsProcessing] = useState(false);
    const [isInitiating, setIsInitiating] = useState(false);

    useEffect(() => {
        const initPayment = async () => {
            try {
                setIsInitiating(true);
                const res = await paymentService.initiatePayment(booking.bookingId);
                const data = res.data;
                setClientSecret(data.clientSecret);
            } catch (err) {
                setError(err.response?.data?.message || 'Có lỗi xảy ra khi khởi tạo thanh toán');
            } finally {
                setIsInitiating(false);
            }
        };

        if (booking) {
            initPayment();
        }
    }, [booking]);

    const handleSuccess = () => {
        onPaymentSuccess();
    };

    if (!booking) return null;

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

                    {isInitiating ? (
                        <div className="flex justify-center flex-col items-center py-8">
                            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mb-4"></div>
                            <p className="text-sm text-gray-500">Đang khởi tạo thanh toán...</p>
                        </div>
                    ) : clientSecret ? (
                        <Elements stripe={stripePromise} options={{ clientSecret }}>
                            <CheckoutForm
                                clientSecret={clientSecret}
                                onSuccess={handleSuccess}
                                onError={setError}
                                isProcessing={isProcessing}
                                setIsProcessing={setIsProcessing}
                            />
                        </Elements>
                    ) : (
                        <div className="text-center text-gray-500 py-4">
                            Không thể tải form thanh toán.
                        </div>
                    )}
                </div>
                <div className="p-4 border-t flex justify-end sm:hidden">
                    <button onClick={onClose} className="px-4 py-2 border rounded-md text-gray-700 w-full hover:bg-gray-50">Đóng</button>
                </div>
            </div>
        </div>
    );
};
