import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import apiClient from '../../services/apiClient';
import './RegisterRestaurant.css';

const USE_MOCK = true;

function RegisterRestaurant() {
    const [form, setForm] = useState({
        restaurantName: '',
        address: '',
        phone: '',
        description: '',
        cuisineType: '',
    });
    const [submitting, setSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(false);

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            if (USE_MOCK) {
                await new Promise(r => setTimeout(r, 1000));
            } else {
                await apiClient.post('/restaurants/register', form);
            }
            setSubmitted(true);
        } catch (err) {
            console.error('Lỗi đăng ký:', err);
        } finally {
            setSubmitting(false);
        }
    };

    if (submitted) {
        return (
            <div className="register-restaurant-page">
                <div className="register-card">
                    <div className="success-banner">
                        <div className="icon">🎉</div>
                        <h3>Đăng ký thành công!</h3>
                        <p>Yêu cầu mở nhà hàng của bạn đã được gửi. Admin sẽ xem xét và phản hồi sớm nhất.</p>
                        <br />
                        <Link to="/customer" className="btn-back-link">← Về trang chủ</Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="register-restaurant-page">
            <Link to="/customer" className="btn-back-link" style={{ color: '#1565c0', textDecoration: 'none', fontWeight: 600 }}>← Quay lại</Link>
            <h2>Đăng ký mở nhà hàng</h2>
            <p className="subtitle">Điền thông tin để gửi yêu cầu đăng ký nhà hàng trên hệ thống</p>

            <div className="register-card">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Tên nhà hàng *</label>
                        <input
                            name="restaurantName"
                            value={form.restaurantName}
                            onChange={handleChange}
                            placeholder="VD: Haidilao - Chi nhánh Quận 1"
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Địa chỉ *</label>
                        <input
                            name="address"
                            value={form.address}
                            onChange={handleChange}
                            placeholder="VD: 126 Hùng Vương, Quận 5, TP.HCM"
                            required
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Số điện thoại *</label>
                            <input
                                name="phone"
                                value={form.phone}
                                onChange={handleChange}
                                placeholder="0901234567"
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Loại ẩm thực</label>
                            <input
                                name="cuisineType"
                                value={form.cuisineType}
                                onChange={handleChange}
                                placeholder="VD: Lẩu, BBQ, Á, Âu..."
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Mô tả nhà hàng</label>
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            placeholder="Giới thiệu ngắn về nhà hàng của bạn..."
                        />
                    </div>

                    <button type="submit" className="btn-submit-register" disabled={submitting}>
                        {submitting ? 'Đang gửi...' : 'Gửi yêu cầu đăng ký'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default RegisterRestaurant;
