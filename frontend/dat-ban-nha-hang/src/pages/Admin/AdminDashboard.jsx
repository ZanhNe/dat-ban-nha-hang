import React, { useState } from 'react';
import useAdminDashboard from '../../hooks/admin/useAdminDashboard';
import './AdminStyles.css';
import PageHeader from '../../components/ui/PageHeader';
import LoadingState from '../../components/ui/LoadingState';
import SectionCard from '../../components/ui/SectionCard';
import {
    ResponsiveContainer,
    BarChart,
    Bar,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
} from 'recharts';

function AdminDashboard() {
    const { metrics, isLoading, error, fetchMetrics } = useAdminDashboard();


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
        return <LoadingState message="Đang tải dữ liệu tổng quan..." />;
    }

    const chartData = [
        {
            name: 'Hoa hồng',
            value: Number(metrics?.totalRevenue ?? 0),
        },
        {
            name: 'Booking',
            value: Number(metrics?.totalBookings ?? 0),
        },
        {
            name: 'Nhà hàng',
            value: Number(metrics?.totalRestaurants ?? 0),
        },
        {
            name: 'Khách mới',
            value: Number(metrics?.totalNewCustomers ?? metrics?.totalNewUsers ?? 0),
        },
    ];

    return (
        <div className="admin-page">
            <PageHeader
                title="Tổng quan hệ thống"
                subtitle="Báo cáo hoạt động kinh doanh nền tảng"
                rightSlot={
                    <form className="date-filter-form flex gap-2 items-end" onSubmit={handleFilter}>
                        <div>
                            <label className="text-xs block text-gray-500">Từ ngày</label>
                            <input
                                type="date"
                                value={dateRange.fromDate}
                                onChange={(e) => setDateRange({ ...dateRange, fromDate: e.target.value })}
                                className="ui-input"
                            />
                        </div>
                        <div>
                            <label className="text-xs block text-gray-500">Đến ngày</label>
                            <input
                                type="date"
                                value={dateRange.toDate}
                                onChange={(e) => setDateRange({ ...dateRange, toDate: e.target.value })}
                                className="ui-input"
                            />
                        </div>
                        <button type="submit" className="ui-btn ui-btn-primary">Lọc</button>
                        {(dateRange.fromDate || dateRange.toDate) && (
                            <button type="button" className="ui-btn ui-btn-danger" onClick={handleClearFilter}>Xóa lọc</button>
                        )}
                    </form>
                }
            />
            {error && <div className="error-banner">{error}</div>}


            <div className={`metrics-grid ${isLoading ? 'opacity-50' : ''}`}>
                <div className="metric-card revenue">
                    <h3>Tổng hoa hồng</h3>
                    <p className="metric-value">
                        {`${Number(metrics?.totalRevenue ?? 0).toLocaleString('vi-VN')} ₫`}
                    </p>
                </div>
                <div className="metric-card">
                    <h3>Tổng Booking</h3>
                    <p className="metric-value">
                        {Number(metrics?.totalBookings ?? 0).toLocaleString('vi-VN')}
                    </p>
                </div>
                <div className="metric-card">
                    <h3>Tổng nhà hàng</h3>
                    <p className="metric-value">
                        {Number(metrics?.totalRestaurants ?? 0).toLocaleString('vi-VN')}
                    </p>
                </div>
                <div className="metric-card warning">
                    <h3>Khách hàng mới</h3>
                    <p className="metric-value">
                        {Number(metrics?.totalNewCustomers ?? metrics?.totalNewUsers ?? 0).toLocaleString('vi-VN')}
                    </p>
                </div>
            </div>

            {metrics?.generatedAt && (
                <p className="text-sm text-gray-500 mt-3">
                    Cập nhật lúc {new Date(metrics.generatedAt).toLocaleString('vi-VN')}
                </p>
            )}

            <div className="dashboard-charts">
                <SectionCard className="p-4">
                    <h3 className="text-base font-semibold mb-3">Biểu đồ tổng quan nhanh</h3>
                    <div className="h-[320px]">
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={chartData} margin={{ top: 8, right: 16, left: 0, bottom: 8 }}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="name" />
                                <YAxis />
                                <Tooltip formatter={(value) => Number(value).toLocaleString('vi-VN')} />
                                <Bar dataKey="value" fill="#2563eb" radius={[8, 8, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </SectionCard>
            </div>
        </div>
    );
}

export default AdminDashboard;