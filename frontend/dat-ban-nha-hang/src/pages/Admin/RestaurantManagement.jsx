
import React, { useState } from 'react';
import { useRestaurantManagement } from '../../hooks/admin/useRestaurantManagement';
import './RestaurantManagement.css';

function RestaurantManagement() {

    const {
        restaurants,
        isLoading,
        isActionLoading,
        toggleStatus,
        updateCommission
    } = useRestaurantManagement();

    const [searchTerm, setSearchTerm] = useState("");


    const [isCommissionModalOpen, setCommissionModalOpen] = useState(false);
    const [editingRest, setEditingRest] = useState(null);
    const [commissionForm, setCommissionForm] = useState({ commissionType: "PERCENTAGE", baseCommissionValue: 0 });

    // Lọc tìm kiếm
    const filteredRestaurants = restaurants.filter(rest =>
        rest.restaurantName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        rest.restaurantId.toString().includes(searchTerm)
    );

    // Mở Modal
    const openCommissionModal = (restaurant) => {
        setEditingRest(restaurant);
        setCommissionForm({
            commissionType: restaurant.commissionType || "PERCENTAGE",
            baseCommissionValue: restaurant.baseCommissionValue || 0
        });
        setCommissionModalOpen(true);
    };

    // Lưu Modal
    const handleSaveCommission = async (e) => {
        e.preventDefault();
        const success = await updateCommission(editingRest.restaurantId, commissionForm);
        if (success) {
            setCommissionModalOpen(false); // Đóng modal nếu gọi API thành công
        }
    };

    if (isLoading) return <div className="admin-page"><p>Đang tải danh sách nhà hàng...</p></div>;

    return (
        <div className="admin-page management-page">
            <header className="page-header flex-between">
                <div>
                    <h1>Quản lý Nhà hàng</h1>
                    <p>Tổng số: {restaurants.length} nhà hàng trên hệ thống</p>
                </div>
                <div className="search-box">
                    <input
                        type="text"
                        placeholder="Tìm theo tên hoặc ID..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </header>

            <div className="table-responsive">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Tên nhà hàng</th>
                            <th>Chủ sở hữu</th>
                            <th>Trạng thái</th>
                            <th>Hoa hồng</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredRestaurants.map((rest) => (
                            <tr key={rest.restaurantId} className={rest.status === "SUSPENDED" ? "row-suspended" : ""}>
                                <td>{rest.restaurantId}</td>
                                <td><strong>{rest.restaurantName}</strong></td>
                                <td>{rest.managerName}</td>
                                <td>
                                    <span className={`badge ${rest.status.toLowerCase()}`}>
                                        {rest.status === "ACTIVE" ? "Đang hoạt động" : "Bị khóa"}
                                    </span>
                                </td>
                                <td>
                                    <div className="commission-display">
                                        {rest.commissionType === "PERCENTAGE"
                                            ? `${rest.baseCommissionValue}% / đơn`
                                            : `${rest.baseCommissionValue.toLocaleString('vi-VN')} ₫ / đơn`
                                        }
                                        <button
                                            className="btn-icon"
                                            onClick={() => openCommissionModal(rest)}
                                            disabled={isActionLoading}
                                        >
                                            Sửa
                                        </button>
                                    </div>
                                </td>
                                <td>
                                    <button
                                        className={`btn-toggle ${rest.status === "ACTIVE" ? "suspend" : "activate"}`}
                                        onClick={() => toggleStatus(rest.restaurantId, rest.status)}
                                        disabled={isActionLoading}
                                    >
                                        {rest.status === "ACTIVE" ? " Khóa" : " Mở khóa"}
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {filteredRestaurants.length === 0 && (
                            <tr><td colSpan="6" className="text-center">Không tìm thấy nhà hàng nào.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Modal Cấu hình Hoa hồng */}
            {isCommissionModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Cấu hình Hoa hồng</h2>
                        <p>Nhà hàng: <strong>{editingRest?.restaurantName}</strong></p>

                        <form onSubmit={handleSaveCommission}>
                            <div className="form-group">
                                <label>Loại hoa hồng</label>
                                <select
                                    value={commissionForm.commissionType}
                                    onChange={(e) => setCommissionForm({ ...commissionForm, commissionType: e.target.value })}
                                    disabled={isActionLoading}
                                >
                                    <option value="PERCENTAGE">Phần trăm (%)</option>
                                    <option value="FIXED">Giá cố định (VNĐ)</option>
                                </select>
                            </div>
                            <div className="form-group">
                                <label>Giá trị</label>
                                <input
                                    type="number"
                                    min="0"
                                    value={commissionForm.baseCommissionValue}
                                    onChange={(e) => setCommissionForm({ ...commissionForm, baseCommissionValue: Number(e.target.value) })}
                                    required
                                    disabled={isActionLoading}
                                />
                            </div>
                            <div className="modal-actions">
                                <button type="submit" className="btn-save" disabled={isActionLoading}>
                                    {isActionLoading ? "Đang lưu..." : "Lưu cấu hình"}
                                </button>
                                <button type="button" className="btn-cancel" onClick={() => setCommissionModalOpen(false)} disabled={isActionLoading}>
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

export default RestaurantManagement;