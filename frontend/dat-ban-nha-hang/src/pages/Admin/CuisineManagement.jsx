
import React, { useState } from 'react';
import useCuisineManagement from '../../hooks/admin/useCuisineManagement';
import './CuisineManagement.css';

function CuisineManagement() {

    const {
        cuisines,
        isLoading,
        isActionLoading,
        createCuisine,
        updateCuisine,
        deleteCuisine
    } = useCuisineManagement();

    console.log(cuisines)

    const [searchTerm, setSearchTerm] = useState("");


    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingCuisine, setEditingCuisine] = useState(null);
    const [formData, setFormData] = useState({ name: "" });


    const handleAddNew = () => {
        setEditingCuisine(null);
        setFormData({ name: "" });
        setIsModalOpen(true);
    };

    const handleEdit = (cuisine) => {
        setEditingCuisine(cuisine);
        setFormData({
            name: cuisine.name,
        });
        setIsModalOpen(true);
    };

    const handleSave = async (e) => {
        e.preventDefault();
        if (!formData.name.trim()) return alert("Vui lòng nhập tên danh mục!");

        let success = false;
        if (editingCuisine) {
            success = await updateCuisine(editingCuisine.cuisineId, formData);
        } else {

            success = await createCuisine(formData);
        }

        if (success) {
            setIsModalOpen(false);
        }
    };

    const filteredCuisines = cuisines.filter(c =>
        c.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (isLoading) return <div className="admin-page"><p>Đang tải danh mục ẩm thực...</p></div>;

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
                    <button className="btn-add-new" onClick={handleAddNew} disabled={isActionLoading}>
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
                            <th width="15%">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredCuisines.map((cuisine) => (
                            <tr key={cuisine.cuisineId} >
                                <td><strong>{cuisine.cuisineId}</strong></td>
                                <td>{cuisine.name}</td>
                                <td>
                                    <div className="action-group">
                                        <button className="btn-edit" onClick={() => handleEdit(cuisine)} disabled={isActionLoading}> Sửa </button>
                                        <button className="btn-delete" onClick={() => deleteCuisine(cuisine.cuisineId, cuisine.name)} disabled={isActionLoading}> Xóa </button>
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
                                    onChange={(e) => setFormData({ name: e.target.value })}
                                    required
                                    disabled={isActionLoading}
                                />
                            </div>





                            <div className="modal-actions">
                                <button type="submit" className="btn-save" disabled={isActionLoading}>
                                    {isActionLoading ? "Đang lưu..." : (editingCuisine ? "Cập nhật" : "Tạo mới")}
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

export default CuisineManagement;