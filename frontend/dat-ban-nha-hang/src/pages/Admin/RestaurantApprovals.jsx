
import React, { useState } from 'react';
import { useRestaurantApprovals } from '../../hooks/admin/useRestaurantApprovals';
import PaginationBar from '../../components/ui/PaginationBar';
import './RestaurantApprovals.css';

function RestaurantApprovals() {

    const {
        pendingRestaurants,
        viewingDetail,
        isLoading,
        isActionLoading,
        error,
        fetchDetail,
        closeDetail,
        handleApprovalAction,
        page,
        setPage,
        meta
    } = useRestaurantApprovals();

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
        handleApprovalAction(id, "REJECTED");
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
                        {error && <div className="empty-state">{error}</div>}
                        <ul className="info-list">
                            <li><strong>Mã nhà hàng:</strong> {viewingDetail.restaurantId}</li>
                            <li><strong>Quản lý:</strong> {viewingDetail.manager?.fullName || 'Chưa có quản lý'}</li>
                            <li><strong>Số điện thoại:</strong> {viewingDetail.manager?.phone || 'Chưa cập nhật'}</li>
                            <li><strong>Trạng thái:</strong> <span className="status-badge">{viewingDetail.status}</span></li>
                            <li><strong>Địa chỉ:</strong> {viewingDetail.address || 'Chưa cập nhật'}</li>
                            <li><strong>Mô tả:</strong> {viewingDetail.description || 'Chưa có mô tả'}</li>
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
                            {viewingDetail.legalDocs.length === 0 && <p>Chưa có tài liệu pháp lý.</p>}
                            {viewingDetail.legalDocs.map((doc) => (
                                <div key={doc.docId} className="mb-4 rounded-lg border p-3">
                                    <p><strong>{doc.docName || `Tài liệu #${doc.docId}`}</strong></p>
                                    <p>Loại: {doc.docType || 'Không xác định'}</p>
                                    <p>Trạng thái: {doc.docStatus || 'Không xác định'}</p>
                                    <p>Hết hạn: {doc.expireDate ? new Date(doc.expireDate).toLocaleString('vi-VN') : 'Không có'}</p>
                                    {doc.docUrl && (
                                        <a href={doc.docUrl} target="_blank" rel="noreferrer" className="btn-view">
                                            Mở tài liệu
                                        </a>
                                    )}
                                </div>
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
            {error && <div className="empty-state">{error}</div>}

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
                                    <td><span className="status-badge pending">{rest.status}</span></td>
                                    <td>
                                        <button className="btn-view" onClick={() => fetchDetail(rest.restaurantId)}>
                                            Xem & Duyệt
                                        </button>
                                    </td>
                                </tr>
                            ))}
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
            )}
        </div>
    );
}

export default RestaurantApprovals;