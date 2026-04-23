import React from 'react';
import './AdminStyles.css';

function AdminDashboard() {
    // Mock data tổng quan
    const metrics = {
        totalRevenue: "125,000,000 ₫",
        totalRestaurants: 142,
        pendingApprovals: 5,
        totalBookings: 3450
    };

    return (
        <div className="admin-page">
            <header className="page-header">
                <h1>Tổng quan hệ thống</h1>
            </header>

            <div className="metrics-grid">
                <div className="metric-card revenue">
                    <h3>Tổng hoa hồng</h3>
                    <p className="metric-value">{metrics.totalRevenue}</p>
                </div>
                <div className="metric-card">
                    <h3>Tổng nhà hàng</h3>
                    <p className="metric-value">{metrics.totalRestaurants}</p>
                </div>
                <div className="metric-card">
                    <h3>Tổng Booking</h3>
                    <p className="metric-value">{metrics.totalBookings}</p>
                </div>
                <div className="metric-card warning">
                    <h3>Chờ duyệt</h3>
                    <p className="metric-value">{metrics.pendingApprovals}</p>
                </div>
            </div>

            {/* Bạn có thể thêm Biểu đồ (Chart.js / Recharts) ở khu vực này */}
            <div className="dashboard-charts">
                <div className="chart-placeholder">
                    [Khu vực hiển thị biểu đồ doanh thu theo tháng]
                </div>
            </div>
        </div>
    );
}

export default AdminDashboard;