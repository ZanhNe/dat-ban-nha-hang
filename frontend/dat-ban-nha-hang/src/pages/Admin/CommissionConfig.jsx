import React, { useState, useEffect } from 'react';
import apiClient from '../../services/apiClient';
import './CommissionConfig.css';

const USE_MOCK = true;

function CommissionConfig() {
    const [currentRate, setCurrentRate] = useState(null);
    const [newRate, setNewRate] = useState('');
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [showSuccess, setShowSuccess] = useState(false);

    useEffect(() => {
        const fetchRate = async () => {
            try {
                if (USE_MOCK) {
                    await new Promise(r => setTimeout(r, 400));
                    setCurrentRate(10);
                    setNewRate('10');
                } else {
                    const result = await apiClient.get('/admin/commission');
                    setCurrentRate(result.data?.rate || 0);
                    setNewRate(String(result.data?.rate || 0));
                }
            } catch (err) {
                console.error('Lỗi tải cấu hình:', err);
            } finally {
                setLoading(false);
            }
        };
        fetchRate();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const rate = parseFloat(newRate);
        if (isNaN(rate) || rate < 0 || rate > 100) return alert('Tỷ lệ phải từ 0 đến 100');
        setSaving(true);
        setShowSuccess(false);
        try {
            if (USE_MOCK) {
                await new Promise(r => setTimeout(r, 600));
            } else {
                await apiClient.put('/admin/commission', { rate });
            }
            setCurrentRate(rate);
            setShowSuccess(true);
            setTimeout(() => setShowSuccess(false), 3000);
        } catch (err) {
            console.error('Lỗi cập nhật:', err);
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="loading-state">Đang tải cấu hình...</div>;

    return (
        <div className="commission-config">
            <h2>Cấu hình hoa hồng</h2>
            <p className="subtitle">Thiết lập tỷ lệ hoa hồng hệ thống thu từ mỗi giao dịch</p>

            <div className="commission-card">
                <div className="current-rate">
                    <div className="rate-value">{currentRate}%</div>
                    <div className="rate-label">Tỷ lệ hoa hồng hiện tại</div>
                </div>

                <form className="commission-form" onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Tỷ lệ mới (%)</label>
                        <input
                            type="number"
                            step="0.1"
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
