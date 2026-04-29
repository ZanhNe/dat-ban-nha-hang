import { useState, useEffect, useCallback } from 'react';
import adminService from '../../services/adminService';
import { formatApiError } from '../../services/apiShape';

export const useCuisineManagement = () => {
    const [cuisines, setCuisines] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isActionLoading, setIsActionLoading] = useState(false);
    const [error, setError] = useState('');

    // 1 Lấy danh sách
    const fetchCuisines = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            const res = await adminService.getAllCuisines();
            if (res.status === 200) {
                setCuisines(res.data);
            }
        } catch (error) {
            console.error("Lỗi fetch Cuisines:", error);
            setError(formatApiError(error, 'Không thể tải danh mục ẩm thực.').displayMessage);
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
        setError('');
        try {
            const res = await adminService.createCuisine(formData);
            if (res.status === 201) {
                setCuisines(prev => [...prev, res.data]); // Thêm data mới vào UI
                return true;
            }
        } catch (error) {
            console.error("Lỗi tạo danh mục:", error);
            setError(formatApiError(error, 'Tạo danh mục thất bại.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    // 3 Cập nhật
    const updateCuisine = async (id, formData) => {
        setIsActionLoading(true);
        setError('');
        try {
            const res = await adminService.updateCuisine(id, formData);
            if (res.status === 200) {
                setCuisines(prev => prev.map(c => c.cuisineId === id ? res.data : c)); // Cập nhật UI
                return true;
            }
        } catch (error) {
            console.error("Lỗi cập nhật danh mục:", error);
            setError(formatApiError(error, 'Cập nhật danh mục thất bại.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
        return false;
    };

    // 4 Xóa
    const deleteCuisine = async (id, name) => {
        if (!window.confirm(`Xác nhận XÓA danh mục "${name}"?`)) return;

        setIsActionLoading(true);
        setError('');
        try {
            const res = await adminService.deleteCuisine(id);
            if (res.status === 200) {
                setCuisines(prev => prev.filter(c => c.cuisineId !== id)); // Xóa khỏi UI
            }
        } catch (error) {
            console.error("Lỗi xóa danh mục:", error);
            setError(formatApiError(error, 'Xóa danh mục thất bại.').displayMessage);
        } finally {
            setIsActionLoading(false);
        }
    };

    return {
        cuisines,
        isLoading,
        isActionLoading,
        error,
        createCuisine,
        updateCuisine,
        deleteCuisine
    };
};

export default useCuisineManagement;