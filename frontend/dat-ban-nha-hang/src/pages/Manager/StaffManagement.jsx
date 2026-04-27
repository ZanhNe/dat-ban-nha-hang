import React, { useState } from 'react';
import { useStaffManagement } from '../../hooks/manager/useStaffManagement';
import './StaffManagement.css';

function StaffManagement() {
    const {
        staffs, isLoading, isActionLoading,
        handleCreate, handleUpdate, handleDelete, handleKick
    } = useStaffManagement();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingStaff, setEditingStaff] = useState(null);
    const [formData, setFormData] = useState({
        username: '', password: '', fullName: '', email: '', phone: '', roleId: 2, status: 'ACTIVE'
    });

    const openModal = (staff = null) => {
        if (staff) {
            setEditingStaff(staff);
            setFormData({
                fullName: staff.fullName,
                email: staff.email || '',
                phone: staff.phone,
                status: staff.status,
                roleId: staff.roles[0]?.id || 2
            });
        } else {
            setEditingStaff(null);
            setFormData({ username: '', password: '', fullName: '', email: '', phone: '', roleId: 2, status: 'ACTIVE' });
        }
        setIsModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Đảm bảo roleId là số
        const payload = { ...formData, roleId: Number(formData.roleId) };
        let success = false;

        if (editingStaff) {
            // PUT yêu cầu không gửi username/password
            const { username, password, ...updatePayload } = payload;
            success = await handleUpdate(editingStaff.userId, updatePayload);
        } else {
            success = await handleCreate(payload);
        }

        if (success) setIsModalOpen(false);
    };

    return (
        <div className="manager-page staff-page">
            <header className="page-header flex-between">
                <div>
                    <h1>Quản lý Nhân sự</h1>
                    <p>Điều phối, thêm mới hoặc gỡ nhân viên khỏi nhà hàng</p>
                </div>
                <button className="btn-add-new" onClick={() => openModal()} disabled={isLoading}>
                    + Thêm nhân viên
                </button>
            </header>

            {isLoading ? (
                <div className="loading-state">Đang tải danh sách nhân viên...</div>
            ) : (
                <div className="table-responsive">
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nhân viên</th>
                                <th>Tài khoản</th>
                                <th>Quyền hạn</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {staffs.map(staff => (
                                <tr key={staff.userId}>
                                    <td>{staff.userId}</td>
                                    <td>
                                        <strong>{staff.fullName}</strong><br />
                                        <small>{staff.phone}</small>
                                    </td>
                                    <td>
                                        {staff.username}<br />
                                        <small className="text-gray">{staff.email}</small>
                                    </td>
                                    <td>
                                        <span className="role-tag">{staff.roles[0]?.name.replace('ROLE_', '')}</span>
                                    </td>
                                    <td>
                                        <span className={`badge ${staff.status.toLowerCase()}`}>{staff.status}</span>
                                    </td>
                                    <td>
                                        <div className="action-group">
                                            <button className="btn-edit" onClick={() => openModal(staff)} disabled={isActionLoading}>Sửa</button>
                                            <button className="btn-warning" onClick={() => handleKick(staff.userId)} disabled={isActionLoading}>Đuổi</button>
                                            <button className="btn-delete" onClick={() => handleDelete(staff.userId)} disabled={isActionLoading}>Xóa</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            {staffs.length === 0 && (
                                <tr><td colSpan="6" className="text-center">Nhà hàng chưa có nhân viên nào.</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            )}

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>{editingStaff ? "Cập nhật nhân viên" : "Thêm nhân viên mới"}</h2>

                        <form onSubmit={handleSubmit}>
                            {!editingStaff && (
                                <div className="form-grid-2">
                                    <div className="form-group">
                                        <label>Username <span className="req">*</span></label>
                                        <input type="text" value={formData.username} onChange={e => setFormData({ ...formData, username: e.target.value })} required disabled={isActionLoading} />
                                    </div>
                                    <div className="form-group">
                                        <label>Mật khẩu <span className="req">*</span></label>
                                        <input type="password" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} required disabled={isActionLoading} />
                                    </div>
                                </div>
                            )}

                            <div className="form-group">
                                <label>Họ và Tên <span className="req">*</span></label>
                                <input type="text" value={formData.fullName} onChange={e => setFormData({ ...formData, fullName: e.target.value })} required disabled={isActionLoading} />
                            </div>

                            <div className="form-grid-2">
                                <div className="form-group">
                                    <label>Số điện thoại <span className="req">*</span></label>
                                    <input type="text" value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })} required disabled={isActionLoading} />
                                </div>
                                <div className="form-group">
                                    <label>Email</label>
                                    <input type="email" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} disabled={isActionLoading} />
                                </div>
                            </div>

                            <div className="form-grid-2">
                                <div className="form-group">
                                    <label>Vị trí (Quyền) <span className="req">*</span></label>
                                    <select value={formData.roleId} onChange={e => setFormData({ ...formData, roleId: e.target.value })} disabled={isActionLoading}>
                                        <option value="2">Phục vụ (WAITER)</option>
                                        <option value="4">Thu ngân (CASHIER)</option>
                                        <option value="5">Lễ tân (RECEPTIONIST)</option>
                                    </select>
                                </div>

                                {editingStaff && (
                                    <div className="form-group">
                                        <label>Trạng thái</label>
                                        <select value={formData.status} onChange={e => setFormData({ ...formData, status: e.target.value })} disabled={isActionLoading}>
                                            <option value="ACTIVE">Hoạt động</option>
                                            <option value="BANNED">Đình chỉ (Banned)</option>
                                        </select>
                                    </div>
                                )}
                            </div>

                            <div className="modal-actions">
                                <button type="submit" className="btn-save" disabled={isActionLoading}>
                                    {isActionLoading ? "Đang lưu..." : "Lưu dữ liệu"}
                                </button>
                                <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)} disabled={isActionLoading}>
                                    Hủy
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default StaffManagement;