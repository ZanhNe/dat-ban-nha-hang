import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './HomeCustomer.css';
import Header from '../../components/Header/Header'
import { paymentService } from '../../services/paymentService';
import { formatApiError, unwrapData } from '../../services/apiShape';

function HomeCustomer() {
    const [pendingCount, setPendingCount] = useState(0);
    const [pendingError, setPendingError] = useState('');

    useEffect(() => {
        const fetchPendingCount = async () => {
            try {
                const res = await paymentService.getPendingBookings();
                const pendingBookings = unwrapData(res) || [];
                if (Array.isArray(pendingBookings)) {
                    setPendingCount(pendingBookings.length);
                }
            } catch (err) {
                setPendingError(formatApiError(err, 'Không thể tải danh sách chờ thanh toán.').displayMessage);
            }
        };
        fetchPendingCount();
    }, []);

    return (
        <>
            <Header />
            <div className="home-customer">
                {/* Thông báo thanh toán cọc */}
                {pendingCount > 0 && (
                    <div className="alert-banner">
                        <strong>Chú ý:</strong> Bạn có {pendingCount} đơn đặt bàn đang chờ thanh toán cọc (VNPay).
                        <Link to="/customer/bookings/pending-payment" className="alert-link">
                            {' '}Thanh toán ngay để giữ chỗ
                        </Link>
                    </div>
                )}
                {pendingError && <div className="alert-banner" style={{ background: '#fdecea', color: '#c0392b' }}>{pendingError}</div>}



                <section className="features-section mt-8">
                    <h2 className="text-lg font-semibold mb-4 border-b pb-2">Bảng điều khiển cá nhân</h2>
                    <div className="dashboard-grid">
                        <Link to="/customer/bookings/pending-payment" className="dashboard-card">
                            <div className="card-icon">💳</div>
                            <div className="card-title">Chờ thanh toán</div>
                            <div className="card-desc">Thanh toán cọc qua VNPay</div>
                            {pendingCount > 0 && <span className="badge">{pendingCount}</span>}
                        </Link>
                        <Link to="/customer/bookings" className="dashboard-card">
                            <div className="card-icon">🕒</div>
                            <div className="card-title">Lịch sử đặt bàn</div>
                            <div className="card-desc">Xem lại các lần ăn uống</div>
                        </Link>
                        <Link to="/customer/register-restaurant" className="dashboard-card">
                            <div className="card-icon">🏢</div>
                            <div className="card-title">Đăng ký nhà hàng</div>
                            <div className="card-desc">Đăng ký nhà hàng của bạn</div>
                        </Link>
                        <Link to="/customer/map-search" className="dashboard-card">
                            <div className="card-icon">📍</div>
                            <div className="card-title">Tìm quanh đây</div>
                            <div className="card-desc">Định vị và tìm nhà hàng</div>
                        </Link>
                    </div>
                </section>
            </div>
        </>
    );
}

export default HomeCustomer;