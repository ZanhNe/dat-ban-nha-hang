import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './HomeCustomer.css'; // File CSS tương ứng
import Header from '../../components/Header/Header'
function HomeCustomer() {
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useState({ keyword: '', cuisine: '' });

    // Mock data: Thông báo chờ thanh toán (Tính năng 4)
    const pendingPayments = 1;

    // Mock data: Danh sách nhà hàng nổi bật (Tính năng 1, 2, 3)
    const featuredRestaurants = [
        { id: 1, name: "Haidilao Hotpot", cuisine: "Lẩu", location: "Quận 1, TP.HCM", rating: 4.8, image: "https://via.placeholder.com/300x200" },
        { id: 2, name: "El Gaucho Steakhouse", cuisine: "Âu", location: "Quận 2, TP.HCM", rating: 4.9, image: "https://via.placeholder.com/300x200" },
        { id: 3, name: "Sushi Hokkaido Sachi", cuisine: "Nhật Bản", location: "Quận 3, TP.HCM", rating: 4.7, image: "https://via.placeholder.com/300x200" },
    ];

    const handleSearch = (e) => {
        e.preventDefault();
        // Xử lý chuyển hướng kèm query params, hoặc gọi API
        console.log("Searching for:", searchParams);
        // navigate(`/customer/search?keyword=${searchParams.keyword}&cuisine=${searchParams.cuisine}`);
    };

    return (
        <>
            <Header />
            <div className="home-customer">
                {/* Cảnh báo thanh toán cọc - Tính năng 4 */}
                {pendingPayments > 0 && (
                    <div className="alert-banner">
                        Bạn có {pendingPayments} đặt bàn đang chờ thanh toán cọc.
                        <Link to="/customer/bookings/pending-payment"> Thanh toán ngay</Link>
                    </div>
                )}

                {/* Hero Section & TÌm kiếm - Tính năng 1 */}
                <section className="hero-section">
                    <h1>Tìm kiếm hương vị yêu thích của bạn</h1>
                    <form className="search-bar" onSubmit={handleSearch}>
                        <input
                            type="text"
                            placeholder="Tên nhà hàng, vị trí..."
                            value={searchParams.keyword}
                            onChange={(e) => setSearchParams({ ...searchParams, keyword: e.target.value })}
                        />
                        <select
                            value={searchParams.cuisine}
                            onChange={(e) => setSearchParams({ ...searchParams, cuisine: e.target.value })}
                        >
                            <option value="">Tất cả ẩm thực</option>
                            <option value="vietnamese">Việt Nam</option>
                            <option value="japanese">Nhật Bản</option>
                            <option value="western">Món Âu</option>
                        </select>
                        <button type="submit">Tìm kiếm</button>
                        <Link to="/customer/map-search" className="btn-map-search">
                            🗺️ Tìm trên bản đồ
                        </Link>
                    </form>
                </section>

                {/* Bảng điều khiển cá nhân (Lịch sử & Đánh giá) - Tính năng 5, 6 */}
                <section className="dashboard-quick-links">
                    <h2>Hoạt động của tôi</h2>
                    <div className="action-cards">
                        <Link to="/customer/history" className="action-card">
                            🕒 Lịch sử đặt bàn
                        </Link>
                        <Link to="/customer/reviews" className="action-card">
                            ⭐ Đánh giá nhà hàng
                        </Link>
                    </div>
                </section>

                {/* Danh sách nhà hàng nổi bật - Tính năng 2, 3 */}
                <section className="featured-section">
                    <h2>Nhà hàng nổi bật</h2>
                    <div className="restaurant-grid">
                        {featuredRestaurants.map((rest) => (
                            <div key={rest.id} className="restaurant-card">
                                <img src={rest.image} alt={rest.name} />
                                <div className="card-info">
                                    <h3>{rest.name}</h3>
                                    <p>🍽️ {rest.cuisine} | 📍 {rest.location}</p>
                                    <p>⭐ {rest.rating}/5.0</p>
                                    <div className="card-actions">
                                        <Link to={`/customer/restaurants/${rest.id}`} className="btn-details">
                                            Xem Menu & Đặt bàn
                                        </Link>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </section>
            </div>
        </>
    );
}

export default HomeCustomer;