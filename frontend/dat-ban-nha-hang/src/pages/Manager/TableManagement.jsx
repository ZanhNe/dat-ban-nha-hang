import React, { useState } from 'react';
import { useTableManagement } from '../../hooks/manager/useTableManagement';
import './TableManagement.css';

function TableManagement() {
    const {
        areas, activeAreaId, setActiveAreaId, tables,
        isLoading, isActionLoading, onSaveArea, onDeleteArea, onSaveTable, onDeleteTable
    } = useTableManagement();

    const [modal, setModal] = useState({ isOpen: false, type: '', data: null });
    const [formData, setFormData] = useState({});

    const openModal = (type, data = null) => {
        setModal({ isOpen: true, type, data });
        setFormData(data || (type === 'AREA' ? { name: '', description: '', status: 'ACTIVE' } : { name: '', capacity: 4, status: 'AVAILABLE' }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        let success = false;
        if (modal.type === 'AREA') {
            success = await onSaveArea(formData, modal.data?.tableAreaId);
        } else {
            success = await onSaveTable(formData, modal.data?.tableId);
        }
        if (success) setModal({ isOpen: false });
    };

    if (isLoading) return <div className="manager-page">Đang tải sơ đồ bàn...</div>;

    return (
        <div className="manager-page table-mgmt">
            <header className="page-header flex-between">
                <div>
                    <h1>Sơ đồ bàn ăn</h1>
                    <p>Quản lý khu vực và trạng thái bàn trống</p>
                </div>
                <div className="header-actions">
                    <button className="btn-secondary" onClick={() => openModal('AREA')}>+ Thêm Khu vực</button>
                    <button className="btn-primary" onClick={() => openModal('TABLE')} disabled={!activeAreaId}>+ Thêm Bàn</button>
                </div>
            </header>

            {/* TAB KHU VỰC */}
            <div className="area-tabs">
                {areas.map(area => (
                    <div key={area.tableAreaId} className={`area-tab ${activeAreaId === area.tableAreaId ? 'active' : ''}`} onClick={() => setActiveAreaId(area.tableAreaId)}>
                        <span>{area.name}</span>
                        <div className="area-actions">
                            <button onClick={(e) => { e.stopPropagation(); openModal('AREA', area); }}>✏️</button>
                            <button onClick={(e) => { e.stopPropagation(); onDeleteArea(area.tableAreaId); }}>🗑️</button>
                        </div>
                    </div>
                ))}
            </div>

            {/* GRID BÀN ĂN */}
            <div className="tables-grid">
                {tables.map(table => (
                    <div key={table.tableId} className={`table-card ${table.status.toLowerCase()}`}>
                        <div className="table-header">
                            <span className="capacity">👥 {table.capacity}</span>
                            <div className="table-actions">
                                <button onClick={() => openModal('TABLE', table)}>✏️</button>
                                <button onClick={() => onDeleteTable(table.tableId)}>🗑️</button>
                            </div>
                        </div>
                        <div className="table-icon"></div>
                        <h3 className="table-name">{table.name}</h3>
                        <span className="status-label">{table.status}</span>
                    </div>
                ))}
                {tables.length === 0 && <p className="empty-msg">Khu vực này chưa có bàn nào.</p>}
            </div>

            {/* MODAL THÊM/SỬA */}
            {modal.isOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>{modal.data ? 'Cập nhật' : 'Thêm mới'} {modal.type === 'AREA' ? 'Khu vực' : 'Bàn ăn'}</h2>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Tên gọi</label>
                                <input type="text" value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} required />
                            </div>

                            {modal.type === 'TABLE' ? (
                                <>
                                    <div className="form-group">
                                        <label>Sức chứa (Người)</label>
                                        <input type="number" value={formData.capacity} onChange={e => setFormData({ ...formData, capacity: e.target.value })} required />
                                    </div>
                                    <div className="form-group">
                                        <label>Trạng thái</label>
                                        <select value={formData.status} onChange={e => setFormData({ ...formData, status: e.target.value })}>
                                            <option value="AVAILABLE">Sẵn sàng (Available)</option>
                                            <option value="OCCUPIED">Có khách (Occupied)</option>
                                            <option value="RESERVED">Đã đặt (Reserved)</option>
                                            <option value="CLEANING">Đang dọn (Cleaning)</option>
                                        </select>
                                    </div>
                                </>
                            ) : (
                                <div className="form-group">
                                    <label>Trạng thái khu vực</label>
                                    <select value={formData.status} onChange={e => setFormData({ ...formData, status: e.target.value })}>
                                        <option value="ACTIVE">Hoạt động</option>
                                        <option value="INACTIVE">Tạm đóng</option>
                                    </select>
                                </div>
                            )}

                            <div className="modal-actions">
                                <button type="submit" className="btn-save" disabled={isActionLoading}>Lưu</button>
                                <button type="button" className="btn-cancel" onClick={() => setModal({ isOpen: false })}>Hủy</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}

export default TableManagement;