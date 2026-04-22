import React, { useState } from 'react';
import './CuisineManagement.css';

function CuisineManagement() {
    // 1. Mock Data: Danh mục ẩm thực
    const [cuisines, setCuisines] = useState([
        { id: "C01", name: "Lẩu (Hotpot)", description: "Các món lẩu truyền thống và hiện đại", isActive: true },
        { id: "C02", name: "Đồ Nướng (BBQ)", description: "Thịt nướng Hàn Quốc, Nhật Bản, BBQ Á Âu", isActive: true },
        { id: "C03", name: "Hải sản", description: "Các món ăn từ hải sản tươi sống", isActive: true },
        { id: "C04", name: "Món Chay", description: "Ẩm thực chay thanh đạm, tốt cho sức khỏe", isActive: false },
    ]);

    const [searchTerm, setSearchTerm] = useState("");

    // State cho Form Modal (Thêm/Sửa)
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingCuisine, setEditingCuisine] = useState(null); // null = Đang Thêm Mới
    const [formData, setFormData] = useState({ name: "", description: "", isActive: true });

    // Mở form Thêm mới
    const handleAddNew = () => {
        setEditingCuisine(null);
        setFormData({ name: "", description: "", isActive: true });
        setIsModalOpen(true);
    };

    // Mở form Chỉnh sửa
    const handleEdit = (cuisine) => {
        setEditingCuisine(cuisine);
        setFormData({ name: cuisine.name, description: cuisine.description, isActive: cuisine.isActive });
        setIsModalOpen(true);
    };

    // Lưu dữ liệu (Thêm hoặc Sửa)
    const handleSave = (e) => {
        e.preventDefault();
        if (!formData.name.trim()) return alert("Vui lòng nhập tên danh mục!");

        if (editingCuisine) {
            // Lưu khi SỬA
            setCuisines(prev => prev.map(c =>
                c.id === editingCuisine.id ? { ...c, ...formData } : c
            ));
        } else {
            // Lưu khi THÊM MỚI
            const newId = "C" + Math.floor(Math.random() * 1000).toString().padStart(2, '0');
            setCuisines([...cuisines, { id: newId, ...formData }]);
        }
        setIsModalOpen(false);
    };

    // Xóa danh mục
    const handleDelete = (id, name) => {
        if (window.confirm(`Xác nhận XÓA danh mục "${name}"? Lưu ý: Hành động này có thể ảnh hưởng đến các nhà hàng đang sử dụng danh mục này.`)) {
            setCuisines(prev => prev.filter(c => c.id !== id));
        }
    };

    // Lọc dữ liệu theo Search
    const filteredCuisines = cuisines.filter(c =>
        c.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="admin-page cuisine-page">
            <header className="page-header flex-between">
                <div>
                    <h1>Quản lý Ẩm thực</h1>
                    <p>Định nghĩa các danh mục để khách hàng dễ dàng phân loại</p>
                </div>
                <div className="header-actions">
                    <input
                        type="text"
                        placeholder="Tìm kiếm danh mục..."
                        className="search-input"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button className="btn-add-new" onClick={handleAddNew}>
                        + Thêm Danh mục
                    </button>
                </div>
            </header>

            <div className="table-responsive">
                <table className="admin-table">
                    <thead>
                        <tr>
                            <th width="10%">ID</th>
                            <th width="25%">Tên danh mục</th>
                            <th width="35%">Mô tả</th>
                            <th width="15%">Trạng thái</th>
                            <th width="15%">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredCuisines.map((cuisine) => (
                            <tr key={cuisine.id} className={!cuisine.isActive ? "row-disabled" : ""}>
                                <td><strong>{cuisine.id}</strong></td>
                                <td>{cuisine.name}</td>
                                <td><span className="text-truncate">{cuisine.description}</span></td>
                                <td>
                                    <span className={`badge ${cuisine.isActive ? 'active' : 'suspended'}`}>
                                        {cuisine.isActive ? "Hiển thị" : "Đã ẩn"}
                                    </span>
                                </td>
                                <td>
                                    <div className="action-group">
                                        <button className="btn-edit" onClick={() => handleEdit(cuisine)}> Sửa </button>
                                        <button className="btn-delete" onClick={() => handleDelete(cuisine.id, cuisine.name)}> Xóa </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                        {filteredCuisines.length === 0 && (
                            <tr><td colSpan="5" className="text-center">Chưa có danh mục nào.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Modal Thêm/Sửa Danh Mục */}
            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>{editingCuisine ? "Chỉnh sửa Danh mục" : "Thêm Danh mục Mới"}</h2>

                        <form onSubmit={handleSave}>
                            <div className="form-group">
                                <label>Tên danh mục <span className="required">*</span></label>
                                <input
                                    type="text"
                                    placeholder="Ví dụ: Đồ Nướng..."
                                    value={formData.name}
                                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Mô tả ngắn</label>
                                <textarea
                                    placeholder="Mô tả các món ăn thuộc danh mục này..."
                                    rows="3"
                                    value={formData.description}
                                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                />
                            </div>

                            <div className="form-group checkbox-group">
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={formData.isActive}
                                        onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                                    />
                                    Hiển thị danh mục này cho Khách hàng
                                </label>
                            </div>

                            <div className="modal-actions">
                                <button type="submit" className="btn-save">
                                    {editingCuisine ? "Cập nhật" : "Tạo mới"}
                                </button>
                                <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>
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

export default CuisineManagement;