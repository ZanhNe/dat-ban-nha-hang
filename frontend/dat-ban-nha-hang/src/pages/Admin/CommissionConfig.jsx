import React, { useState, useEffect } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';
import './CommissionConfig.css';

function CommissionConfig() {
    const [restaurants, setRestaurants] = useState([]);
    const [selectedRestaurantId, setSelectedRestaurantId] = useState('');
    const [currentRate, setCurrentRate] = useState(null);
    const [newRate, setNewRate] = useState('');
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchRate = async () => {
            try {
                const result = await adminService.getAllRestaurants(0, 50);
                const list = result.data || [];
                setRestaurants(list);
                if (list.length > 0) {
                    const first = list[0];
                    setSelectedRestaurantId(String(first.restaurantId));
                    setCurrentRate(first.baseCommissionValue || 0);
                    setNewRate(String(first.baseCommissionValue || 0));
                }
            } catch (err) {
                console.error('Lỗi tải cấu hình:', err);
                setError(formatApiError(err, 'Không thể tải cấu hình hoa hồng.').displayMessage);
            } finally {
                setLoading(false);
            }
        };
        fetchRate();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedRestaurantId) return;
        const rate = Math.round(Number(newRate));
        if (isNaN(rate) || rate < 0 || rate > 100) return alert('Tỷ lệ phải từ 0 đến 100');
        setSaving(true);
        setError('');
        setShowSuccess(false);
        try {
            await adminService.updateRestaurantCommission(selectedRestaurantId, {
                commissionType: 'PERCENTAGE',
                baseCommissionValue: rate
            });
            setRestaurants((prev) => prev.map((restaurant) => (
                String(restaurant.restaurantId) === String(selectedRestaurantId)
                    ? { ...restaurant, commissionType: 'PERCENTAGE', baseCommissionValue: rate }
                    : restaurant
            )));
            setCurrentRate(rate);
            setShowSuccess(true);
            setTimeout(() => setShowSuccess(false), 3000);
        } catch (err) {
            console.error('Lỗi cập nhật:', err);
            setError(formatApiError(err, 'Không thể cập nhật cấu hình hoa hồng.').displayMessage);
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="loading-state">Đang tải cấu hình...</div>;

    return (
        <div className="commission-config">
            <h2>Cấu hình hoa hồng</h2>
            <p className="subtitle">Thiết lập tỷ lệ hoa hồng hệ thống thu từ mỗi giao dịch</p>
            {error && <div className="save-success" style={{ background: '#fdecea', color: '#c0392b' }}>{error}</div>}

            <div className="commission-card">
                <div className="current-rate">
                    <div className="rate-value">{currentRate}%</div>
                    <div className="rate-label">Tỷ lệ hoa hồng hiện tại</div>
                </div>

                <form className="commission-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Nhà hàng</label>
                        <select
                            value={selectedRestaurantId}
                            onChange={(e) => {
                                const id = e.target.value;
                                setSelectedRestaurantId(id);
                                const selected = restaurants.find((r) => String(r.restaurantId) === id);
                                const val = selected?.baseCommissionValue ?? 0;
                                setCurrentRate(val);
                                setNewRate(String(val));
                            }}
                        >
                            {restaurants.map((rest) => (
                                <option key={rest.restaurantId} value={rest.restaurantId}>
                                    {rest.restaurantName}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div className="form-group">
                        <label>Tỷ lệ mới (%)</label>
                        <input
                            type="number"
                            step="1"
                            min="0"
                            max="100"
                            value={newRate}
                            onChange={(e) => setNewRate(e.target.value)}
                            placeholder="VD: 10"
                        />
                    </div>
                    <button type="submit" className="btn-save-commission" disabled={saving}>
                        {saving ? 'Đang lưu...' : 'Cập nhật tỷ lệ'}
                    </button>
                    {showSuccess && <div className="save-success">✅ Đã cập nhật thành công!</div>}
                </form>
            </div>
        </div>
    );
}

export default CommissionConfig;
