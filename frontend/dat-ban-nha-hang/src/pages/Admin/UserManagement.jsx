import React, { useState } from 'react';
import { useUserManagement } from '../../hooks/admin/useUserManagement';
import './UserManagement.css';

function UserManagement() {
    const { users, isLoading, isActionLoading, filters, setFilters, handleSaveUser } = useUserManagement();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [formData, setFormData] = useState({ username: '', fullName: '', email: '', phone: '', roleId: 3, status: 'ACTIVE' });

    const openModal = (user = null) => {
        if (user) {
            setEditingUser(user);
            setFormData({
                fullName: user.fullName,
                email: user.email,
                phone: user.phone,
                status: user.status,
                roleId: user.roles[0].id,
                username: user.username // Username thường không cho sửa nhưng gửi đi để nhất quán
            });
        } else {
            setEditingUser(null);
            setFormData({ username: '', fullName: '', email: '', phone: '', roleId: 3, status: 'ACTIVE' });
        }
        setIsModalOpen(true);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const success = await handleSaveUser(formData, editingUser?.userId);
        if (success) setIsModalOpen(false);
    };

    return (
        <div className="admin-page">
            <header className="page-header flex-between">
                <div>
                    <h1>Quản lý Người dùng</h1>
                    <p>Quản trị tài khoản và điều phối nhân sự toàn hệ thống</p>
                </div>
                <button className="btn-add-new" onClick={() => openModal()}>+ Tạo tài khoản</button>
            </header>

            <div className="filter-bar">
                <input
                    type="text"
                    placeholder="Tìm tên, email..."
                    value={filters.search}
                    onChange={(e) => setFilters({ ...filters, search: e.target.value })}
                />
                <select onChange={(e) => setFilters({ ...filters, role: e.target.value })}>
                    <option value="">Tất cả Role</option>
                    <option value="ROLE_ADMIN">Admin</option>
                    <option value="ROLE_MANAGER">Manager</option>
                    <option value="ROLE_WAITER">Waiter</option>
                    <option value="ROLE_CUSTOMER">Customer</option>
                </select>
            </div>

            <div className="table-responsive">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ tên</th>
                            <th>Username</th>
                            <th>Quyền</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {users.map(user => (
                            <tr key={user.userId}>
                                <td>{user.userId}</td>
                                <td><strong>{user.fullName}</strong><br /><small>{user.email}</small></td>
                                <td>{user.username}</td>
                                <td>{user.roles.map(r => <span key={r.id} className="role-tag">{r.name}</span>)}</td>
                                <td><span className={`badge ${user.status.toLowerCase()}`}>{user.status}</span></td>
                                <td>
                                    <button className="btn-edit" onClick={() => openModal(user)}>Sửa</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>{editingUser ? "Cập nhật tài khoản" : "Tạo tài khoản mới"}</h2>
                        <form onSubmit={handleSubmit}>
                            {!editingUser && (
                                <div className="form-group">
                                    <label>Username</label>
                                    <input type="text" required onChange={(e) => setFormData({ ...formData, username: e.target.value })} />
                                </div>
                            )}
                            {!editingUser && (
                                <div className="form-group">
                                    <label>Mật khẩu</label>
                                    <input type="password" required onChange={(e) => setFormData({ ...formData, password: e.target.value })} />
                                </div>
                            )}
                            <div className="form-group">
                                <label>Họ tên</label>
                                <input type="text" value={formData.fullName} required onChange={(e) => setFormData({ ...formData, fullName: e.target.value })} />
                            </div>
                            <div className="form-group">
                                <label>Số điện thoại</label>
                                <input type="text" value={formData.phone} required onChange={(e) => setFormData({ ...formData, phone: e.target.value })} />
                            </div>
                            <div className="form-group">
                                <label>Quyền hạn</label>
                                <select value={formData.roleId} onChange={(e) => setFormData({ ...formData, roleId: e.target.value })}>
                                    <option value="1">MANAGER</option>
                                    <option value="2">WAITER</option>
                                    <option value="3">CUSTOMER</option>
                                </select>
                            </div>
                            {editingUser && (
                                <div className="form-group">
                                    <label>Trạng thái</label>
                                    <select value={formData.status} onChange={(e) => setFormData({ ...formData, status: e.target.value })}>
                                        <option value="ACTIVE">Hoạt động</option>
                                        <option value="BANNED">Khóa</option>
                                    </select>
                                </div>
                            )}
                            <div className="modal-actions">
                                <button type="submit" className="btn-save" disabled={isActionLoading}>Lưu</button>
                                <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default UserManagement;