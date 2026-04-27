import React, { useState } from 'react';
import useAdminDashboard from '../../hooks/admin/useAdminDashboard';
import './AdminStyles.css';

function AdminDashboard() {
    const { metrics, isLoading, fetchMetrics } = useAdminDashboard();


    const [dateRange, setDateRange] = useState({ fromDate: '', toDate: '' });

    const handleFilter = (e) => {
        e.preventDefault();
        fetchMetrics(dateRange.fromDate, dateRange.toDate);
    };

    const handleClearFilter = () => {
        setDateRange({ fromDate: '', toDate: '' });
        fetchMetrics();
    };

    if (isLoading && !metrics) {
        return <div className="admin-page"><p>Đang tải dữ liệu tổng quan...</p></div>;
    }

    return (
        <div className="admin-page">
            <header className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                <div>
                    <h1>Tổng quan hệ thống</h1>
                    <p>Báo cáo hoạt động kinh doanh nền tảng</p>
                </div>


                <form className="date-filter-form" onSubmit={handleFilter} style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                    <div>
                        <label style={{ fontSize: '12px', display: 'block', color: '#7f8c8d' }}>Từ ngày</label>
                        <input
                            type="date"
                            value={dateRange.fromDate}
                            onChange={(e) => setDateRange({ ...dateRange, fromDate: e.target.value })}
                            style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    <div>
                        <label style={{ fontSize: '12px', display: 'block', color: '#7f8c8d' }}>Đến ngày</label>
                        <input
                            type="date"
                            value={dateRange.toDate}
                            onChange={(e) => setDateRange({ ...dateRange, toDate: e.target.value })}
                            style={{ padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                    </div>
                    <button type="submit" style={{ padding: '8px 15px', background: '#3498db', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', alignSelf: 'flex-end' }}>
                        Lọc
                    </button>
                    {(dateRange.fromDate || dateRange.toDate) && (
                        <button type="button" onClick={handleClearFilter} style={{ padding: '8px 15px', background: '#e74c3c', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', alignSelf: 'flex-end' }}>
                            Xóa lọc
                        </button>
                    )}
                </form>
            </header>


            <div className="metrics-grid" style={{ opacity: isLoading ? 0.5 : 1, transition: '0.3s' }}>
                <div className="metric-card revenue">
                    <h3>Tổng hoa hồng</h3>
                    <p className="metric-value">
                        {metrics?.totalRevenue ? `${metrics.totalRevenue.toLocaleString('vi-VN')} ₫` : '0 ₫'}
                    </p>
                </div>
                <div className="metric-card">
                    <h3>Tổng Booking</h3>
                    <p className="metric-value">
                        {metrics?.totalBookings ? metrics.totalBookings.toLocaleString('vi-VN') : 0}
                    </p>
                </div>
                <div className="metric-card">
                    <h3>Tổng nhà hàng</h3>
                    <p className="metric-value">
                        {metrics?.totalRestaurants ? metrics.totalRestaurants.toLocaleString('vi-VN') : 0}
                    </p>
                </div>
                <div className="metric-card warning">
                    <h3>Khách hàng mới</h3>
                    <p className="metric-value">
                        {metrics?.totalNewUsers ? metrics.totalNewUsers.toLocaleString('vi-VN') : 0}
                    </p>
                </div>
            </div>

            <div className="dashboard-charts">
                <div className="chart-placeholder">
                    [Khu vực tích hợp biểu đồ (Chart.js / Recharts) sau này]
                </div>
            </div>
        </div>
    );
}

export default AdminDashboard;