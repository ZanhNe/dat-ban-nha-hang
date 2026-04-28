import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './HomeCustomer.css';
import Header from '../../components/Header/Header'
import { paymentService } from '../../services/paymentService';

function HomeCustomer() {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useState({ keyword: '', cuisine: '' });
    const [pendingCount, setPendingCount] = useState(0);

    useEffect(() => {
        const fetchPendingCount = async () => {
            try {
                const res = await paymentService.getPendingBookings();
                if (res.data) {
                    setPendingCount(res.data.length);
                }
            } catch (err) {
                console.error("Không thể lấy danh sách chờ thanh toán", err);
            }
        };
        fetchPendingCount();
    }, []);

    const handleSearch = (e) => {
        e.preventDefault();
        navigate(`/customer/map-search?keyword=${searchParams.keyword}&cuisine=${searchParams.cuisine}`);
    };

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

                {/* Phần Tìm Kiếm Đơn Giản */}
                <section className="search-section shadow-sm border border-gray-200">
                    <h1 className="text-xl font-bold mb-4">Tìm Nhà Hàng Nhanh</h1>
                    <form className="search-form" onSubmit={handleSearch}>
                        <div className="search-inputs">
                            <input
                                type="text"
                                placeholder="Nhập tên nhà hàng hoặc địa chỉ..."
                                value={searchParams.keyword}
                                onChange={(e) => setSearchParams({ ...searchParams, keyword: e.target.value })}
                                className="form-control"
                            />
                            <select
                                value={searchParams.cuisine}
                                onChange={(e) => setSearchParams({ ...searchParams, cuisine: e.target.value })}
                                className="form-control"
                            >
                                <option value="">Tất cả loại hình</option>
                                <option value="Lẩu">Lẩu</option>
                                <option value="Món Trung">Món Trung</option>
                                <option value="BBQ">BBQ</option>
                            </select>
                        </div>
                        <div className="search-actions mt-4 flex gap-2">
                            <button type="submit" className="btn-primary">Tìm Kiếm</button>
                            <Link to="/customer/map-search" className="btn-secondary">
                                Xem trên Bản đồ
                            </Link>
                        </div>
                    </form>
                </section>

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