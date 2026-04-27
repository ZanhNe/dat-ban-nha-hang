import React, { useState, useEffect } from 'react';
import { useRestaurantInfo } from '../../hooks/manager/useRestaurantInfo';
import './RestaurantInfo.css';

function RestaurantInfo() {
    const { info, isLoading, isSaving, updateInfo, deleteRestaurant } = useRestaurantInfo();
    const [formData, setFormData] = useState({});

    // Cập nhật formData khi API fetch xong dữ liệu
    useEffect(() => {
        if (info) {
            setFormData({
                name: info.name || '',
                logo: info.logo || '',
                description: info.description || '',
                address: info.address || '',
                baseDepositValue: info.baseDepositValue || 0,
                depositPolicy: info.depositPolicy || 'NONE',
                dayOfWeek: info.dayOfWeek || 7
            });
        }
    }, [info]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        // Xử lý kiểu dữ liệu (ép kiểu số cho deposit và days)
        const payload = {
            ...formData,
            baseDepositValue: Number(formData.baseDepositValue),
            dayOfWeek: Number(formData.dayOfWeek)
        };
        await updateInfo(payload);
    };

    if (isLoading) return <div className="manager-page">Đang tải thông tin...</div>;

    return (
        <div className="manager-page info-page">
            <header className="page-header flex-between">
                <div>
                    <h1>Thông tin nhà hàng</h1>
                    <p>Quản lý hồ sơ và cấu hình đặt cọc của nhà hàng</p>
                </div>
                {info?.status && (
                    <span className={`status-badge ${info.status.toLowerCase()}`}>
                        Trạng thái: {info.status}
                    </span>
                )}
            </header>

            <form className="info-form-container" onSubmit={handleSubmit}>
                <div className="form-grid">
                    <div className="form-card">
                        <h3> Thông tin cơ bản</h3>
                        <div className="form-group">
                            <label>Tên nhà hàng <span className="req">*</span></label>
                            <input
                                type="text" name="name"
                                value={formData.name || ''} onChange={handleChange} required
                                disabled={isSaving}
                            />
                        </div>

                        <div className="form-group">
                            <label>Địa chỉ <span className="req">*</span></label>
                            <input
                                type="text" name="address"
                                value={formData.address || ''} onChange={handleChange} required
                                disabled={isSaving}
                            />
                        </div>

                        <div className="form-group">
                            <label>URL Logo (Hình ảnh)</label>
                            <input
                                type="url" name="logo"
                                value={formData.logo || ''} onChange={handleChange}
                                placeholder="https://..." disabled={isSaving}
                            />
                        </div>

                        <div className="form-group">
                            <label>Giới thiệu / Mô tả</label>
                            <textarea
                                name="description" rows="4"
                                value={formData.description || ''} onChange={handleChange}
                                disabled={isSaving}
                            />
                        </div>
                    </div>

                    <div className="form-card">
                        <h3>💳 Cấu hình đặt cọc</h3>

                        <div className="form-group">
                            <label>Chính sách đặt cọc</label>
                            <select name="depositPolicy" value={formData.depositPolicy || 'NONE'} onChange={handleChange} disabled={isSaving}>
                                <option value="NONE">Không yêu cầu cọc</option>
                                <option value="FIXED">Cọc cố định (Mỗi đơn 1 giá)</option>
                                <option value="PER_GUEST">Cọc theo đầu người (Giá x Số khách)</option>
                            </select>
                        </div>

                        {formData.depositPolicy !== 'NONE' && (
                            <div className="form-group highlight-box">
                                <label>Mức cọc cơ sở (VNĐ) <span className="req">*</span></label>
                                <input
                                    type="number" name="baseDepositValue" min="0"
                                    value={formData.baseDepositValue || 0} onChange={handleChange} required
                                    disabled={isSaving}
                                />
                                <small className="hint">
                                    {formData.depositPolicy === 'FIXED'
                                        ? "Khách sẽ phải cọc đúng số tiền này cho mỗi lượt đặt bàn."
                                        : "Hệ thống sẽ lấy số tiền này nhân với số lượng khách để tính tổng tiền cọc."}
                                </small>
                            </div>
                        )}

                        <div className="form-group mt-20">
                            <h3> Thời gian hoạt động</h3>
                            <label>Số ngày phục vụ trong tuần</label>
                            <input
                                type="number" name="dayOfWeek" min="1" max="7"
                                value={formData.dayOfWeek || 7} onChange={handleChange} required
                                disabled={isSaving}
                            />
                        </div>
                    </div>
                </div>

                <div className="form-actions-bar">
                    <button type="submit" className="btn-save-info" disabled={isSaving}>
                        {isSaving ? "Đang lưu..." : " Lưu thay đổi"}
                    </button>

                    <button type="button" className="btn-delete-danger" onClick={deleteRestaurant} disabled={isSaving}>
                        Xóa nhà hàng
                    </button>
                </div>
            </form>
        </div>
    );
}

export default RestaurantInfo;