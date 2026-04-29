
import React, { useState } from 'react';
import { useRestaurantManagement } from '../../hooks/admin/useRestaurantManagement';
import PaginationBar from '../../components/ui/PaginationBar';
import './RestaurantManagement.css';

const statusLabelMap = {
    OPENING: "Đang hoạt động",
    ACTIVE: "Đang hoạt động",
    PENDING: "Chờ duyệt",
    SUSPENDED: "Tạm khóa",
    REJECTED: "Từ chối",
    CLOSED: "Đóng cửa"
};

function RestaurantManagement() {

    const {
        restaurants,
        isLoading,
        isActionLoading,
        error,
        toggleStatus,
        updateCommission,
        page,
        setPage,
        meta
    } = useRestaurantManagement();

    const [searchTerm, setSearchTerm] = useState("");


    const [isCommissionModalOpen, setCommissionModalOpen] = useState(false);
    const [editingRest, setEditingRest] = useState(null);
    const [commissionForm, setCommissionForm] = useState({ commissionType: "PERCENTAGE", baseCommissionValue: 0 });

    // Lọc tìm kiếm
    const filteredRestaurants = restaurants.filter(rest =>
        String(rest.restaurantName || "").toLowerCase().includes(searchTerm.toLowerCase()) ||
        String(rest.restaurantId || "").includes(searchTerm)
    );

    // Mở Modal
    const openCommissionModal = (restaurant) => {
        setEditingRest(restaurant);
        setCommissionForm({
            commissionType: restaurant.commissionType || "PERCENTAGE",
            baseCommissionValue: Number(restaurant.baseCommissionValue ?? 0)
        });
        setCommissionModalOpen(true);
    };

    const handleSaveCommission = async (e) => {
        e.preventDefault();
        const success = await updateCommission(editingRest.restaurantId, commissionForm);
        if (success) {
            setCommissionModalOpen(false);
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
            {error && <p className="status-error">{error}</p>}

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
                                    <span className={`status-badge ${String(rest.status || "pending").toLowerCase()}`}>
                                        {statusLabelMap[rest.status] || rest.status}
                                    </span>
                                </td>
                                <td>
                                    {rest.status === "PENDING" || rest.status === "REJECTED" || rest.status === "CLOSED" ? "---" : (
                                        <div className="commission-display">
                                            {rest.commissionType === "PERCENTAGE"
                                                ? `${Number(rest.baseCommissionValue ?? 0)}% / đơn`
                                                : `${Number(rest.baseCommissionValue ?? 0).toLocaleString('vi-VN')} ₫ / đơn`
                                            }
                                            <button
                                                className="btn-icon"
                                                onClick={() => openCommissionModal(rest)}
                                                disabled={isActionLoading}
                                            >
                                                Sửa
                                            </button>
                                        </div>
                                    )}
                                </td>
                                <td>
                                    {rest.status !== "PENDING" && rest.status !== "REJECTED" && rest.status !== "CLOSED" ? (

                                        <button
                                            className={`btn-toggle ${rest.status === "OPENING" || rest.status === "ACTIVE" ? "suspend" : "activate"}`}
                                            onClick={() => toggleStatus(rest.restaurantId, rest.status)}
                                            disabled={isActionLoading}
                                        >
                                            {rest.status === "OPENING" || rest.status === "ACTIVE" ? " Khóa" : " Mở khóa"}
                                        </button>
                                    ) : null}
                                </td>
                            </tr>
                        ))}
                        {filteredRestaurants.length === 0 && (
                            <tr><td colSpan="6" className="text-center">Không tìm thấy nhà hàng nào.</td></tr>
                        )}
                    </tbody>
                </table>
                {meta && (
                    <PaginationBar
                        page={page}
                        totalPages={meta.totalPages}
                        totalItems={meta.totalItems}
                        onPageChange={setPage}
                        disabled={isLoading || isActionLoading}
                    />
                )}
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