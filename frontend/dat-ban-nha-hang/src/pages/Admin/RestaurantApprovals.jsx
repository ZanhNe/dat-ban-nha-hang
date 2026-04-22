
import React, { useState } from 'react';
import { useRestaurantApprovals } from '../../hooks/admin/useRestaurantApprovals';
import './RestaurantApprovals.css';

function RestaurantApprovals() {

    const {
        pendingRestaurants,
        viewingDetail,
        isLoading,
        isActionLoading,
        fetchDetail,
        closeDetail,
        handleApprovalAction
    } = useRestaurantApprovals();

    const [rejectReason, setRejectReason] = useState("");
    const [showRejectForm, setShowRejectForm] = useState(false);

    // Bấm nút Duyệt
    const onApprove = (id) => {
        if (window.confirm("Bạn có chắc chắn muốn DUYỆT nhà hàng này?")) {
            handleApprovalAction(id, "APPROVED");
        }
    };

    // Submit form Từ chối
    const onRejectSubmit = (e, id) => {
        e.preventDefault();
        if (!rejectReason.trim()) {
            alert("Vui lòng nhập lý do từ chối!");
            return;
        }
        handleApprovalAction(id, "REJECTED", rejectReason);
        setRejectReason("");
        setShowRejectForm(false);
    };

    // Nếu đang tải dữ liệu (để tránh màn hình giật)
    if (isLoading && !viewingDetail) {
        return <div className="admin-page approvals-page"><p>Đang tải dữ liệu...</p></div>;
    }


    if (viewingDetail) {
        return (
            <div className="admin-page approvals-page">
                <button className="btn-back" onClick={closeDetail} disabled={isActionLoading}>
                    ⬅ Quay lại danh sách
                </button>

                <div className="detail-container">
                    <div className="detail-info">
                        <h2>Chi tiết đăng ký: {viewingDetail.restaurantName}</h2>
                        <ul className="info-list">
                            <li><strong>Mã nhà hàng:</strong> {viewingDetail.restaurantId}</li>
                            <li><strong>Quản lý:</strong> {viewingDetail.manager.fullName}</li>
                            <li><strong>Số điện thoại:</strong> {viewingDetail.manager.phone}</li>
                            <li><strong>Trạng thái:</strong> <span className="badge pending">{viewingDetail.status}</span></li>
                        </ul>

                        {!showRejectForm ? (
                            <div className="action-buttons">
                                <button className="btn-approve" onClick={() => onApprove(viewingDetail.restaurantId)} disabled={isActionLoading}>
                                    {isActionLoading ? "Đang xử lý..." : " Duyệt (Approve)"}
                                </button>
                                <button className="btn-reject" onClick={() => setShowRejectForm(true)} disabled={isActionLoading}>
                                    Từ chối (Reject)
                                </button>
                            </div>
                        ) : (
                            <form className="reject-form" onSubmit={(e) => onRejectSubmit(e, viewingDetail.restaurantId)}>
                                <h3>Lý do từ chối</h3>
                                <textarea
                                    placeholder="Nhập lý do chi tiết để thông báo cho đối tác..."
                                    value={rejectReason}
                                    onChange={(e) => setRejectReason(e.target.value)}
                                    rows="4"
                                    disabled={isActionLoading}
                                />
                                <div className="reject-actions">
                                    <button type="submit" className="btn-confirm-reject" disabled={isActionLoading}>
                                        {isActionLoading ? "Đang xử lý..." : "Xác nhận Từ chối"}
                                    </button>
                                    <button type="button" className="btn-cancel" onClick={() => setShowRejectForm(false)} disabled={isActionLoading}>
                                        Hủy
                                    </button>
                                </div>
                            </form>
                        )}
                    </div>

                    <div className="detail-docs">
                        <h3>Giấy phép kinh doanh / Tài liệu pháp lý</h3>
                        <div className="doc-preview">
                            {viewingDetail.legalDocs.map((doc) => (
                                <img key={doc.docId} src={doc.docUrl} alt="Giấy phép kinh doanh" style={{ marginBottom: '10px' }} />
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="admin-page approvals-page">
            <header className="page-header">
                <h1>Duyệt Yêu Cầu Mở Nhà Hàng</h1>
                <p>Có {pendingRestaurants.length} yêu cầu đang chờ xử lý</p>
            </header>

            {pendingRestaurants.length === 0 ? (
                <div className="empty-state">Không có yêu cầu nào đang chờ duyệt lúc này.</div>
            ) : (
                <div className="table-responsive">
                    <table className="admin-table">
                        <thead>
                            <tr>
                                <th>Mã NH</th>
                                <th>Tên nhà hàng</th>
                                <th>Người quản lý</th>
                                <th>Ngày nộp</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            {pendingRestaurants.map((rest) => (
                                <tr key={rest.restaurantId}>
                                    <td>{rest.restaurantId}</td>
                                    <td><strong>{rest.restaurantName}</strong></td>
                                    <td>{rest.managerName}</td>

                                    <td>{new Date(rest.createdAt).toLocaleString('vi-VN')}</td>
                                    <td><span className="badge pending">{rest.status}</span></td>
                                    <td>
                                        <button className="btn-view" onClick={() => fetchDetail(rest.restaurantId)}>
                                            Xem & Duyệt
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default RestaurantApprovals;