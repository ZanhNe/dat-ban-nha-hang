import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { restaurantService } from '../../services/restaurantService';
import './RegisterRestaurant.css';
import PageHeader from '../../components/ui/PageHeader';

const CUISINE_OPTIONS = [
    { id: 1, name: "Vietnamese" },
    { id: 2, name: "Chinese" },
    { id: 3, name: "Japanese" },
    { id: 4, name: "Korean" },
    { id: 5, name: "Thai" },
    { id: 6, name: "Hotpot" },
    { id: 7, name: "Seafood" },
    { id: 8, name: "BBQ" }
];

function RegisterRestaurant() {
    const [form, setForm] = useState({
        name: '',
        description: '',
        address: '',
        baseDepositValue: 200000,
        depositPolicy: 'FIXED',
        cuisineIds: []
    });

    const [logo, setLogo] = useState(null);
    const [legalDocs, setLegalDocs] = useState([]);

    const [submitting, setSubmitting] = useState(false);
    const [submitted, setSubmitted] = useState(false);

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleCuisineToggle = (id) => {
        setForm(prev => {
            const currentIds = prev.cuisineIds;
            if (currentIds.includes(id)) {
                return { ...prev, cuisineIds: currentIds.filter(cId => cId !== id) };
            } else {
                return { ...prev, cuisineIds: [...currentIds, id] };
            }
        });
    };

    const handleFileChange = (e) => {
        if (e.target.name === 'logo') {
            setLogo(e.target.files[0]);
        } else if (e.target.name === 'legalDocs') {
            setLegalDocs(Array.from(e.target.files));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSubmitting(true);
        try {
            const formData = new FormData();
            formData.append('name', form.name);
            formData.append('description', form.description);
            formData.append('address', form.address);
            formData.append('latitude', 10.762622);
            formData.append('longitude', 106.660172);
            formData.append('baseDepositValue', form.baseDepositValue);
            formData.append('depositPolicy', form.depositPolicy);

            form.cuisineIds.forEach(id => {
                formData.append('cuisineIds', id);
            });

            if (logo) {
                formData.append('logo', logo);
            }

            if (legalDocs && legalDocs.length > 0) {
                legalDocs.forEach(doc => {
                    formData.append('legalDocs', doc);
                });
            }

            await restaurantService.registerRestaurant(formData);
            setSubmitted(true);
        } catch (err) {
            console.error('Lỗi đăng ký:', err);
            alert("Có lỗi xảy ra khi đăng ký: " + (err.response?.data?.message || err.message));
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
            <PageHeader
                title="Đăng ký mở nhà hàng"
                subtitle="Điền thông tin và đính kèm giấy tờ pháp lý để gửi yêu cầu đăng ký"
                rightSlot={<Link to="/customer" className="ui-btn">Quay lại</Link>}
            />

            <div className="register-card">
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Tên nhà hàng *</label>
                        <input
                            name="name"
                            value={form.name}
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
                            <label>Mức cọc cơ bản (VNĐ) *</label>
                            <input
                                type="number"
                                name="baseDepositValue"
                                value={form.baseDepositValue}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Chính sách cọc *</label>
                            <select name="depositPolicy" value={form.depositPolicy} onChange={handleChange} required>
                                <option value="FIXED">Cố định (FIXED)</option>
                                <option value="PER_GUEST">Theo người (PER_GUEST)</option>
                                <option value="NONE">Không cọc (NONE)</option>
                            </select>
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Danh mục ẩm thực *</label>
                        <div className="cuisine-checkboxes flex gap-4 flex-wrap">
                            {CUISINE_OPTIONS.map(c => (
                                <label key={c.id} className="flex items-center gap-1">
                                    <input
                                        type="checkbox"
                                        checked={form.cuisineIds.includes(c.id)}
                                        onChange={() => handleCuisineToggle(c.id)}
                                    />
                                    {c.name}
                                </label>
                            ))}
                        </div>
                    </div>

                    <div className="form-group">
                        <label>Mô tả nhà hàng *</label>
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            placeholder="Giới thiệu ngắn về nhà hàng của bạn..."
                            required
                        />
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Logo nhà hàng (Ảnh) *</label>
                            <input
                                type="file"
                                name="logo"
                                accept="image/*"
                                onChange={handleFileChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Giấy tờ pháp lý (Nhiều file) *</label>
                            <input
                                type="file"
                                name="legalDocs"
                                multiple
                                onChange={handleFileChange}
                                required
                            />
                        </div>
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
