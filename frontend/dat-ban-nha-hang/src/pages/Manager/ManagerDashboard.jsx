import React from 'react';
import { useManagerDashboard } from '../../hooks/manager/useManagerDashboard';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import './ManagerDashboard.css';

function ManagerDashboard() {
    const { overview, chartData, topFoods, isLoading, filters, setFilters } = useManagerDashboard();

    const handleFilterSubmit = (e) => {
        e.preventDefault();
        // Hook tự động fetch lại do useEffect phụ thuộc vào filters
        setFilters({ ...filters });
    };

    if (isLoading && !overview) {
        return <div className="manager-page"><p>Đang tải dữ liệu tổng quan...</p></div>;
    }

    return (
        <div className="manager-page">
            <header className="dashboard-header flex-between">
                <div>
                    <h1>Tổng quan hoạt động</h1>
                    <p>Báo cáo doanh thu và tình hình kinh doanh của nhà hàng</p>
                </div>

                <form className="filter-form" onSubmit={handleFilterSubmit}>
                    <div className="filter-group">
                        <label>Từ ngày</label>
                        <input type="date" value={filters.fromDate} onChange={e => setFilters({ ...filters, fromDate: e.target.value })} required />
                    </div>
                    <div className="filter-group">
                        <label>Đến ngày</label>
                        <input type="date" value={filters.toDate} onChange={e => setFilters({ ...filters, toDate: e.target.value })} required />
                    </div>
                    <div className="filter-group">
                        <label>Theo</label>
                        <select value={filters.timeUnit} onChange={e => setFilters({ ...filters, timeUnit: e.target.value })}>
                            <option value="DAY">Ngày</option>
                            <option value="WEEK">Tuần</option>
                            <option value="MONTH">Tháng</option>
                        </select>
                    </div>
                </form>
            </header>

            {/* KHU VỰC 1: KPIs Thống kê */}
            <div className="kpi-grid" style={{ opacity: isLoading ? 0.5 : 1 }}>
                <div className="kpi-card highlight">
                    <h3>Doanh thu gộp</h3>
                    <p className="kpi-value">{overview?.totalRevenue?.toLocaleString('vi-VN')} ₫</p>
                </div>
                <div className="kpi-card success">
                    <h3>Thực nhận (Net Revenue)</h3>
                    <p className="kpi-value">{overview?.netRevenue?.toLocaleString('vi-VN')} ₫</p>
                    <small>Đã trừ phí hoa hồng</small>
                </div>
                <div className="kpi-card">
                    <h3>Tổng Bookings</h3>
                    <p className="kpi-value">{overview?.totalBookings}</p>
                </div>
                <div className="kpi-card">
                    <h3>Khách hàng</h3>
                    <p className="kpi-value">{overview?.totalCustomers}</p>
                </div>
                <div className="kpi-card">
                    <h3>TB / Hóa đơn</h3>
                    <p className="kpi-value">{overview?.averageBill?.toLocaleString('vi-VN')} ₫</p>
                </div>
            </div>

            <div className="dashboard-body">
                {/* KHU VỰC 2: Biểu đồ doanh thu */}
                <div className="chart-container" style={{ opacity: isLoading ? 0.5 : 1 }}>
                    <h3>Biểu đồ doanh thu</h3>
                    <div className="chart-wrapper">
                        <ResponsiveContainer width="100%" height={350}>
                            <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                                <XAxis dataKey="date" />
                                <YAxis tickFormatter={(value) => `${value / 1000000}tr`} />
                                <Tooltip formatter={(value) => `${value.toLocaleString('vi-VN')} ₫`} />
                                <Legend />
                                <Line type="monotone" name="Doanh thu gộp" dataKey="revenue" stroke="#3498db" strokeWidth={3} activeDot={{ r: 8 }} />
                                <Line type="monotone" name="Thực nhận (Net)" dataKey="netRevenue" stroke="#2ecc71" strokeWidth={3} />
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* KHU VỰC 3: Top món ăn */}
                <div className="top-foods-container" style={{ opacity: isLoading ? 0.5 : 1 }}>
                    <h3>Top 5 Món bán chạy</h3>
                    <ul className="top-foods-list">
                        {topFoods.map((food, index) => (
                            <li key={food.foodId} className="food-item">
                                <div className="food-rank">#{index + 1}</div>
                                <div className="food-info">
                                    <h4>{food.foodName}</h4>
                                    <span className="food-sold">Đã bán: <strong>{food.quantitySold}</strong> phần</span>
                                </div>
                                <div className="food-revenue">
                                    {food.revenue.toLocaleString('vi-VN')} ₫
                                </div>
                            </li>
                        ))}
                        {topFoods.length === 0 && <p className="text-center">Chưa có dữ liệu món ăn</p>}
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default ManagerDashboard;