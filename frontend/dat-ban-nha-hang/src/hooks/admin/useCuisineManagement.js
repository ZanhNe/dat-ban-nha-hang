import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';

export const useCuisineManagement = () => {
    const [cuisines, setCuisines] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);

    // 1 Lấy danh sách
    const fetchCuisines = useCallback(async () => {
        setIsLoading(true);
        try {
            const res = await adminService.getAllCuisines();
            if (res.status === 200) {
                setCuisines(res.data);
            }
        } catch (error) {
            console.error("Lỗi fetch Cuisines:", error);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchCuisines();
    }, [fetchCuisines]);
    // Thêm mới
    const createCuisine = async (formData) => {
        setIsActionLoading(true);
        try {
            const res = await adminService.createCuisine(formData);
            if (res.status === 201) {
                alert(res.message);
                setCuisines(prev => [...prev, res.data]); // Thêm data mới vào UI
                return true;
            }
        } catch (error) {
            console.error("Lỗi tạo danh mục:", error);
            alert("Tạo thất bại!");
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    // 3 Cập nhật
    const updateCuisine = async (id, formData) => {
        setIsActionLoading(true);
        try {
            const res = await adminService.updateCuisine(id, formData);
            if (res.status === 200) {
                alert(res.message);
                setCuisines(prev => prev.map(c => c.cuisineId === id ? res.data : c)); // Cập nhật UI
                return true;
            }
        } catch (error) {
            console.error("Lỗi cập nhật danh mục:", error);
            alert("Cập nhật thất bại!");
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    // 4 Xóa
    const deleteCuisine = async (id, name) => {
        if (!window.confirm(`Xác nhận XÓA danh mục "${name}"?`)) return;

        setIsActionLoading(true);
        try {
            const res = await adminService.deleteCuisine(id);
            if (res.status === 200) {
                alert(res.message);
                setCuisines(prev => prev.filter(c => c.cuisineId !== id)); // Xóa khỏi UI
            }
        } catch (error) {
            console.error("Lỗi xóa danh mục:", error);
            alert("Xóa thất bại!");
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        cuisines,
        isLoading,
        isActionLoading,
        createCuisine,
        updateCuisine,
        deleteCuisine
    };
};

export default useCuisineManagement;