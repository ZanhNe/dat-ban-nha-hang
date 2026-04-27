import { useState, useEffect, useCallback } from 'react';
import { managerService } from '../../services/managerService';
import { useAtomValue } from "jotai";
import { userAtom } from "../../store/authStore";
import { useNavigate } from 'react-router-dom';

export const useRestaurantInfo = () => {
    const user = useAtomValue(userAtom);
    const restaurantId = user?.workplace?.restaurantId || 101; // Fallback an toàn
    const navigate = useNavigate();

    const [info, setInfo] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    // GET: Fetch thông tin
    const fetchInfo = useCallback(async () => {
        setIsLoading(true);
        try {
            const res = await managerService.getRestaurantInfo(restaurantId);
            if (res.status === 200) {
                setInfo(res.data);
            }
        } catch (error) {
            console.error("Lỗi khi lấy thông tin nhà hàng:", error);
        } finally {
            setIsLoading(false);
        }
    }, [restaurantId]);

    useEffect(() => {
        if (restaurantId) fetchInfo();
    }, [fetchInfo, restaurantId]);

    // PUT: Cập nhật
    const updateInfo = async (formData) => {
        setIsSaving(true);
        try {
            const res = await managerService.updateRestaurantInfo(restaurantId, formData);
            if (res.status === 200) {
                alert(" " + res.message);
                setInfo(res.data);
                return true;
            } else {
                alert("❌ Lỗi: " + res.message);
            }
        } catch (error) {
            console.error("Lỗi cập nhật:", error);
            alert("Lỗi hệ thống khi cập nhật!");
        } finally {
            setIsSaving(false);
        }
        return false;
    };

    // DELETE: Xóa
    const deleteRestaurant = async () => {
        if (!window.confirm(" CẢNH BÁO NGUY HIỂM: Bạn có chắc chắn muốn XÓA VĨNH VIỄN nhà hàng này? Hành động này không thể hoàn tác!")) {
            return;
        }

        setIsSaving(true);
        try {
            const res = await managerService.deleteRestaurant(restaurantId);
            if (res.status === 200) {
                alert(" Đã xóa nhà hàng thành công.");
                navigate('/');
            } else {
                alert(" Lỗi: " + res.message);
            }
        } catch (error) {
            console.error("Lỗi xóa:", error);
            alert("Lỗi hệ thống khi xóa!");
        } finally {
            setIsSaving(false);
        }
    };

    return { info, isLoading, isSaving, updateInfo, deleteRestaurant };
};