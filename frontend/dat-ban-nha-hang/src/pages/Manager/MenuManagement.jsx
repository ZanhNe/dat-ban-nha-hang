import React, { useState } from 'react';
import { useMenuManagement } from '../../hooks/manager/useMenuManagement';
import './MenuManagement.css';

function MenuManagement() {
    const {
        menus, activeMenuId, setActiveMenuId,
        foodGroups, foodsByGroup, fetchFoodsForGroup,
        isLoading, isActionLoading, error, handleSave, handleDelete
    } = useMenuManagement();


    const [modal, setModal] = useState({ isOpen: false, type: '', action: 'ADD', data: null, parentId: null });
    const [formData, setFormData] = useState({});

    const [expandedGroups, setExpandedGroups] = useState({});
    const toggleGroup = (groupId) => {
        setExpandedGroups(prev => ({ ...prev, [groupId]: !prev[groupId] }));
        if (!expandedGroups[groupId]) fetchFoodsForGroup(groupId);
    };

    const openModal = (type, action, data = null, parentId = null) => {
        setModal({ isOpen: true, type, action, data, parentId });
        if (action === 'EDIT') {
            setFormData(data);
        } else {
            if (type === 'FOOD') setFormData({ name: '', description: '', price: 0, status: 'OPENING' });
            else setFormData({ name: '', description: '' });
        }
    };

    const onSubmitModal = async (e) => {
        e.preventDefault();
        const payload = { ...formData };
        if (modal.type === 'FOOD') payload.price = Number(payload.price);

        const currentId = modal.action === 'EDIT'
            ? (modal.type === 'MENU' ? formData.menuId : modal.type === 'GROUP' ? formData.groupId : formData.foodId)
            : null;

        const success = await handleSave(modal.type, payload, currentId, modal.parentId);
        if (success) setModal({ isOpen: false, type: '', action: 'ADD', data: null, parentId: null });
    };

    if (isLoading) return <div className="manager-page">Đang tải cấu trúc Menu...</div>;

    return (
        <div className="manager-page menu-page">
            <header className="page-header">
                <h1>Quản lý Thực đơn (Menu)</h1>
                <p>Cấu hình thực đơn, nhóm món ăn và giá bán</p>
            </header>
            {error && <p className="status-error">{error}</p>}

            <div className="menu-layout">
                {/* CỘT TRÁI: DANH SÁCH MENU */}
                <div className="menu-sidebar">
                    <div className="sidebar-header">
                        <h3>Danh sách Thực đơn</h3>
                        <button className="btn-icon-add" onClick={() => openModal('MENU', 'ADD')}>➕</button>
                    </div>
                    <ul className="menu-list">
                        {menus.map(menu => (
                            <li key={menu.menuId} className={activeMenuId === menu.menuId ? 'active' : ''} onClick={() => setActiveMenuId(menu.menuId)}>
                                <div className="menu-info">
                                    <strong>{menu.name}</strong>
                                    <small>{menu.description}</small>
                                </div>
                                <div className="menu-actions" onClick={e => e.stopPropagation()}>
                                    <button onClick={() => openModal('MENU', 'EDIT', menu)}>✏️</button>
                                    <button className="delete" onClick={() => handleDelete('MENU', menu.menuId)}>🗑️</button>
                                </div>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* CỘT PHẢI: NHÓM MÓN & MÓN ĂN TRONG MENU ĐƯỢC CHỌN */}
                <div className="menu-content">
                    {activeMenuId ? (
                        <>
                            <div className="content-header">
                                <h2>Nhóm món ăn</h2>
                                <button className="btn-add-primary" onClick={() => openModal('GROUP', 'ADD', null, activeMenuId)}>
                                    + Thêm Nhóm Món
                                </button>
                            </div>

                            <div className="groups-container">
                                {foodGroups.map(group => (
                                    <div key={group.groupId} className="food-group-card">
                                        {/* Tiêu đề Group */}
                                        <div className="group-header" onClick={() => toggleGroup(group.groupId)}>
                                            <div className="group-title">
                                                <span className="toggle-icon">{expandedGroups[group.groupId] ? '▼' : '▶'}</span>
                                                <h3>{group.name}</h3>
                                                <small>{group.description}</small>
                                            </div>
                                            <div className="group-actions" onClick={e => e.stopPropagation()}>
                                                <button onClick={() => openModal('FOOD', 'ADD', null, group.groupId)}>+ Thêm món</button>
                                                <button onClick={() => openModal('GROUP', 'EDIT', group)}>✏️</button>
                                                <button className="text-danger" onClick={() => handleDelete('GROUP', group.groupId)}>🗑️</button>
                                            </div>
                                        </div>

                                        {/* Danh sách Món Ăn bên trong (Chỉ hiện khi mở rộng) */}
                                        {expandedGroups[group.groupId] && (
                                            <div className="foods-list">
                                                {foodsByGroup[group.groupId]?.length > 0 ? (
                                                    <table className="foods-table">
                                                        <tbody>
                                                            {foodsByGroup[group.groupId].map(food => (
                                                                <tr key={food.foodId}>
                                                                    <td width="50px"><div className="food-img-placeholder">🍲</div></td>
                                                                    <td>
                                                                        <strong>{food.name}</strong><br />
                                                                        <small>{food.description}</small>
                                                                    </td>
                                                                    <td width="150px" className="text-right text-success font-bold">
                                                                        {food.price.toLocaleString('vi-VN')} ₫
                                                                    </td>
                                                                    <td width="100px" className="text-center">
                                                                        <span className={`badge ${food.status.toLowerCase()}`}>{food.status}</span>
                                                                    </td>
                                                                    <td width="100px" className="text-right">
                                                                        <button className="btn-icon" onClick={() => openModal('FOOD', 'EDIT', food, group.groupId)}>✏️</button>
                                                                        <button className="btn-icon text-danger" onClick={() => handleDelete('FOOD', food.foodId, group.groupId)}>🗑️</button>
                                                                    </td>
                                                                </tr>
                                                            ))}
                                                        </tbody>
                                                    </table>
                                                ) : (
                                                    <p className="empty-foods">Chưa có món ăn nào trong nhóm này.</p>
                                                )}
                                            </div>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </>
                    ) : (
                        <div className="empty-state">Vui lòng chọn một Menu ở cột bên trái</div>
                    )}
                </div>
            </div>

            {modal.isOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>
                            {modal.action === 'ADD' ? 'Thêm mới ' : 'Cập nhật '}
                            {modal.type === 'MENU' ? 'Thực đơn' : modal.type === 'GROUP' ? 'Nhóm món' : 'Món ăn'}
                        </h2>
                        <form onSubmit={onSubmitModal}>
                            <div className="form-group">
                                <label>Tên <span className="req">*</span></label>
                                <input type="text" value={formData.name || ''} onChange={e => setFormData({ ...formData, name: e.target.value })} required disabled={isActionLoading} />
                            </div>
                            <div className="form-group">
                                <label>Mô tả <span className="req">*</span></label>
                                <textarea value={formData.description || ''} onChange={e => setFormData({ ...formData, description: e.target.value })} required disabled={isActionLoading} />
                            </div>

                            {modal.type === 'FOOD' && (
                                <div className="form-grid-2">
                                    <div className="form-group">
                                        <label>Giá bán (VNĐ) <span className="req">*</span></label>
                                        <input type="number" value={formData.price || 0} onChange={e => setFormData({ ...formData, price: e.target.value })} required disabled={isActionLoading} />
                                    </div>
                                    <div className="form-group">
                                        <label>Trạng thái</label>
                                        <select value={formData.status || 'OPENING'} onChange={e => setFormData({ ...formData, status: e.target.value })} disabled={isActionLoading}>
                                            <option value="OPENING">Đang bán</option>
                                            <option value="CLOSED">Tạm hết</option>
                                        </select>
                                    </div>
                                </div>
                            )}

                            <div className="modal-actions">
                                <button type="submit" className="btn-save" disabled={isActionLoading}>
                                    {isActionLoading ? "Đang lưu..." : "Lưu dữ liệu"}
                                </button>
                                <button type="button" className="btn-cancel" onClick={() => setModal({ isOpen: false })} disabled={isActionLoading}>Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default MenuManagement;