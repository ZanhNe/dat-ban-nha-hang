import React, { useState } from 'react';
import useBroadcastCenter from '../../hooks/admin/useBroadcastCenter';
import './BroadcastCenter.css';

function BroadcastCenter() {
    const { isSending, error, handleSendNotification } = useBroadcastCenter();

    // State quản lý form
    const [formData, setFormData] = useState({
        targetType: 'ALL', // ALL, ROLE, INDIVIDUAL
        targetUserId: '',
        title: '',
        content: '',
        type: 'SYSTEM_ALERT'
    });

    const onSubmit = async (e) => {
        e.preventDefault();

        const success = await handleSendNotification(formData);

        // Nếu gửi thành công thì reset form (giữ lại targetType)
        if (success.success) {
            setFormData({
                ...formData,
                title: '',
                content: '',
                targetUserId: ''
            });
        }
    };

    return (
        <div className="admin-page broadcast-page">
            <header className="page-header">
                <h1>Trung tâm thông báo</h1>
                <p>Gửi thông báo hệ thống, cảnh báo hoặc khuyến mãi đến người dùng.</p>
            </header>
            {error && <div className="empty-state">{error}</div>}

            <div className="compose-container">
                <form className="compose-form" onSubmit={onSubmit}>

                    <div className="form-section">
                        <h3>1. Đối tượng nhận</h3>
                        <div className="target-selection">
                            <label className="radio-label">
                                <input
                                    type="radio"
                                    name="targetType"
                                    value="ALL"
                                    checked={formData.targetType === 'ALL'}
                                    onChange={(e) => setFormData({ ...formData, targetType: e.target.value })}
                                /> Toàn bộ hệ thống
                            </label>
                            <label className="radio-label">
                                <input
                                    type="radio"
                                    name="targetType"
                                    value="INDIVIDUAL"
                                    checked={formData.targetType === 'INDIVIDUAL'}
                                    onChange={(e) => setFormData({ ...formData, targetType: e.target.value })}
                                /> Gửi cá nhân
                            </label>
                        </div>




                        {formData.targetType === 'INDIVIDUAL' && (
                            <div className="dynamic-input fade-in">
                                <label>Nhập ID Người dùng (User ID):</label>
                                <input
                                    type="number"
                                    placeholder="Ví dụ: 105"
                                    value={formData.targetUserId}
                                    onChange={(e) => setFormData({ ...formData, targetUserId: e.target.value })}
                                    required={formData.targetType === 'INDIVIDUAL'}
                                />
                            </div>
                        )}
                    </div>


                    <div className="form-section">
                        <h3>2. Nội dung thông báo</h3>

                        <div className="form-group">
                            <label>Loại thông báo:</label>
                            <select
                                value={formData.type}
                                onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                            >
                                <option value="SYSTEM_ALERT">Cảnh báo hệ thống (Khẩn cấp)</option>
                                <option value="SYSTEM_MESSAGE">Thông báo chung</option>
                                <option value="PROMOTION">Khuyến mãi / Sự kiện</option>
                            </select>
                        </div>

                        <div className="form-group">
                            <label>Tiêu đề thông báo:</label>
                            <input
                                type="text"
                                placeholder="Nhập tiêu đề..."
                                value={formData.title}
                                onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>Nội dung chi tiết:</label>
                            <textarea
                                placeholder="Viết nội dung thông báo ở đây..."
                                rows="6"
                                value={formData.content}
                                onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                                required
                            />
                        </div>
                    </div>


                    <div className="form-actions">
                        <button type="submit" className="btn-send-broadcast" disabled={isSending}>
                            {isSending ? " Đang gửi đi..." : " Phát sóng thông báo"}
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
}

export default BroadcastCenter;